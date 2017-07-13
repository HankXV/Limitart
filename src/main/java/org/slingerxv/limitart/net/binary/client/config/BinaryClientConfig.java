package org.slingerxv.limitart.net.binary.client.config;

import org.slingerxv.limitart.net.binary.codec.AbstractBinaryDecoder;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryEncoder;

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
	private String connectionPass;
	private AbstractBinaryDecoder decoder;
	private AbstractBinaryEncoder encoder;

	private BinaryClientConfig(BinaryClientConfigBuilder builder) {
		this.clientName = builder.clientName;
		this.remoteIp = builder.remoteIp;
		this.remotePort = builder.remotePort;
		this.autoReconnect = builder.autoReconnect;
		this.connectionPass = builder.connectionPass;
		this.decoder = builder.decoder;
		this.encoder = builder.encoder;
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

	public String getConnectionPass() {
		return connectionPass;
	}

	public AbstractBinaryDecoder getDecoder() {
		return decoder;
	}

	public AbstractBinaryEncoder getEncoder() {
		return encoder;
	}

	public static class BinaryClientConfigBuilder {
		private String clientName;
		private String remoteIp;
		private int remotePort;
		private int autoReconnect;
		private String connectionPass;
		private AbstractBinaryDecoder decoder;
		private AbstractBinaryEncoder encoder;

		public BinaryClientConfigBuilder() {
			this.clientName = "Binary-Client";
			this.remoteIp = "127.0.0.1";
			this.remotePort = 8888;
			this.autoReconnect = 0;
			this.connectionPass = "limitart-core";
			this.decoder = AbstractBinaryDecoder.DEFAULT_DECODER;
			this.encoder = AbstractBinaryEncoder.DEFAULT_ENCODER;
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public BinaryClientConfig build() {
			return new BinaryClientConfig(this);
		}

		public BinaryClientConfigBuilder decoder(AbstractBinaryDecoder decoder) {
			this.decoder = decoder;
			return this;
		}

		public BinaryClientConfigBuilder encoder(AbstractBinaryEncoder encoder) {
			this.encoder = encoder;
			return this;
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
