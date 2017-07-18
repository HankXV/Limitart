package org.slingerxv.limitart.net.binary.distributed.message;

import org.slingerxv.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;
import org.slingerxv.limitart.net.binary.message.Message;

public class ReqServerLoadSlave2MasterMessage extends Message {
	public int load;

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ReqServerLoadSlave2MasterMessage.getValue();
	}
}
