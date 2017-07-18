package org.slingerxv.limitart.net.binary.distributed.message;

import org.slingerxv.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;
import org.slingerxv.limitart.net.binary.message.Message;

public class ReqConnectionReportSlave2MasterMessage extends Message {
	public InnerServerInfo serverInfo;

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ReqConnectionReportSlave2MasterMessage.getValue();
	}
}
