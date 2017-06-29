package com.limitart.game.innerserver.demo;

import com.limitart.game.innerserver.InnerFightServer;
import com.limitart.net.binary.message.MessageFactory;

public class InnerFightServerImpl extends InnerFightServer {

	public InnerFightServerImpl(int serverId, String outIp, int outPort, int innerPort, String outPass,
			MessageFactory factory, String innerMasterIp, int innerMasterPort) throws Exception {
		super(serverId, outIp, outPort, innerPort, outPass, factory, innerMasterIp, innerMasterPort);
	}

	@Override
	public int serverLoad() {
		return 0;
	}

}
