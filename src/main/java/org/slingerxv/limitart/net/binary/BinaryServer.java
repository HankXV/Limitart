package org.slingerxv.limitart.net.binary;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

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
import org.slingerxv.limitart.net.define.AbstractNettyServer;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.net.struct.AddressPair;
import org.slingerxv.limitart.util.RandomUtil;
import org.slingerxv.limitart.util.StringUtil;
import org.slingerxv.limitart.util.SymmetricEncryptionUtil;
import org.slingerxv.limitart.util.TimerUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
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
	private ConcurrentHashMap<String, SessionValidateData> tempChannels = new ConcurrentHashMap<>();
	private SymmetricEncryptionUtil encrypUtil;
	private TimerTask clearTask;

	// --config
	private String serverName;
	private AddressPair addressPair;
	private int connectionValidateTimeInSec;
	private AbstractBinaryDecoder decoder;
	private AbstractBinaryEncoder encoder;
	private HashSet<String> whiteList;
	private MessageFactory factory;

	// ---listener
	private Proc2<Channel, Boolean> onChannelStateChanged;
	private Proc2<Channel, Throwable> onExceptionCaught;
	private Proc1<Channel> onServerBind;
	private Proc1<Channel> onConnectionEffective;
	private Proc2<Message, IHandler<Message>> dispatchMessage;

	private BinaryServer(BinaryServerBuilder builder) throws Exception {
		this.serverName = builder.serverName;
		this.addressPair = Objects.requireNonNull(builder.addressPair, "addressPair");
		this.connectionValidateTimeInSec = builder.connectionValidateTimeInSec;
		this.decoder = Objects.requireNonNull(builder.decoder, "decoder");
		this.encoder = Objects.requireNonNull(builder.encoder, "encoder");
		this.whiteList = Objects.requireNonNull(builder.whiteList, "whiteList");
		this.factory = Objects.requireNonNull(builder.factory, "factory");
		this.onChannelStateChanged = builder.onChannelStateChanged;
		this.onExceptionCaught = builder.onExceptionCaught;
		this.onServerBind = builder.onServerBind;
		this.onConnectionEffective = builder.onConnectionEffective;
		this.dispatchMessage = builder.dispatchMessage;
		// 初始化内部消息
		this.factory.registerMsg(new ConnectionValidateClientHandler());
		boot = createSocketServerBoot(serverName, new ChannelInitializerImpl());
		if (needPass()) {
			// 初始化加密工具
			encrypUtil = SymmetricEncryptionUtil.getEncodeInstance(addressPair.getPass(), "20170106");
			clearTask = new TimerTask() {

				@Override
				public void run() {
					clearUnvalidatedConnection();
				}
			};
			TimerUtil.scheduleGlobal(1000, 1000, clearTask);
		}
	}

	@Override
	public void startServer() {
		new Thread(() -> {
			try {
				boot.bind(addressPair.getPort()).addListener((ChannelFuture arg0) -> {
					if (arg0.isSuccess()) {
						channel = arg0.channel();
						log.info(serverName + " bind at port:" + addressPair.getPort());
						Procs.invoke(onServerBind, arg0.channel());
					}
				}).sync().channel().closeFuture().sync();
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}, serverName + "-Binder").start();
	}

	@Override
	public void stopServer() {
		if (channel != null) {
			channel.close();
		}
		TimerUtil.unScheduleGlobal(clearTask);
	}

	public void sendMessage(Channel channel, Message msg) throws Exception {
		sendMessage(channel, msg, null);
	}

	public void sendMessage(Channel channel, Message msg, Proc3<Boolean, Throwable, Channel> listener)
			throws Exception {
		SendMessageUtil.sendMessage(encoder, channel, msg, listener);
	}

	public void sendMessage(List<Channel> channels, Message msg) throws Exception {
		sendMessage(channels, msg, null);
	}

	public void sendMessage(List<Channel> channels, Message msg, Proc3<Boolean, Throwable, Channel> listener)
			throws Exception {
		SendMessageUtil.sendMessage(encoder, channels, msg, listener);
	}

	private class ChannelInitializerImpl extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) {
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
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
							log.error(ctx.channel() + " cause:", cause);
							Procs.invoke(onExceptionCaught, ctx.channel(), cause);
						}

						@Override
						public void channelActive(ChannelHandlerContext ctx) {
							log.info(ctx.channel().remoteAddress() + " connected！");
							if (whiteList != null && !whiteList.isEmpty()) {
								InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
								String remoteAddress = insocket.getAddress().getHostAddress();
								if (!whiteList.contains(remoteAddress)) {
									ctx.channel().close();
									log.info("ip: " + remoteAddress + " rejected link!");
									return;
								}
							}
							Procs.invoke(onChannelStateChanged, ctx.channel(), true);
							if (needPass()) {
								startConnectionValidate(ctx.channel());
							} else {
								// 通知客户端成功
								try {
									sendMessage(channel, new ConnectionValidateSuccessServerMessage(), null);
								} catch (Exception e) {
									log.error(e, e);
								}
								Procs.invoke(onConnectionEffective, channel);
							}
						}

						@Override
						public void channelInactive(ChannelHandlerContext ctx) {
							log.info(ctx.channel().remoteAddress() + " disconnected！");
							Procs.invoke(onChannelStateChanged, ctx.channel(), false);
						}

						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) {
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
			log.info(serverName + " remote connection " + data.channel.remoteAddress()
					+ " discarded，server encryp util error！");
			return;
		}
		msg.validateStr = encode;
		try {
			sendMessage(channel, msg, (isSuccess, cause, channel1) -> {
				if (isSuccess) {
					log.info(serverName + " send client " + channel1.remoteAddress() + " validate token:" + encode
							+ "success！");
				} else {
					log.error(serverName + " send client " + channel1.remoteAddress() + " validate token:" + encode
							+ "fail！", cause);
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
			if (now - startValidateTime > connectionValidateTimeInSec * 1000) {
				iterator.remove();
				data.channel.close();
				// 移除链接
				log.error(serverName + " connection " + data.channel.remoteAddress()
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
			log.info(serverName + " remote connection " + channel.remoteAddress() + " discarded，validate time out！");
			return;
		}
		// 对比结果
		if (sessionValidateData.validateRandom != validateRandom) {
			// 移除链接
			log.info(serverName + " remote connection " + channel.remoteAddress() + " discarded，validate wrong！");
			return;
		}
		tempChannels.remove(asLongText);
		log.info(serverName + " remote connection " + channel.remoteAddress() + " validate success!");
		// 通知客户端成功
		try {
			sendMessage(channel, new ConnectionValidateSuccessServerMessage(), null);
		} catch (Exception e) {
			log.error(e, e);
		}
		Procs.invoke(onConnectionEffective, channel);
	}

	private void channelRead0(ChannelHandlerContext ctx, Object arg) {
		ByteBuf buffer = (ByteBuf) arg;
		try {
			// 消息id
			short messageId = decoder.readMessageId(ctx.channel(), buffer);
			Message msg = factory.getMessage(messageId);
			if (msg == null) {
				throw new MessageCodecException(serverName + " message empty,id:" + Integer.toHexString(messageId));
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
						serverName + " can not find handler for message,id:" + Integer.toHexString(messageId));
			}
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
				if (dispatchMessage != null) {
					try {
						dispatchMessage.run(msg, handler);
					} catch (Exception e) {
						log.error(ctx.channel() + " cause:", e);
						Procs.invoke(onExceptionCaught, channel, e);
					}
				} else {
					log.warn(serverName + " no dispatch message listener!");
				}
			}
		} catch (Exception e) {
			ctx.channel().close();
			log.error("close session:" + ctx.channel(), e);
		} finally {
			buffer.release();
		}
	}

	public String getServerName() {
		return serverName;
	}

	public AddressPair getAddressPair() {
		return addressPair;
	}

	public int getConnectionValidateTimeInSec() {
		return connectionValidateTimeInSec;
	}

	public AbstractBinaryDecoder getDecoder() {
		return decoder;
	}

	public AbstractBinaryEncoder getEncoder() {
		return encoder;
	}

	public HashSet<String> getWhiteList() {
		return whiteList;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	private boolean needPass() {
		return addressPair.getPass() != null;
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
			msg.getServer().onClientConnectionValidate(msg.getChannel(), msg.validateRandom);
		}
	}

	public static class BinaryServerBuilder {
		private String serverName;
		private AddressPair addressPair;
		private int connectionValidateTimeInSec;
		private AbstractBinaryDecoder decoder;
		private AbstractBinaryEncoder encoder;
		private HashSet<String> whiteList = new HashSet<>();
		private MessageFactory factory;
		// ---listener
		private Proc2<Channel, Boolean> onChannelStateChanged;
		private Proc2<Channel, Throwable> onExceptionCaught;
		private Proc1<Channel> onServerBind;
		private Proc1<Channel> onConnectionEffective;
		private Proc2<Message, IHandler<Message>> dispatchMessage;

		public BinaryServerBuilder() {
			this.serverName = "Binary-Server";
			this.addressPair = new AddressPair(8888, "limitart-core");
			this.connectionValidateTimeInSec = 20;
			this.decoder = AbstractBinaryDecoder.DEFAULT_DECODER;
			this.encoder = AbstractBinaryEncoder.DEFAULT_ENCODER;
			this.dispatchMessage = (t1, t2) -> {
				t2.handle(t1);
			};
		}

		/**
		 * 构建服务器
		 * 
		 * @return
		 * @throws Exception
		 */
		public BinaryServer build() throws Exception {
			return new BinaryServer(this);
		}

		/**
		 * 自定义解码器
		 * 
		 * @param decoder
		 * @return
		 */
		public BinaryServerBuilder decoder(AbstractBinaryDecoder decoder) {
			this.decoder = decoder;
			return this;
		}

		public BinaryServerBuilder encoder(AbstractBinaryEncoder encoder) {
			this.encoder = encoder;
			return this;
		}

		public BinaryServerBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		/**
		 * 绑定端口
		 * 
		 * @param port
		 * @return
		 */
		public BinaryServerBuilder addressPair(AddressPair addressPair) {
			this.addressPair = addressPair;
			return this;
		}

		public BinaryServerBuilder factory(MessageFactory factory) {
			this.factory = factory;
			return this;
		}

		/**
		 * 链接验证超时(秒)
		 * 
		 * @param connectionValidateTimeInSec
		 * @return
		 */
		public BinaryServerBuilder connectionValidateTimeInSec(int connectionValidateTimeInSec) {
			this.connectionValidateTimeInSec = connectionValidateTimeInSec;
			return this;
		}

		public BinaryServerBuilder whiteList(String... remoteAddress) {
			for (String ip : remoteAddress) {
				if (StringUtil.isIp4(ip)) {
					this.whiteList.add(ip);
				}
			}
			return this;
		}

		public BinaryServerBuilder onChannelStateChanged(Proc2<Channel, Boolean> onChannelStateChanged) {
			this.onChannelStateChanged = onChannelStateChanged;
			return this;
		}

		public BinaryServerBuilder onExceptionCaught(Proc2<Channel, Throwable> onExceptionCaught) {
			this.onExceptionCaught = onExceptionCaught;
			return this;
		}

		public BinaryServerBuilder onServerBind(Proc1<Channel> onServerBind) {
			this.onServerBind = onServerBind;
			return this;
		}

		public BinaryServerBuilder onConnectionEffective(Proc1<Channel> onConnectionEffective) {
			this.onConnectionEffective = onConnectionEffective;
			return this;
		}

		public BinaryServerBuilder dispatchMessage(Proc2<Message, IHandler<Message>> dispatchMessage) {
			this.dispatchMessage = dispatchMessage;
			return this;
		}
	}
}
