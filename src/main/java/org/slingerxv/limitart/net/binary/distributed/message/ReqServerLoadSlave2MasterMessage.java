package org.slingerxv.limitart.net.binary.distributed.message;

import org.slingerxv.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;

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
