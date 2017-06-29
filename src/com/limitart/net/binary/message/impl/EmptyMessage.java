package com.limitart.net.binary.message.impl;

import com.limitart.net.binary.message.Message;

public class EmptyMessage extends Message {

	@Override
	public short getMessageId() {
		return 0;
	}

	@Override
	public void encode() throws Exception {

	}

	@Override
	public void decode() throws Exception {

	}
}
