package org.slingerxv.limitart.net.binary.distributed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.net.binary.distributed.config.InnerMasterServerConfig;
import org.slingerxv.limitart.net.binary.distributed.handler.ReqConnectionReportSlave2MasterHandler;
import org.slingerxv.limitart.net.binary.distributed.handler.ReqServerLoadSlave2MasterHandler;
import org.slingerxv.limitart.net.binary.distributed.message.InnerServerInfo;
import org.slingerxv.limitart.net.binary.distributed.message.ReqConnectionReportSlave2MasterMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ReqServerLoadSlave2MasterMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ResServerJoinMaster2SlaveMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ResServerQuitMaster2SlaveMessage;
import org.slingerxv.limitart.net.binary.distributed.struct.InnerServerData;
import org.slingerxv.limitart.net.binary.distributed.util.InnerServerUtil;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.binary.server.BinaryServer;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig;
import org.slingerxv.limitart.net.binary.server.listener.BinaryServerEventListener;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.net.struct.AddressPair;

import io.netty.channel.Channel;

/**
 * 内部主服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerMasterServer implements BinaryServerEventListener, IServer {
	private static Logger log = LogManager.getLogger();
	// 从服务器集合
	private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, InnerServerData>> slaves = new ConcurrentHashMap<>();
	private BinaryServer server;
	private InnerMasterServerConfig config;

	public InnerMasterServer(InnerMasterServerConfig config) throws MessageIDDuplicatedException {
		this.config = config;
		config.getFactory().registerMsg(new ReqConnectionReportSlave2MasterHandler());
		config.getFactory().registerMsg(new ReqServerLoadSlave2MasterHandler());
		server = new BinaryServer(new BinaryServerConfig.BinaryServerConfigBuilder()
				.addressPair(new AddressPair(config.getMasterPort(), InnerServerUtil.getInnerPass()))
				.serverName(config.getServerName()).build(), this, config.getFactory());
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
		Map<Integer, InnerServerData> servers = slaves.get(serverType);
		if (servers == null) {
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

	@Override
	public void startServer() {
		server.startServer();
	}

	@Override
	public void stopServer() {
		server.stopServer();
	}

	@Override
	public void onChannelActive(Channel channel) {

	}

	@Override
	public void onChannelInactive(Channel channel) {
		Integer serverType = InnerServerUtil.getServerType(channel);
		Integer serverId = InnerServerUtil.getServerId(channel);
		if (serverType != null && serverId != null) {
			ConcurrentHashMap<Integer, InnerServerData> concurrentHashMap = slaves.get(serverType);
			if (concurrentHashMap != null) {
				InnerServerData remove = concurrentHashMap.remove(serverId);
				if (remove != null) {
					log.info("slave server disconnected,type:" + serverType + ",serverId:" + serverId);
					ResServerQuitMaster2SlaveMessage msg = new ResServerQuitMaster2SlaveMessage();
					msg.serverId = remove.getServerId();
					msg.serverType = remove.getServerType();
					List<Channel> channelList = new ArrayList<>();
					for (ConcurrentHashMap<Integer, InnerServerData> dats : slaves.values()) {
						for (InnerServerData data : dats.values()) {
							channelList.add(data.getChannel());
						}
					}
					try {
						server.sendMessage(channelList, msg, null);
					} catch (Exception e) {
						log.error(e, e);
					}
					onSlaveDisconnected(remove);
				}
			}
		}
	}

	@Override
	public void onExceptionCaught(Channel channel, Throwable cause) {
		log.error("session:" + channel, cause);
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

	protected abstract void onSlaveConnected(InnerServerData data);

	protected abstract void onSlaveDisconnected(InnerServerData data);

	/**
	 * 收到SlaveServer的上报
	 * 
	 * @param msg
	 */
	public synchronized void reqConnectionReportSlave2Master(ReqConnectionReportSlave2MasterMessage msg) {
		InnerServerInfo serverInfo = msg.serverInfo;
		InnerServerData data = new InnerServerData();
		data.setChannel(msg.getChannel());
		data.setServerType(serverInfo.serverType);
		data.setInnerPort(serverInfo.innerPort);
		data.setOutIp(serverInfo.outIp);
		data.setOutPass(serverInfo.outPass);
		data.setOutPort(serverInfo.outPort);
		data.setServerId(serverInfo.serverId);
		data.setServerLoad(0);
		ConcurrentHashMap<Integer, InnerServerData> concurrentHashMap = slaves.get(serverInfo.serverType);
		if (concurrentHashMap == null) {
			concurrentHashMap = new ConcurrentHashMap<>();
			ConcurrentHashMap<Integer, InnerServerData> putIfAbsent = slaves.putIfAbsent(serverInfo.serverType,
					concurrentHashMap);
			if (putIfAbsent != null) {
				concurrentHashMap = putIfAbsent;
			}

		}
		if (concurrentHashMap.containsKey(serverInfo.serverId)) {
			log.error("slave server " + msg.getChannel() + " id duplicated:" + serverInfo.serverId);
			return;
		}
		InnerServerUtil.setServerType(msg.getChannel(), serverInfo.serverType);
		InnerServerUtil.setServerId(msg.getChannel(), serverInfo.serverId);
		log.info("slave server " + msg.getChannel() + " connected,current type size:" + concurrentHashMap.size()
				+ ",server:" + data);
		// 告知所有服新服信息
		ResServerJoinMaster2SlaveMessage sjm0 = new ResServerJoinMaster2SlaveMessage();
		sjm0.infos.add(serverData2ServerInfo(data));
		List<Channel> channelList = new ArrayList<>();
		for (ConcurrentHashMap<Integer, InnerServerData> map : slaves.values()) {
			for (InnerServerData temp : map.values()) {
				channelList.add(temp.getChannel());
			}
		}
		if (!channelList.isEmpty()) {
			try {
				server.sendMessage(channelList, sjm0, (isSuccess, cause, channel) -> {
					if (isSuccess) {
						log.info(server.getConfig().getServerName() + " tell new slave server info " + msg.getChannel()
								+ " to other slave servers success,slave server Id:" + data.getServerId());
					} else {
						log.error(
								server.getConfig().getServerName() + " tell new slave server info " + msg.getChannel()
										+ " to other slave servers fail,new slave server Id:" + data.getServerId(),
								cause);
					}
				});
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		// 告诉新服所有服信息
		ResServerJoinMaster2SlaveMessage sjm = new ResServerJoinMaster2SlaveMessage();
		for (ConcurrentHashMap<Integer, InnerServerData> map : slaves.values()) {
			for (InnerServerData temp : map.values()) {
				InnerServerInfo info = serverData2ServerInfo(temp);
				sjm.infos.add(info);
			}
		}
		if (!sjm.infos.isEmpty()) {
			try {
				server.sendMessage(msg.getChannel(), sjm, (isSuccess, cause, channel) -> {
					if (isSuccess) {
						log.info(server.getConfig().getServerName()
								+ " tell other slave servers info to new slave server " + msg.getChannel()
								+ " success,slave server Id:" + data.getServerId());
					} else {
						log.error(server.getConfig().getServerName()
								+ " tell other slave servers info to new slave server " + msg.getChannel()
								+ " fail,new slave server Id:" + data.getServerId(), cause);
					}
				});
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		concurrentHashMap.put(serverInfo.serverId, data);
		onSlaveConnected(data);
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
		ConcurrentHashMap<Integer, InnerServerData> map = slaves.get(serverType);
		if (map == null) {
			log.error("server type :" + serverType + ",has not servers!");
			return;
		}
		InnerServerData serverData = map.get(serverId);
		if (serverData == null) {
			log.error("can not find server info,server id:" + serverId + ",server type:" + serverType);
			return;
		}
		serverData.setServerLoad(msg.load);
		log.debug("receive slave server,server id:{} report load:{}", serverId, msg.load);
	}

	public List<InnerServerData> getSlaves(int serverType) {
		List<InnerServerData> result = new ArrayList<>();
		ConcurrentHashMap<Integer, InnerServerData> concurrentHashMap = slaves.get(serverType);
		if (concurrentHashMap == null) {
			return result;
		}
		result.addAll(concurrentHashMap.values());
		return result;
	}

	public InnerServerData getSlave(int serverType, int serverId) {
		ConcurrentHashMap<Integer, InnerServerData> concurrentHashMap = slaves.get(serverType);
		if (concurrentHashMap == null) {
			return null;
		}
		return concurrentHashMap.get(serverId);
	}

	public InnerMasterServerConfig getConfig() {
		return config;
	}

	private InnerServerInfo serverData2ServerInfo(InnerServerData data) {
		InnerServerInfo info = new InnerServerInfo();
		info.innerPort = data.getInnerPort();
		info.outIp = data.getOutIp();
		info.outPass = data.getOutPass();
		info.outPort = data.getOutPort();
		info.serverId = data.getServerId();
		info.serverType = data.getServerType();
		return info;
	}
}
