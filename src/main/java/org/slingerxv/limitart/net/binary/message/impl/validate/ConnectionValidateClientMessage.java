package org.slingerxv.limitart.net.binary.message.impl.validate;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.constant.InnerMessageEnum;

public class ConnectionValidateClientMessage extends Message {
	public int validateRandom;

	@Override
	public short getMessageId() {
		return InnerMessageEnum.ConnectionValidateClientMessage.getValue();
	}
}
