package org.slingerxv.limitart.net.listener;

import io.netty.channel.Channel;

public interface NettyEventListener {
	void onChannelStateChanged(Channel channel, boolean active);

	void onExceptionCaught(Channel channel, Throwable cause);
}
