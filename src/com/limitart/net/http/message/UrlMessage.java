package com.limitart.net.http.message;

import java.util.HashMap;

import com.limitart.net.http.constant.QueryMethod;
import com.limitart.net.http.handler.HttpHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class UrlMessage<URL> {

	private transient Channel channel;
	private transient FullHttpRequest request;
	private transient HashMap<String, byte[]> files = new HashMap<>();
	private transient HttpHandler<UrlMessage<String>> handler;

	public abstract URL getUrl();

	public abstract QueryMethod getMethod();

	public FullHttpRequest getRequest() {
		return request;
	}

	public void setRequest(FullHttpRequest request) {
		this.request = request;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public HashMap<String, byte[]> getFiles() {
		return files;
	}

	public HttpHandler<UrlMessage<String>> getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler<UrlMessage<String>> handler) {
		this.handler = handler;
	}
}
