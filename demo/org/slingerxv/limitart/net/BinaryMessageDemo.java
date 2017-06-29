package org.slingerxv.limitart.net;

import org.slingerxv.limitart.net.binary.BinaryMessage;
import org.slingerxv.limitart.net.binary.BinaryMessages;

public class BinaryMessageDemo extends BinaryMessage {
	public String content = "hello limitart!";

	@Override
	public short messageID() {
		return BinaryMessages.createID(0X00, 0X01);
	}

}
