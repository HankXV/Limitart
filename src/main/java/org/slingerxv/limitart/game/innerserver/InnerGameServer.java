package org.slingerxv.limitart.game.innerserver;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;

import javax.crypto.NoSuchPaddingException;

import org.slingerxv.limitart.game.innerserver.constant.InnerGameServerType;
import org.slingerxv.limitart.net.binary.distributed.InnerSlaveServer;
import org.slingerxv.limitart.net.binary.distributed.message.InnerServerInfo;
import org.slingerxv.limitart.net.binary.distributed.util.InnerServerUtil;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.define.IServer;

/**
 * 内部游戏服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerGameServer implements IServer {
	private static Logger log = LogManager.getLogger();
	private ConcurrentHashMap<Integer, InnerSlaveServer> toFights = new ConcurrentHashMap<>();
	private InnerSlaveServer toPublic;

	public InnerGameServer(int serverId, String gameServerIp, int gameServerPort, String gameServerPass,
			String publicIp, int publicPort, MessageFactory factory)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, MessageIDDuplicatedException {
		toPublic = new InnerSlaveServer("Game-To-Public", serverId, gameServerIp, gameServerPort, gameServerPass, 0, "",
				publicIp, 0, "", publicPort, InnerServerUtil.getInnerPass(), factory) {
			@Override
			public int serverType() {
				return InnerGameServerType.SERVER_TYPE_GAME;
			}

			@Override
			public int serverLoad() {
				return getGameServerLoad();
			}

			@Override
			public void onNewSlaveQuit(int serverType, int serverId) {
				if (serverType == InnerGameServerType.SERVER_TYPE_FIGHT) {
					InnerSlaveServer innerSlaveServer = toFights.remove(serverId);
					if (innerSlaveServer != null) {
						innerSlaveServer.stopServer();
					}
				}
			}

			@Override
			public void onNewSlaveJoin(InnerServerInfo info) {
				// 有战斗服加入，主动去连接
				if (info.serverType == InnerGameServerType.SERVER_TYPE_FIGHT) {
					if (toFights.containsKey(info.serverId)) {
						return;
					}
					try {
						InnerSlaveServer client = new InnerSlaveServer("Game-To-Fight", serverId, gameServerIp,
								gameServerPort, gameServerPass, 0, "", info.outIp, info.outPort, info.outPass,
								info.innerPort, InnerServerUtil.getInnerPass(), factory) {

							@Override
							public int serverType() {
								return InnerGameServerType.SERVER_TYPE_GAME;
							}

							@Override
							public int serverLoad() {
								return getGameServerLoad();
							}

							@Override
							public void onNewSlaveQuit(int serverType, int serverId) {

							}

							@Override
							public void onNewSlaveJoin(InnerServerInfo info) {

							}

							@Override
							protected void onConnectMasterSuccess(InnerSlaveServer slave) {
								InnerServerUtil.setServerType(slave.getMasterClient().channel(),
										InnerGameServerType.SERVER_TYPE_FIGHT);
								InnerServerUtil.setServerId(slave.getMasterClient().channel(), info.serverId);
							}
						};
						toFights.put(info.serverId, client);
						client.startServer();
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
							| InvalidAlgorithmParameterException | MessageIDDuplicatedException e) {
						log.error(e, e);
					}
				}
			}

			@Override
			protected void onConnectMasterSuccess(InnerSlaveServer slave) {
				onConnectPublic(slave);
			}
		};
	}

	protected abstract int getGameServerLoad();

	protected abstract void onConnectPublic(InnerSlaveServer slave);

	@Override
	public synchronized void startServer() {
		toPublic.startServer();
	}

	@Override
	public synchronized void stopServer() {
		toPublic.stopServer();
	}

	public InnerSlaveServer getPublicClient() {
		return this.toPublic;
	}

	public InnerSlaveServer getFightClient(int serverId) {
		return toFights.get(serverId);
	}

	public List<InnerSlaveServer> getFightClients() {
		return new ArrayList<>(toFights.values());
	}
}
