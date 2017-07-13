package org.slingerxv.limitart.net.binary.distributed.message;

import org.slingerxv.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;

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
