package com.limitart.net.binary.distributed;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

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
import com.limitart.net.binary.listener.SendMessageListener;
import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import com.limitart.net.define.IServer;
import com.limitart.util.TimerUtil;

import io.netty.channel.Channel;

/**
 * 内部从服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerSlaveServer implements BinaryClientEventListener, IServer {
	private static Logger log = LogManager.getLogger();
	private int myServerId;
	private String myIp;
	private int myOutServerPort;
	private String myOutServerPass;
	private int myInnerServerPort;
	private String masterIp;
	private int masterPort;
	private String masterPass;
	private BinaryClient toMaster;
	private TimerTask reportTask;

	public InnerSlaveServer(String serverName, int myServerId, String myIp, int myOutServerPort, int myInnerServerPort,
			String myOutServerPass, MessageFactory factory, String masterIp, int masterPort, String masterPass)
			throws MessageIDDuplicatedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {
		this.myServerId = myServerId;
		this.myIp = myIp;
		this.myOutServerPort = myOutServerPort;
		this.myInnerServerPort = myInnerServerPort;
		this.myOutServerPass = myOutServerPass;
		this.masterIp = masterIp;
		this.masterPort = masterPort;
		this.masterPass = masterPass;
		factory.registerMsg(new ResServerJoinMaster2SlaveHandler());
		factory.registerMsg(new ResServerQuitMaster2SlaveHandler());
		toMaster = new BinaryClient(new BinaryClientConfig.BinaryClientConfigBuilder().autoReconnect(5)
				.clientName(serverName).connectionPass(masterPass).remoteIp(masterIp).remotePort(masterPort).build(),
				this, factory);
		reportTask = new TimerTask() {

			@Override
			public void run() {
				reportLoad();
			}
		};
	}

	@Override
	public void startServer() {
		toMaster.connect();
	}

	@Override
	public void stopServer() {
		TimerUtil.unScheduleGlobal(reportTask);
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
		info.innerPort = this.myInnerServerPort;
		info.outIp = this.myIp;
		info.outPass = this.myOutServerPass;
		info.outPort = this.myOutServerPort;
		info.serverId = this.myServerId;
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
		TimerUtil.scheduleGlobal(5000, 5000, reportTask);
		onConnectMasterSuccess(this);
	}

	private void reportLoad() {
		ReqServerLoadSlave2MasterMessage slm = new ReqServerLoadSlave2MasterMessage();
		slm.load = serverLoad();
		try {
			toMaster.sendMessage(slm, new SendMessageListener() {

				@Override
				public void onComplete(boolean isSuccess, Throwable cause, Channel channel) {
					if (isSuccess) {
						log.debug("send server load to master {} success,current load:{}", channel, slm.load);
					} else {
						log.error("send server load to master {} fail,current load:{}", channel, slm.load);
					}
				}
			});
		} catch (Exception e) {
			log.error(e, e);
		}
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

	public int getMyServerId() {
		return myServerId;
	}

	public String getMyIp() {
		return myIp;
	}

	public int getMyOutServerPort() {
		return myOutServerPort;
	}

	public String getMyOutServerPass() {
		return myOutServerPass;
	}

	public int getMyInnerServerPort() {
		return myInnerServerPort;
	}

	public String getMasterIp() {
		return masterIp;
	}

	public int getMasterPort() {
		return masterPort;
	}

	public String getMasterPass() {
		return masterPass;
	}

	public abstract int serverType();

	public abstract int serverLoad();

	public abstract void onNewSlaveJoin(InnerServerInfo info);

	public abstract void onNewSlaveQuit(int serverType, int serverId);

	protected abstract void onConnectMasterSuccess(InnerSlaveServer slave);
}
