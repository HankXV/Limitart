package com.limitart.rpcx.center.config;

/**
 * 服务中心配置
 * 
 * @author Hank
 *
 */
public final class ServiceCenterXConfig {
	private int port;

	private ServiceCenterXConfig(ServiceCenterXConfigBuilder builder) {
		this.port = builder.port;
	}

	public int getPort() {
		return port;
	}

	public static class ServiceCenterXConfigBuilder {
		private int port;

		public ServiceCenterXConfigBuilder() {
			this.port = 9000;
		}

		public ServiceCenterXConfig build() {
			return new ServiceCenterXConfig(this);
		}

		public ServiceCenterXConfigBuilder port(int port) {
			if (port >= 1024) {
				this.port = port;
			}
			return this;
		}
	}
}
