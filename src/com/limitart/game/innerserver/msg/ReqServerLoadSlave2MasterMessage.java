package com.limitart.game.innerserver.msg;

import com.limitart.game.innerserver.InnerMessageEnum;
import com.limitart.net.binary.message.Message;

public class ReqServerLoadSlave2MasterMessage extends Message {
	public int load;

	@Override
	public short getMessageId() {
		return InnerMessageEnum.ReqServerLoadSlave2MasterMessage.getValue();
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
