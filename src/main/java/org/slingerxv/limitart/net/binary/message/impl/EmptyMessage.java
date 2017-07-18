package org.slingerxv.limitart.net.binary.message.impl;

import org.slingerxv.limitart.net.binary.message.Message;

public class EmptyMessage extends Message {

	@Override
	public short getMessageId() {
		return 0;
	}
}
