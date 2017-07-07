package com.limitart.net.binary;

import com.limitart.net.binary.handler.IHandler;

public class BinaryHandlerDemo implements IHandler<BinaryMessageDemo> {

	@Override
	public void handle(BinaryMessageDemo msg) {
		System.out.println("server received message:" + msg.info);
	}

}
