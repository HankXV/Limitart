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
import org.slingerxv.limitart.util.StringUtil;

public abstract class RPCMeta extends Message {

	protected final Object decodeObj(Class<?> type) throws Exception {
		return decodeObj(type.getName());
	}

	@SuppressWarnings("unchecked")
	protected final Object decodeObj(String type) throws Exception {
		if (type == null || type.equals("null")) {
			return null;
		} else if (type.equals("java.lang.Integer") || type.equals("int")) {
			return getInt();
		} else if (type.equals("[I")) {
			return getIntArray();
		} else if (type.equals("java.lang.Byte") || type.equals("byte")) {
			return getByte();
		} else if (type.equals("[B")) {
			return getByteArray();
		} else if (type.equals("java.lang.Short") || type.equals("short")) {
			return getShort();
		} else if (type.equals("[S")) {
			return getShortArray();
		} else if (type.equals("java.lang.Long") || type.equals("long")) {
			return getLong();
		} else if (type.equals("[J")) {
			return getLongArray();
		} else if (type.equals("java.lang.Boolean") || type.equals("boolean")) {
			return getBoolean();
		} else if (type.equals("[Z")) {
			return getBooleanArray();
		} else if (type.equals("java.lang.String")) {
			return getString();
		} else if (type.equals("[Ljava.lang.String;")) {
			return getStringArray();
		} else if (type.equals("java.lang.Float") || type.equals("float")) {
			return getFloat();
		} else if (type.equals("F")) {
			return getFloatArray();
		} else if (type.equals("java.lang.Double") || type.equals("double")) {
			return getDouble();
		} else if (type.equals("[D")) {
			return getDoubleArray();
		} else if (type.equals("java.lang.Character") || type.equals("char")) {
			return getChar();
		} else if (type.equals("[C")) {
			return getCharArray();
		} else if (type.equals("java.util.ArrayList") || type.equals("java.util.List")) {
			List<Object> objList = new ArrayList<>();
			short length = getShort();
			if (length > 0) {
				String listType = getString();
				for (int i = 0; i < length; ++i) {
					objList.add(decodeObj(listType));
				}
			}
			return objList;
		} else if (type.equals("java.util.HashMap") || type.equals("java.util.Map")) {
			short length = getShort();
			if (length == 0) {
				return null;
			}
			String keyType = getString();
			String valueType = getString();
			HashMap<Object, Object> map = new HashMap<>();
			for (int i = 0; i < length; ++i) {
				map.put(decodeObj(keyType), decodeObj(valueType));
			}
			return map;
		} else if (type.equals("java.util.HashSet") || type.equals("java.util.Set")) {
			short length = getShort();
			if (length == 0) {
				return null;
			}
			String setType = getString();
			HashSet<Object> set = new HashSet<>();
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

	protected final void encodeObj(Object object) throws Exception {
		encodeObj(object, null);
	}

	@SuppressWarnings("unchecked")
	protected final void encodeObj(Object object, String type) throws Exception {
		if (object == null) {
			return;
		}
		if (StringUtil.isEmptyOrNull(type)) {
			type = object.getClass().getName();
		}
		if (type.equals("java.lang.Integer") || type.equals("int")) {
			putInt((int) object);
		} else if (type.equals("[I")) {
			putIntArray((int[]) object);
		} else if (type.equals("java.lang.Byte") || type.equals("byte")) {
			putByte((byte) object);
		} else if (type.equals("[B")) {
			putByteArray((byte[]) object);
		} else if (type.equals("java.lang.Short") || type.equals("short")) {
			putShort((short) object);
		} else if (type.equals("[S")) {
			putShortArray((short[]) object);
		} else if (type.equals("java.lang.Long") || type.equals("long")) {
			putLong((long) object);
		} else if (type.equals("[J")) {
			putLongArray((long[]) object);
		} else if (type.equals("java.lang.Boolean") || type.equals("boolean")) {
			putBoolean((boolean) object);
		} else if (type.equals("[Z")) {
			putBooleanArray((boolean[]) object);
		} else if (type.equals("java.lang.String")) {
			putString((String) object);
		} else if (type.equals("[Ljava.lang.String;")) {
			putStringArray((String[]) object);
		} else if (type.equals("java.lang.Float") || type.equals("float")) {
			putFloat((float) object);
		} else if (type.equals("F")) {
			putFloatArray((float[]) object);
		} else if (type.equals("java.lang.Double") || type.equals("double")) {
			putDouble((double) object);
		} else if (type.equals("[D")) {
			putDoubleArray((double[]) object);
		} else if (type.equals("java.lang.Character") || type.equals("char")) {
			putChar((char) object);
		} else if (type.equals("[C")) {
			putCharArray((char[]) object);
		} else if (type.equals("java.util.ArrayList") || type.equals("java.util.List")) {
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
		} else if (type.equals("java.util.HashMap") || type.equals("java.util.Map")) {
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
		} else if (type.equals("java.util.HashSet") || type.equals("java.util.Set")) {
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
