package org.slingerxv.limitart.net.http.message;

import java.util.HashMap;

import org.slingerxv.limitart.net.http.constant.QueryMethod;

public abstract class UrlMessage {

	private transient Channel channel;
	private transient HashMap<String, byte[]> files = new HashMap<>();
	private transient HttpHandler<UrlMessage> handler;

	public abstract String getUrl();

	public abstract QueryMethod getMethod();

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public HashMap<String, byte[]> getFiles() {
		return files;
	}

	public HttpHandler<UrlMessage> getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler<UrlMessage> handler) {
		this.handler = handler;
	}
}
