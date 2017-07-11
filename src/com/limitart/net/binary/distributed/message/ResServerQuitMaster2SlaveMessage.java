package com.limitart.net.binary.distributed.message;

import com.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;
import com.limitart.net.binary.message.Message;

public class ResServerQuitMaster2SlaveMessage extends Message {
	public int serverType;
	public int serverId;

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ResServerQuitMaster2SlaveMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.serverType);
		putInt(this.serverId);
	}

	@Override
	public void decode() throws Exception {
		this.serverType = getInt();
		this.serverId = getInt();
	}

}
