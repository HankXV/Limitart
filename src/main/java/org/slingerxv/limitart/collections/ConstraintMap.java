package org.slingerxv.limitart.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slingerxv.limitart.util.StringUtil;

/**
 * String键约束型Map
 * 
 * @author hank
 *
 */
public class ConstraintMap<K> implements IPrimitiveTypeMap<K> {
	private Map<K, Object> map;

	protected ConstraintMap(Map<K, Object> map) {
		this.map = map;
	}

	public ConstraintMap() {
		map = new HashMap<>();
	}

	public void putAll(Map<K, Object> map) {
		this.map.putAll(map);
	}

	public void putAll(ConstraintMap<K> map) {
		this.map.putAll(map.map);
	}

	public void clear() {
		map.clear();
	}

	public boolean hasKey(K key) {
		return map.containsKey(key);
	}

	public boolean removeKey(K key) {
		return map.remove(key) != null;
	}

	public int size() {
		return map.size();
	}

	@Override
	public void putByte(K key, byte value) {
		map.put(key, value);
	}

	@Override
	public byte getByte(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return (byte) map.get(key);
	}

	@Override
	public void putShort(K key, short value) {
		map.put(key, value);
	}

	@Override
	public short getShort(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return (short) map.get(key);
	}

	@Override
	public void putInt(K key, int value) {
		map.put(key, value);
	}

	@Override
	public int getInt(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return (int) map.get(key);
	}

	@Override
	public void putLong(K key, long value) {
		map.put(key, value);
	}

	@Override
	public long getLong(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return (long) map.get(key);
	}

	@Override
	public void putFloat(K key, float value) {
		map.put(key, value);
	}

	@Override
	public float getFloat(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return (float) map.get(key);
	}

	@Override
	public void putDouble(K key, double value) {
		map.put(key, value);
	}

	@Override
	public double getDouble(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return (double) map.get(key);
	}

	@Override
	public void putChar(K key, char value) {
		map.put(key, value);
	}

	@Override
	public char getChar(K key) {
		if (!hasKey(key)) {
			return 0;
		}
		return (char) map.get(key);
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
			map.put(key, "");
		} else {
			map.put(key, value);
		}
	}

	@Override
	public String getString(K key) {
		if (!hasKey(key)) {
			return "";
		}
		return (String) map.get(key);
	}

	public <T> void putObject(K key, T value) {
		map.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(K key) {
		if (!hasKey(key)) {
			return null;
		}
		return (T) map.get(key);
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
