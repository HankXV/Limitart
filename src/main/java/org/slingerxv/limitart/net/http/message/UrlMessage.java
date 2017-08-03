package org.slingerxv.limitart.net.http.message;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.net.http.constant.QueryMethod;
import org.slingerxv.limitart.reflectasm.FieldAccess;
import org.slingerxv.limitart.util.filter.FieldFilter;

import io.netty.channel.Channel;

public abstract class UrlMessage extends ConstraintMap<String> {
	private static ConcurrentHashMap<Class<? extends UrlMessage>, FieldAccess> messageMetaFieldCache = new ConcurrentHashMap<>();
	private transient Channel channel;
	private transient HashMap<String, byte[]> files = new HashMap<>();
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

	public HashMap<String, byte[]> getFiles() {
		return files;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
}
