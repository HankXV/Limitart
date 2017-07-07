package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerMasterServer;
import com.limitart.game.innerserver.msg.ReqServerLoadSlave2MasterMessage;
import com.limitart.net.binary.handler.IHandler;

public class ReqServerLoadSlave2MasterHandler implements IHandler<ReqServerLoadSlave2MasterMessage> {

	@Override
	public void handle(ReqServerLoadSlave2MasterMessage msg) {
		((InnerMasterServer) msg.getExtra()).reqServerLoadSlave2Master(msg);
	}

}
