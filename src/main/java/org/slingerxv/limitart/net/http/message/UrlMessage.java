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
package org.slingerxv.limitart.net.http.message;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.net.http.constant.QueryMethod;
import org.slingerxv.limitart.reflectasm.FieldAccess;
import org.slingerxv.limitart.util.FieldFilter;

import io.netty.channel.Channel;

public abstract class UrlMessage extends ConstraintMap<String> {
	private static Map<Class<? extends UrlMessage>, FieldAccess> messageMetaFieldCache = new ConcurrentHashMap<>();
	private transient Channel channel;
	private transient Map<String, byte[]> files = new HashMap<>();
	private boolean keepAlive;

	public abstract String getUrl();

	public abstract QueryMethod getMethod();

	public void decode() throws Exception {
		FieldAccess fieldAccess = getFieldAccess();
		Field[] declaredFields = fieldAccess.getFields();
		for (Field field : declaredFields) {
			Object object = getObj(field.getName());
			if (object != null) {
				field.set(this, object);
			}
		}
	}

	public void encode() throws Exception {
		FieldAccess fieldAccess = getFieldAccess();
		Field[] declaredFields = fieldAccess.getFields();
		for (Field field : declaredFields) {
			Object object = field.get(this);
			if (object != null) {
				putObj(field.getName(), object);
			}
		}
	}

	private FieldAccess getFieldAccess() {
		FieldAccess fieldAccess = messageMetaFieldCache.get(getClass());
		if (fieldAccess == null) {
			fieldAccess = FieldAccess.get(getClass(), false, field -> {
				return !(FieldFilter.isStatic(field) || FieldFilter.isTransient(field) || FieldFilter.isFinal(field));
			});
			FieldAccess put = messageMetaFieldCache.putIfAbsent(getClass(), fieldAccess);
			if (put != null) {
				fieldAccess = put;
			}
		}
		return fieldAccess;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Map<String, byte[]> getFiles() {
		return files;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
}
