package com.limitart.net.binary.message.impl.validate;

import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.constant.InnerMessageEnum;

public class ConnectionValidateServerMessage extends Message {
	private String validateStr;

	public String getValidateStr() {
		return validateStr;
	}

	public void setValidateStr(String validateStr) {
		this.validateStr = validateStr;
	}

	@Override
	public short getMessageId() {
		return InnerMessageEnum.ConnectionValidateServerMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putString(this.validateStr);
	}

	@Override
	public void decode() throws Exception {
		this.validateStr = getString();
	}
}
