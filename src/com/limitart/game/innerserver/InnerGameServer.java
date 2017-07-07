package com.limitart.game.innerserver;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.game.innerserver.msg.InnerServerInfo;
import com.limitart.game.innerserver.msg.ReqConnectionReportGame2FightMessage;
import com.limitart.game.innerserver.msg.ResFightServerJoinMaster2GameMessage;
import com.limitart.game.innerserver.msg.ResFightServerQuitMaster2GameMessage;
import com.limitart.game.innerserver.struct.InnerServerData;
import com.limitart.game.innerserver.util.InnerServerUtil;
import com.limitart.net.binary.client.BinaryClient;
import com.limitart.net.binary.client.config.BinaryClientConfig;
import com.limitart.net.binary.client.listener.BinaryClientEventListener;
import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.MessageFactory;

import io.netty.channel.Channel;

/**
 * 内部游戏服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerGameServer extends InnerSlaveServer {
	private static Logger log = LogManager.getLogger();
	private ConcurrentHashMap<Integer, InnerServerData> fightServers = new ConcurrentHashMap<>();

	public InnerGameServer(int serverId, String outIp, int outPort, String outPass, MessageFactory factory,
			String innerMasterIp, int innerMasterPort) throws Exception {
		super(serverId, outIp, outPort, 0, outPass, factory, innerMasterIp, innerMasterPort);
	}

	@Override
	public int serverType() {
		return InnerServerUtil.SERVER_TYPE_GAME;
	}

	public synchronized void start() {
		log.info("inner game server start...");
		connectMaster();
	}

	public synchronized void stop() {
		log.info("inner game server stop...");
		disconnectMaster();
	}

	/**
	 * master通知新的战斗服加入
	 * 
	 * @param msg
	 */
	public synchronized void resFightServerJoinMaster2Game(ResFightServerJoinMaster2GameMessage msg) {
		for (InnerServerInfo info : msg.serverInfo) {
			try {
				BinaryClient client = new BinaryClient(
						new BinaryClientConfig.BinaryClientConfigBuilder().clientName("Game-Fight-Inner-Client")
								.autoReconnect(5).connectionPass(InnerServerUtil.getInnerPass()).remoteIp(info.outIp)
								.remotePort(info.innerPort).build(),
						new BinaryClientEventListener() {

							@Override
							public void onExceptionCaught(BinaryClient client, Throwable cause) {
								log.error("session:" + client.channel(), cause);
							}

							@Override
							public void onConnectionEffective(BinaryClient client) {
								InnerServerUtil.setServerId(client.channel(), info.serverId);
								InnerServerData data = serverInfo2ServerData(client.channel(), info);
								data.setBinaryClient(client);
								fightServers.put(info.serverId, data);
								log.info("connect fight server success,fight server id:" + info.serverId);
								reportServerToFightServer(client);
							}

							@Override
							public void onChannelUnregistered(BinaryClient client) {
								Channel channel = client.channel();
								if (channel != null) {
									Integer serverId = InnerServerUtil.getServerId(channel);
									if (serverId != null) {
										InnerServerData remove = fightServers.remove(serverId);
										if (remove != null) {
											log.info("fight server disconnected,fight server id:" + serverId);
										}
									}
								}
							}

							@Override
							public void onChannelRegistered(BinaryClient client) {

							}

							@Override
							public void onChannelInactive(BinaryClient client) {

							}

							@Override
							public void onChannelActive(BinaryClient client) {

							}

							@Override
							public void dispatchMessage(Message message) {
								message.setExtra(InnerGameServer.this);
								message.getHandler().handle(message);
							}
						}, factory);
				client.connect();
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	private void reportServerToFightServer(BinaryClient client) {
		ReqConnectionReportGame2FightMessage msg = new ReqConnectionReportGame2FightMessage();
		msg.serverId = super.serverId;
		try {
			client.sendMessage(msg, (isSuccess, cause, channel) -> {
                if (isSuccess) {
                    log.info("report game server info to fight server seccess:" + msg.serverId);
                } else {
                    log.error("report game server info to fight server fail:" + msg.serverId, cause);
                }
            });
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * master通知战斗服下线
	 * 
	 * @param msg
	 */
	public void resFightServerQuitMaster2Game(ResFightServerQuitMaster2GameMessage msg) {
		int fightServerId = msg.fightServerId;
		InnerServerData innerServerData = this.fightServers.remove(fightServerId);
		if (innerServerData != null) {
			innerServerData.getBinaryClient().disConnect();
			log.info("master notice fight server quit,cancel auto connect,fight server id:" + fightServerId);
		}
	}

	public ConcurrentHashMap<Integer, InnerServerData> getFightServers() {
		return this.fightServers;
	}

	private InnerServerData serverInfo2ServerData(Channel channel, InnerServerInfo info) {
		InnerServerData data = new InnerServerData();
		data.setChannel(channel);
		data.setInnerPort(info.innerPort);
		data.setOutIp(info.outIp);
		data.setOutPass(info.outPass);
		data.setOutPort(info.outPort);
		data.setServerId(info.serverId);
		return data;
	}
}
