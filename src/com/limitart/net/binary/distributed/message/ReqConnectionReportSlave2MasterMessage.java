package com.limitart.net.binary.distributed.message;

import com.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;
import com.limitart.net.binary.message.Message;

public class ReqConnectionReportSlave2MasterMessage extends Message {
	public InnerServerInfo serverInfo;

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ReqConnectionReportSlave2MasterMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putMessageMeta(this.serverInfo);
	}

	@Override
	public void decode() throws Exception {
		this.serverInfo = getMessageMeta(InnerServerInfo.class);
	}

}
