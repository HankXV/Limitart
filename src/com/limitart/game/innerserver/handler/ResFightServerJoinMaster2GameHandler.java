package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerGameServer;
import com.limitart.game.innerserver.msg.ResFightServerJoinMaster2GameMessage;
import com.limitart.net.binary.handler.IHandler;

public class ResFightServerJoinMaster2GameHandler implements IHandler<ResFightServerJoinMaster2GameMessage> {

	@Override
	public void handle(ResFightServerJoinMaster2GameMessage msg) {
		((InnerGameServer) msg.getExtra()).resFightServerJoinMaster2Game(msg);
	}
}
