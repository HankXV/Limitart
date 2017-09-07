/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.net.protobuf;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.AbstractNettyClient;
import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;
import org.slingerxv.limitart.net.protobuf.handler.ProtoBufHandler;
import org.slingerxv.limitart.net.protobuf.message.ProtoBufFactory;

import com.google.protobuf.Message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * ProtoBuf客户端
 * 
 * @author hank
 *
 */
public class ProtoBufClient extends AbstractNettyClient {
	private static Logger log = LoggerFactory.getLogger(ProtoBufClient.class);
	// private long serverStartTime;
	// ----config
	private AddressPair remoteAddress;
	private ProtoBufFactory factory;
	// ----listener
	private Proc2<ProtoBufClient, Boolean> onChannelStateChanged;
	private Proc2<ProtoBufClient, Throwable> onExceptionCaught;
	private Proc3<Message, ProtoBufHandler<Message>, Channel> dispatchMessage;

	/**
	 * @param clientName
	 * @param autoReconnect
	 */
	protected ProtoBufClient(ProtoBufClientBuilder builder) {
		super(builder.clientName, builder.autoReconnect);
		this.remoteAddress = Objects.requireNonNull(builder.remoteAddress, "remoteAddress");
		this.factory = Objects.requireNonNull(builder.factory, "factory");
		this.onChannelStateChanged = builder.onChannelStateChanged;
		this.onExceptionCaught = builder.onExceptionCaught;
		this.dispatchMessage = builder.dispatchMessage;
	}

	public void sendMessage(Message msg) {
		sendMessage(msg, null);
	}

	public void sendMessage(Message msg, Proc3<Boolean, Throwable, Channel> listener) {
		if (channel() == null) {
			Procs.invoke(listener, false, new NullPointerException("channel"), null);
			return;
		}
		if (!channel().isWritable()) {
			Procs.invoke(listener, false, new IOException(" channel " + channel().remoteAddress() + " is unwritable"),
					channel());
			return;
		}
		channel().writeAndFlush(msg).addListener((ChannelFutureListener) arg0 -> {
			Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
		});
	}

	@Override
	protected void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		factory.copyToChannelPipeline(pipeline);
		pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
		pipeline.addLast(new ProtobufEncoder());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
		Message message = (Message) msg;
		try {
			@SuppressWarnings("unchecked")
			ProtoBufHandler<Message> handler = (ProtoBufHandler<Message>) factory.getHandler(message.getClass());
			if (handler == null) {
				throw new MessageCodecException(getClientName() + " handler empty:" + msg.getClass());
			}
			if (dispatchMessage != null) {
				try {
					dispatchMessage.run(message, handler, ctx.channel());
				} catch (Exception e) {
					log.error(ctx.channel() + " cause:", e);
					Procs.invoke(onExceptionCaught, this, e);
				}
			} else {
				log.warn(getClientName() + " no dispatch message listener!");
			}
		} catch (Exception e) {
			log.error(ctx.channel().remoteAddress().toString(), e);
		}

	}

	@Override
	protected void channelInactive0(ChannelHandlerContext ctx) throws Exception {
		Procs.invoke(onChannelStateChanged, ProtoBufClient.this, false);
		if (getAutoReconnect() > 0) {
			tryReconnect(remoteAddress.getIp(), remoteAddress.getPort(), getAutoReconnect());
		}
	}

	@Override
	protected void channelActive0(ChannelHandlerContext ctx) throws Exception {
		Procs.invoke(onChannelStateChanged, ProtoBufClient.this, true);
	}

	@Override
	protected void exceptionCaught0(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Procs.invoke(onExceptionCaught, ProtoBufClient.this, cause);
	}

	public SocketAddress remoteAddress() {
		return channel().remoteAddress();
	}

	public AddressPair getRemoteAddress() {
		return remoteAddress;
	}

	public ProtoBufFactory getFactory() {
		return factory;
	}

	@Override
	public void connect() {
		tryReconnect(remoteAddress.getIp(), remoteAddress.getPort(), 0);
	}

	@Override
	public void disConnect() {
		tryDisConnect();
	}

	public static class ProtoBufClientBuilder {
		private String clientName;
		private AddressPair remoteAddress;
		private int autoReconnect;
		private ProtoBufFactory factory;
		// ----listener
		private Proc2<ProtoBufClient, Boolean> onChannelStateChanged;
		private Proc2<ProtoBufClient, Throwable> onExceptionCaught;
		private Proc3<Message, ProtoBufHandler<Message>, Channel> dispatchMessage;

		public ProtoBufClientBuilder() {
			this.clientName = "ProtoBuf-Client";
			this.remoteAddress = new AddressPair("127.0.0.1", 8888);
			this.autoReconnect = 0;
			this.factory = new ProtoBufFactory();
			this.dispatchMessage = (t1, t2, t3) -> {
				t2.handle(t3, t1);
			};
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 * @throws Exception
		 */
		public ProtoBufClient build() throws Exception {
			return new ProtoBufClient(this);
		}

		public ProtoBufClientBuilder clientName(String clientName) {
			this.clientName = clientName;
			return this;
		}

		/**
		 * 服务器IP
		 * 
		 * @param remoteIp
		 * @return
		 */
		public ProtoBufClientBuilder remoteAddress(AddressPair remoteAddress) {
			this.remoteAddress = remoteAddress;
			return this;
		}

		/**
		 * 自动重连尝试间隔(秒)
		 * 
		 * @param autoReconnect
		 * @return
		 */
		public ProtoBufClientBuilder autoReconnect(int autoReconnect) {
			this.autoReconnect = autoReconnect;
			return this;
		}

		public ProtoBufClientBuilder factory(ProtoBufFactory factory) {
			this.factory = factory;
			return this;
		}

		public ProtoBufClientBuilder onChannelStateChanged(Proc2<ProtoBufClient, Boolean> onChannelStateChanged) {
			this.onChannelStateChanged = onChannelStateChanged;
			return this;
		}

		public ProtoBufClientBuilder onExceptionCaught(Proc2<ProtoBufClient, Throwable> onExceptionCaught) {
			this.onExceptionCaught = onExceptionCaught;
			return this;
		}

		public ProtoBufClientBuilder dispatchMessage(
				Proc3<Message, ProtoBufHandler<Message>, Channel> dispatchMessage) {
			this.dispatchMessage = dispatchMessage;
			return this;
		}
	}
}
