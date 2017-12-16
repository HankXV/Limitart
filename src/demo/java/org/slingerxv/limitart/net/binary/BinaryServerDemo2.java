package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.binary.handler.annotation.Controller;
import org.slingerxv.limitart.net.binary.handler.annotation.Handler;
import org.slingerxv.limitart.net.binary.message.MessageFactory;

@Controller
public class BinaryServerDemo2 {
	public static void main(String[] args) throws Exception {
		MessageFactory messageFactory = new MessageFactory().registerController(BinaryServerDemo2.class);
		BinaryServer server = new BinaryServer.BinaryServerBuilder()
				// 指定端口
				.addressPair(new AddressPair(8888))
				// 注册消息
				.factory(messageFactory).build();
		server.startServer();
	}

	@Handler(BinaryMessageDemo.class)
	public void hello(BinaryMessageDemo message) {
		System.out.println(message.info);
	}
}
