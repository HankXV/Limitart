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
package org.slingerxv.limitart.net.define;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Procs;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 抽象Netty服务器
 * 
 * @author hank
 *
 */
public abstract class AbstractNettyServer {
	private static Logger log = LogManager.getLogger();
	protected static EventLoopGroup bossGroup;
	protected static EventLoopGroup workerGroup;
	private ServerBootstrap bootstrap;
	private Channel channel;
	private String serverName;
	static {
		if (bossGroup == null || workerGroup == null) {
			if (Epoll.isAvailable()) {
				bossGroup = new EpollEventLoopGroup(1);
				workerGroup = new EpollEventLoopGroup();
			} else {
				bossGroup = new NioEventLoopGroup(1);
				workerGroup = new NioEventLoopGroup();
			}
		}
	}

	protected AbstractNettyServer(String serverName) {
		this.serverName = Objects.requireNonNull(serverName, "server name");
		bootstrap = new ServerBootstrap();
		if (Epoll.isAvailable()) {
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024).channel(EpollServerSocketChannel.class)
					.childOption(ChannelOption.SO_LINGER, 0).childOption(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			log.info(serverName + " epoll init");
		} else {
			bootstrap.channel(NioServerSocketChannel.class);
			log.info(serverName + " nio init");
		}
		bootstrap.group(bossGroup, workerGroup).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						initPipeline(ch.pipeline());
					}
				});
	}

	protected abstract void initPipeline(ChannelPipeline pipeline);

	protected void bind(int port, Proc1<Channel> listener) {
		new Thread(() -> {
			try {
				bootstrap.bind(port).addListener((ChannelFuture arg0) -> {
					if (arg0.isSuccess()) {
						log.info(serverName + " bind at port:" + port);
						channel = arg0.channel();
						Procs.invoke(listener, arg0.channel());
					}
				}).sync().channel().closeFuture().sync();
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}, serverName + "-Binder").start();
	}

	protected void unbind() {
		if (channel != null) {
			channel.close();
		}
	}

	public Channel channel() {
		return channel;
	}
}
