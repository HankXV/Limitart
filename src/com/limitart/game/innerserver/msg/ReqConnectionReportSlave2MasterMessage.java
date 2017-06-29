package com.limitart.game.innerserver.msg;

import com.limitart.game.innerserver.InnerMessageEnum;
import com.limitart.net.binary.message.Message;

public class ReqConnectionReportSlave2MasterMessage extends Message {
	public InnerServerInfo serverInfo;

	@Override
	public short getMessageId() {
		return InnerMessageEnum.ReqConnectionReportSlave2MasterMessage.getValue();
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
