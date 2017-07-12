package com.limitart.net.http.message;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import com.limitart.net.http.handler.HttpHandler;
import com.limitart.reflectasm.ConstructorAccess;
import com.limitart.util.ReflectionUtil;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 * 消息工厂 注意：这里的handler是单例，一定不能往里存成员变量
 * 
 * @author hank
 *
 */
public class UrlMessageFactory {
	private static Logger log = LogManager.getLogger();
	private Map<String, ConstructorAccess<? extends UrlMessage<String>>> messages = new HashMap<>();
	private Map<String, HttpHandler<? extends UrlMessage<String>>> handlers = new HashMap<>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static UrlMessageFactory createByPackage(String packageName) throws ClassNotFoundException, IOException,
			MessageIDDuplicatedException, InstantiationException, IllegalAccessException {
		UrlMessageFactory messageFactory = new UrlMessageFactory();
		List<Class<?>> classesByPackage = ReflectionUtil.getClassesByPackage(packageName, HttpHandler.class);
		for (Class<?> clzz : classesByPackage) {
			if (clzz.getName().contains("$")) {
				log.warn("inner class or anonymous class will not be register:" + clzz.getName());
				continue;
			}
			messageFactory.registerMsg((HttpHandler) clzz.newInstance());
		}
		return messageFactory;
	}

	public synchronized UrlMessageFactory registerMsg(HttpHandler<? extends UrlMessage<String>> handler)
			throws InstantiationException, IllegalAccessException {
		Type[] genericInterfaces = handler.getClass().getGenericInterfaces();
		ParameterizedTypeImpl handlerInterface = null;
		for (Type temp : genericInterfaces) {
			if (temp instanceof ParameterizedTypeImpl) {
				ParameterizedTypeImpl ttemp = (ParameterizedTypeImpl) temp;
				if (ttemp.getRawType() == HttpHandler.class) {
					handlerInterface = ttemp;
					break;
				}
			}
		}
		if (handlerInterface == null) {
			return this;
		}
		@SuppressWarnings("unchecked")
		Class<? extends UrlMessage<String>> msgClass = (Class<? extends UrlMessage<String>>) handlerInterface
				.getActualTypeArguments()[0];
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

	public UrlMessageFactory registerMsg(Class<? extends HttpHandler<? extends UrlMessage<String>>> handlerClass)
			throws InstantiationException, IllegalAccessException {
		return registerMsg(handlerClass.newInstance());
	}

	public UrlMessage<String> getMessage(String url) throws InstantiationException, IllegalAccessException {
		if (!messages.containsKey(url)) {
			return null;
		}
		return messages.get(url).newInstance();
	}

	public HttpHandler<? extends UrlMessage<String>> getHandler(String url) throws InstantiationException, IllegalAccessException {
		return handlers.get(url);
	}

}
