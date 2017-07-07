package com.limitart.game.innerserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.game.innerserver.handler.ReqConnectionReportSlave2MasterHandler;
import com.limitart.game.innerserver.handler.ReqServerLoadSlave2MasterHandler;
import com.limitart.game.innerserver.msg.InnerServerInfo;
import com.limitart.game.innerserver.msg.ReqConnectionReportSlave2MasterMessage;
import com.limitart.game.innerserver.msg.ReqServerLoadSlave2MasterMessage;
import com.limitart.game.innerserver.msg.ResFightServerJoinMaster2GameMessage;
import com.limitart.game.innerserver.struct.InnerServerData;
import com.limitart.game.innerserver.util.InnerServerUtil;
import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.server.BinaryServer;
import com.limitart.net.binary.server.config.BinaryServerConfig;
import com.limitart.net.binary.server.listener.BinaryServerEventListener;

import io.netty.channel.Channel;

/**
 * 内部主服务器
 * 
 * @author Hank
 *
 */
public class InnerMasterServer implements BinaryServerEventListener {
	private static Logger log = LogManager.getLogger();
	// 游戏服务器管理
	private ConcurrentHashMap<Integer, InnerServerData> gameServers = new ConcurrentHashMap<>();
	// 战斗服务器管理
	private ConcurrentHashMap<Integer, InnerServerData> fightServers = new ConcurrentHashMap<>();
	private BinaryServer server;

	public InnerMasterServer(int masterPort, MessageFactory facotry) {
		facotry.registerMsg(ReqConnectionReportSlave2MasterMessage.class, new ReqConnectionReportSlave2MasterHandler());
		facotry.registerMsg(ReqServerLoadSlave2MasterMessage.class, new ReqServerLoadSlave2MasterHandler());
		server = new BinaryServer(
				new BinaryServerConfig.BinaryServerConfigBuilder().connectionPass(InnerServerUtil.getInnerPass())
						.port(masterPort).serverName("Master-Inner-Server").build(),
				this, facotry);
	}

	/**
	 * 选出负载最小的服务器
	 * 
	 * @param serverType
	 * @return
	 */
	public InnerServerData findLowestServer(int serverType) {
		InnerServerData currentServer = null;
		int currentMinLoad = Integer.MAX_VALUE;
		Map<Integer, InnerServerData> servers = null;
		if (serverType == InnerServerUtil.SERVER_TYPE_GAME) {
			servers = gameServers;
		} else if (serverType == InnerServerUtil.SERVER_TYPE_FIGHT) {
			servers = fightServers;
		} else {
			return null;
		}
		for (InnerServerData data : servers.values()) {
			if (currentMinLoad > data.getServerLoad()) {
				currentMinLoad = data.getServerLoad();
				currentServer = data;
			}
		}
		return currentServer;
	}

	public synchronized void start() {
		log.info("inner master server start...");
		server.bind();
	}

	public synchronized void stop() {
		log.info("inner master server stop...");
		server.stop();
	}

	@Override
	public void onChannelActive(Channel channel) {

	}

	@Override
	public void onChannelInactive(Channel channel) {

	}

	@Override
	public void onExceptionCaught(Channel channel, Throwable cause) {
		log.error("session:" + channel, cause);
	}

	@Override
	public void onChannelRegistered(Channel channel) {

	}

	@Override
	public void onChannelUnregistered(Channel channel) {
		Integer serverType = InnerServerUtil.getServerType(channel);
		Integer serverId = InnerServerUtil.getServerId(channel);
		if (serverType != null && serverId != null) {
			if (serverType == InnerServerUtil.SERVER_TYPE_GAME) {
				InnerServerData remove = gameServers.remove(serverId);
				if (remove != null) {
					log.info("game server disconnected:" + remove);
				}
			} else if (serverType == InnerServerUtil.SERVER_TYPE_FIGHT) {
				InnerServerData remove = fightServers.remove(serverId);
				if (remove != null) {
					log.info("fight server disconnected:" + remove);
				}
			}
		}
	}

	@Override
	public void onServerBind(Channel channel) {

	}

	@Override
	public void onConnectionEffective(Channel channel) {

	}

	@Override
	public void dispatchMessage(Message message) {
		message.setExtra(this);
		message.getHandler().handle(message);
	}

