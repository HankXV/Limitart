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
package org.slingerxv.limitart.rpcx.message.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.MessageMeta;

public abstract class RPCMeta extends Message {

	protected final Object decodeObj(Class<?> type) throws Exception {
		return decodeObj(type.getName());
	}

	@SuppressWarnings("unchecked")
	protected final Object decodeObj(String type) throws Exception {
		if (type == null || type.equals("null")) {
			return null;
		} else if (type.equals(Integer.class.getName()) || type.equals(int.class.getName())) {
			return getInt();
		} else if (type.equals(int[].class.getName())) {
			return getIntArray();
		} else if (type.equals(Byte.class.getName()) || type.equals(byte.class.getName())) {
			return getByte();
		} else if (type.equals(byte[].class.getName())) {
			return getByteArray();
		} else if (type.equals(Short.class.getName()) || type.equals(short.class.getName())) {
			return getShort();
		} else if (type.equals(short[].class.getName())) {
			return getShortArray();
		} else if (type.equals(Long.class.getName()) || type.equals(long.class.getName())) {
			return getLong();
		} else if (type.equals(long[].class.getName())) {
			return getLongArray();
		} else if (type.equals(Boolean.class.getName()) || type.equals(boolean.class.getName())) {
			return getBoolean();
		} else if (type.equals(boolean[].class.getName())) {
			return getBooleanArray();
		} else if (type.equals(String.class.getName())) {
			return getString();
		} else if (type.equals(String[].class.getName())) {
			return getStringArray();
		} else if (type.equals(Float.class.getName()) || type.equals(float.class.getName())) {
			return getFloat();
		} else if (type.equals(float[].class.getName())) {
			return getFloatArray();
		} else if (type.equals(Double.class.getName()) || type.equals(double.class.getName())) {
			return getDouble();
		} else if (type.equals(double[].class.getName())) {
			return getDoubleArray();
		} else if (type.equals(Character.class.getName()) || type.equals(char.class.getName())) {
			return getChar();
		} else if (type.equals(char[].class.getName())) {
			return getCharArray();
		} else if (type.equals(ArrayList.class.getName()) || type.equals(List.class.getName())) {
			List<Object> objList = new ArrayList<>();
			short length = getShort();
			if (length > 0) {
				String listType = getString();
				for (int i = 0; i < length; ++i) {
					objList.add(decodeObj(listType));
				}
			}
			return objList;
		} else if (type.equals(HashMap.class.getName()) || type.equals(Map.class.getName())) {
			short length = getShort();
			if (length == 0) {
				return null;
			}
			String keyType = getString();
			String valueType = getString();
			Map<Object, Object> map = new HashMap<>();
			for (int i = 0; i < length; ++i) {
				map.put(decodeObj(keyType), decodeObj(valueType));
			}
			return map;
		} else if (type.equals(HashSet.class.getName()) || type.equals(Set.class.getName())) {
			short length = getShort();
			if (length == 0) {
				return null;
			}
			String setType = getString();
			Set<Object> set = new HashSet<>();
			for (int i = 0; i < length; ++i) {
				set.add(decodeObj(setType));
			}
			return set;
		} else if (type.startsWith("[L")) {
			@SuppressWarnings("rawtypes")
			Class forName = Class.forName(type);
			return getMessageMetaArray(forName.getComponentType());
		} else {
			@SuppressWarnings("rawtypes")
			Class forName = Class.forName(type);
			return getMessageMeta(forName);
		}
	}

	@SuppressWarnings("unchecked")
	protected final void encodeObj(Object object) throws Exception {
		if (object == null) {
			return;
		}
		Class<?> type = object.getClass();
		if (type.equals(Integer.class) || type.equals(int.class)) {
			putInt((int) object);
		} else if (type.equals(int[].class)) {
			putIntArray((int[]) object);
		} else if (type.equals(Byte.class) || type.equals(byte.class)) {
			putByte((int) object);
		} else if (type.equals(byte[].class)) {
			putByteArray((byte[]) object);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			putShort((short) object);
		} else if (type.equals(short[].class)) {
			putShortArray((short[]) object);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			putLong((long) object);
		} else if (type.equals(long[].class)) {
			putLongArray((long[]) object);
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			putBoolean((boolean) object);
		} else if (type.equals(boolean[].class)) {
			putBooleanArray((boolean[]) object);
		} else if (type.equals(String.class)) {
			putString((String) object);
		} else if (type.equals(String[].class)) {
			putStringArray((String[]) object);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			putFloat((float) object);
		} else if (type.equals(float[].class)) {
			putFloatArray((float[]) object);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			putDouble((double) object);
		} else if (type.equals(double[].class)) {
			putDoubleArray((double[]) object);
		} else if (type.equals(Character.class) || type.equals(char.class)) {
			putChar((char) object);
		} else if (type.equals(char[].class)) {
			putCharArray((char[]) object);
		} else if (type.equals(ArrayList.class) || type.equals(List.class)) {
			List<Object> objs = (List<Object>) object;
			if (objs.isEmpty()) {
				putShort((short) 0);
			} else {
				putShort((short) objs.size());
				String name = objs.get(0).getClass().getName();
				putString(name);
				for (Object obj : objs) {
					encodeObj(obj);
				}
			}
		} else if (type.equals(HashMap.class) || type.equals(Map.class)) {
			Map<Object, Object> map = (Map<Object, Object>) object;
			if (map.isEmpty()) {
				putShort((short) 0);
			} else {
				putShort((short) map.size());
				boolean f = false;
				for (Entry<Object, Object> next : map.entrySet()) {
					if (!f) {
						f = true;
						putString(next.getKey().getClass().getName());
						putString(next.getValue().getClass().getName());
					}
					encodeObj(next.getKey());
					encodeObj(next.getValue());
				}
			}
		} else if (type.equals(HashSet.class) || type.equals(Set.class)) {
			Set<Object> set = (Set<Object>) object;
			if (set.isEmpty()) {
				putShort((short) 0);
			} else {
				putShort((short) set.size());
				boolean f = false;
				for (Object value : set) {
					if (!f) {
						f = true;
						putString(value.getClass().getName());
					}
					encodeObj(value);
				}
			}
		} else if (object instanceof MessageMeta) {
			putMessageMeta((MessageMeta) object);
		} else if (object.getClass().isArray()
				&& object.getClass().getComponentType().getSuperclass() == MessageMeta.class) {
			putMessageMetaArray((MessageMeta[]) object);
		} else {
			throw new IOException(object.getClass().getName() + " does not supported yet!");
		}
	}
}
