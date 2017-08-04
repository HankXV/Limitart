package org.slingerxv.limitart.net.binary.message.impl.validate;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.constant.InnerMessageEnum;

public class HeartServerMessage extends Message {
	public long serverTime;
	public long serverStartTime;

	@Override
	public short getMessageId() {
		return InnerMessageEnum.HeartServerMessage.getValue();
	}

}
