package org.slingerxv.limitart.net.binary.message.impl.validate;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.constant.InnerMessageEnum;

public class ConnectionValidateSuccessServerMessage extends Message {

	@Override
	public short getMessageId() {
		return InnerMessageEnum.ConnectionValidateSuccessServerMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
	}

	@Override
	public void decode() throws Exception {
	}
}
