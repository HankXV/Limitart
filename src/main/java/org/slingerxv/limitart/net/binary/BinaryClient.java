package org.slingerxv.limitart.net.binary;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryDecoder;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryEncoder;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.binary.message.constant.InnerMessageEnum;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateClientMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateServerMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateSuccessServerMessage;
import org.slingerxv.limitart.net.binary.util.SendMessageUtil;
import org.slingerxv.limitart.net.struct.AddressPair;
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
public class BinaryClient {
	private static Logger log = LogManager.getLogger();
	private static EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap;
	private Channel channel;
	private SymmetricEncryptionUtil decodeUtil;
	private TimerTask reconnectTask;
	// ----config
	private String clientName;
	private AddressPair remoteAddress;
	private int autoReconnect;
	private AbstractBinaryDecoder decoder;
	private AbstractBinaryEncoder encoder;
	private MessageFactory factory;
	// ----listener
	private Proc2<BinaryClient, Boolean> onChannelStateChanged;
	private Proc2<BinaryClient, Throwable> onExceptionCaught;
	private Proc1<BinaryClient> onConnectionEffective;
	private Proc2<Message, IHandler<Message>> dispatchMessage;

	private BinaryClient(BinaryClientBuilder builder) throws Exception {
		this.clientName = builder.clientName;
		this.remoteAddress = Objects.requireNonNull(builder.remoteAddress, "remoteAddress");
		this.autoReconnect = builder.autoReconnect;
		this.decoder = Objects.requireNonNull(builder.decoder, "decoder");
		this.encoder = Objects.requireNonNull(builder.encoder, "encoder");
		this.factory = Objects.requireNonNull(builder.factory, "factory");
		this.onChannelStateChanged = builder.onChannelStateChanged;
		this.onExceptionCaught = builder.onExceptionCaught;
		this.onConnectionEffective = builder.onConnectionEffective;
		this.dispatchMessage = builder.dispatchMessage;
		// 内部消息注册
		factory.registerMsg(new ConnectionValidateServerHandler())
				.registerMsg(new ConnectionValidateSuccessServerHandler());
		decodeUtil = SymmetricEncryptionUtil.getDecodeInstance(remoteAddress.getPass());
		bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		log.info(clientName + " nio init");
		bootstrap.group(group).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.handler(new ChannelInitializerImpl());
		reconnectTask = new TimerTask() {

			@Override
			public void run() {
				connect0();
			}
		};
	}

