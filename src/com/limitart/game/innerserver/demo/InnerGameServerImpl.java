package com.limitart.game.innerserver.demo;

import com.limitart.game.innerserver.InnerGameServer;
import com.limitart.net.binary.message.MessageFactory;

public class InnerGameServerImpl extends InnerGameServer {

	public InnerGameServerImpl(int serverId, String outIp, int outPort, String outPass, MessageFactory factory,
			String innerMasterIp, int innerMasterPort) throws Exception {
		super(serverId, outIp, outPort, outPass, factory, innerMasterIp, innerMasterPort);
	}

	@Override
	public int serverLoad() {
		return 0;
	}

}
