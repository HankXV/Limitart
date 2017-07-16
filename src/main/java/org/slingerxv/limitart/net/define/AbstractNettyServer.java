package org.slingerxv.limitart.net.define;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public abstract class AbstractNettyServer {
	protected static EventLoopGroup bossGroup;
	protected static EventLoopGroup workerGroup;
	static {
		if (Epoll.isAvailable()) {
			bossGroup = new EpollEventLoopGroup(1);
			workerGroup = new EpollEventLoopGroup();
		} else {
			bossGroup = new NioEventLoopGroup(1);
			workerGroup = new NioEventLoopGroup();
		}
	}
}
