package com.limitart.game.innerserver.handler;

import com.limitart.game.innerserver.InnerGameServer;
import com.limitart.game.innerserver.msg.ResFightServerJoinMaster2GameMessage;
import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.message.Message;

public class ResFightServerJoinMaster2GameHandler implements IHandler {

	@Override
	public void handle(Message message) {
		ResFightServerJoinMaster2GameMessage msg = (ResFightServerJoinMaster2GameMessage) message;
		((InnerGameServer) msg.getExtra()).resFightServerJoinMaster2Game(msg);
	}
}
