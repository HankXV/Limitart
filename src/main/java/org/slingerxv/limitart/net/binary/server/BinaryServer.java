package org.slingerxv.limitart.net.binary.server;

import java.net.InetSocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryDecoder;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.constant.InnerMessageEnum;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateClientMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateServerMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateSuccessServerMessage;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig;
import org.slingerxv.limitart.net.binary.util.SendMessageUtil;
import org.slingerxv.limitart.net.define.AbstractNettyServer;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.util.RandomUtil;
import org.slingerxv.limitart.util.SymmetricEncryptionUtil;
import org.slingerxv.limitart.util.TimerUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 二进制通信服务器
 * 
 * @author Hank
 *
 */
public class BinaryServer extends AbstractNettyServer implements IServer {
	private static Logger log = LogManager.getLogger();
	private ServerBootstrap boot;
	private Channel channel;
	private BinaryServerConfig config;
	private ConcurrentHashMap<String, SessionValidateData> tempChannels = new ConcurrentHashMap<>();
	private SymmetricEncryptionUtil encrypUtil;
	private TimerTask clearTask;

	public BinaryServer(BinaryServerConfig config)
			throws MessageIDDuplicatedException {
		if (config == null) {
			throw new NullPointerException("BinaryServerConfig");
		}
		if (config.getFactory() == null) {
			throw new NullPointerException("MessageFactory");
		}
		this.config = config;
		// 初始化内部消息
		config.getFactory().registerMsg(new ConnectionValidateClientHandler());
		// 初始化加密工具
		try {
			encrypUtil = SymmetricEncryptionUtil.getEncodeInstance(config.getAddressPair().getPass(), "20170106");
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException e) {
			log.error(e, e);
		}
		boot = new ServerBootstrap();
		if (Epoll.isAvailable()) {
			boot.option(ChannelOption.SO_BACKLOG, 1024).channel(EpollServerSocketChannel.class)
					.childOption(ChannelOption.SO_LINGER, 0).childOption(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			log.info(config.getServerName() + " epoll init");
		} else {
			boot.channel(NioServerSocketChannel.class);
			log.info(config.getServerName() + " nio init");
		}
		boot.group(bossGroup, workerGroup).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializerImpl(this));
		clearTask = new TimerTask() {

			@Override
			public void run() {
				clearUnvalidatedConnection();
			}
		};
		TimerUtil.scheduleGlobal(1000, clearTask);
	}

