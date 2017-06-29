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
	public void onChannelActive(BinaryClient client);

	public void onChannelInactive(BinaryClient client);

	public void onExceptionCaught(BinaryClient client, Throwable cause);

	public void onChannelRegistered(BinaryClient client);

	public void onChannelUnregistered(BinaryClient client);

	public void onConnectionEffective(BinaryClient client);

	public void dispatchMessage(Message message);
}
