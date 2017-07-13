package org.slingerxv.limitart.net.binary.server.listener;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.listener.NettyEventListener;

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
