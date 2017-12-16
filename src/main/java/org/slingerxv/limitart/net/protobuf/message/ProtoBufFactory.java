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
package org.slingerxv.limitart.net.protobuf.message;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.protobuf.handler.ProtoBufHandler;
import org.slingerxv.limitart.util.ReflectionUtil;

import com.google.protobuf.Message;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 * @author hank
 *
 */
public class ProtoBufFactory {
	private static Logger log = LoggerFactory.getLogger(ProtoBufFactory.class);
	private final Map<Class<? extends Message>, ProtoBufHandler<? extends Message>> handlers = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static ProtoBufFactory createByPackage(String packageName)
			throws IOException, ReflectiveOperationException, MessageIDDuplicatedException {
		List<Class<?>> classesByPackage = ReflectionUtil.getClassesByPackage(packageName, ProtoBufHandler.class);
		ProtoBufFactory messageFactory = new ProtoBufFactory();
		for (Class<?> clzz : classesByPackage) {
			if (Modifier.isAbstract(clzz.getModifiers()) || clzz.isAnonymousClass() || clzz.isMemberClass()
					|| clzz.isLocalClass()) {
				log.warn("inner class or anonymous class or abstract class will not be registerd:" + clzz.getName());
				continue;
			}
			messageFactory.registerHandler((ProtoBufHandler<? extends Message>) clzz.newInstance());
		}
		return messageFactory;
	}

	public synchronized <T extends Message> ProtoBufFactory registerHandler(ProtoBufHandler<? extends Message> handler)
			throws InstantiationException, IllegalAccessException, MessageIDDuplicatedException {
		Type[] genericInterfaces = handler.getClass().getGenericInterfaces();
		ParameterizedType handlerInterface = null;
		for (Type temp : genericInterfaces) {
			if (temp instanceof ParameterizedType) {
				ParameterizedType ttemp = (ParameterizedType) temp;
				if (ttemp.getRawType() == ProtoBufHandler.class) {
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
		if (handlers.containsKey(msgClass)) {
			throw new MessageIDDuplicatedException("class :" + msgClass.getName());
		}
		handlers.put(msgClass, handler);
		log.info("regist msg: {}，handler:{}", msgClass.getSimpleName(), handler.getClass().getSimpleName());
		return this;
	}

	public ProtoBufHandler<? extends Message> getHandler(Class<? extends Message> clazz)
			throws ReflectiveOperationException {
		return handlers.get(clazz);
	}

	public void copyToChannelPipeline(ChannelPipeline pipeline) {
		for (ProtoBufHandler<? extends Message> temp : handlers.values()) {
			pipeline.addLast(new ProtobufDecoder(temp.getDefaultInstance()));
		}
	}
}
