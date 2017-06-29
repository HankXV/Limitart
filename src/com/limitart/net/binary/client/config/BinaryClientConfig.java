package com.limitart.net.binary.client.config;

/**
 * 二进制通信客户端配置
 * 
 * @author hank
 *
 */
public final class BinaryClientConfig {
	private String clientName;
	private String remoteIp;
	private int remotePort;
	private int autoReconnect;
	private int dataMaxLength;
	private String connectionPass;

	private BinaryClientConfig(BinaryClientConfigBuilder builder) {
		this.clientName = builder.clientName;
		this.remoteIp = builder.remoteIp;
		this.remotePort = builder.remotePort;
		this.autoReconnect = builder.autoReconnect;
		this.dataMaxLength = builder.dataMaxLength;
		this.connectionPass = builder.connectionPass;
	}

	public String getClientName() {
		return this.clientName;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public int getAutoReconnect() {
		return autoReconnect;
	}

	public int getDataMaxLength() {
		return dataMaxLength;
	}

	public String getConnectionPass() {
		return connectionPass;
	}

	public static class BinaryClientConfigBuilder {
		private String clientName;
		private String remoteIp;
		private int remotePort;
		private int autoReconnect;
		private int dataMaxLength;
		private String connectionPass;

		public BinaryClientConfigBuilder() {
			this.clientName = "Binary-Client";
			this.remoteIp = "127.0.0.1";
			this.remotePort = 8888;
			this.autoReconnect = 0;
			this.dataMaxLength = 20 * 1024 * 1024;
			this.connectionPass = "limitart-core";
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public BinaryClientConfig build() {
			return new BinaryClientConfig(this);
		}

		public BinaryClientConfigBuilder clientName(String clientName) {
			this.clientName = clientName;
			return this;
		}

		/**
		 * 服务器IP
		 * 
		 * @param remoteIp
		 * @return
		 */
		public BinaryClientConfigBuilder remoteIp(String remoteIp) {
			this.remoteIp = remoteIp;
			return this;
		}

		/**
		 * 服务器端口
		 * 
		 * @param remotePort
		 * @return
		 */
		public BinaryClientConfigBuilder remotePort(int remotePort) {
			this.remotePort = remotePort;
			return this;
		}

		/**
		 * 自动重连尝试间隔(秒)
		 * 
		 * @param autoReconnect
		 * @return
		 */
		public BinaryClientConfigBuilder autoReconnect(int autoReconnect) {
			this.autoReconnect = autoReconnect;
			return this;
		}

		/**
		 * 最大数据传输长度
		 * 
		 * @param dataMaxLength
		 * @return
		 */
		public BinaryClientConfigBuilder dataMaxLength(int dataMaxLength) {
			this.dataMaxLength = dataMaxLength;
			return this;
		}

		/**
		 * 链接验证密码
		 * 
		 * @param connectionPass
		 * @return
		 */
		public BinaryClientConfigBuilder connectionPass(String connectionPass) {
			this.connectionPass = connectionPass;
			return this;
		}
	}
}
