package com.limitart.net.binary.distributed;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.net.binary.client.BinaryClient;
import com.limitart.net.binary.client.config.BinaryClientConfig;
import com.limitart.net.binary.client.listener.BinaryClientEventListener;
import com.limitart.net.binary.distributed.handler.ResServerJoinMaster2SlaveHandler;
import com.limitart.net.binary.distributed.handler.ResServerQuitMaster2SlaveHandler;
import com.limitart.net.binary.distributed.message.InnerServerInfo;
import com.limitart.net.binary.distributed.message.ReqConnectionReportSlave2MasterMessage;
import com.limitart.net.binary.distributed.message.ReqServerLoadSlave2MasterMessage;
import com.limitart.net.binary.distributed.message.ResServerJoinMaster2SlaveMessage;
import com.limitart.net.binary.distributed.message.ResServerQuitMaster2SlaveMessage;
import com.limitart.net.binary.distributed.util.InnerServerUtil;
import com.limitart.net.binary.listener.SendMessageListener;
import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import com.limitart.net.define.IServer;

import io.netty.channel.Channel;

/**
 * 内部从服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerSlaveServer implements BinaryClientEventListener, IServer {
	private static Logger log = LogManager.getLogger();
	protected int serverId;
	private String outIp;
	private int outPort;
	private int innerPort;
	private String outPass;
	private BinaryClient toMaster;

	public InnerSlaveServer(String serverName, int serverId, String outIp, int outPort, int innerPort, String outPass,
			MessageFactory factory, String innerMasterIp, int innerMasterPort) throws MessageIDDuplicatedException,
			InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		this.serverId = serverId;
		this.outIp = outIp;
		this.outPort = outPort;
		this.innerPort = innerPort;
		this.outPass = outPass;
		factory.registerMsg(new ResServerJoinMaster2SlaveHandler());
		factory.registerMsg(new ResServerQuitMaster2SlaveHandler());
		toMaster = new BinaryClient(new BinaryClientConfig.BinaryClientConfigBuilder().autoReconnect(5)
				.clientName(serverName + "-ToMaster").connectionPass(InnerServerUtil.getInnerPass())
				.remoteIp(innerMasterIp).remotePort(innerMasterPort).build(), this, factory);
	}

	@Override
	public void startServer() {
		toMaster.connect();
	}

	@Override
	public void stopServer() {
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
		log.error(toMaster.getConfig().getClientName() + " server disconnected," + client.channel());
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
			client.sendMessage(msg, (isSuccess, cause, channel) -> {
				if (isSuccess) {
					log.info("report my server info to " + client.getConfig().getClientName() + " success:" + info);
				} else {
					log.error("report my server info to master " + client.getConfig().getClientName() + " fail:" + info,
							cause);
				}
			});
		} catch (Exception e) {
			log.error(e, e);
		}
		toMaster.schedule(() -> {
			ReqServerLoadSlave2MasterMessage slm = new ReqServerLoadSlave2MasterMessage();
			slm.load = serverLoad();
			try {
				toMaster.sendMessage(slm, new SendMessageListener() {

					@Override
					public void onComplete(boolean isSuccess, Throwable cause, Channel channel) {
						if (isSuccess) {
							log.debug("send server load to master {} success,current load:{}", client.channel(),
									slm.load);
						} else {
							log.error("send server load to master {} fail,current load:{}", client.channel(), slm.load);
						}
					}
				});
			} catch (Exception e) {
				log.error(e, e);
			}
		}, 5, 5, TimeUnit.SECONDS);
		onConnectMasterSuccess();
	}

	@Override
	public void dispatchMessage(Message message) {
		message.setExtra(this);
		message.getHandler().handle(message);
	}

	public void ResServerJoinMaster2Slave(ResServerJoinMaster2SlaveMessage msg) {
		for (InnerServerInfo info : msg.infos) {
			onNewSlaveJoin(info);
		}
	}

	public void ResServerQuitMaster2Slave(ResServerQuitMaster2SlaveMessage msg) {
		onNewSlaveQuit(msg.serverType, msg.serverId);
	}

	public BinaryClient getMasterClient() {
		return this.toMaster;
	}

	public String getOutIp() {
		return outIp;
	}

	public int getOutPort() {
		return outPort;
	}

	public String getOutPass() {
		return outPass;
	}

	public abstract int serverType();

	public abstract int serverLoad();

	public abstract void onNewSlaveJoin(InnerServerInfo info);

	public abstract void onNewSlaveQuit(int serverType, int serverId);

	protected abstract void onConnectMasterSuccess();
}
