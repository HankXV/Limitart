package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.client.BinaryClient;
import org.slingerxv.limitart.net.binary.client.config.BinaryClientConfig;
import org.slingerxv.limitart.net.binary.client.config.BinaryClientConfig.BinaryClientConfigBuilder;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.struct.AddressPair;

public class BinaryClientDemo {

	public static void main(String[] args) throws Exception {
		MessageFactory factory = new MessageFactory();
		BinaryClientConfig config = new BinaryClientConfigBuilder()
				.remoteAddress(new AddressPair("127.0.0.1", 8888))
				.factory(factory)
				.onConnectionEffective(client -> {
					BinaryMessageDemo message = new BinaryMessageDemo();
					message.info = "Hello Limitart!";
					try {
						client.sendMessage(message, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).build();
		BinaryClient client = new BinaryClient(config);
		client.connect();
	}
}
