package com.limitart.net.http.message;

import java.util.HashMap;

import com.limitart.collections.ConstraintMap;
import com.limitart.net.http.constant.QueryMethod;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class UrlMessage<T> {

	private transient Channel channel;
	private transient FullHttpRequest request;
	private HashMap<String, byte[]> files = new HashMap<>();

	public abstract T getUrl();

	public abstract QueryMethod getMethod();

	public abstract void readMessage(ConstraintMap<String> buf);

	public abstract void writeMessage(ConstraintMap<String> buf) throws Exception;

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
}
