package com.limitart.net.http.server.config;

public final class HttpServerConfig {
	private String serverName;
	private int port;
	// 消息聚合最大（1024KB）,即Content-Length
	private int httpObjectAggregatorMax;

	private HttpServerConfig(HttpServerConfigBuilder builder) {
		this.port = builder.port;
		this.httpObjectAggregatorMax = builder.httpObjectAggregatorMax;
		this.serverName = builder.serverName;
	}

	public String getServerName() {
		return this.serverName;
	}

	public int getPort() {
		return port;
	}

	public int getHttpObjectAggregatorMax() {
		return httpObjectAggregatorMax;
	}

	public static class HttpServerConfigBuilder {
		private String serverName;
		private int port;
		private int httpObjectAggregatorMax;

		public HttpServerConfigBuilder() {
			this.serverName = "Http-Server";
			this.port = 8888;
			this.httpObjectAggregatorMax = 1024 * 1024;
		}

		public HttpServerConfig build() {
			return new HttpServerConfig(this);
		}

		public HttpServerConfigBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		public HttpServerConfigBuilder port(int port) {
			if (port < 1024) {
				this.port = 8888;
			} else {
				this.port = port;
			}
			return this;
		}

		public HttpServerConfigBuilder httpObjectAggregatorMax(int httpObjectAggregatorMax) {
			this.httpObjectAggregatorMax = Math.max(512, httpObjectAggregatorMax);
			return this;
		}

	}
}
