package org.slingerxv.limitart.net.binary.message.impl.validate;

import org.slingerxv.limitart.net.binary.message.constant.InnerMessageEnum;

public class ConnectionValidateClientMessage extends Message {
	private int validateRandom;

	public int getValidateRandom() {
		return validateRandom;
	}

	public void setValidateRandom(int validateRandom) {
		this.validateRandom = validateRandom;
	}

	@Override
	public short getMessageId() {
		return InnerMessageEnum.ConnectionValidateClientMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.validateRandom);
	}

	@Override
	public void decode() throws Exception {
		this.validateRandom = getInt();
	}
}
