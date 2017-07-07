package com.limitart.net.binary.listener;

import io.netty.channel.Channel;

public interface SendMessageListener {
	void onComplete(boolean isSuccess, Throwable cause, Channel channel);
}
