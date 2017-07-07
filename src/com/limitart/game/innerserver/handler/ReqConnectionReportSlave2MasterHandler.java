package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerMasterServer;
import com.limitart.game.innerserver.msg.ReqConnectionReportSlave2MasterMessage;
import com.limitart.net.binary.handler.IHandler;

public class ReqConnectionReportSlave2MasterHandler implements IHandler<ReqConnectionReportSlave2MasterMessage> {

	@Override
	public void handle(ReqConnectionReportSlave2MasterMessage msg) {
		((InnerMasterServer) msg.getExtra()).reqConnectionReportSlave2Master(msg);
	}

}
