package com.limitart.net.http.handler;

import com.limitart.collections.ConstraintMap;
import com.limitart.net.http.message.UrlMessage;

/**
 * 消息处理接口
 * 
 * @author hank
 *
 */
public interface HttpHandler<T extends UrlMessage> {

	ConstraintMap<String> doServer(T msg) throws Exception;
}
