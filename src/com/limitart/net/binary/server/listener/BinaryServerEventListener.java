package com.limitart.net.binary.server.listener;

import com.limitart.net.binary.message.Message;
import com.limitart.net.listener.NettyEventListener;

import io.netty.channel.Channel;

/**
 * 服务器事件
 * 
 * @author Hank
 *
 */
public interface BinaryServerEventListener extends NettyEventListener {
	void onServerBind(Channel channel);

	void onConnectionEffective(Channel channel);

	void dispatchMessage(Message message);
}
