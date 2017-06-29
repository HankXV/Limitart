package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerGameServer;
import com.limitart.game.innerserver.msg.ResFightServerQuitMaster2GameMessage;
import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.message.Message;

public class ResFightServerQuitMaster2GameHandler implements IHandler {

	@Override
	public void handle(Message message) {
		ResFightServerQuitMaster2GameMessage msg = (ResFightServerQuitMaster2GameMessage) message;
		((InnerGameServer) message.getExtra()).resFightServerQuitMaster2Game(msg);
	}

}