	@Override
	public void startServer() {
		new Thread(() -> {
			try {
				boot.bind(config.getAddressPair().getPort()).addListener((ChannelFuture arg0) -> {
					if (arg0.isSuccess()) {
						channel = arg0.channel();
						log.info(config.getServerName() + " bind at port:" + config.getAddressPair().getPort());
						if(config.getOnServerBind() != null){
							config.getOnServerBind().run(arg0.channel());
						}
					}
				}).sync().channel().closeFuture().sync();
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}, config.getServerName() + "-Binder").start();
	}

	@Override
	public void stopServer() {
		if (channel != null) {
			channel.close();
		}
		TimerUtil.unScheduleGlobal(clearTask);
	}

	public void sendMessage(Channel channel, Message msg, Proc3<Boolean, Throwable, Channel> listener)
			throws Exception {
		SendMessageUtil.sendMessage(this.config.getEncoder(), channel, msg, listener);
	}

	public void sendMessage(List<Channel> channels, Message msg, Proc3<Boolean, Throwable, Channel> listener)
			throws Exception {
		SendMessageUtil.sendMessage(this.config.getEncoder(), channels, msg, listener);
	}

	private class ChannelInitializerImpl extends ChannelInitializer<SocketChannel> {
		private BinaryServer server;

		private ChannelInitializerImpl(BinaryServer server) {
			this.server = server;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			AbstractBinaryDecoder decoder = server.config.getDecoder();
			ch.pipeline()
					.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(),
							decoder.getLengthFieldOffset(), decoder.getLengthFieldLength(),
							decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()))
					.addLast(new ChannelInboundHandlerAdapter() {
						@Override
						public boolean isSharable() {
							return true;
						}

						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							exceptionCaught0(ctx, cause);
						}

						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							channelActive0(ctx);
						}

						@Override
						public void channelInactive(ChannelHandlerContext ctx) throws Exception {
							channelInactive0(ctx);
						}

						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							channelRead0(ctx, msg);
						}
					});
		}
	}

	/**
	 * 开始验证链接
	 * 
	 * @param context
	 */
	private void startConnectionValidate(Channel channel) {
		SessionValidateData data = new SessionValidateData(channel, System.currentTimeMillis(),
				RandomUtil.randomInt(0, 10000));
		// 增加到临时会话集合
		tempChannels.put(data.channel.id().asLongText(), data);
		// 通知客户端
		ConnectionValidateServerMessage msg = new ConnectionValidateServerMessage();
		String encode;
		try {
			encode = encrypUtil.encode(data.validateRandom + "");
		} catch (Exception e) {
			log.error(e, e);
			channel.close();
			log.info(config.getServerName() + " remote connection " + data.channel.remoteAddress()
					+ " discarded，server encryp util error！");
			return;
		}
		msg.setValidateStr(encode);
		try {
			sendMessage(channel, msg, (isSuccess, cause, channel1) -> {
				if (isSuccess) {
					log.info(config.getServerName() + " send client " + channel1.remoteAddress() + " validate token:"
							+ encode + "success！");
				} else {
					log.error(config.getServerName() + " send client " + channel1.remoteAddress() + " validate token:"
							+ encode + "fail！", cause);
				}
			});
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 清理没通过验证的链接
	 */
	private void clearUnvalidatedConnection() {
		if (tempChannels.isEmpty()) {
			return;
		}
		long now = System.currentTimeMillis();
		Iterator<SessionValidateData> iterator = tempChannels.values().iterator();
		for (; iterator.hasNext();) {
			SessionValidateData data = iterator.next();
			long startValidateTime = data.startValidateTime;
			if (now - startValidateTime > this.config.getConnectionValidateTimeInSec() * 1000) {
				iterator.remove();
				data.channel.close();
				// 移除链接
				log.error(config.getServerName() + " connection " + data.channel.remoteAddress()
						+ " discarded，validate time out,wait validate size:" + tempChannels.size());
			}
		}
	}

	/**
	 * 客户端发送密码解析结果
	 * 
	 * @param context
	 * @param validateRandom
	 */
	private void onClientConnectionValidate(Channel channel, int validateRandom) {
		// 查找临时缓存
		String asLongText = channel.id().asLongText();
		SessionValidateData sessionValidateData = tempChannels.get(asLongText);
		if (sessionValidateData == null) {
			channel.close();
			// 移除链接
			log.info(config.getServerName() + " remote connection " + channel.remoteAddress()
					+ " discarded，validate time out！");
			return;
		}
		// 对比结果
		if (sessionValidateData.validateRandom != validateRandom) {
			// 移除链接
			log.info(config.getServerName() + " remote connection " + channel.remoteAddress()
					+ " discarded，validate wrong！");
			return;
		}
		tempChannels.remove(asLongText);
		log.info(config.getServerName() + " remote connection " + channel.remoteAddress() + " validate success!");
		// 通知客户端成功
		try {
			sendMessage(channel, new ConnectionValidateSuccessServerMessage(), null);
		} catch (Exception e) {
			log.error(e, e);
		}
		if(config.getOnConnectionEffective() != null){
			config.getOnConnectionEffective().run(channel);
		}
	}

	private void channelRead0(ChannelHandlerContext ctx, Object arg) {
		ByteBuf buffer = (ByteBuf) arg;
		try {
			// 消息id
			short messageId = this.config.getDecoder().readMessageId(ctx.channel(), buffer);
			Message msg = config.getFactory().getMessage(messageId);
			if (msg == null) {
				throw new Exception(config.getServerName() + " message empty,id:" + messageId);
			}
			msg.buffer(buffer);
			msg.decode();
			msg.buffer(null);
			@SuppressWarnings("unchecked")
			IHandler<Message> handler = (IHandler<Message>) config.getFactory().getHandler(messageId);
			if (handler == null) {
				throw new Exception(config.getServerName() + " can not find handler for message,id:" + messageId);
			}
			msg.setHandler(handler);
			msg.setChannel(ctx.channel());
			msg.setServer(this);
			// 如果是内部消息，则自己消化
			if (InnerMessageEnum.getTypeByValue(messageId) != null) {
				handler.handle(msg);
			} else {
				// 如果没通过验证，不接受消息
				if (tempChannels.containsKey(ctx.channel().id().asLongText())) {
					log.error("channel " + ctx.channel() + " has not validate yet!");
					return;
				}
				if(config.getDispatchMessage() != null){
					config.getDispatchMessage().run(msg);
				}
			}
		} catch (Exception e) {
			ctx.channel().close();
			log.error("close session:" + ctx.channel(), e);
		} finally {
			buffer.release();
		}
	}

	private void channelActive0(ChannelHandlerContext ctx) throws Exception {
		HashSet<String> whiteList = config.getWhiteList();
		if (whiteList != null && !config.getWhiteList().isEmpty()) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String remoteAddress = insocket.getAddress().getHostAddress();
			if (!whiteList.contains(remoteAddress)) {
				ctx.channel().close();
				log.info("ip: " + remoteAddress + " rejected link!");
				return;
			}
		}
		this.startConnectionValidate(ctx.channel());
		if(config.getOnChannelStateChanged() != null){
			config.getOnChannelStateChanged().run(ctx.channel(), true);
		}
	}

	private void channelInactive0(ChannelHandlerContext ctx) throws Exception {
		log.info(ctx.channel().remoteAddress() + " disconnected！");
		if(config.getOnChannelStateChanged() != null){
			config.getOnChannelStateChanged().run(ctx.channel(), false);
		}
	}

	private void exceptionCaught0(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(config.getOnExceptionCaught() != null){
			config.getOnExceptionCaught().run(ctx.channel(), cause);
		}
	}

	public BinaryServerConfig getConfig() {
		return this.config;
	}

	private class SessionValidateData {
		private Channel channel;
		private long startValidateTime;
		private int validateRandom;

		private SessionValidateData(Channel channel, long startValidateTime, int validateRandom) {
			this.channel = channel;
			this.startValidateTime = startValidateTime;
			this.validateRandom = validateRandom;
		}
	}

	private class ConnectionValidateClientHandler implements IHandler<ConnectionValidateClientMessage> {

		@Override
		public void handle(ConnectionValidateClientMessage msg) {
			int validateRandom = msg.getValidateRandom();
			msg.getServer().onClientConnectionValidate(msg.getChannel(), validateRandom);
		}
	}
}
