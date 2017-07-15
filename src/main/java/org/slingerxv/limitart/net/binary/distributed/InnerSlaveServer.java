package org.slingerxv.limitart.net.binary.distributed;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.net.binary.client.BinaryClient;
import org.slingerxv.limitart.net.binary.client.config.BinaryClientConfig;
import org.slingerxv.limitart.net.binary.client.listener.BinaryClientEventListener;
import org.slingerxv.limitart.net.binary.distributed.config.InnerSlaveServerConfig;
import org.slingerxv.limitart.net.binary.distributed.handler.ResServerJoinMaster2SlaveHandler;
import org.slingerxv.limitart.net.binary.distributed.handler.ResServerQuitMaster2SlaveHandler;
import org.slingerxv.limitart.net.binary.distributed.message.InnerServerInfo;
import org.slingerxv.limitart.net.binary.distributed.message.ReqConnectionReportSlave2MasterMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ReqServerLoadSlave2MasterMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ResServerJoinMaster2SlaveMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ResServerQuitMaster2SlaveMessage;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.net.struct.AddressPair;
import org.slingerxv.limitart.util.TimerUtil;

/**
 * 内部从服务器
 * 
 * @author Hank
 *
 */
public abstract class InnerSlaveServer implements BinaryClientEventListener, IServer {
	private static Logger log = LogManager.getLogger();
	private InnerSlaveServerConfig config;
	private BinaryClient toMaster;
	private TimerTask reportTask;

	public InnerSlaveServer(InnerSlaveServerConfig config) throws MessageIDDuplicatedException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		this.config = config;
		config.getFactory().registerMsg(new ResServerJoinMaster2SlaveHandler())
				.registerMsg(new ResServerQuitMaster2SlaveHandler());
		toMaster = new BinaryClient(
				new BinaryClientConfig.BinaryClientConfigBuilder().autoReconnect(5).clientName(config.getSlaveName())
						.connectionPass(config.getMasterInnerPass())
						.remoteAddress(new AddressPair(config.getMasterIp(), config.getMasterInnerPort())).build(),
				this, config.getFactory());
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
		toMaster.disConnect();
	}

	@Override
	public void onChannelActive(BinaryClient client) {
	}

	@Override
	public void onChannelInactive(BinaryClient client) {
		TimerUtil.unScheduleGlobal(reportTask);
		log.error(toMaster.getConfig().getClientName() + " server disconnected," + client.channel());
	}

	@Override
	public void onExceptionCaught(BinaryClient client, Throwable cause) {
		log.error("session:" + client.channel(), cause);
	}

	@Override
	public void onConnectionEffective(BinaryClient client) {
		// 链接成功,上报本服务器的服务端口等信息
		ReqConnectionReportSlave2MasterMessage msg = new ReqConnectionReportSlave2MasterMessage();
		InnerServerInfo info = new InnerServerInfo();
		info.innerPort = config.getMyInnerServerPort();
		info.outIp = config.getMyIp();
		info.outPass = config.getMyServerPass();
		info.outPort = config.getMyServerPort();
		info.serverId = config.getMyServerId();
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
			toMaster.sendMessage(slm, (isSuccess, cause, channel) -> {
				if (isSuccess) {
					log.debug("send server load to master {} success,current load:{}", channel, slm.load);
				} else {
					log.error("send server load to master {} fail,current load:{}", channel, slm.load);
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

	public InnerSlaveServerConfig getConfig() {
		return config;
	}

	public abstract int serverType();

	public abstract int serverLoad();

	public abstract void onNewSlaveJoin(InnerServerInfo info);

	public abstract void onNewSlaveQuit(int serverType, int serverId);

	protected abstract void onConnectMasterSuccess(InnerSlaveServer slave);
}
