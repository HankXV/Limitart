package org.slingerxv.limitart.net.define;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class AbstractNettyServer {
	private static Logger log = LogManager.getLogger();
	protected static EventLoopGroup bossGroup;
	protected static EventLoopGroup workerGroup;
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

	protected static ServerBootstrap createSocketServerBoot(String serverName,
			ChannelInitializer<SocketChannel> channelInitializer) {
		ServerBootstrap boot = new ServerBootstrap();
		if (Epoll.isAvailable()) {
			boot.option(ChannelOption.SO_BACKLOG, 1024).channel(EpollServerSocketChannel.class)
					.childOption(ChannelOption.SO_LINGER, 0).childOption(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			log.info(serverName + " epoll init");
		} else {
			boot.channel(NioServerSocketChannel.class);
			log.info(serverName + " nio init");
		}
		boot.group(bossGroup, workerGroup).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.TCP_NODELAY, true).childHandler(channelInitializer);
		return boot;
	}
}
