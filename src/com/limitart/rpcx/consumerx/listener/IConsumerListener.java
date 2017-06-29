package com.limitart.rpcx.consumerx.listener;

import com.limitart.net.binary.client.BinaryClient;
import com.limitart.rpcx.consumerx.ConsumerX;

public interface IConsumerListener {
	public void onServiceCenterConnected(ConsumerX consumer);

	public void onConsumerConnected(BinaryClient client);
}
