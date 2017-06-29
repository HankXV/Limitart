package com.limitart.game.innerserver.msg;

import java.util.ArrayList;
import java.util.List;

import com.limitart.game.innerserver.InnerMessageEnum;
import com.limitart.net.binary.message.Message;

public class ResFightServerJoinMaster2GameMessage extends Message {
	public List<InnerServerInfo> serverInfo = new ArrayList<>();

	@Override
	public short getMessageId() {
		return InnerMessageEnum.ResFightServerJoinMaster2GameMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putMessageMetaList(this.serverInfo);
	}

	@Override
	public void decode() throws Exception {
		this.serverInfo = getMessageMetaList(InnerServerInfo.class);
	}

}
