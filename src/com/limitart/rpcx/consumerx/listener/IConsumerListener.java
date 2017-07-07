package com.limitart.rpcx.consumerx.listener;

import com.limitart.net.binary.client.BinaryClient;
import com.limitart.rpcx.consumerx.ConsumerX;

public interface IConsumerListener {
	void onServiceCenterConnected(ConsumerX consumer);

	void onConsumerConnected(BinaryClient client);
}
