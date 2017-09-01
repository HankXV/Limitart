/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.net.binary;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.collections.ConcurrentHashSet;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryDecoder;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryEncoder;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.handler.annotation.Controller;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.binary.message.constant.InnerMessageEnum;
import org.slingerxv.limitart.net.binary.message.exception.HeartNotAnswerException;
import org.slingerxv.limitart.net.binary.message.exception.HeartTooQuickException;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;
import org.slingerxv.limitart.net.binary.message.exception.SendMessageTooFastException;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateClientMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateServerMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.ConnectionValidateSuccessServerMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.HeartClientMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.HeartServerMessage;
import org.slingerxv.limitart.net.binary.util.SendMessageUtil;
import org.slingerxv.limitart.net.define.AbstractNettyServer;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.net.struct.AddressPair;
import org.slingerxv.limitart.util.RandomUtil;
import org.slingerxv.limitart.util.StringUtil;
import org.slingerxv.limitart.util.SymmetricEncryptionUtil;
import org.slingerxv.limitart.util.TimerUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.AttributeKey;

/**
 * 二进制通信服务器
 * 
 * @author Hank
 *
 */
@Controller
public class BinaryServer extends AbstractNettyServer implements IServer {
	private static Logger log = LoggerFactory.getLogger(BinaryServer.class);
	private static AttributeKey<Long> LAST_HEART_TIME = AttributeKey.newInstance("LAST_HEART_TIME");
	private static AttributeKey<Long> FIRST_HEART_TIME = AttributeKey.newInstance("FIRST_HEART_TIME");
	private static AttributeKey<Long> LAST_RECEIVE_MSG_TIME = AttributeKey.newInstance("LAST_RECEIVE_MSG_TIME");
	private static AttributeKey<Integer> HEART_COUNT = AttributeKey.newInstance("HEART_COUNT");
	private Map<String, SessionValidateData> unvalidatedChannels = new ConcurrentHashMap<>();
	private Set<Channel> validatedChannels = new ConcurrentHashSet<>();
	private SymmetricEncryptionUtil encrypUtil;
	private TimerTask clearTask;
	private TimerTask heartTask;
	private AtomicInteger connectionCount = new AtomicInteger(0);
	private long startTime;

	// --config
	private String serverName;
	private AddressPair addressPair;
	private int connectionValidateTimeInSec;
	private AbstractBinaryDecoder decoder;
	private AbstractBinaryEncoder encoder;
	private Set<String> whiteList;
	private MessageFactory factory;
	private int maxConnection;
	private int heartIntervalSec;
	private int checkHeartWhenConnectionCount;
	private int receiveIntervalMills;

	// ---listener
	private Proc2<Channel, Boolean> onChannelStateChanged;
	private Proc2<Channel, Throwable> onExceptionCaught;
	private Proc1<Channel> onServerBind;
	private Proc1<Channel> onConnectionEffective;
	private Proc2<Message, IHandler<Message>> dispatchMessage;

	private BinaryServer(BinaryServerBuilder builder) throws Exception {
		super(builder.serverName);
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
		this.maxConnection = builder.maxConnection;
		this.heartIntervalSec = builder.heartIntervalSec;
		this.checkHeartWhenConnectionCount = builder.checkHeartWhenConnectionCount;
		this.receiveIntervalMills = builder.receiveIntervalMills;
		// 初始化内部消息
		this.factory.registerMsg(new ConnectionValidateClientHandler()).registerMsg(new HeartClientHandler());
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
		if (heartIntervalSec > 0) {
			heartTask = new TimerTask() {

				@Override
				public void run() {
					clearUnheart();
				}
			};
			TimerUtil.scheduleGlobal(5000, 5000, heartTask);
		}
	}

