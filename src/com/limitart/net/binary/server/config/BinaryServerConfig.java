package com.limitart.net.binary.server.config;

/**
 * 二进制服务器配置
 * 
 * @author hank
 *
 */
public final class BinaryServerConfig {
	private String serverName;
	private int port;
	private int dataMaxLength;
	private String connectionPass;
	private int connectionValidateTimeInSec;

	private BinaryServerConfig(BinaryServerConfigBuilder builder) {
		this.serverName = builder.serverName;
		this.port = builder.port;
		this.dataMaxLength = builder.dataMaxLength;
		this.connectionPass = builder.connectionPass;
		this.connectionValidateTimeInSec = builder.connectionValidateTimeInSec;
	}

	public String getServerName() {
		return this.serverName;
	}

	public int getConnectionValidateTimeInSec() {
		return connectionValidateTimeInSec;
	}

	public String getConnectionPass() {
		return connectionPass;
	}

	public int getPort() {
		return port;
	}

	public int getDataMaxLength() {
		return dataMaxLength;
	}

	public static class BinaryServerConfigBuilder {
		private String serverName;
		private int port;
		private int dataMaxLength;
		private String connectionPass;
		private int connectionValidateTimeInSec;

		public BinaryServerConfigBuilder() {
			this.serverName = "Binary-Server";
			this.port = 8888;
			this.dataMaxLength = 20 * 1024 * 1024;
			this.connectionPass = "limitart-core";
			this.connectionValidateTimeInSec = 20;
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public BinaryServerConfig build() {
			return new BinaryServerConfig(this);
		}

		public BinaryServerConfigBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		/**
		 * 绑定端口
		 * 
		 * @param port
		 * @return
		 */
		public BinaryServerConfigBuilder port(int port) {
			if (port >= 1024) {
				this.port = port;
			}
			return this;
		}

		/**
		 * 数据传输最大长度
		 * 
		 * @param dataMaxLength
		 * @return
		 */
		public BinaryServerConfigBuilder dataMaxLength(int dataMaxLength) {
			this.dataMaxLength = dataMaxLength;
			return this;
		}

		/**
		 * 链接验证密码
		 * 
		 * @param connectionPass
		 * @return
		 */
		public BinaryServerConfigBuilder connectionPass(String connectionPass) {
			this.connectionPass = connectionPass;
			return this;
		}

		/**
		 * 链接验证超时(秒)
		 * 
		 * @param connectionValidateTimeInSec
		 * @return
		 */
		public BinaryServerConfigBuilder connectionValidateTimeInSec(int connectionValidateTimeInSec) {
			this.connectionValidateTimeInSec = connectionValidateTimeInSec;
			return this;
		}
	}
}
