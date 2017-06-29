package com.limitart.game.innerserver;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.game.innerserver.msg.ReqConnectionReportGame2FightMessage;
import com.limitart.game.innerserver.struct.InnerServerData;
import com.limitart.game.innerserver.util.InnerServerUtil;
import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.server.BinaryServer;
import com.limitart.net.binary.server.config.BinaryServerConfig;
import com.limitart.net.binary.server.listener.BinaryServerEventListener;

import io.netty.channel.Channel;

/**
 * 内部战斗服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerFightServer extends InnerSlaveServer {
	private static Logger log = LogManager.getLogger();
	private BinaryServer server;
	private ConcurrentHashMap<Integer, InnerServerData> gameServers = new ConcurrentHashMap<>();

	public InnerFightServer(int serverId, String outIp, int outPort, int innerPort, String outPass,
			MessageFactory factory, String innerMasterIp, int innerMasterPort) throws Exception {
		super(serverId, outIp, outPort, innerPort, outPass, factory, innerMasterIp, innerMasterPort);
		server = new BinaryServer(
				new BinaryServerConfig.BinaryServerConfigBuilder().connectionPass(InnerServerUtil.getInnerPass())
						.port(innerPort).serverName("Fight-Inner-Server").build(),
				new BinaryServerEventListener() {

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
						Integer serverId = InnerServerUtil.getServerId(channel);
						if (serverId != null) {
							InnerServerData remove = gameServers.remove(serverId);
							if (remove != null) {
								log.info("game server disconnected,game server id:" + serverId);
							}
						}
					}

					@Override
					public void onServerBind(Channel channel) {
						connectMaster();
					}

					@Override
					public void onConnectionEffective(Channel channel) {

					}

					@Override
					public void dispatchMessage(Message message) {
						message.setExtra(InnerFightServer.this);
						message.getHandler().handle(message);
					}
				}, factory);
	}

	public synchronized void start() {
		log.info("inner fight server start...");
		server.bind();
	}

	public synchronized void stop() {
		log.info("inner fight server stop...");
		disconnectMaster();
		server.stop();
	}

	@Override
	public int serverType() {
		return InnerServerUtil.SERVER_TYPE_FIGHT;
	}

	public synchronized void reqConnectionReportGame2Fight(ReqConnectionReportGame2FightMessage msg) {
		if (gameServers.containsKey(msg.serverId)) {
			log.error("game server duplicated:" + msg.serverId);
			return;
		}
		InnerServerUtil.setServerType(msg.getChannel(), InnerServerUtil.SERVER_TYPE_GAME);
		InnerServerUtil.setServerId(msg.getChannel(), msg.serverId);
		InnerServerData data = new InnerServerData();
		data.setChannel(msg.getChannel());
		data.setServerId(msg.serverId);
		gameServers.put(msg.serverId, data);
		log.info("game server connected,game server id:" + msg.serverId);
	}

	public ConcurrentHashMap<Integer, InnerServerData> getGameServers() {
		return this.gameServers;
	}
}
