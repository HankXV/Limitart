package org.slingerxv.limitart.net;

import org.slingerxv.limitart.net.binary.BinaryHandler;
import org.slingerxv.limitart.net.binary.BinaryManager;
import org.slingerxv.limitart.net.binary.BinaryRequestParam;

@BinaryManager
public class BinaryManagerDemo {
	@BinaryHandler(BinaryMessageDemo.class)
	public void doMessageDemo(BinaryRequestParam param) {
		BinaryMessageDemo msg = param.msg();
		System.out.println(msg.content);
	}
}
