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
		Objects.requireNonNull(map, "map");
		this.map = map;
	}

	public ConstraintMap() {
		map = new HashMap<>();
	}

	public void clear() {
		map.clear();
	}

	public int size() {
		return map.size();
	}

	@Override
	public void putByte(K key, byte value) {
		put(key, value);
	}

	@Override
	public byte getByte(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (byte) get(key);
	}

	@Override
	public void putShort(K key, short value) {
		put(key, value);
	}

	@Override
	public short getShort(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (short) get(key);
	}

	@Override
	public void putInt(K key, int value) {
		put(key, value);
	}

	@Override
	public int getInt(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (int) get(key);
	}

	@Override
	public void putLong(K key, long value) {
		put(key, value);
	}

	@Override
	public long getLong(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (long) get(key);
	}

	@Override
	public void putFloat(K key, float value) {
		put(key, value);
	}

	@Override
	public float getFloat(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (float) get(key);
	}

	@Override
	public void putDouble(K key, double value) {
		put(key, value);
	}

	@Override
	public double getDouble(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (double) get(key);
	}

	@Override
	public void putChar(K key, char value) {
		put(key, value);
	}

	@Override
	public char getChar(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return (char) get(key);
	}

	@Override
	public void putBoolean(K key, boolean value) {
		putInt(key, value ? 1 : 0);
	}

	@Override
	public boolean getBoolean(K key) {
		return getInt(key) == 1;
	}

	@Override
	public void putString(K key, String value) {
		if (value == null) {
			put(key, "");
		} else {
			put(key, value);
		}
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

	private void fromJSON(String jsonContent) {
		if (!StringUtil.isEmptyOrNull(jsonContent)) {
			@SuppressWarnings("unchecked")
			HashMap<K, Object> object = StringUtil.toObject(jsonContent, HashMap.class);
			map.putAll(object);
		}
	}

	public static <T> ConstraintMap<T> createMap(String jsonContent) {
		ConstraintMap<T> map = new ConstraintMap<>();
		map.fromJSON(jsonContent);
		return map;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		Objects.requireNonNull(key, "key");
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		Objects.requireNonNull(value, "value");
		return map.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		Objects.requireNonNull(key, "key");
		return map.get(key);
	}

	@Override
	public Object put(K key, Object value) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(value, "value");
		return map.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		Objects.requireNonNull(key, "key");
		return map.remove(key) != null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends Object> map) {
		Objects.requireNonNull(map, "map");
		this.map.putAll(map);
	}

}
