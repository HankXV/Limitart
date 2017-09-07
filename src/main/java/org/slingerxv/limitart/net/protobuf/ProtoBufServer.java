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

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;
import org.slingerxv.limitart.net.define.AbstractNettyServer;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.net.protobuf.handler.ProtoBufHandler;
import org.slingerxv.limitart.net.protobuf.message.ProtoBufFactory;
import org.slingerxv.limitart.net.struct.AddressPair;
import org.slingerxv.limitart.util.Beta;
import org.slingerxv.limitart.util.StringUtil;

import com.google.protobuf.Message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * ProtoBuf服务器
 * 
 * @author hank
 *
 */
@Beta
public class ProtoBufServer extends AbstractNettyServer implements IServer {
	private static Logger log = LoggerFactory.getLogger(ProtoBufServer.class);
	private AtomicInteger connectionCount = new AtomicInteger(0);
	private long startTime;
	// --config
	private AddressPair addressPair;
	private Set<String> whiteList;
	private ProtoBufFactory factory;
	private int maxConnection;
	private int receiveIntervalMills;

	// ---listener
	private Proc2<Channel, Boolean> onChannelStateChanged;
	private Proc2<Channel, Throwable> onExceptionCaught;
	private Proc1<Channel> onServerBind;
	private Proc3<Message, ProtoBufHandler<Message>, Channel> dispatchMessage;

	/**
	 * @param serverName
	 */
	protected ProtoBufServer(ProtoBufServerBuilder builder) {
		super(builder.serverName);
		this.addressPair = Objects.requireNonNull(builder.addressPair, "addressPair");
		this.whiteList = Objects.requireNonNull(builder.whiteList, "whiteList");
		this.factory = Objects.requireNonNull(builder.factory, "factory");
		this.onChannelStateChanged = builder.onChannelStateChanged;
		this.onExceptionCaught = builder.onExceptionCaught;
		this.onServerBind = builder.onServerBind;
		this.dispatchMessage = builder.dispatchMessage;
		this.maxConnection = builder.maxConnection;
		this.receiveIntervalMills = builder.receiveIntervalMills;
	}

	@Override
	public void startServer() throws Exception {
		startTime = System.currentTimeMillis();
		bind(addressPair.getPort(), onServerBind);
	}

	@Override
	public void stopServer() {
		unbind();
	}

	@Override
	protected void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		factory.copyToChannelPipeline(pipeline);
		pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
		pipeline.addLast(new ProtobufEncoder());
	}

	@Override
	protected void exceptionCaught0(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Procs.invoke(onExceptionCaught, ctx.channel(), cause);
	}

	@Override
	protected void channelActive0(ChannelHandlerContext ctx) throws Exception {
		if (maxConnection > 0 && connectionCount.get() >= maxConnection) {
			log.error("connection count is greater than " + maxConnection + " close channel:" + ctx.channel());
			ctx.channel().close();
			return;
		}
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
	}

	@Override
	protected void channelInactive0(ChannelHandlerContext ctx) throws Exception {
		connectionCount.decrementAndGet();
		Procs.invoke(onChannelStateChanged, ctx.channel(), false);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

		Message message = (Message) msg;
		try {
			@SuppressWarnings("unchecked")
			ProtoBufHandler<Message> handler = (ProtoBufHandler<Message>) factory.getHandler(message.getClass());
			if (handler == null) {
				throw new MessageCodecException(getServerName() + " handler empty:" + msg.getClass());
			}
			if (dispatchMessage != null) {
				try {
					dispatchMessage.run(message, handler, ctx.channel());
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
		}

	}

	public AddressPair getAddressPair() {
		return addressPair;
	}

	public Set<String> getWhiteList() {
		return whiteList;
	}

	public ProtoBufFactory getFactory() {
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

	public int getReceiveIntervalMills() {
		return receiveIntervalMills;
	}

	public static class ProtoBufServerBuilder {
		private String serverName;
		private AddressPair addressPair;
		private Set<String> whiteList = new HashSet<>();
		private ProtoBufFactory factory;
		private int maxConnection;
		private int receiveIntervalMills;
		// ---listener
		private Proc2<Channel, Boolean> onChannelStateChanged;
		private Proc2<Channel, Throwable> onExceptionCaught;
		private Proc1<Channel> onServerBind;
		private Proc3<Message, ProtoBufHandler<Message>, Channel> dispatchMessage;

		public ProtoBufServerBuilder() {
			this.serverName = "ProtoBuf-Server";
			this.addressPair = new AddressPair(8888);
			this.dispatchMessage = (t1, t2, t3) -> {
				t2.handle(t3, t1);
			};
			this.maxConnection = 20000;
			this.receiveIntervalMills = 0;
		}

		/**
		 * 构建服务器
		 * 
		 * @return
		 * @throws Exception
		 */
		public ProtoBufServer build() throws Exception {
			return new ProtoBufServer(this);
		}

		/**
		 * 服务器名称
		 * 
		 * @param serverName
		 * @return
		 */
		public ProtoBufServerBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		/**
		 * 绑定端口
		 * 
		 * @param port
		 * @return
		 */
		public ProtoBufServerBuilder addressPair(AddressPair addressPair) {
			this.addressPair = addressPair;
			return this;
		}

		/**
		 * 消息工厂
		 * 
		 * @param factory
		 * @return
		 */
		public ProtoBufServerBuilder factory(ProtoBufFactory factory) {
			this.factory = factory;
			return this;
		}

		/**
		 * 访问白名单
		 * 
		 * @param remoteAddress
		 * @return
		 */
		public ProtoBufServerBuilder whiteList(String... remoteAddress) {
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
		public ProtoBufServerBuilder onChannelStateChanged(Proc2<Channel, Boolean> onChannelStateChanged) {
			this.onChannelStateChanged = onChannelStateChanged;
			return this;
		}

		/**
		 * 异常监听
		 * 
		 * @param onExceptionCaught
		 * @return
		 */
		public ProtoBufServerBuilder onExceptionCaught(Proc2<Channel, Throwable> onExceptionCaught) {
			this.onExceptionCaught = onExceptionCaught;
			return this;
		}

		/**
		 * 服务器绑定成功监听
		 * 
		 * @param onServerBind
		 * @return
		 */
		public ProtoBufServerBuilder onServerBind(Proc1<Channel> onServerBind) {
			this.onServerBind = onServerBind;
			return this;
		}

		/**
		 * 分发消息监听
		 * 
		 * @param dispatchMessage
		 * @return
		 */
		public ProtoBufServerBuilder dispatchMessage(
				Proc3<Message, ProtoBufHandler<Message>, Channel> dispatchMessage) {
			this.dispatchMessage = dispatchMessage;
			return this;
		}

		/**
		 * 最大链接数限制
		 * 
		 * @param maxConnection
		 * @return
		 */
		public ProtoBufServerBuilder maxConnection(int maxConnection) {
			this.maxConnection = maxConnection;
			return this;
		}

		/**
		 * 消息接收间隔不能大于的毫秒数
		 * 
		 * @param receiveIntervalMills
		 * @return
		 */
		public ProtoBufServerBuilder receiveIntervalMills(int receiveIntervalMills) {
			this.receiveIntervalMills = receiveIntervalMills;
			return this;
		}
	}
}
