package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerFightServer;
import com.limitart.game.innerserver.msg.ReqConnectionReportGame2FightMessage;
import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.message.Message;

public class ReqConnectionReportGame2FightHandler implements IHandler {

	@Override
	public void handle(Message message) {
		ReqConnectionReportGame2FightMessage msg = (ReqConnectionReportGame2FightMessage) message;
		((InnerFightServer) msg.getExtra()).reqConnectionReportGame2Fight(msg);
	}

}
