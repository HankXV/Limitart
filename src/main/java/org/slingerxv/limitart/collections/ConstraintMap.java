package org.slingerxv.limitart.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slingerxv.limitart.util.StringUtil;

/**
 * String键约束型Map
 * 
 * @author hank
 *
 */
public class ConstraintMap<K> implements IPrimitiveTypeMap<K>, Map<K, Object> {
	private Map<K, Object> map;

	protected ConstraintMap(Map<K, Object> map) {
		this.map = Objects.requireNonNull(map, "map");
	}

	protected ConstraintMap() {
		map = new HashMap<>();
	}

	public void clear() {
		map.clear();
	}

	public int size() {
		return map.size();
	}

	@Override
	public IPrimitiveTypeMap<K> putByte(K key, byte value) {
		put(key, value);
		return this;
	}

	@Override
	public byte getByte(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (byte) get(key);
	}

	@Override
	public IPrimitiveTypeMap<K> putShort(K key, short value) {
		put(key, value);
		return this;
	}

	@Override
	public short getShort(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (short) get(key);
	}

	@Override
	public IPrimitiveTypeMap<K> putInt(K key, int value) {
		put(key, value);
		return this;
	}

	@Override
	public int getInt(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (int) get(key);
	}

	@Override
	public IPrimitiveTypeMap<K> putLong(K key, long value) {
		put(key, value);
		return this;
	}

	@Override
	public long getLong(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (long) get(key);
	}

	@Override
	public IPrimitiveTypeMap<K> putFloat(K key, float value) {
		put(key, value);
		return this;
	}

	@Override
	public float getFloat(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (float) get(key);
	}

	@Override
	public IPrimitiveTypeMap<K> putDouble(K key, double value) {
		put(key, value);
		return this;
	}

	@Override
	public double getDouble(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (double) get(key);
	}

	@Override
	public IPrimitiveTypeMap<K> putChar(K key, char value) {
		put(key, value);
		return this;
	}

	@Override
	public char getChar(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (char) get(key);
	}

	@Override
	public IPrimitiveTypeMap<K> putBoolean(K key, boolean value) {
		putInt(key, value ? 1 : 0);
		return this;
	}

	@Override
	public boolean getBoolean(K key) {
		return getInt(key) == 1;
	}

	@Override
	public IPrimitiveTypeMap<K> putString(K key, String value) {
		if (value == null) {
			put(key, "");
		} else {
			put(key, value);
		}
		return this;
	}

	@Override
	public String getString(K key) {
		if (!containsKey(key)) {
			return "";
		}
		return (String) map.get(key);
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Collection<Object> values() {
		return map.values();
	}

	public Set<Entry<K, Object>> entrySet() {
		return map.entrySet();
	}

	public String toJSON() {
		return StringUtil.toJSON(this.map);
	}

	public String toJSONWithClassInfo() {
		return StringUtil.toJSONWithClassInfo(this.map);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(Objects.requireNonNull(key, "key"));
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(Objects.requireNonNull(value, "value"));
	}

	@Override
	public Object get(Object key) {
		return map.get(Objects.requireNonNull(key, "key"));
	}

	@Override
	public Object put(K key, Object value) {
		return map.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
	}

	@Override
	public IPrimitiveTypeMap<K> putObj(K key, Object value) {
		put(key, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V getObj(K key) {
		return (V) get(key);
	}

	@Override
	public Object remove(Object key) {
		return map.remove(Objects.requireNonNull(key, "key"));
	}

	@Override
	public void putAll(Map<? extends K, ? extends Object> map) {
		this.map.putAll(Objects.requireNonNull(map, "map"));
	}

	public static <K> ConstraintMap<K> fromJSON(String jsonContent) {
		ConstraintMap<K> map = new ConstraintMap<>();
		if (!StringUtil.isEmptyOrNull(jsonContent)) {
			@SuppressWarnings("unchecked")
			HashMap<K, Object> object = StringUtil.toObject(jsonContent, HashMap.class);
			map.putAll(object);
		}
		return map;
	}

	public static <K> ConstraintMap<K> empty() {
		return new ConstraintMap<K>();
	}

	@SuppressWarnings("unchecked")
	public static <K> ConstraintMap<K> just(Object... kvs) {
		Objects.requireNonNull(kvs, "kvs");
		ConstraintMap<K> empty = ConstraintMap.empty();
		for (int i = 0; i < kvs.length; i += 2) {
			Objects.requireNonNull(kvs[i], "key");
			Objects.requireNonNull(kvs[i + 1], "value");
			empty.put((K) kvs[i], kvs[i + 1]);
		}
		return empty;
	}

	public static <K> ConstraintMap<K> from(Map<? extends K, ? extends Object> map) {
		ConstraintMap<K> empty = ConstraintMap.empty();
		empty.putAll(map);
		return empty;
	}

}
