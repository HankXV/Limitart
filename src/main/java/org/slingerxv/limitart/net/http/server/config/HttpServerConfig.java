package org.slingerxv.limitart.net.http.server.config;

import java.util.HashSet;

import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.net.http.message.UrlMessage;
import org.slingerxv.limitart.net.http.message.UrlMessageFactory;
import org.slingerxv.limitart.util.StringUtil;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpMessage;

public final class HttpServerConfig {
	private String serverName;
	private int port;
	// 消息聚合最大（1024KB）,即Content-Length
	private int httpObjectAggregatorMax;
	private UrlMessageFactory facotry;
	private HashSet<String> whiteList;
	//listener
	private Proc1<Channel> onServerBind;
	private Proc2<Channel, Boolean> onChannelStateChanged;
	private Proc2<UrlMessage, ConstraintMap<String>> dispatchMessage;
	private Proc2<Channel, HttpMessage> onMessageOverSize;
	private Proc2<Channel, Throwable> onExceptionCaught;

	private HttpServerConfig(HttpServerConfigBuilder builder) {
		this.port = builder.port;
		this.httpObjectAggregatorMax = builder.httpObjectAggregatorMax;
		this.serverName = builder.serverName;
		this.whiteList = builder.whiteList;
		if (builder.facotry == null) {
			throw new NullPointerException("factory");
		}
		this.facotry = builder.facotry;
		this.onServerBind = builder.onServerBind;
		this.onChannelStateChanged = builder.onChannelStateChanged;
		this.dispatchMessage = builder.dispatchMessage;
		this.onMessageOverSize = builder.onMessageOverSize;
		this.onExceptionCaught = builder.onExceptionCaught;
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
	
	public Proc1<Channel> getOnServerBind() {
		return onServerBind;
	}
	
	public Proc2<Channel, Boolean> getOnChannelStateChanged() {
		return onChannelStateChanged;
	}
	
	public Proc2<UrlMessage, ConstraintMap<String>> getDispatchMessage() {
		return dispatchMessage;
	}
	
	public Proc2<Channel, HttpMessage> getOnMessageOverSize() {
		return onMessageOverSize;
	}
	
	public Proc2<Channel, Throwable> getOnExceptionCaught() {
		return onExceptionCaught;
	}

	public static class HttpServerConfigBuilder {
		private String serverName;
		private int port;
		private int httpObjectAggregatorMax;
		private UrlMessageFactory facotry;
		private HashSet<String> whiteList;
		//listener
		private Proc1<Channel> onServerBind;
		private Proc2<Channel, Boolean> onChannelStateChanged;
		private Proc2<UrlMessage, ConstraintMap<String>> dispatchMessage;
		private Proc2<Channel, HttpMessage> onMessageOverSize;
		private Proc2<Channel, Throwable> onExceptionCaught;
		
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
		
		public HttpServerConfigBuilder onServerBind(Proc1<Channel> onServerBind){
			this.onServerBind = onServerBind;
			return this;
		}
		
		public HttpServerConfigBuilder onChannelStateChanged(Proc2<Channel,Boolean> onChannelStateChanged){
			this.onChannelStateChanged = onChannelStateChanged;
			return this;
		}
		
		public HttpServerConfigBuilder dispatchMessage(Proc2<UrlMessage,ConstraintMap<String>> dispatchMessage){
			this.dispatchMessage = dispatchMessage;
			return this;
		}
		
		public HttpServerConfigBuilder onMessageOverSize(Proc2<Channel, HttpMessage> onMessageOverSize){
			this.onMessageOverSize = onMessageOverSize;
			return this;
		}
		
		public HttpServerConfigBuilder onExceptionCaught(Proc2<Channel, Throwable> onExceptionCaught){
			this.onExceptionCaught = onExceptionCaught;
			return this;
		}
	}
}
