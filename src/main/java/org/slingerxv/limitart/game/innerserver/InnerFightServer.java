package org.slingerxv.limitart.game.innerserver;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.game.innerserver.config.InnerFightServerConfig;
import org.slingerxv.limitart.game.innerserver.constant.InnerGameServerType;
import org.slingerxv.limitart.net.binary.distributed.InnerMasterServer;
import org.slingerxv.limitart.net.binary.distributed.InnerSlaveServer;
import org.slingerxv.limitart.net.binary.distributed.struct.InnerServerData;
import org.slingerxv.limitart.net.binary.distributed.util.InnerServerUtil;
import org.slingerxv.limitart.net.define.IServer;

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

	public InnerFightServer(InnerFightServerConfig config) throws Exception {
		server = new InnerMasterServer.InnerMasterServerBuilder().serverName("Fight")
				.masterPort(config.getFightServerInnerPort()).factory(config.getFactory())
				.onConnectionChanged((data, isConnected) -> {
					if (isConnected) {
						if (data.getServerType() == InnerGameServerType.SERVER_TYPE_GAME) {
							log.info("game server connected:" + data.getServerId() + ",cur size:"
									+ server.getSlaves(InnerGameServerType.SERVER_TYPE_GAME).size());
						} else {
							log.error("server type :" + data.getServerType() + " connected!!!!!!!check!!!!");
						}
					}
				}).build();

		toMaster = new InnerSlaveServer.InnerSlaveServerBuilder().slaveName("Fight-To-Public")
				.slaveType(InnerGameServerType.SERVER_TYPE_FIGHT).myServerId(config.getServerId())
				.myServerIp(config.getFightServerIp()).myServerPort(config.getFightServerPort())
				.myServerPass(config.getFightServerPass()).masterServerPort(config.getFightServerPort())
				.myInnerServerPort(config.getFightServerInnerPort()).myInnerServerPass(InnerServerUtil.getInnerPass())
				.masterIp(config.getPublicIp()).masterInnerPort(config.getPublicPort())
				.masterInnerPass(InnerServerUtil.getInnerPass()).facotry(config.getFactory()).serverLoad(() -> {
					return getFightServerLoad();
				}).onConnectMasterSuccess((slave) -> {
					server.startServer();
					onConnectPublic(slave);
				}).build();

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
