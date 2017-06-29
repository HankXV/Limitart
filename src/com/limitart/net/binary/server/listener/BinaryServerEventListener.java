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
	public void onServerBind(Channel channel);

	public void onConnectionEffective(Channel channel);

	public void dispatchMessage(Message message);
}
