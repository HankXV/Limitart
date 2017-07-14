package org.slingerxv.limitart.net.binary.distributed.config;

import org.slingerxv.limitart.net.binary.message.MessageFactory;

/**
 * 二进制服务器配置
 * 
 * @author hank
 *
 */
public final class InnerMasterServerConfig {
	private String serverName;
	private int masterPort;
	private MessageFactory factory;

	private InnerMasterServerConfig(InnerMasterServerConfigBuilder builder) {
		this.serverName = builder.serverName;
		this.masterPort = builder.masterPort;
		if (builder.factory == null) {
			throw new NullPointerException("factory");
		}
		this.factory = builder.factory;
	}

	public String getServerName() {
		return this.serverName;
	}

	public int getMasterPort() {
		return masterPort;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	public static class InnerMasterServerConfigBuilder {
		private String serverName;
		private int masterPort;
		private MessageFactory factory;

		public InnerMasterServerConfigBuilder() {
			this.serverName = "Inner-Master-Server";
			this.masterPort = 8888;
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public InnerMasterServerConfig build() {
			return new InnerMasterServerConfig(this);
		}

		public InnerMasterServerConfigBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		/**
		 * 绑定端口
		 * 
		 * @param port
		 * @return
		 */
		public InnerMasterServerConfigBuilder masterPort(int masterPort) {
			if (masterPort >= 1024) {
				this.masterPort = masterPort;
			}
			return this;
		}

		public InnerMasterServerConfigBuilder factory(MessageFactory factory) {
			this.factory = factory;
			return this;
		}
	}
}
