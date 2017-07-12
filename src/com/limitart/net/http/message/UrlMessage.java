package com.limitart.net.http.message;

import java.util.HashMap;

import com.limitart.net.http.constant.QueryMethod;
import com.limitart.net.http.handler.HttpHandler;

import io.netty.channel.Channel;

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
