package org.slingerxv.limitart.net.http.handler;

import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.net.http.message.UrlMessage;

/**
 * 消息处理接口
 * 
 * @author hank
 *
 */
public interface HttpHandler<T extends UrlMessage> {

	ConstraintMap<String> doServer(T msg);
}
