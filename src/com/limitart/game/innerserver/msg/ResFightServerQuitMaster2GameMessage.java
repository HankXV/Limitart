package com.limitart.game.innerserver.msg;

import com.limitart.net.binary.message.Message;

public class ResFightServerQuitMaster2GameMessage extends Message {
	public int fightServerId;

	@Override
	public short getMessageId() {
		return 0;
	}

	@Override
	public void encode() throws Exception {
		putInt(this.fightServerId);
	}

	@Override
	public void decode() throws Exception {
		this.fightServerId = getInt();
	}

}
