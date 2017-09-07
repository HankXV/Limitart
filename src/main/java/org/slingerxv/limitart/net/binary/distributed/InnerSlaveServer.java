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
package org.slingerxv.limitart.net.binary.distributed;

import java.util.Objects;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.funcs.Func;
import org.slingerxv.limitart.funcs.Funcs;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.IServer;
import org.slingerxv.limitart.net.binary.BinaryClient;
import org.slingerxv.limitart.net.binary.distributed.handler.ResServerJoinMaster2SlaveHandler;
import org.slingerxv.limitart.net.binary.distributed.handler.ResServerQuitMaster2SlaveHandler;
import org.slingerxv.limitart.net.binary.distributed.message.InnerServerInfo;
import org.slingerxv.limitart.net.binary.distributed.message.ReqConnectionReportSlave2MasterMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ReqServerLoadSlave2MasterMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ResServerJoinMaster2SlaveMessage;
import org.slingerxv.limitart.net.binary.distributed.message.ResServerQuitMaster2SlaveMessage;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.util.TimerUtil;

/**
 * 内部从服务器
 * 
 * @author Hank
 *
 */
public class InnerSlaveServer implements IServer {
	private static Logger log = LoggerFactory.getLogger(InnerSlaveServer.class);
	private BinaryClient toMaster;
	private TimerTask reportTask;
	// config---
	private String slaveName;
	private int slaveType;
	private int myServerId;
	private String myIp;
	private int myServerPort;
	private String myServerPass;
	private int myInnerServerPort;
	private String myInnerServerPass;
	private String masterIp;
	private int masterServerPort;
	private String masterServerPass;
	private int masterInnerPort;
	private String masterInnerPass;
	private MessageFactory factory;
	// listener--
	private Func<Integer> serverLoad;
	private Proc1<InnerServerInfo> onNewSlaveJoin;
	private Proc2<Integer, Integer> onNewSlaveQuit;
	private Proc1<InnerSlaveServer> onConnectMasterSuccess;

	public InnerSlaveServer(InnerSlaveServerBuilder builder) throws Exception {
		this.slaveName = builder.slaveName;
		this.slaveType = builder.slaveType;
		this.myServerId = builder.myServerId;
		this.myIp = builder.myServerIp;
		this.myServerPort = builder.myServerPort;
		this.myServerPass = builder.myServerPass;
		this.myInnerServerPort = builder.myInnerServerPort;
		this.myInnerServerPass = builder.myInnerServerPass;
		this.masterIp = builder.masterIp;
		this.masterServerPort = builder.masterServerPort;
		this.masterServerPass = builder.masterServerPass;
		this.masterInnerPort = builder.masterInnerPort;
		this.masterInnerPass = builder.masterInnerPass;
		this.factory = Objects.requireNonNull(builder.factory, "factory");
		this.serverLoad = builder.serverLoad;
		this.onNewSlaveJoin = builder.onNewSlaveJoin;
		this.onNewSlaveQuit = builder.onNewSlaveQuit;
		this.onConnectMasterSuccess = builder.onConnectMasterSuccess;
		getFactory().registerMsg(new ResServerJoinMaster2SlaveHandler())
				.registerMsg(new ResServerQuitMaster2SlaveHandler());
		toMaster = new BinaryClient.BinaryClientBuilder().autoReconnect(5).clientName(getSlaveName())
				.remoteAddress(new AddressPair(getMasterIp(), getMasterInnerPort(), getMasterInnerPass()))
				.factory(getFactory()).onChannelStateChanged((binaryClient, active) -> {
					if (!active) {
						log.error(toMaster.getClientName() + " server disconnected," + binaryClient.channel());
					}
				}).onConnectionEffective(client -> {
					// 链接成功,上报本服务器的服务端口等信息
					ReqConnectionReportSlave2MasterMessage msg = new ReqConnectionReportSlave2MasterMessage();
					InnerServerInfo info = new InnerServerInfo();
					info.innerPort = getMyInnerServerPort();
					info.outIp = getMyIp();
					info.outPass = getMyServerPass();
					info.outPort = getMyServerPort();
					info.serverId = getMyServerId();
					info.serverType = getSlaveType();
					msg.serverInfo = info;
					try {
						client.sendMessage(msg, (isSuccess, cause, channel) -> {
							if (isSuccess) {
								log.info("report my server info to " + client.getClientName() + " success:" + info);
							} else {
								log.error("report my server info to master " + client.getClientName() + " fail:" + info,
										cause);
							}
						});
					} catch (Exception e) {
						log.error("report message error", e);
					}
					if (reportTask == null) {
						reportTask = new TimerTask() {

							@Override
							public void run() {
								reportLoad();
							}
						};
						TimerUtil.scheduleGlobal(5000, 5000, reportTask);
					}
					Procs.invoke(onConnectMasterSuccess, InnerSlaveServer.this);
				}).dispatchMessage((message, handler) -> {
					message.setExtra(this);
					try {
						handler.handle(message);
					} catch (Exception e) {
						log.error("handle error", e);
					}
				}).build();
	}

	@Override
	public void startServer() {
		toMaster.connect();
	}

	@Override
	public void stopServer() {
		toMaster.disConnect();
	}

