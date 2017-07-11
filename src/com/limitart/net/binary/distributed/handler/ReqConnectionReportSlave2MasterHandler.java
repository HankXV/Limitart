package com.limitart.net.binary.distributed.handler;

import com.limitart.net.binary.distributed.InnerMasterServer;
import com.limitart.net.binary.distributed.message.ReqConnectionReportSlave2MasterMessage;
import com.limitart.net.binary.handler.IHandler;

public class ReqConnectionReportSlave2MasterHandler implements IHandler<ReqConnectionReportSlave2MasterMessage> {

	@Override
	public void handle(ReqConnectionReportSlave2MasterMessage msg) {
		((InnerMasterServer) msg.getExtra()).reqConnectionReportSlave2Master(msg);
	}

}
