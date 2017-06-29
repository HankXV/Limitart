package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerMasterServer;
import com.limitart.game.innerserver.msg.ReqConnectionReportSlave2MasterMessage;
import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.message.Message;

public class ReqConnectionReportSlave2MasterHandler implements IHandler {

	@Override
	public void handle(Message message) {
		ReqConnectionReportSlave2MasterMessage msg = (ReqConnectionReportSlave2MasterMessage) message;
		((InnerMasterServer) msg.getExtra()).reqConnectionReportSlave2Master(msg);
	}

}
