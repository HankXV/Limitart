package org.slingerxv.limitart.net.binary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.net.binary.client.BinaryClient;
import org.slingerxv.limitart.net.binary.client.config.BinaryClientConfig;
import org.slingerxv.limitart.net.binary.client.config.BinaryClientConfig.BinaryClientConfigBuilder;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.struct.AddressPair;

public class BinaryClientDemo {
	private static Logger log = LogManager.getLogger();

	public static void main(String[] args) throws Exception {
		MessageFactory factory = new MessageFactory();
		BinaryClientConfig build = new BinaryClientConfigBuilder().remoteAddress(new AddressPair("127.0.0.1", 8888))
				.clientName("BinaryClientDemo").factory(factory).onExceptionCaught((client, cause) -> {
					log.error(cause, cause);
					cause.printStackTrace();
				}).onConnectionEffective(client -> {
					BinaryMessageDemo message = new BinaryMessageDemo();
					message.info = "Hello Limitart!";
					try {
						client.sendMessage(message, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).dispatchMessage(message -> {
					message.getHandler().handle(message);
				}).build();
		BinaryClient client = new BinaryClient(build);
		client.connect();
	}
}
