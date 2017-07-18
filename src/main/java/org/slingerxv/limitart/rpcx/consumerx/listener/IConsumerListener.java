package org.slingerxv.limitart.rpcx.consumerx.listener;

import org.slingerxv.limitart.net.binary.BinaryClient;
import org.slingerxv.limitart.rpcx.consumerx.ConsumerX;

public interface IConsumerListener {
	void onServiceCenterConnected(ConsumerX consumer);

	void onConsumerConnected(BinaryClient client);
}
