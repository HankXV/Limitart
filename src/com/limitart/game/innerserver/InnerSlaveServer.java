package com.limitart.game.innerserver;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.game.innerserver.handler.ReqConnectionReportGame2FightHandler;
import com.limitart.game.innerserver.handler.ResFightServerJoinMaster2GameHandler;
import com.limitart.game.innerserver.handler.ResFightServerQuitMaster2GameHandler;
import com.limitart.game.innerserver.msg.InnerServerInfo;
import com.limitart.game.innerserver.msg.ReqConnectionReportGame2FightMessage;
import com.limitart.game.innerserver.msg.ReqConnectionReportSlave2MasterMessage;
import com.limitart.game.innerserver.msg.ReqServerLoadSlave2MasterMessage;
import com.limitart.game.innerserver.msg.ResFightServerJoinMaster2GameMessage;
import com.limitart.game.innerserver.msg.ResFightServerQuitMaster2GameMessage;
import com.limitart.game.innerserver.util.InnerServerUtil;
import com.limitart.net.binary.client.BinaryClient;
import com.limitart.net.binary.client.config.BinaryClientConfig;
import com.limitart.net.binary.client.listener.BinaryClientEventListener;
import com.limitart.net.binary.listener.SendMessageListener;
import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.util.SendMessageUtil;

import io.netty.channel.Channel;

/**
 * 内部从服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerSlaveServer implements BinaryClientEventListener {
	private static Logger log = LogManager.getLogger();
	protected int serverId;
	private String outIp;
	private int outPort;
	private int innerPort;
	private String outPass;
	private BinaryClient toMaster;
	protected MessageFactory factory;

	public InnerSlaveServer(int serverId, String outIp, int outPort, int innerPort, String outPass,
			MessageFactory factory, String innerMasterIp, int innerMasterPort) throws Exception {
		this.serverId = serverId;
		this.outIp = outIp;
		this.outPort = outPort;
		this.innerPort = innerPort;
		this.outPass = outPass;
		this.factory = factory;
		this.factory.registerMsg(InnerMessageEnum.ResFightServerJoinMaster2GameMessage.getValue(),
				ResFightServerJoinMaster2GameMessage.class, new ResFightServerJoinMaster2GameHandler());
		this.factory.registerMsg(InnerMessageEnum.ResFightServerQuitMaster2GameMessage.getValue(),
				ResFightServerQuitMaster2GameMessage.class, ResFightServerQuitMaster2GameHandler.class);
		this.factory.registerMsg(InnerMessageEnum.ReqConnectionReportGame2FightMessage.getValue(),
				ReqConnectionReportGame2FightMessage.class, new ReqConnectionReportGame2FightHandler());
		toMaster = new BinaryClient(new BinaryClientConfig.BinaryClientConfigBuilder().autoReconnect(5)
				.clientName("Slave-Inner-Client").connectionPass(InnerServerUtil.getInnerPass()).remoteIp(innerMasterIp)
				.remotePort(innerMasterPort).build(), this, factory);
	}

	protected void connectMaster() {
		toMaster.connect();
	}

	protected void disconnectMaster() {
		toMaster.disConnect();
	}

	@Override
	public void onChannelActive(BinaryClient client) {

	}

	@Override
	public void onChannelInactive(BinaryClient client) {

	}

	@Override
	public void onExceptionCaught(BinaryClient client, Throwable cause) {
		log.error("session:" + client.channel(), cause);
	}

	@Override
	public void onChannelRegistered(BinaryClient client) {

	}

	@Override
	public void onChannelUnregistered(BinaryClient client) {
		log.error("master server disconnected," + client.channel());
	}

	@Override
	public void onConnectionEffective(BinaryClient client) {
		// 链接成功,上报本服务器的服务端口等信息
		ReqConnectionReportSlave2MasterMessage msg = new ReqConnectionReportSlave2MasterMessage();
		InnerServerInfo info = new InnerServerInfo();
		info.innerPort = this.innerPort;
		info.outIp = this.outIp;
		info.outPass = this.outPass;
		info.outPort = this.outPort;
		info.serverId = this.serverId;
		info.serverType = serverType();
		msg.serverInfo = info;
		try {
			SendMessageUtil.sendMessage(client.channel(), msg, new SendMessageListener() {

				@Override
				public void onComplete(boolean isSuccess, Throwable cause, Channel channel) {
					if (isSuccess) {
						log.info("report my server info to master success:" + info);
					} else {
						log.error("report my server info to master fail:" + info, cause);
					}
				}
			});
		} catch (Exception e) {
			log.error(e, e);
		}
		toMaster.schedule(new Runnable() {

			@Override
			public void run() {
				ReqServerLoadSlave2MasterMessage slm = new ReqServerLoadSlave2MasterMessage();
				slm.load = serverLoad();
				try {
					SendMessageUtil.sendMessage(toMaster.channel(), slm, new SendMessageListener() {

						@Override
						public void onComplete(boolean isSuccess, Throwable cause, Channel channel) {
							if (isSuccess) {
								log.debug("send server load to master success,current load:{}", slm.load);
							} else {
								log.error("send server load to master fail,current load:{}", slm.load);
							}
						}
					});
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}, 5, 5, TimeUnit.SECONDS);
	}

	@Override
	public void dispatchMessage(Message message) {
		message.setExtra(this);
		message.getHandler().handle(message);
	}

	public BinaryClient getMasterClient() {
		return this.toMaster;
	}

	public abstract int serverType();

	public abstract int serverLoad();
}
