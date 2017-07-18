package org.slingerxv.limitart.net.binary.message;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.reflectasm.ConstructorAccess;
import org.slingerxv.limitart.util.ReflectionUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 消息工厂 注意：这里的handler是单例，一定不能往里存成员变量
 * 
 * @author hank
 *
 */
public class MessageFactory {
	private static Logger log = LogManager.getLogger();
	// !!这里的asm应用经测试在JAVA8下最优
	private final HashMap<Short, ConstructorAccess<? extends Message>> msgs = new HashMap<>();
	private final HashMap<Short, IHandler<? extends Message>> handlers = new HashMap<>();

	/**
	 * 通过反射包来加载所有handler
	 * 
	 * @param packageName
	 * @throws MessageIDDuplicatedException
	 * @returns
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static MessageFactory createByPackage(String packageName)
			throws MessageIDDuplicatedException, Exception {
		MessageFactory messageFactory = new MessageFactory();
		List<Class<?>> classesByPackage = ReflectionUtil.getClassesByPackage(packageName, IHandler.class);
		for (Class<?> clzz : classesByPackage) {
			if (clzz.getName().contains("$")) {
				log.warn("inner class or anonymous class will not be registerd:" + clzz.getName());
				continue;
			}
			messageFactory.registerMsg((IHandler) clzz.newInstance());
		}
		return messageFactory;
	}

	public synchronized MessageFactory registerMsg(IHandler<? extends Message> handler)
			throws MessageIDDuplicatedException, Exception {
		Type[] genericInterfaces = handler.getClass().getGenericInterfaces();
		ParameterizedType handlerInterface = null;
		for (Type temp : genericInterfaces) {
			if (temp instanceof ParameterizedType) {
				ParameterizedType ttemp = (ParameterizedType) temp;
				if (ttemp.getRawType() == IHandler.class) {
					handlerInterface = ttemp;
					break;
				}
			}
		}
		if (handlerInterface == null) {
			return this;
		}
		@SuppressWarnings("unchecked")
		Class<? extends Message> msgClass = (Class<? extends Message>) handlerInterface.getActualTypeArguments()[0];
		// 这里先实例化一个出来获取其ID
		Message newInstance = msgClass.newInstance();
		// 检查消息字段
		ByteBuf buffer = Unpooled.directBuffer(256);
		newInstance.buffer(buffer);
		newInstance.encode();
		newInstance.decode();
		newInstance.buffer(null);
		buffer.release();
		short id = newInstance.getMessageId();
		if (msgs.containsKey(id)) {
			Class<? extends Message> class1 = msgs.get(id).newInstance().getClass();
			if (!class1.getName().equals(msgClass.getName())) {
				throw new MessageIDDuplicatedException("message id duplicated:" + id + ",class old:" + class1.getName()
						+ ",class new:" + msgClass.getName());
			} else {
				return this;
			}
		}
		if (handlers.containsKey(id)) {
			throw new MessageIDDuplicatedException("handler id duplicated:" + id);
		}
		ConstructorAccess<? extends Message> constructorAccess = ConstructorAccess.get(msgClass);
		msgs.put(id, constructorAccess);
		handlers.put(id, handler);
		log.info("regist msg: {}，handler:{}", msgClass.getSimpleName(), handler.getClass().getSimpleName());
		return this;
	}

	public synchronized MessageFactory registerMsg(Class<? extends IHandler<? extends Message>> handlerClass)
			throws Exception {
		return registerMsg(handlerClass.newInstance());
	}

	public Message getMessage(short msgId) throws InstantiationException, IllegalAccessException {
		if (!msgs.containsKey(msgId)) {
			return null;
		}
		ConstructorAccess<? extends Message> constructorAccess = msgs.get(msgId);
		return constructorAccess.newInstance();
	}

	public IHandler<? extends Message> getHandler(short msgId) throws InstantiationException, IllegalAccessException {
		return handlers.get(msgId);
	}

}
