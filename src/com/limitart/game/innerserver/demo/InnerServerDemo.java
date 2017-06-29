package com.limitart.game.innerserver.demo;

import com.limitart.game.innerserver.InnerFightServer;
import com.limitart.game.innerserver.InnerGameServer;
import com.limitart.game.innerserver.InnerMasterServer;
import com.limitart.net.binary.message.MessageFactory;

public class InnerServerDemo {
	public static void main(String[] args) throws Exception {
		MessageFactory messageFactory = new MessageFactory();
		InnerFightServer fightServer = new InnerFightServerImpl(1, "127.0.0.1", 7777, 7778, "outpass", messageFactory,
				"127.0.0.1", 10000);
		InnerGameServer gameServer = new InnerGameServerImpl(1, "127.0.0.1", 8888, "outpass", messageFactory,
				"127.0.0.1", 10000);
		InnerMasterServer masterServer = new InnerMasterServer(10000, messageFactory);
		masterServer.start();
		gameServer.start();
		fightServer.start();
	}
}
