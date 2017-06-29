/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.net;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.reflectasm.ConstructorAccess;

/**
 * 消息工厂 注意：这里的handler是单例，一定不能往里存成员变量
 * 
 * @author hank
 *
 */
public class MessageFactory {
	private static Logger log = LoggerFactory.getLogger(MessageFactory.class);
	// !!这里的asm应用经测试在JAVA8下最优
	private final Map<Short, ConstructorAccess<? extends Message>> msgs = new HashMap<>();
	private final Map<Short, IHandler<? extends Message>> handlers = new HashMap<>();

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
			throws ReflectiveOperationException, IOException, MessageCodecException {
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
