package com.limitart.net.binary.client.listener;

import com.limitart.net.binary.client.BinaryClient;
import com.limitart.net.binary.message.Message;

/**
 * 服务器事件
 * 
 * @author Hank
 *
 */
public interface BinaryClientEventListener {
	void onChannelActive(BinaryClient client);

	void onChannelInactive(BinaryClient client);

	void onExceptionCaught(BinaryClient client, Throwable cause);

	void onChannelRegistered(BinaryClient client);

	void onChannelUnregistered(BinaryClient client);

	void onConnectionEffective(BinaryClient client);

	void dispatchMessage(Message message);
}
