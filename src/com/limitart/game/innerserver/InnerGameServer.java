package com.limitart.game.innerserver;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.game.innerserver.constant.InnerGameServerType;
import com.limitart.net.binary.distributed.InnerSlaveServer;
import com.limitart.net.binary.distributed.message.InnerServerInfo;
import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import com.limitart.net.define.IServer;

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
			MessageFactory factory, String publicIp, int publicPort)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, MessageIDDuplicatedException {
		toPublic = new InnerSlaveServer("To-Public", serverId, gameServerIp, gameServerPort, 0, gameServerPass, factory,
				publicIp, publicPort) {

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
				// 有战斗服加入，主动去连接
				if (info.serverType == InnerGameServerType.SERVER_TYPE_FIGHT) {
					if (toFights.containsKey(info.serverId)) {
						return;
					}
					try {
						InnerSlaveServer client = new InnerSlaveServer("To-Fight", serverId, gameServerIp,
								gameServerPort, 0, gameServerPass, factory, info.outIp, info.innerPort) {

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
							protected void onConnectMasterSuccess() {

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
			protected void onConnectMasterSuccess() {
				onConnectPublic();
			}
		};
	}

	protected abstract int getGameServerLoad();

	protected abstract void onConnectPublic();

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
