package com.limitart.game.innerserver;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.game.innerserver.constant.InnerGameServerType;
import com.limitart.net.binary.distributed.InnerMasterServer;
import com.limitart.net.binary.distributed.InnerSlaveServer;
import com.limitart.net.binary.distributed.message.InnerServerInfo;
import com.limitart.net.binary.distributed.struct.InnerServerData;
import com.limitart.net.binary.distributed.util.InnerServerUtil;
import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import com.limitart.net.define.IServer;

/**
 * 内部战斗服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerFightServer implements IServer {
	private static Logger log = LogManager.getLogger();
	private InnerMasterServer server;
	private InnerSlaveServer toMaster;

	public InnerFightServer(int serverId, String fightServerIp, int fightServerPort, int FightServerInnerPort,
			String fightServerPass, MessageFactory factory, String publicIp, int publicPort)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, MessageIDDuplicatedException {
		server = new InnerMasterServer("Fight", FightServerInnerPort, factory) {

			@Override
			protected void onSlaveConnected(InnerServerData data) {
				if (data.getServerType() == InnerGameServerType.SERVER_TYPE_GAME) {
					log.info("game server connected:" + data.getServerId() + ",cur size:"
							+ getSlaves(InnerGameServerType.SERVER_TYPE_GAME).size());
				} else {
					log.error("server type :" + data.getServerType() + " connected!!!!!!!check!!!!");
				}
			}

			@Override
			protected void onSlaveDisconnected(InnerServerData data) {

			}
		};
		toMaster = new InnerSlaveServer("Fight-To-Public", serverId, fightServerIp, fightServerPort, FightServerInnerPort,
				fightServerPass, factory, publicIp, publicPort, InnerServerUtil.getInnerPass()) {

			@Override
			public int serverType() {
				return InnerGameServerType.SERVER_TYPE_FIGHT;
			}

			@Override
			public int serverLoad() {
				return getFightServerLoad();
			}

			@Override
			public void onNewSlaveQuit(int serverType, int serverId) {

			}

			@Override
			public void onNewSlaveJoin(InnerServerInfo info) {

			}

			@Override
			protected void onConnectMasterSuccess(InnerSlaveServer slave) {
				server.startServer();
				onConnectPublic(slave);
			}
		};
	}

	@Override
	public synchronized void startServer() {
		toMaster.startServer();
	}

	@Override
	public synchronized void stopServer() {
		toMaster.stopServer();
		server.stopServer();
	}

	protected abstract int getFightServerLoad();

	protected abstract void onConnectPublic(InnerSlaveServer slave);

	public InnerSlaveServer getPublicClient() {
		return this.toMaster;
	}

	public InnerServerData getGameServer(int serverId) {
		return server.getSlave(InnerGameServerType.SERVER_TYPE_GAME, serverId);
	}

	public List<InnerServerData> getGameServers() {
		return server.getSlaves(InnerGameServerType.SERVER_TYPE_GAME);
	}
}
