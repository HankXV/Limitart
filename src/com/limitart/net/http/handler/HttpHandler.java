package com.limitart.net.http.handler;

import com.limitart.net.http.message.UrlMessage;

/**
 * 消息处理接口
 * 
 * @author hank
 *
 */
public interface HttpHandler {

	public abstract UrlMessage<Integer> doServer(UrlMessage<String> message) throws Exception;
}
