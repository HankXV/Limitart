package com.limitart.net.binary;

import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.message.Message;

public class BinaryHandlerDemo implements IHandler {

	@Override
	public void handle(Message message) {
		BinaryMessageDemo msg = (BinaryMessageDemo) message;
		System.out.println("server received message:" + msg.info);
	}

}
