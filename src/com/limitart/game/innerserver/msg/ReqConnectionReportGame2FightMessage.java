package com.limitart.game.innerserver.msg;

import com.limitart.game.innerserver.InnerMessageEnum;
import com.limitart.net.binary.message.Message;

public class ReqConnectionReportGame2FightMessage extends Message {
	public int serverId;

	@Override
	public short getMessageId() {
		return InnerMessageEnum.ReqConnectionReportGame2FightMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.serverId);
	}

	@Override
	public void decode() throws Exception {
		this.serverId = getInt();
	}

}