	/**
	 * 收到SlaveServer的上报
	 * 
	 * @param msg
	 */
	public synchronized void reqConnectionReportSlave2Master(ReqConnectionReportSlave2MasterMessage msg) {
		InnerServerInfo serverInfo = msg.serverInfo;
		InnerServerData data = new InnerServerData();
		data.setChannel(msg.getChannel());
		data.setInnerPort(serverInfo.innerPort);
		data.setOutIp(serverInfo.outIp);
		data.setOutPass(serverInfo.outPass);
		data.setOutPort(serverInfo.outPort);
		data.setServerId(serverInfo.serverId);
		data.setServerLoad(0);
		if (serverInfo.serverType == InnerServerUtil.SERVER_TYPE_GAME) {
			if (gameServers.containsKey(serverInfo.serverId)) {
				log.error("game server id duplicated:" + serverInfo.serverId);
				return;
			}
			InnerServerUtil.setServerType(msg.getChannel(), serverInfo.serverType);
			InnerServerUtil.setServerId(msg.getChannel(), serverInfo.serverId);
			gameServers.put(serverInfo.serverId, data);
			log.info("game server connected,current size:" + gameServers.size() + ",server:" + data);
			// 告知所有战斗服服信息
			ResFightServerJoinMaster2GameMessage fsjm = new ResFightServerJoinMaster2GameMessage();
			for (InnerServerData temp : fightServers.values()) {
				InnerServerInfo info = serverData2ServerInfo(InnerServerUtil.SERVER_TYPE_FIGHT, temp);
				fsjm.serverInfo.add(info);
			}
			try {
				server.sendMessage(msg.getChannel(), fsjm, (isSuccess, cause, channel) -> {
                    if (isSuccess) {
                        log.info("tell fight servers info to game server success,game server Id:"
                                + data.getServerId());
                    } else {
                        log.error(
                                "tell fight servers info to game server fail,game server Id:" + data.getServerId(),
                                cause);
                    }
                });
			} catch (Exception e) {
				log.error(e, e);
			}
		} else if (serverInfo.serverType == InnerServerUtil.SERVER_TYPE_FIGHT) {
			if (fightServers.containsKey(serverInfo.serverId)) {
				log.error("fight server id duplicated:" + serverInfo.serverId);
				return;
			}
			InnerServerUtil.setServerType(msg.getChannel(), serverInfo.serverType);
			InnerServerUtil.setServerId(msg.getChannel(), serverInfo.serverId);
			fightServers.put(serverInfo.serverId, data);
			log.info("fight server connected,current size:" + fightServers.size() + ",server:" + data);
			// 通知所有游戏服去连接
			ResFightServerJoinMaster2GameMessage fsjm = new ResFightServerJoinMaster2GameMessage();
			InnerServerInfo info = serverData2ServerInfo(InnerServerUtil.SERVER_TYPE_FIGHT, data);
			fsjm.serverInfo.add(info);
			List<Channel> channels = new ArrayList<>();
			for (InnerServerData temp : gameServers.values()) {
				channels.add(temp.getChannel());
			}
			try {
				server.sendMessage(channels, fsjm, null);
				log.info("tell new fight server info to all game server success,fight server id:" + data.getServerId());
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	/**
	 * 收到服务器上报负载信息
	 * 
	 * @param msg
	 */
	public void reqServerLoadSlave2Master(ReqServerLoadSlave2MasterMessage msg) {
		Integer serverType = InnerServerUtil.getServerType(msg.getChannel());
		Integer serverId = InnerServerUtil.getServerId(msg.getChannel());
		if (serverType == null || serverId == null) {
			log.error("can not find server info:" + msg.getChannel());
			return;
		}
		if (serverType == InnerServerUtil.SERVER_TYPE_GAME) {
			InnerServerData serverData = gameServers.get(serverId);
			if (serverData == null) {
				log.error("can not find server info:" + serverId);
				return;
			}
			serverData.setServerLoad(msg.load);
			log.debug("receive game server,server id:{} report load:{}", serverId, msg.load);
		} else if (serverType == InnerServerUtil.SERVER_TYPE_FIGHT) {
			InnerServerData serverData = fightServers.get(serverId);
			if (serverData == null) {
				log.error("can not find server info:" + serverId);
				return;
			}
			serverData.setServerLoad(msg.load);
			log.debug("receive fight server,server id:{} report load:{}", serverId, msg.load);
		}
	}

	public ConcurrentHashMap<Integer, InnerServerData> getGameServers() {
		return this.gameServers;
	}

	public ConcurrentHashMap<Integer, InnerServerData> getFightServers() {
		return this.fightServers;
	}

	private InnerServerInfo serverData2ServerInfo(int serverType, InnerServerData data) {
		InnerServerInfo info = new InnerServerInfo();
		info.innerPort = data.getInnerPort();
		info.outIp = data.getOutIp();
		info.outPass = data.getOutPass();
		info.outPort = data.getOutPort();
		info.serverId = data.getServerId();
		info.serverType = serverType;
		return info;
	}
}
