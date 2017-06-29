package com.limitart.net.listener;

import io.netty.channel.Channel;

public interface NettyEventListener {
	public void onChannelActive(Channel channel);

	public void onChannelInactive(Channel channel);

	public void onExceptionCaught(Channel channel, Throwable cause);

	public void onChannelRegistered(Channel channel);

	public void onChannelUnregistered(Channel channel);
}
