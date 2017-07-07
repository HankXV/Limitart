package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerFightServer;
import com.limitart.game.innerserver.msg.ReqConnectionReportGame2FightMessage;
import com.limitart.net.binary.handler.IHandler;

public class ReqConnectionReportGame2FightHandler implements IHandler<ReqConnectionReportGame2FightMessage> {

	@Override
	public void handle(ReqConnectionReportGame2FightMessage msg) {
		((InnerFightServer) msg.getExtra()).reqConnectionReportGame2Fight(msg);
	}

}