	private void reportLoad() {
		Integer invoke = Funcs.invoke(serverLoad);
		if (invoke == null) {
			return;
		}
		ReqServerLoadSlave2MasterMessage slm = new ReqServerLoadSlave2MasterMessage();
		slm.load = invoke;
		try {
			toMaster.sendMessage(slm, (isSuccess, cause, channel) -> {
				if (isSuccess) {
					log.debug("send server load to master {} success,current load:{}", channel, slm.load);
				} else {
					log.error("send server load to master {} fail,current load:{}", channel, slm.load);
				}
			});
		} catch (Exception e) {
			log.error("report load error", e);
		}
	}

	public void ResServerJoinMaster2Slave(ResServerJoinMaster2SlaveMessage msg) {
		for (InnerServerInfo info : msg.infos) {
			Procs.invoke(onNewSlaveJoin, info);
		}
	}

	public void ResServerQuitMaster2Slave(ResServerQuitMaster2SlaveMessage msg) {
		Procs.invoke(onNewSlaveQuit, msg.serverType, msg.serverId);
	}

	public BinaryClient getMasterClient() {
		return this.toMaster;
	}

	public String getSlaveName() {
		return slaveName;
	}

	public int getSlaveType() {
		return slaveType;
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

	public int getMyInnerServerPort() {
		return myInnerServerPort;
	}

	public String getMyInnerServerPass() {
		return myInnerServerPass;
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

	public MessageFactory getFactory() {
		return factory;
	}

	public static class InnerSlaveServerBuilder {
		private String slaveName;
		private int slaveType;
		private int myServerId;
		private String myServerIp;
		private int myServerPort;
		private String myServerPass;
		private int myInnerServerPort;
		private String myInnerServerPass;
		private String masterIp;
		private int masterServerPort;
		private String masterServerPass;
		private int masterInnerPort;
		private String masterInnerPass;
		private MessageFactory factory;
		// listener--
		private Func<Integer> serverLoad;
		private Proc1<InnerServerInfo> onNewSlaveJoin;
		private Proc2<Integer, Integer> onNewSlaveQuit;
		private Proc1<InnerSlaveServer> onConnectMasterSuccess;

		public InnerSlaveServerBuilder() {
			this.slaveName = "Inner-Slave-Server";
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 * @throws Exception
		 */
		public InnerSlaveServer build() throws Exception {
			return new InnerSlaveServer(this);
		}

		public InnerSlaveServerBuilder slaveName(String slaveName) {
			this.slaveName = slaveName;
			return this;
		}

		public InnerSlaveServerBuilder slaveType(int slaveType) {
			this.slaveType = slaveType;
			return this;
		}

		public InnerSlaveServerBuilder myServerId(int myServerId) {
			this.myServerId = myServerId;
			return this;
		}

		public InnerSlaveServerBuilder myServerIp(String myServerIp) {
			this.myServerIp = myServerIp;
			return this;
		}

		public InnerSlaveServerBuilder myServerPort(int myServerPort) {
			if (myServerPort >= 1024) {
				this.myServerPort = myServerPort;
			}
			return this;
		}

		public InnerSlaveServerBuilder myServerPass(String myServerPass) {
			this.myServerPass = myServerPass;
			return this;
		}

		public InnerSlaveServerBuilder myInnerServerPort(int myInnerServerPort) {
			if (myInnerServerPort >= 1024) {
				this.myInnerServerPort = myInnerServerPort;
			}
			return this;
		}

		public InnerSlaveServerBuilder myInnerServerPass(String myInnerServerPass) {
			this.myInnerServerPass = myInnerServerPass;
			return this;
		}

		public InnerSlaveServerBuilder masterIp(String masterIp) {
			this.masterIp = masterIp;
			return this;
		}

		public InnerSlaveServerBuilder masterServerPort(int masterServerPort) {
			if (masterServerPort >= 1024) {
				this.masterServerPort = masterServerPort;
			}
			return this;
		}

		public InnerSlaveServerBuilder masterServerPass(String masterServerPass) {
			this.masterServerPass = masterServerPass;
			return this;
		}

		public InnerSlaveServerBuilder masterInnerPort(int masterInnerPort) {
			if (masterInnerPort >= 1024) {
				this.masterInnerPort = masterInnerPort;
			}
			return this;
		}

		public InnerSlaveServerBuilder masterInnerPass(String masterInnerPass) {
			this.masterInnerPass = masterInnerPass;
			return this;
		}

		public InnerSlaveServerBuilder facotry(MessageFactory factory) {
			this.factory = factory;
			return this;
		}

		public InnerSlaveServerBuilder serverLoad(Func<Integer> serverLoad) {
			this.serverLoad = serverLoad;
			return this;
		}

		public InnerSlaveServerBuilder onNewSlaveJoin(Proc1<InnerServerInfo> onNewSlaveJoin) {
			this.onNewSlaveJoin = onNewSlaveJoin;
			return this;
		}

		public InnerSlaveServerBuilder onNewSlaveQuit(Proc2<Integer, Integer> onNewSlaveQuit) {
			this.onNewSlaveQuit = onNewSlaveQuit;
			return this;
		}

		public InnerSlaveServerBuilder onConnectMasterSuccess(Proc1<InnerSlaveServer> onConnectMasterSuccess) {
			this.onConnectMasterSuccess = onConnectMasterSuccess;
			return this;
		}
	}
}
