package com.limitart.net.http.message;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.net.http.handler.HttpHandler;
import com.limitart.reflectasm.ConstructorAccess;

/**
 * 消息工厂 注意：这里的handler是单例，一定不能往里存成员变量
 * 
 * @author hank
 *
 */
public class UrlMessageFactory {
	private static Logger log = LogManager.getLogger();
	private Map<String, ConstructorAccess<? extends UrlMessage<String>>> messages = new HashMap<>();
	private Map<String, HttpHandler> handlers = new HashMap<>();

	public synchronized UrlMessageFactory registerMsg(Class<? extends UrlMessage<String>> msgClass, HttpHandler handler)
			throws InstantiationException, IllegalAccessException {
		UrlMessage<String> newInstance = msgClass.newInstance();
		String url = newInstance.getUrl();
		if (messages.containsKey(url)) {
			throw new IllegalArgumentException("message url duplicated:" + url);
		}
		if (handlers.containsKey(url)) {
			throw new IllegalArgumentException("handler url duplicated:" + url);
		}
		ConstructorAccess<? extends UrlMessage<String>> constructorAccess = ConstructorAccess.get(msgClass);
		messages.put(url, constructorAccess);
		handlers.put(url, handler);
		log.debug("regist msg: {}，handler:{}", msgClass.getSimpleName(), handler.getClass().getSimpleName());
		return this;
	}

	public UrlMessageFactory registerMsg(Class<? extends UrlMessage<String>> msgClass,
			Class<? extends HttpHandler> handlerClass) throws InstantiationException, IllegalAccessException {
		return registerMsg(msgClass, handlerClass.newInstance());
	}

	public UrlMessage<String> getMessage(String url) throws InstantiationException, IllegalAccessException {
		if (!messages.containsKey(url)) {
			return null;
		}
		return messages.get(url).newInstance();
	}

	public HttpHandler getHandler(String url) throws InstantiationException, IllegalAccessException {
		return handlers.get(url);
	}

}
