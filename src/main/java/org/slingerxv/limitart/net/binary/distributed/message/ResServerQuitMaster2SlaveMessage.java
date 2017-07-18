package org.slingerxv.limitart.net.binary.distributed.message;

import org.slingerxv.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;
import org.slingerxv.limitart.net.binary.message.Message;

public class ResServerQuitMaster2SlaveMessage extends Message {
	public int serverType;
	public int serverId;

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ResServerQuitMaster2SlaveMessage.getValue();
	}
}
