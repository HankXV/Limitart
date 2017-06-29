package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerMasterServer;
import com.limitart.game.innerserver.msg.ReqServerLoadSlave2MasterMessage;
import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.message.Message;

public class ReqServerLoadSlave2MasterHandler implements IHandler {

	@Override
	public void handle(Message message) {
		ReqServerLoadSlave2MasterMessage msg = (ReqServerLoadSlave2MasterMessage) message;
		((InnerMasterServer) msg.getExtra()).reqServerLoadSlave2Master(msg);
	}

}
