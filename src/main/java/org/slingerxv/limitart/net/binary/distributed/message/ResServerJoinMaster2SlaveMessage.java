package org.slingerxv.limitart.net.binary.distributed.message;

import java.util.ArrayList;

import org.slingerxv.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;
import org.slingerxv.limitart.net.binary.message.Message;

public class ResServerJoinMaster2SlaveMessage extends Message {
	public ArrayList<InnerServerInfo> infos = new ArrayList<>();

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ResServerJoinMaster2SlaveMessage.getValue();
	}
}
