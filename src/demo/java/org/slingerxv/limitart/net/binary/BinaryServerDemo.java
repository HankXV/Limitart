package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.binary.server.BinaryServer;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig.BinaryServerConfigBuilder;
import org.slingerxv.limitart.net.struct.AddressPair;

public class BinaryServerDemo {
	public static void main(String[] args)
			throws Exception {
		MessageFactory facotry = new MessageFactory().registerMsg(BinaryHandlerDemo.class);
		BinaryServerConfig config = new BinaryServerConfigBuilder()
				//指定端口
				.addressPair(new AddressPair(8888))
				//注册消息
				.factory(facotry)
				//派发消息
				.dispatchMessage(message -> {
					message.getHandler().handle(message);
				}).build();
		BinaryServer server = new BinaryServer(config);
		server.startServer();
	}
}
