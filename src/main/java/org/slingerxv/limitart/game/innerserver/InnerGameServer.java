/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.game.innerserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.game.innerserver.config.InnerGameServerConfig;
import org.slingerxv.limitart.game.innerserver.constant.InnerGameServerType;
import org.slingerxv.limitart.net.binary.distributed.InnerSlaveServer;
import org.slingerxv.limitart.net.binary.distributed.util.InnerServerUtil;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.util.Beta;

/**
 * 内部游戏服务器
 * 
 * @author Hank
 *
 */
@Beta
public abstract class InnerGameServer implements IServer {
	private static Logger log = LoggerFactory.getLogger(InnerGameServer.class);
	private Map<Integer, InnerSlaveServer> toFights = new ConcurrentHashMap<>();
	private InnerSlaveServer toPublic;

	public InnerGameServer(InnerGameServerConfig config) throws Exception {
		toPublic = new InnerSlaveServer.InnerSlaveServerBuilder().slaveName("Game-To-Public")
				.myServerId(config.getServerId()).myServerIp(config.getGameServerIp())
				.myServerPort(config.getGameServerPort()).myServerPass(config.getGameServerPass())
				.masterIp(config.getPublicIp()).masterInnerPort(config.getPublicPort())
				.masterInnerPass(InnerServerUtil.getInnerPass()).facotry(config.getFactory())
				.slaveType(InnerGameServerType.SERVER_TYPE_GAME).serverLoad(() -> {
					return getGameServerLoad();
				}).onNewSlaveQuit((serverType, serverId) -> {
					if (serverType == InnerGameServerType.SERVER_TYPE_FIGHT) {
						// InnerSlaveServer innerSlaveServer = toFights.remove(serverId);
						// if (innerSlaveServer != null) {
						// innerSlaveServer.stopServer();
						// }
					}
				}).onNewSlaveJoin((info) -> {
					// 有战斗服加入，主动去连接
					if (info.serverType == InnerGameServerType.SERVER_TYPE_FIGHT) {
						if (toFights.containsKey(info.serverId)) {
							return;
						}
						try {

							InnerSlaveServer client = new InnerSlaveServer.InnerSlaveServerBuilder()
									.slaveName("Game-To-Fight").myServerId(config.getServerId())
									.myServerIp(config.getGameServerIp()).myServerPort(config.getGameServerPort())
									.myServerPass(config.getGameServerPass()).masterIp(info.outIp)
									.masterServerPort(info.outPort).masterServerPass(info.outPass)
									.masterInnerPort(info.innerPort).masterInnerPass(InnerServerUtil.getInnerPass())
									.facotry(config.getFactory()).slaveType(InnerGameServerType.SERVER_TYPE_GAME)
									.serverLoad(() -> {
										return getGameServerLoad();
									}).onConnectMasterSuccess((slave) -> {
										InnerServerUtil.setServerType(slave.getMasterClient().channel(),
												InnerGameServerType.SERVER_TYPE_FIGHT);
										InnerServerUtil.setServerId(slave.getMasterClient().channel(), info.serverId);
									}).build();
							toFights.put(info.serverId, client);
							client.startServer();
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}).onConnectMasterSuccess((slave) -> {
					onConnectPublic(slave);
				}).build();

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
