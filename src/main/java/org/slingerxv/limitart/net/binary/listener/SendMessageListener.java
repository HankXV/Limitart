package org.slingerxv.limitart.net.binary.listener;

import io.netty.channel.Channel;

public interface SendMessageListener {
	void onComplete(boolean isSuccess, Throwable cause, Channel channel);
}
