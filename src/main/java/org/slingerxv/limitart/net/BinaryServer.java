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
package org.slingerxv.limitart.net;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 二进制通信服务器
 * 
 * @author Hank
 *
 */
public class BinaryServer extends AbstractNettyServer implements Server {
	private static Logger log = LoggerFactory.getLogger(BinaryServer.class);
	// --config
	private AddressPair addressPair;
	private AbstractBinaryDecoder decoder;
	private AbstractBinaryEncoder encoder;
	private MessageFactory factory;

	// ---listener
	private Proc2<Channel, Boolean> onChannelStateChanged;
	private Proc2<Channel, Throwable> onExceptionCaught;
	private Proc1<Channel> onServerBind;
	private Proc2<Message, IHandler<Message>> dispatchMessage;

	private BinaryServer(BinaryServerBuilder builder) throws Exception {
		super(builder.serverName);
		this.addressPair = Objects.requireNonNull(builder.addressPair, "addressPair");
		this.decoder = Objects.requireNonNull(builder.decoder, "decoder");
		this.encoder = Objects.requireNonNull(builder.encoder, "encoder");
		this.factory = Objects.requireNonNull(builder.factory, "factory");
		this.onChannelStateChanged = builder.onChannelStateChanged;
		this.onExceptionCaught = builder.onExceptionCaught;
		this.onServerBind = builder.onServerBind;
		this.dispatchMessage = builder.dispatchMessage;
	}

	@Override
	protected void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(), decoder.getLengthFieldOffset(),
				decoder.getLengthFieldLength(), decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()));
	}

	@Override
	protected void exceptionCaught0(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Procs.invoke(onExceptionCaught, ctx.channel(), cause);
	}

	@Override
	protected void channelActive0(ChannelHandlerContext ctx) throws Exception {
		Procs.invoke(onChannelStateChanged, ctx.channel(), true);
	}

	@Override
	protected void channelInactive0(ChannelHandlerContext ctx) throws Exception {
		Procs.invoke(onChannelStateChanged, ctx.channel(), false);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object arg) throws Exception {
		ByteBuf buffer = (ByteBuf) arg;
		try {
			// 消息id
			short messageId = decoder.readMessageId(ctx.channel(), buffer);
			Message msg = factory.getMessage(messageId);
			if (msg == null) {
				throw new MessageCodecException(
						getServerName() + " message empty,id:" + Integer.toHexString(messageId));
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
						getServerName() + " can not find handler for message,id:" + Integer.toHexString(messageId));
			}
			msg.setChannel(ctx.channel());
			msg.setServer(this);
			if (dispatchMessage != null) {
				try {
					dispatchMessage.run(msg, handler);
				} catch (Exception e) {
					log.error(ctx.channel() + " cause:", e);
					Procs.invoke(onExceptionCaught, ctx.channel(), e);
				}
			} else {
				log.warn(getServerName() + " no dispatch message listener!");
			}
		} catch (Exception e) {
			ctx.channel().close();
			log.error("close session:" + ctx.channel(), e);
		} finally {
			buffer.release();
		}

	}

	@Override
	public void startServer() {
		bind(addressPair.getPort(), onServerBind);
	}

	@Override
	public void stopServer() {
		unbind();
	}

	public void sendMessage(Channel channel, Message msg) throws MessageCodecException {
		sendMessage(channel, msg, null);
	}

	public void sendMessage(Channel channel, Message msg, Proc3<Boolean, Throwable, Channel> listener)
			throws MessageCodecException {
		Messages.sendMessage(encoder, channel, msg, listener);
	}

	public void sendMessage(List<Channel> channels, Message msg) throws MessageCodecException {
		sendMessage(channels, msg, null);
	}

	public void sendMessage(List<Channel> channels, Message msg, Proc3<Boolean, Throwable, Channel> listener)
			throws MessageCodecException {
		Messages.sendMessage(encoder, channels, msg, listener);
	}

	public AddressPair getAddressPair() {
		return addressPair;
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

	public static class BinaryServerBuilder {
		private String serverName;
		private AddressPair addressPair;
		private AbstractBinaryDecoder decoder;
		private AbstractBinaryEncoder encoder;
		private MessageFactory factory;
		// ---listener
		private Proc2<Channel, Boolean> onChannelStateChanged;
		private Proc2<Channel, Throwable> onExceptionCaught;
		private Proc1<Channel> onServerBind;
		private Proc2<Message, IHandler<Message>> dispatchMessage;

		public BinaryServerBuilder() {
			this.serverName = "Binary-Server";
			this.addressPair = new AddressPair(8888);
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
		 * 分发消息监听
		 * 
		 * @param dispatchMessage
		 * @return
		 */
		public BinaryServerBuilder dispatchMessage(Proc2<Message, IHandler<Message>> dispatchMessage) {
			this.dispatchMessage = dispatchMessage;
			return this;
		}
	}
}
