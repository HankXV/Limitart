package com.limitart.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.limitart.util.StringUtil;

/**
 * String键约束型Map
 * 
 * @author hank
 *
 */
public class ConstraintMap<K> {
	private Map<K, Object> map;

	protected ConstraintMap(Map<K, Object> map) {
		this.map = map;
	}

	public ConstraintMap() {
		map = new HashMap<>();
	}

	public boolean hasKey(K key) {
		return map.containsKey(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean removeKey(K key) {
		return map.remove(key) != null;
	}

	public int size() {
		return map.size();
	}

	public void putByte(K key, byte value) {
		map.put(key, value);
	}

	public Byte getByte(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return Byte.valueOf(map.get(key).toString());
	}

	public byte getByte2(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return getByte(key);
	}

	public void putShort(K key, short value) {
		map.put(key, value);
	}

	public Short getShort(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return Short.valueOf(map.get(key).toString());
	}

	public short getShort2(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return getShort(key);
	}

	public void putInt(K key, int value) {
		map.put(key, value);
	}

	public Integer getInt(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return Integer.valueOf(map.get(key).toString());
	}

	public int getInt2(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return getInt(key);
	}

	public void putLong(K key, long value) {
		map.put(key, value);
	}

	public Long getLong(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return Long.valueOf(map.get(key).toString());
	}

	public long getLong2(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return getLong(key);
	}

	public void putFloat(K key, float value) {
		map.put(key, value);
	}

	public Float getFloat(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return Float.valueOf(map.get(key).toString());
	}

	public float getFloat2(K key) {
		if (!hasKey(key)) {
			return 0F;
		}
		return getFloat(key);
	}

	public void putDouble(K key, double value) {
		map.put(key, value);
	}

	public Double getDouble(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return Double.valueOf(map.get(key).toString());
	}

	public double getDouble2(K key) {
		if (!hasKey(key)) {
			return 0D;
		}
		return getDouble(key);
	}

	public void putBoolean(K key, boolean value) {
		putInt(key, value ? 1 : 0);
	}

	public boolean getBoolean(K key) {
		return getInt2(key) == 1;
	}

	public void putString(K key, String value) {
		if (value == null) {
			map.put(key, "");
		} else {
			map.put(key, value);
		}
	}

	public String getString(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return (String) map.get(key);
	}

	public String getString2(K key) {
		if (!hasKey(key)) {
			return "";
		}
		return getString(key);
	}

	public <T> void putObject(K key, T value) {
		map.put(key, value);
	}

	public void putObject2(K key, Object value) {
		map.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return (T) map.get(key);
	}

	public Object getObject2(K key) {
		return map.get(key);
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
}