	@Override
	protected void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(), decoder.getLengthFieldOffset(),
				decoder.getLengthFieldLength(), decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()))
				.addLast(new ChannelInboundHandlerAdapter() {
					@Override
					public boolean isSharable() {
						return true;
					}

					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						log.error(ctx.channel() + " cause:", cause);
						Procs.invoke(onExceptionCaught, ctx.channel(), cause);
					}

					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						if (maxConnection > 0 && connectionCount.get() >= maxConnection) {
							log.error("connection count is greater than " + maxConnection + " close channel:"
									+ ctx.channel());
							ctx.channel().close();
							return;
						}
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
						connectionCount.incrementAndGet();
						Procs.invoke(onChannelStateChanged, ctx.channel(), true);
						if (needPass()) {
							startConnectionValidate(ctx.channel());
						} else {
							// 通知客户端成功
							try {
								sendMessage(channel(), new ConnectionValidateSuccessServerMessage(), null);
							} catch (Exception e) {
								log.error("error", e);
							}
							validatedChannels.add(ctx.channel());
							Procs.invoke(onConnectionEffective, channel());
						}
					}

					@Override
					public void channelInactive(ChannelHandlerContext ctx) throws Exception {
						log.info(ctx.channel().remoteAddress() + " disconnected！");
						connectionCount.decrementAndGet();
						validatedChannels.remove(ctx.channel());
						Procs.invoke(onChannelStateChanged, ctx.channel(), false);
					}

					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						channelRead0(ctx, msg);
					}
				});
	}

	@Override
	public void startServer() {
		startTime = System.currentTimeMillis();
		bind(addressPair.getPort(), onServerBind);
	}

	@Override
	public void stopServer() {
		unbind();
		TimerUtil.unScheduleGlobal(clearTask);
		TimerUtil.unScheduleGlobal(heartTask);
	}

	public void sendMessage(Channel channel, Message msg) throws MessageCodecException {
		sendMessage(channel, msg, null);
	}

	public void sendMessage(Channel channel, Message msg, Proc3<Boolean, Throwable, Channel> listener)
			throws MessageCodecException {
		SendMessageUtil.sendMessage(encoder, channel, msg, listener);
	}

	public void sendMessage(List<Channel> channels, Message msg) throws MessageCodecException {
		sendMessage(channels, msg, null);
	}

	public void sendMessage(List<Channel> channels, Message msg, Proc3<Boolean, Throwable, Channel> listener)
			throws MessageCodecException {
		SendMessageUtil.sendMessage(encoder, channels, msg, listener);
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
		unvalidatedChannels.put(data.channel.id().asLongText(), data);
		// 通知客户端
		ConnectionValidateServerMessage msg = new ConnectionValidateServerMessage();
		String encode;
		try {
			encode = encrypUtil.encode(data.validateRandom + "");
		} catch (Exception e) {
			log.error("encode link validate code error", e);
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
			log.error("send vlidate error", e);
		}
	}

	/**
	 * 清理没通过验证的链接
	 */
	private void clearUnvalidatedConnection() {
		if (unvalidatedChannels.isEmpty()) {
			return;
		}
		long now = System.currentTimeMillis();
		Iterator<SessionValidateData> iterator = unvalidatedChannels.values().iterator();
		for (; iterator.hasNext();) {
			SessionValidateData data = iterator.next();
			long startValidateTime = data.startValidateTime;
			if (now - startValidateTime > connectionValidateTimeInSec * 1000) {
				iterator.remove();
				data.channel.close();
				// 移除链接
				log.error(serverName + " connection " + data.channel.remoteAddress()
						+ " discarded，validate time out,wait validate size:" + unvalidatedChannels.size());
			}
		}
	}

	private void clearUnheart() {
		if (checkHeartWhenConnectionCount > connectionCount.get()) {
			return;
		}
		long now = System.currentTimeMillis();
		for (Channel channel : validatedChannels) {
			long last = 0;
			long first = 0;
			int count = 0;
			if (channel.hasAttr(LAST_HEART_TIME)) {
				last = channel.attr(LAST_HEART_TIME).get();
			}
			if (channel.hasAttr(FIRST_HEART_TIME)) {
				first = channel.attr(FIRST_HEART_TIME).get();
			}
			if (channel.hasAttr(HEART_COUNT)) {
				count = channel.attr(HEART_COUNT).get();
			}
			int allow = (int) ((now - first) / (heartIntervalSec * 1000));
			if (count - 2 > allow) {
				log.error(channel + " heart too quick,might be Game Accelerator,please check!");
				channel.pipeline().fireExceptionCaught(new HeartTooQuickException(channel, first, now, count, allow));
				channel.attr(FIRST_HEART_TIME).set(now);
				channel.attr(HEART_COUNT).set(0);
			}
			if (count < allow - 2) {
				channel.pipeline().fireExceptionCaught(new HeartNotAnswerException(channel, first, last, count));
				channel.close();
			}
		}
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
				log.error("message id:" + Integer.toHexString(messageId) + " decode error!");
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
				if (unvalidatedChannels.containsKey(ctx.channel().id().asLongText())) {
					log.error("channel " + ctx.channel() + " has not validate yet!");
					return;
				}
				long now = System.currentTimeMillis();
				// 记录消息接收时间
				if (receiveIntervalMills > 0) {
					if (ctx.channel().hasAttr(LAST_RECEIVE_MSG_TIME)) {
						Long lastReceiveTime = ctx.channel().attr(LAST_RECEIVE_MSG_TIME).get();
						if (lastReceiveTime != null && (now - lastReceiveTime) < receiveIntervalMills) {
							ctx.channel().pipeline().fireExceptionCaught(new SendMessageTooFastException(ctx.channel(),
									receiveIntervalMills, (int) (now - lastReceiveTime)));
							ctx.channel().close();
						}
					}
					ctx.channel().attr(LAST_RECEIVE_MSG_TIME).set(now);
				}
				if (dispatchMessage != null) {
					try {
						dispatchMessage.run(msg, handler);
					} catch (Exception e) {
						log.error(ctx.channel() + " cause:", e);
						Procs.invoke(onExceptionCaught, ctx.channel(), e);
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

	private void connectionValidateClient(ConnectionValidateClientMessage msg) {
		// 查找临时缓存
		String asLongText = msg.getChannel().id().asLongText();
		SessionValidateData sessionValidateData = unvalidatedChannels.get(asLongText);
		if (sessionValidateData == null) {
			msg.getChannel().close();
			// 移除链接
			log.info(serverName + " remote connection " + msg.getChannel().remoteAddress()
					+ " discarded，validate time out！");
			return;
		}
		// 对比结果
		if (sessionValidateData.validateRandom != msg.validateRandom) {
			// 移除链接
			log.info(serverName + " remote connection " + msg.getChannel().remoteAddress()
					+ " discarded，validate wrong！");
			return;
		}
		unvalidatedChannels.remove(asLongText);
		log.info(serverName + " remote connection " + msg.getChannel().remoteAddress() + " validate success!");
		// 通知客户端成功
		try {
			sendMessage(msg.getChannel(), new ConnectionValidateSuccessServerMessage(), null);
		} catch (Exception e) {
			msg.getChannel().pipeline().fireExceptionCaught(e);
		}
		validatedChannels.add(msg.getChannel());
		Procs.invoke(onConnectionEffective, msg.getChannel());
	}

	private void heartClient(HeartClientMessage msg) {
		long now = System.currentTimeMillis();
		// 设置上次心跳时间
		msg.getChannel().attr(LAST_HEART_TIME).set(now);

		// 是否包含首次心跳
		if (!msg.getChannel().hasAttr(FIRST_HEART_TIME)) {
			msg.getChannel().attr(FIRST_HEART_TIME).set(now);
			msg.getChannel().attr(HEART_COUNT).set(0);
			return;
		}
		int times = msg.getChannel().attr(HEART_COUNT).get();
		msg.getChannel().attr(HEART_COUNT).set(++times);
		HeartServerMessage message = new HeartServerMessage();
		// message.serverStartTime = startTime;
		message.serverTime = now;
		message.timeLocale = TimeZone.getDefault().getOffset(now);
		try {
			sendMessage(msg.getChannel(), message);
		} catch (Exception e) {
			log.error("send heart error", e);
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

	public Set<String> getWhiteList() {
		return whiteList;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public int getConnectionCount() {
		return connectionCount.get();
	}

	public long getStartTime() {
		return startTime;
	}

	public int getHeartIntervalSec() {
		return heartIntervalSec;
	}

	public int getCheckHeartWhenConnectionCount() {
		return checkHeartWhenConnectionCount;
	}

	public int getReceiveIntervalMills() {
		return receiveIntervalMills;
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
			msg.getServer().connectionValidateClient(msg);
		}
	}

	private class HeartClientHandler implements IHandler<HeartClientMessage> {

		@Override
		public void handle(HeartClientMessage msg) {
			msg.getServer().heartClient(msg);
		}
	}

	public static class BinaryServerBuilder {
		private String serverName;
		private AddressPair addressPair;
		private int connectionValidateTimeInSec;
		private AbstractBinaryDecoder decoder;
		private AbstractBinaryEncoder encoder;
		private Set<String> whiteList = new HashSet<>();
		private MessageFactory factory;
		private int maxConnection;
		private int heartIntervalSec;
		private int checkHeartWhenConnectionCount;
		private int receiveIntervalMills;
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
			this.maxConnection = 20000;
			this.heartIntervalSec = 0;
			this.checkHeartWhenConnectionCount = 0;
			this.receiveIntervalMills = 0;
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

		/**
		 * 自定义编码器
		 * 
		 * @param encoder
		 * @return
		 */
		public BinaryServerBuilder encoder(AbstractBinaryEncoder encoder) {
			this.encoder = encoder;
			return this;
		}

		/**
		 * 服务器名称
		 * 
		 * @param serverName
		 * @return
		 */
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

		/**
		 * 消息工厂
		 * 
		 * @param factory
		 * @return
		 */
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

		/**
		 * 访问白名单
		 * 
		 * @param remoteAddress
		 * @return
		 */
		public BinaryServerBuilder whiteList(String... remoteAddress) {
			for (String ip : remoteAddress) {
				if (StringUtil.isIp4(ip)) {
					this.whiteList.add(ip);
				}
			}
			return this;
		}

		/**
		 * 链接断开或连接监听
		 * 
		 * @param onChannelStateChanged
		 * @return
		 */
		public BinaryServerBuilder onChannelStateChanged(Proc2<Channel, Boolean> onChannelStateChanged) {
			this.onChannelStateChanged = onChannelStateChanged;
			return this;
		}

		/**
		 * 异常监听
		 * 
		 * @param onExceptionCaught
		 * @return
		 */
		public BinaryServerBuilder onExceptionCaught(Proc2<Channel, Throwable> onExceptionCaught) {
			this.onExceptionCaught = onExceptionCaught;
			return this;
		}

		/**
		 * 服务器绑定成功监听
		 * 
		 * @param onServerBind
		 * @return
		 */
		public BinaryServerBuilder onServerBind(Proc1<Channel> onServerBind) {
			this.onServerBind = onServerBind;
			return this;
		}

		/**
		 * 当链接有效时监听
		 * 
		 * @param onConnectionEffective
		 * @return
		 */
		public BinaryServerBuilder onConnectionEffective(Proc1<Channel> onConnectionEffective) {
			this.onConnectionEffective = onConnectionEffective;
			return this;
		}

		/**
		 * 分发消息监听
		 * 
		 * @param dispatchMessage
		 * @return
		 */
		public BinaryServerBuilder dispatchMessage(Proc2<Message, IHandler<Message>> dispatchMessage) {
			this.dispatchMessage = dispatchMessage;
			return this;
		}

		/**
		 * 最大链接数限制
		 * 
		 * @param maxConnection
		 * @return
		 */
		public BinaryServerBuilder maxConnection(int maxConnection) {
			this.maxConnection = maxConnection;
			return this;
		}

		/**
		 * 心跳检测间隔
		 * 
		 * @param heartIntervalSec
		 * @return
		 */
		public BinaryServerBuilder heartIntervalSec(int heartIntervalSec) {
			this.heartIntervalSec = heartIntervalSec;
			return this;
		}

		/**
		 * 当链接达到多少时检测心跳
		 * 
		 * @param checkHeartWhenConnectionCount
		 * @return
		 */
		public BinaryServerBuilder checkHeartWhenConnectionCount(int checkHeartWhenConnectionCount) {
			this.checkHeartWhenConnectionCount = checkHeartWhenConnectionCount;
			return this;
		}

		/**
		 * 消息接收间隔不能大于的毫秒数
		 * 
		 * @param receiveIntervalMills
		 * @return
		 */
		public BinaryServerBuilder receiveIntervalMills(int receiveIntervalMills) {
			this.receiveIntervalMills = receiveIntervalMills;
			return this;
		}
	}
}
