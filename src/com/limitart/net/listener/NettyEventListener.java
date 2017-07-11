package com.limitart.net.listener;

import io.netty.channel.Channel;

public interface NettyEventListener {
	void onChannelActive(Channel channel);

	void onChannelInactive(Channel channel);

	void onExceptionCaught(Channel channel, Throwable cause);
}
