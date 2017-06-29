package com.limitart.net.http.server.event;

import com.limitart.collections.ConstraintMap;
import com.limitart.net.http.handler.HttpHandler;
import com.limitart.net.http.message.UrlMessage;
import com.limitart.net.listener.NettyEventListener;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpMessage;

/**
 * 服务器事件
 * 
 * @author Hank
 *
 */
public interface HttpServerEventListener extends NettyEventListener {
	public void dispatchMessage(UrlMessage<String> message, HttpHandler handler, ConstraintMap<String> map);

	/**
	 * 当消息超标时会调用此函数
	 * 
	 * @param ctx
	 * @param oversized
	 */
	public void onMessageOverSize(Channel channel, HttpMessage oversized);

	public void onServerBind(Channel channel);
}
