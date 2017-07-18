package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.binary.server.BinaryServer;
import org.slingerxv.limitart.net.struct.AddressPair;

public class BinaryServerDemo {
	public static void main(String[] args) throws Exception {
		MessageFactory messageFactory = new MessageFactory().registerMsg(BinaryHandlerDemo.class);
		BinaryServer server = new BinaryServer.BinaryServerBuilder()
				// 指定端口
				.addressPair(new AddressPair(8888))
				// 注册消息
				.factory(messageFactory).build();
		server.startServer();
	}
}
