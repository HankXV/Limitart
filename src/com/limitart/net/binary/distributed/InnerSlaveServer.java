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
	private int myServerPort;
	private String myServerPass;
	private String myInnerServerPass;
	private int myInnerServerPort;
	private String masterIp;
	private int masterServerPort;
	private String masterServerPass;
	private int masterInnerPort;
	private String masterInnerPass;
	private BinaryClient toMaster;
	private TimerTask reportTask;

	public InnerSlaveServer(String slaveName, int myServerId, String myIp, int myServerPort, String myServerPass,
			int myInnerServerPort, String myInnerServerPass, String masterIp, int masterServerPort,
			String masterServerPass, int masterInnerPort, String masterInnerPass, MessageFactory factory)
			throws MessageIDDuplicatedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {
		this.myServerId = myServerId;
		this.myIp = myIp;
		this.myServerPort = myServerPort;
		this.myServerPass = myServerPass;
		this.myInnerServerPass = myInnerServerPass;
		this.myInnerServerPort = myInnerServerPort;
		this.masterIp = masterIp;
		this.masterServerPort = masterServerPort;
		this.masterServerPass = masterServerPass;
		this.masterInnerPort = masterInnerPort;
		this.masterInnerPass = masterInnerPass;
		factory.registerMsg(new ResServerJoinMaster2SlaveHandler());
		factory.registerMsg(new ResServerQuitMaster2SlaveHandler());
		toMaster = new BinaryClient(
				new BinaryClientConfig.BinaryClientConfigBuilder().autoReconnect(5).clientName(slaveName)
						.connectionPass(masterInnerPass).remoteIp(masterIp).remotePort(masterInnerPort).build(),
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
		info.outPass = this.myServerPass;
		info.outPort = this.myServerPort;
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

	public int getMyServerPort() {
		return myServerPort;
	}

	public String getMyServerPass() {
		return myServerPass;
	}

	public String getMyInnerServerPass() {
		return myInnerServerPass;
	}

	public int getMyInnerServerPort() {
		return myInnerServerPort;
	}

	public String getMasterIp() {
		return masterIp;
	}

	public int getMasterServerPort() {
		return masterServerPort;
	}

	public String getMasterServerPass() {
		return masterServerPass;
	}

	public int getMasterInnerPort() {
		return masterInnerPort;
	}

	public String getMasterInnerPass() {
		return masterInnerPass;
	}

	public abstract int serverType();

	public abstract int serverLoad();

	public abstract void onNewSlaveJoin(InnerServerInfo info);

	public abstract void onNewSlaveQuit(int serverType, int serverId);

	protected abstract void onConnectMasterSuccess(InnerSlaveServer slave);
}
