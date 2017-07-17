package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.binary.server.BinaryServer;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig.BinaryServerConfigBuilder;
import org.slingerxv.limitart.net.struct.AddressPair;

public class BinaryServerDemo {
	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, MessageIDDuplicatedException {
		BinaryServerConfig build = new BinaryServerConfigBuilder().addressPair(new AddressPair(8888))
				.serverName("BinaryServerDemo").factory(new MessageFactory().registerMsg(BinaryHandlerDemo.class))
				.onExceptionCaught((channel, cause) -> {
					cause.printStackTrace();
				}).dispatchMessage(message -> {
					message.getHandler().handle(message);
				}).build();
		BinaryServer server = new BinaryServer(build);
		server.startServer();
	}
}
