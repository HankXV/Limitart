package org.slingerxv.limitart.net.binary.client;

import java.net.SocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.net.binary.client.config.BinaryClientConfig;
import org.slingerxv.limitart.net.binary.client.listener.BinaryClientEventListener;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryDecoder;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.constant.InnerMessageEnum;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateClientMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateServerMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateSuccessServerMessage;
import org.slingerxv.limitart.net.binary.util.SendMessageUtil;
import org.slingerxv.limitart.util.SymmetricEncryptionUtil;
import org.slingerxv.limitart.util.TimerUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 二进制通信客户端
 * 
 * @author hank
 *
 */
public class BinaryClient extends ChannelInboundHandlerAdapter {
	private static Logger log = LogManager.getLogger();
	private BinaryClientEventListener clientEventListener;
	private BinaryClientConfig config;
	private static EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap;
	private Channel channel;
	private SymmetricEncryptionUtil decodeUtil;
	private TimerTask reconnectTask;

	public BinaryClient(BinaryClientConfig config, BinaryClientEventListener clientEventListener)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, MessageIDDuplicatedException {
		if (config == null) {
			throw new NullPointerException("BinaryClientConfig");
		}
		if (clientEventListener == null) {
			throw new NullPointerException("BinaryClientEventListener");
		}
		if (config.getFactory() == null) {
			throw new NullPointerException("MessageFactory");
		}
		this.config = config;
		this.clientEventListener = clientEventListener;
		// 内部消息注册
		config.getFactory().registerMsg(new ConnectionValidateServerHandler())
				.registerMsg(new ConnectionValidateSuccessServerHandler());
		decodeUtil = SymmetricEncryptionUtil.getDecodeInstance(config.getRemoteAddress().getPass());
		bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		log.info(config.getClientName() + " nio init");
		bootstrap.group(group).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.handler(new ChannelInitializerImpl(this));
		reconnectTask = new TimerTask() {

			@Override
			public void run() {
				connect0();
			}
		};
	}

	private class ChannelInitializerImpl extends ChannelInitializer<SocketChannel> {
		private BinaryClient client;

		private ChannelInitializerImpl(BinaryClient client) {
			this.client = client;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			AbstractBinaryDecoder decoder = config.getDecoder();
			ch.pipeline()
					.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(),
							decoder.getLengthFieldOffset(), decoder.getLengthFieldLength(),
							decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()));
			ch.pipeline().addLast(this.client);
		}

	}

	public void sendMessage(Message msg, Proc3<Boolean, Throwable, Channel> listener) throws Exception {
		SendMessageUtil.sendMessage(this.config.getEncoder(), channel, msg, listener);
	}

	public BinaryClient disConnect() {
		if (channel != null) {
			channel.close();
			channel = null;
		}
		return this;
	}

	public BinaryClient connect() {
		tryReconnect(0);
		return this;
	}

	private void connect0() {
		if (channel != null && channel.isWritable()) {
			return;
		}
		log.info(config.getClientName() + " start connect server：" + config.getRemoteAddress().getIp() + ":"
				+ config.getRemoteAddress().getPort() + "...");
		try {
			bootstrap.connect(config.getRemoteAddress().getIp(), config.getRemoteAddress().getPort())
					.addListener((ChannelFutureListener) channelFuture -> {
						channel = channelFuture.channel();
						if (channelFuture.isSuccess()) {
							log.info(config.getClientName() + " connect server：" + config.getRemoteAddress().getIp()
									+ ":" + config.getRemoteAddress().getPort() + " success！");
						} else {
							log.error(
									config.getClientName() + " try connect server：" + config.getRemoteAddress().getIp()
											+ ":" + config.getRemoteAddress().getPort() + " fail",
									channelFuture.cause().getMessage());
							if (config.getAutoReconnect() > 0) {
								tryReconnect(config.getAutoReconnect());
							}
						}
					}).sync();
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	@Override
	public boolean isSharable() {
		return true;
	}

	private void tryReconnect(int waitSeconds) {
		if (channel != null) {
			channel.close();
			channel = null;
		}
		if (waitSeconds > 0) {
			TimerUtil.scheduleGlobal(waitSeconds * 1000, reconnectTask);
		} else {
			connect0();
		}
	}

	private void decodeConnectionValidateData(String validateStr) {
		try {
			String decode = decodeUtil.decode(validateStr);
			int validateRandom = Integer.parseInt(decode);
			ConnectionValidateClientMessage msg = new ConnectionValidateClientMessage();
			msg.setValidateRandom(validateRandom);
			sendMessage(msg, null);
			log.info(config.getClientName() + " parse validate code success，return result：" + validateRandom);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private void onConnectionValidateSeccuss(String remote) {
		log.info("server validate success,remote:" + remote);
		this.clientEventListener.onConnectionEffective(this);
	}

	public String channelLongID() {
		return this.channel.id().asLongText();
	}

	public Channel channel() {
		return this.channel;
	}

	public SocketAddress remoteAddress() {
		return this.channel.remoteAddress();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object arg) throws Exception {
		ByteBuf buffer = (ByteBuf) arg;
		try {
			// 消息id
			short messageId = this.config.getDecoder().readMessageId(ctx.channel(), buffer);
			Message msg = config.getFactory().getMessage(messageId);
			if (msg == null) {
				throw new Exception(config.getClientName() + " message empty,id:" + messageId);
			}
			msg.buffer(buffer);
			msg.decode();
			msg.buffer(null);
			@SuppressWarnings("unchecked")
			IHandler<Message> handler = (IHandler<Message>) config.getFactory().getHandler(messageId);
			if (handler == null) {
				throw new Exception(config.getClientName() + " can not find handler for message,id:" + messageId);
			}
			msg.setHandler(handler);
			msg.setChannel(ctx.channel());
			msg.setClient(this);
			// 如果是内部消息，则自己消化
			if (InnerMessageEnum.getTypeByValue(messageId) != null) {
				handler.handle(msg);
			} else {
				this.clientEventListener.dispatchMessage(msg);
			}
		} finally {
			buffer.release();
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.clientEventListener.onChannelActive(this);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.clientEventListener.onChannelInactive(this);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		this.clientEventListener.onExceptionCaught(this, cause);
	}

	public BinaryClientConfig getConfig() {
		return this.config;
	}

	private class ConnectionValidateServerHandler implements IHandler<ConnectionValidateServerMessage> {

		@Override
		public void handle(ConnectionValidateServerMessage msg) {
			String validateStr = msg.getValidateStr();
			msg.getClient().decodeConnectionValidateData(validateStr);
		}

	}

	private class ConnectionValidateSuccessServerHandler implements IHandler<ConnectionValidateSuccessServerMessage> {

		@Override
		public void handle(ConnectionValidateSuccessServerMessage msg) {
			msg.getClient().onConnectionValidateSeccuss(msg.getChannel().remoteAddress().toString());
		}
	}
}
