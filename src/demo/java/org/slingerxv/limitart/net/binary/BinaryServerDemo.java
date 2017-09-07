package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.binary.message.MessageFactory;

public class BinaryServerDemo {
	public static void main(String[] args) throws Exception {
		MessageFactory messageFactory = new MessageFactory().registerMsg(BinaryHandlerDemo.class);
		BinaryServer server = new BinaryServer.BinaryServerBuilder()
				// 指定端口
				.addressPair(new AddressPair(8888))
				// 注册消息
				.factory(messageFactory).heartIntervalSec(5).build();
		server.startServer();
	}
}
