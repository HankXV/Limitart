package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.struct.AddressPair;

public class BinaryClientDemo {

	public static void main(String[] args) throws Exception {
		MessageFactory factory = new MessageFactory();
		BinaryClient client = new BinaryClient.BinaryClientBuilder().remoteAddress(new AddressPair("127.0.0.1", 8888))
				.factory(factory).onConnectionEffective(c -> {
					BinaryMessageDemo message = new BinaryMessageDemo();
					message.info = "Hello Limitart!";
					try {
						c.sendMessage(message, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).heartIntervalSec(5).build();
		client.connect();
	}
}
