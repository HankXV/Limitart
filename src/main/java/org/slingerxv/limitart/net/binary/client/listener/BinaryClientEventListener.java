package org.slingerxv.limitart.net.binary.client.listener;

import org.slingerxv.limitart.net.binary.client.BinaryClient;

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

	void onConnectionEffective(BinaryClient client);

	void dispatchMessage(Message message);
}
