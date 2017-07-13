package org.slingerxv.limitart.net.listener;

public interface NettyEventListener {
	void onChannelActive(Channel channel);

	void onChannelInactive(Channel channel);

	void onExceptionCaught(Channel channel, Throwable cause);
}
