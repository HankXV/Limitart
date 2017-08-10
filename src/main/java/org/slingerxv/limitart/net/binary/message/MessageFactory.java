/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.net.binary.message;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Func1;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.handler.annotation.Controller;
import org.slingerxv.limitart.net.binary.handler.annotation.Handler;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.reflectasm.ConstructorAccess;
import org.slingerxv.limitart.reflectasm.MethodAccess;
import org.slingerxv.limitart.util.Beta;
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

	@Beta
	public static MessageFactory createByPackage(String packageName, Func1<Class<?>, Object> confirmInstance)
			throws ReflectiveOperationException, IOException, MessageCodecException, MessageIDDuplicatedException {
		MessageFactory messageFactory = new MessageFactory();
		List<Class<?>> classesOfAnnotation = ReflectionUtil.getClassesByPackage(packageName, (clazz) -> {
			return clazz.getAnnotation(Controller.class) != null;
		});
		for (Class<?> clzz : classesOfAnnotation) {
			messageFactory.registerController(clzz, confirmInstance);
		}
		return messageFactory;
	}

	/**
	 * 通过反射包来加载所有handler
	 * 
	 * @param packageName
	 * @throws MessageCodecException
	 * @throws IOException
	 * @throws ReflectiveOperationException
	 * @throws MessageIDDuplicatedException
	 * @returns
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static MessageFactory createByPackage(String packageName)
			throws ReflectiveOperationException, IOException, MessageCodecException, MessageIDDuplicatedException {
		List<Class<?>> classesByPackage = ReflectionUtil.getClassesByPackage(packageName, IHandler.class);
		MessageFactory messageFactory = new MessageFactory();
		for (Class<?> clzz : classesByPackage) {
			if (Modifier.isAbstract(clzz.getModifiers()) || clzz.isAnonymousClass() || clzz.isMemberClass()
					|| clzz.isLocalClass()) {
				log.warn("inner class or anonymous class or abstract class will not be registerd:" + clzz.getName());
				continue;
			}
			messageFactory.registerMsg(null, (IHandler) clzz.newInstance());
		}
		return messageFactory;
	}

	private static void checkMessageMeta(MessageMeta meta)
			throws ReflectiveOperationException, IOException, MessageCodecException {
		log.info("check message:" + meta.getClass().getName());
		ByteBuf buffer = Unpooled.directBuffer(256);
		meta.buffer(buffer);
		try {
			meta.encode();
			meta.decode();
		} catch (Exception e) {
			throw new MessageCodecException(e);
		}
		meta.buffer(null);
		buffer.release();
	}

	@Beta
	public MessageFactory registerController(Class<?> controllerClazz)
			throws MessageIDDuplicatedException, ReflectiveOperationException, IOException, MessageCodecException {
		return registerController(controllerClazz, (clzz) -> {
			try {
				return clzz.newInstance();
			} catch (ReflectiveOperationException e) {
			}
			return null;
		});
	}

	@Beta
	public MessageFactory registerController(Class<?> controllerClazz, Func1<Class<?>, Object> confirmInstance)
			throws MessageIDDuplicatedException, ReflectiveOperationException, IOException, MessageCodecException {
		MethodAccess methodAccess = MethodAccess.get(controllerClazz);
		ArrayList<Method> methods = methodAccess.getMethods();
		for (Method method : methods) {
			Handler annotation = method.getAnnotation(Handler.class);
			if (annotation == null) {
				continue;
			}
			registerMsg(annotation.value(), new IHandler<Message>() {

				@Override
				public void handle(Message msg) {
					methodAccess.invoke(confirmInstance.run(controllerClazz), method.getName(), msg);
				}
			});
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends Message> MessageFactory registerMsg(Class<T> msgClazz,
			IHandler<? extends Message> handler)
			throws MessageIDDuplicatedException, ReflectiveOperationException, IOException, MessageCodecException {
		Class<? extends Message> msgClass = msgClazz;
		if (msgClass == null) {
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
			msgClass = (Class<? extends Message>) handlerInterface.getActualTypeArguments()[0];
		}
		// 这里先实例化一个出来获取其ID
		Message newInstance = msgClass.newInstance();
		checkMessageMeta(newInstance);
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

	public <T extends IHandler<? extends Message>> MessageFactory registerMsg(Class<T> handlerClass)
			throws InstantiationException, IllegalAccessException, MessageIDDuplicatedException,
			ReflectiveOperationException, IOException, MessageCodecException {
		return registerMsg(null, handlerClass.newInstance());
	}

	public MessageFactory registerMsg(IHandler<? extends Message> handler)
			throws MessageIDDuplicatedException, ReflectiveOperationException, IOException, MessageCodecException {
		return registerMsg(null, handler);
	}

	public Message getMessage(short msgId) throws ReflectiveOperationException {
		if (!msgs.containsKey(msgId)) {
			return null;
		}
		ConstructorAccess<? extends Message> constructorAccess = msgs.get(msgId);
		return constructorAccess.newInstance();
	}

	public IHandler<? extends Message> getHandler(short msgId) throws ReflectiveOperationException {
		return handlers.get(msgId);
	}

}
