package org.slingerxv.limitart.net.http.message;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.http.handler.HttpHandler;
import org.slingerxv.limitart.reflectasm.ConstructorAccess;
import org.slingerxv.limitart.util.ReflectionUtil;

/**
 * 消息工厂 注意：这里的handler是单例，一定不能往里存成员变量
 * 
 * @author hank
 *
 */
public class UrlMessageFactory {
	private static Logger log = LogManager.getLogger();
	private Map<String, ConstructorAccess<? extends UrlMessage>> messages = new HashMap<>();
	private Map<String, HttpHandler<? extends UrlMessage>> handlers = new HashMap<>();

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

	public synchronized UrlMessageFactory registerMsg(HttpHandler<? extends UrlMessage> handler)
			throws InstantiationException, IllegalAccessException, MessageIDDuplicatedException {
		Type[] genericInterfaces = handler.getClass().getGenericInterfaces();
		ParameterizedType handlerInterface = null;
		for (Type temp : genericInterfaces) {
			if (temp instanceof ParameterizedType) {
				ParameterizedType ttemp = (ParameterizedType) temp;
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
		Class<? extends UrlMessage> msgClass = (Class<? extends UrlMessage>) handlerInterface
				.getActualTypeArguments()[0];
		UrlMessage newInstance = msgClass.newInstance();
		String url = newInstance.getUrl();
		if (messages.containsKey(url)) {
			Class<? extends UrlMessage> class1 = messages.get(url).newInstance().getClass();
			if (!class1.getName().equals(msgClass.getName())) {
				throw new MessageIDDuplicatedException("message id duplicated:" + url + ",class old:" + class1.getName()
						+ ",class new:" + msgClass.getName());
			} else {
				return this;
			}
		}
		if (handlers.containsKey(url)) {
			throw new IllegalArgumentException("handler url duplicated:" + url);
		}
		ConstructorAccess<? extends UrlMessage> constructorAccess = ConstructorAccess.get(msgClass);
		messages.put(url, constructorAccess);
		handlers.put(url, handler);
		log.info("regist msg: {}，handler:{}", msgClass.getSimpleName(), handler.getClass().getSimpleName());
		return this;
	}

	public UrlMessageFactory registerMsg(Class<? extends HttpHandler<? extends UrlMessage>> handlerClass)
			throws InstantiationException, IllegalAccessException, MessageIDDuplicatedException {
		return registerMsg(handlerClass.newInstance());
	}

	public UrlMessage getMessage(String url) throws InstantiationException, IllegalAccessException {
		if (!messages.containsKey(url)) {
			return null;
		}
		return messages.get(url).newInstance();
	}

	public HttpHandler<? extends UrlMessage> getHandler(String url)
			throws InstantiationException, IllegalAccessException {
		return handlers.get(url);
	}

}
