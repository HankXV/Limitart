package com.limitart.net.binary.distributed.message;

import com.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;
import com.limitart.net.binary.message.Message;

public class ReqServerLoadSlave2MasterMessage extends Message {
	public int load;

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ReqServerLoadSlave2MasterMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.load);
	}

	@Override
	public void decode() throws Exception {
		this.load = getInt();
	}

}
