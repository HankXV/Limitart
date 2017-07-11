package com.limitart.net.binary.distributed.handler;

import com.limitart.net.binary.distributed.InnerMasterServer;
import com.limitart.net.binary.distributed.message.ReqServerLoadSlave2MasterMessage;
import com.limitart.net.binary.handler.IHandler;

public class ReqServerLoadSlave2MasterHandler implements IHandler<ReqServerLoadSlave2MasterMessage> {

	@Override
	public void handle(ReqServerLoadSlave2MasterMessage msg) {
		((InnerMasterServer) msg.getExtra()).reqServerLoadSlave2Master(msg);
	}

}
