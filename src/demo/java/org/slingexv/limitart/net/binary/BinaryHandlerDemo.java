package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.handler.IHandler;

public class BinaryHandlerDemo implements IHandler<BinaryMessageDemo> {

	@Override
	public void handle(BinaryMessageDemo msg) {
		System.out.println("server received message:" + msg.info);
	}

}
