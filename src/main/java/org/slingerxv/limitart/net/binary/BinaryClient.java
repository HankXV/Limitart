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

import java.net.SocketAddress;
import java.util.Objects;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.slingerxv.limitart.net.binary.message.impl.validate.HeartClientMessage;
import org.slingerxv.limitart.net.binary.message.impl.validate.HeartServerMessage;
import org.slingerxv.limitart.net.binary.util.SendMessageUtil;
import org.slingerxv.limitart.net.define.AbstractNettyClient;
import org.slingerxv.limitart.net.struct.AddressPair;
import org.slingerxv.limitart.util.SymmetricEncryptionUtil;
import org.slingerxv.limitart.util.TimerUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 二进制通信客户端
 * 
 * @author hank
 *
 */
public class BinaryClient extends AbstractNettyClient {
	private static Logger log = LoggerFactory.getLogger(BinaryClient.class);
	private SymmetricEncryptionUtil decodeUtil;
	// private long serverStartTime;
	private long serverUTCTime;
	private TimerTask hearTask;
	// ----config
	private AddressPair remoteAddress;
	private AbstractBinaryDecoder decoder;
	private AbstractBinaryEncoder encoder;
	private MessageFactory factory;
	private int heartIntervalSec;
	// ----listener
	private Proc2<BinaryClient, Boolean> onChannelStateChanged;
	private Proc2<BinaryClient, Throwable> onExceptionCaught;
	private Proc1<BinaryClient> onConnectionEffective;
	private Proc2<Message, IHandler<Message>> dispatchMessage;

	private BinaryClient(BinaryClientBuilder builder) throws Exception {
		super(builder.clientName, builder.autoReconnect);
		this.remoteAddress = Objects.requireNonNull(builder.remoteAddress, "remoteAddress");
		this.decoder = Objects.requireNonNull(builder.decoder, "decoder");
		this.encoder = Objects.requireNonNull(builder.encoder, "encoder");
		this.factory = Objects.requireNonNull(builder.factory, "factory");
		this.onChannelStateChanged = builder.onChannelStateChanged;
		this.onExceptionCaught = builder.onExceptionCaught;
		this.onConnectionEffective = builder.onConnectionEffective;
		this.dispatchMessage = builder.dispatchMessage;
		this.heartIntervalSec = builder.heartIntervalSec;
		// 内部消息注册
		factory.registerMsg(new ConnectionValidateServerHandler())
				.registerMsg(new ConnectionValidateSuccessServerHandler()).registerMsg(new HeartServerHandler());
		decodeUtil = SymmetricEncryptionUtil.getDecodeInstance(remoteAddress.getPass());
		log.info(getClientName() + " nio init");
	}

	public void sendMessage(Message msg) throws Exception {
		sendMessage(msg, null);
	}

	public void sendMessage(Message msg, Proc3<Boolean, Throwable, Channel> listener) throws Exception {
		try {
			SendMessageUtil.sendMessage(encoder, channel(), msg, listener);
		} catch (MessageCodecException e) {
			Procs.invoke(onExceptionCaught, BinaryClient.this, e);
		}
	}

	@Override
	public void connect() {
		tryReconnect(remoteAddress.getIp(), remoteAddress.getPort(), 0);
	}

	@Override
	public void disConnect() {
		tryDisConnect();
	}

	@Override
	protected void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(), decoder.getLengthFieldOffset(),
				decoder.getLengthFieldLength(), decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()));
		pipeline.addLast(new ChannelInboundHandlerAdapter() {
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
				log.info(getClientName() + " disconnected!");
				if (heartIntervalSec > 0 && hearTask != null) {
					TimerUtil.unScheduleGlobal(hearTask);
				}
				Procs.invoke(onChannelStateChanged, BinaryClient.this, false);
				if (getAutoReconnect() > 0) {
					tryReconnect(remoteAddress.getIp(), remoteAddress.getPort(), getAutoReconnect());
				}
			}

			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				log.info(getClientName() + " connected!");
				Procs.invoke(onChannelStateChanged, BinaryClient.this, true);
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				log.error(ctx.channel() + " cause:", cause);
				Procs.invoke(onExceptionCaught, BinaryClient.this, cause);
			}
		});
	}

	private void decodeConnectionValidateData(String validateStr) {
		try {
			String decode = decodeUtil.decode(validateStr);
			int validateRandom = Integer.parseInt(decode);
			ConnectionValidateClientMessage msg = new ConnectionValidateClientMessage();
			msg.validateRandom = validateRandom;
			sendMessage(msg, null);
			log.info(getClientName() + " parse validate code success，return result：" + validateRandom);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void onConnectionValidateSeccuss(String remote) {
		log.info("server validate success,remote:" + remote);
		if (heartIntervalSec > 0) {
			hearTask = new TimerTask() {

				@Override
				public void run() {
					try {
						sendMessage(new HeartClientMessage());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			};
			TimerUtil.scheduleGlobal(0, heartIntervalSec * 1000, hearTask);
		}
		Procs.invoke(onConnectionEffective, this);
	}

	private void onHeartServer(long serverUTCTime) {
		this.serverUTCTime = serverUTCTime;
	}

	public SocketAddress remoteAddress() {
		return channel().remoteAddress();
	}

	public AddressPair getRemoteAddress() {
		return remoteAddress;
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

	public long getServerUTCTime() {
		return serverUTCTime;
	}

	public int getHeartIntervalSec() {
		return heartIntervalSec;
	}

	private void channelRead0(ChannelHandlerContext ctx, Object arg)
			throws MessageCodecException, ReflectiveOperationException {
		ByteBuf buffer = (ByteBuf) arg;
		try {
			// 消息id
			short messageId = decoder.readMessageId(ctx.channel(), buffer);
			Message msg = factory.getMessage(messageId);
			if (msg == null) {
				throw new MessageCodecException(
						getClientName() + " message empty,id:" + Integer.toHexString(messageId));
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
						getClientName() + " can not find handler for message,id:" + Integer.toHexString(messageId));
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
					log.warn(getClientName() + " no dispatch message listener!");
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

	private class HeartServerHandler implements IHandler<HeartServerMessage> {

		@Override
		public void handle(HeartServerMessage msg) {
			msg.getClient().onHeartServer(msg.serverUtcTime);
		}
	}

	public static class BinaryClientBuilder {
		private String clientName;
		private AddressPair remoteAddress;
		private int autoReconnect;
		private AbstractBinaryDecoder decoder;
		private AbstractBinaryEncoder encoder;
		private MessageFactory factory;
		private int heartIntervalSec;
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
			this.heartIntervalSec = 0;
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

		public BinaryClientBuilder heartIntervalSec(int heartIntervalSec) {
			this.heartIntervalSec = heartIntervalSec;
			return this;
		}
	}
}
