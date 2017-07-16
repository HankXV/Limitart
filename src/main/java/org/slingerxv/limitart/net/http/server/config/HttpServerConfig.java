package org.slingerxv.limitart.net.http.server.config;

import java.util.HashSet;

import org.slingerxv.limitart.net.http.message.UrlMessageFactory;
import org.slingerxv.limitart.util.StringUtil;

public final class HttpServerConfig {
	private String serverName;
	private int port;
	// 消息聚合最大（1024KB）,即Content-Length
	private int httpObjectAggregatorMax;
	private UrlMessageFactory facotry;
	private HashSet<String> whiteList;

	private HttpServerConfig(HttpServerConfigBuilder builder) {
		this.port = builder.port;
		this.httpObjectAggregatorMax = builder.httpObjectAggregatorMax;
		this.serverName = builder.serverName;
		this.whiteList = builder.whiteList;
		if (builder.facotry == null) {
			throw new NullPointerException("factory");
		}
		this.facotry = builder.facotry;
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

	public HashSet<String> getWhiteList() {
		return whiteList;
	}

	public UrlMessageFactory getFacotry() {
		return facotry;
	}

	public static class HttpServerConfigBuilder {
		private String serverName;
		private int port;
		private int httpObjectAggregatorMax;
		private UrlMessageFactory facotry;
		private HashSet<String> whiteList;

		public HttpServerConfigBuilder() {
			this.serverName = "Http-Server";
			this.port = 8888;
			this.httpObjectAggregatorMax = 1024 * 1024;
			this.whiteList = new HashSet<>();
		}

		public HttpServerConfig build() {
			return new HttpServerConfig(this);
		}

		public HttpServerConfigBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		public HttpServerConfigBuilder port(int port) {
			if (port >= 1024) {
				this.port = port;
			}
			return this;
		}

		public HttpServerConfigBuilder httpObjectAggregatorMax(int httpObjectAggregatorMax) {
			this.httpObjectAggregatorMax = Math.max(512, httpObjectAggregatorMax);
			return this;
		}

		public HttpServerConfigBuilder whiteList(String... remoteAddress) {
			for (String ip : remoteAddress) {
				if (StringUtil.isIp(ip)) {
					this.whiteList.add(ip);
				}
			}
			return this;
		}

		public HttpServerConfigBuilder factory(UrlMessageFactory factory) {
			this.facotry = factory;
			return this;
		}
	}
}