	private class ChannelInitializerImpl extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline()
					.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(),
							decoder.getLengthFieldOffset(), decoder.getLengthFieldLength(),
							decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()));
			ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
				@Override
				public boolean isSharable() {
					return true;
				}

				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
					channelRead0(ctx, msg);
				}

				@Override
				public void channelInactive(ChannelHandlerContext ctx) throws Exception {
					Procs.invoke(onChannelStateChanged, BinaryClient.this, false);
				}

				@Override
				public void channelActive(ChannelHandlerContext ctx) throws Exception {
					Procs.invoke(onChannelStateChanged, BinaryClient.this, true);
				}

				@Override
				public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
					log.error(ctx.channel() + " cause:", cause);
					Procs.invoke(onExceptionCaught, BinaryClient.this, cause);
				}
			});
		}

	}

	public void sendMessage(Message msg) {
		sendMessage(msg, null);
	}

	public void sendMessage(Message msg, Proc3<Boolean, Throwable, Channel> listener) {
		channel.eventLoop().execute(() -> {
			try {
				SendMessageUtil.sendMessage(encoder, channel, msg, listener);
			} catch (MessageCodecException e) {
				Procs.invoke(onExceptionCaught, BinaryClient.this, e);
			}
		});
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
		log.info(clientName + " start connect server：" + remoteAddress.getIp() + ":" + remoteAddress.getPort() + "...");
		try {
			bootstrap.connect(remoteAddress.getIp(), remoteAddress.getPort())
					.addListener((ChannelFutureListener) channelFuture -> {
						channel = channelFuture.channel();
						if (channelFuture.isSuccess()) {
							log.info(clientName + " connect server：" + remoteAddress.getIp() + ":"
									+ remoteAddress.getPort() + " success！");
						} else {
							log.error(clientName + " try connect server：" + remoteAddress.getIp() + ":"
									+ remoteAddress.getPort() + " fail", channelFuture.cause().getMessage());
							if (autoReconnect > 0) {
								tryReconnect(autoReconnect);
							}
						}
					}).sync();
		} catch (Exception e) {
			log.error(e, e);
		}
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
			msg.validateRandom = validateRandom;
			sendMessage(msg, null);
			log.info(clientName + " parse validate code success，return result：" + validateRandom);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private void onConnectionValidateSeccuss(String remote) {
		log.info("server validate success,remote:" + remote);
		Procs.invoke(onConnectionEffective, this);
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

	public String getClientName() {
		return this.clientName;
	}

	public AddressPair getRemoteAddress() {
		return remoteAddress;
	}

	public int getAutoReconnect() {
		return autoReconnect;
	}

	public AbstractBinaryDecoder getDecoder() {
		return decoder;
	}

	public AbstractBinaryEncoder getEncoder() {
		return encoder;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	private void channelRead0(ChannelHandlerContext ctx, Object arg)
			throws MessageCodecException, ReflectiveOperationException {
		ByteBuf buffer = (ByteBuf) arg;
		try {
			// 消息id
			short messageId = decoder.readMessageId(ctx.channel(), buffer);
			Message msg = factory.getMessage(messageId);
			if (msg == null) {
				throw new MessageCodecException(clientName + " message empty,id:" + Integer.toHexString(messageId));
			}
			msg.buffer(buffer);
			try {
				msg.decode();
			} catch (Exception e) {
				throw new MessageCodecException(e);
			}
			msg.buffer(null);
			@SuppressWarnings("unchecked")
			IHandler<Message> handler = (IHandler<Message>) factory.getHandler(messageId);
			if (handler == null) {
				throw new MessageCodecException(
						clientName + " can not find handler for message,id:" + Integer.toHexString(messageId));
			}
			msg.setChannel(ctx.channel());
			msg.setClient(this);
			// 如果是内部消息，则自己消化
			if (InnerMessageEnum.getTypeByValue(messageId) != null) {
				handler.handle(msg);
			} else {
				if (dispatchMessage != null) {
					try {
						dispatchMessage.run(msg, handler);
					} catch (Exception e) {
						log.error(ctx.channel() + " cause:", e);
						Procs.invoke(onExceptionCaught, this, e);
					}
				} else {
					log.warn(clientName + " no dispatch message listener!");
				}
			}
		} finally {
			buffer.release();
		}
	}

	private class ConnectionValidateServerHandler implements IHandler<ConnectionValidateServerMessage> {

		@Override
		public void handle(ConnectionValidateServerMessage msg) {
			msg.getClient().decodeConnectionValidateData(msg.validateStr);
		}

	}

	private class ConnectionValidateSuccessServerHandler implements IHandler<ConnectionValidateSuccessServerMessage> {

		@Override
		public void handle(ConnectionValidateSuccessServerMessage msg) {
			msg.getClient().onConnectionValidateSeccuss(msg.getChannel().remoteAddress().toString());
		}
	}

	public static class BinaryClientBuilder {
		private String clientName;
		private AddressPair remoteAddress;
		private int autoReconnect;
		private AbstractBinaryDecoder decoder;
		private AbstractBinaryEncoder encoder;
		private MessageFactory factory;
		// ----listener
		private Proc2<BinaryClient, Boolean> onChannelStateChanged;
		private Proc2<BinaryClient, Throwable> onExceptionCaught;
		private Proc1<BinaryClient> onConnectionEffective;
		private Proc2<Message, IHandler<Message>> dispatchMessage;

		public BinaryClientBuilder() {
			this.clientName = "Binary-Client";
			this.remoteAddress = new AddressPair("127.0.0.1", 8888);
			this.autoReconnect = 0;
			this.decoder = AbstractBinaryDecoder.DEFAULT_DECODER;
			this.encoder = AbstractBinaryEncoder.DEFAULT_ENCODER;
			this.factory = new MessageFactory();
			this.dispatchMessage = (t1, t2) -> {
				t2.handle(t1);
			};
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 * @throws Exception
		 */
		public BinaryClient build() throws Exception {
			return new BinaryClient(this);
		}

		public BinaryClientBuilder decoder(AbstractBinaryDecoder decoder) {
			this.decoder = decoder;
			return this;
		}

		public BinaryClientBuilder encoder(AbstractBinaryEncoder encoder) {
			this.encoder = encoder;
			return this;
		}

		public BinaryClientBuilder clientName(String clientName) {
			this.clientName = clientName;
			return this;
		}

		/**
		 * 服务器IP
		 * 
		 * @param remoteIp
		 * @return
		 */
		public BinaryClientBuilder remoteAddress(AddressPair remoteAddress) {
			this.remoteAddress = remoteAddress;
			return this;
		}

		/**
		 * 自动重连尝试间隔(秒)
		 * 
		 * @param autoReconnect
		 * @return
		 */
		public BinaryClientBuilder autoReconnect(int autoReconnect) {
			this.autoReconnect = autoReconnect;
			return this;
		}

		public BinaryClientBuilder factory(MessageFactory factory) {
			this.factory = factory;
			return this;
		}

		public BinaryClientBuilder onChannelStateChanged(Proc2<BinaryClient, Boolean> onChannelStateChanged) {
			this.onChannelStateChanged = onChannelStateChanged;
			return this;
		}

		public BinaryClientBuilder onExceptionCaught(Proc2<BinaryClient, Throwable> onExceptionCaught) {
			this.onExceptionCaught = onExceptionCaught;
			return this;
		}

		public BinaryClientBuilder onConnectionEffective(Proc1<BinaryClient> onConnectionEffective) {
			this.onConnectionEffective = onConnectionEffective;
			return this;
		}

		public BinaryClientBuilder dispatchMessage(Proc2<Message, IHandler<Message>> dispatchMessage) {
			this.dispatchMessage = dispatchMessage;
			return this;
		}
	}
}
