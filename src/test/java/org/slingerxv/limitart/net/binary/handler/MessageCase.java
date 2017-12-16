package org.slingerxv.limitart.net.binary.handler;

import org.slingerxv.limitart.net.binary.message.Message;

public class MessageCase extends Message {
	public String info;

	@Override
	public short getMessageId() {
		return -1;
	}

}
