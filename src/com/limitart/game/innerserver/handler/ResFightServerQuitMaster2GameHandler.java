package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerGameServer;
import com.limitart.game.innerserver.msg.ResFightServerQuitMaster2GameMessage;
import com.limitart.net.binary.handler.IHandler;

public class ResFightServerQuitMaster2GameHandler implements IHandler<ResFightServerQuitMaster2GameMessage> {

	@Override
	public void handle(ResFightServerQuitMaster2GameMessage msg) {
		((InnerGameServer) msg.getExtra()).resFightServerQuitMaster2Game(msg);
	}

}
