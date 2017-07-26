package org.slingerxv.limitart.collections;

/**
 * 基础类型操作Map
 * 
 * @author hank
 *
 * @param <K>
 */
public interface IPrimitiveTypeMap<K> {

	IPrimitiveTypeMap<K> putByte(K key, byte value);

	byte getByte(K key);

	IPrimitiveTypeMap<K> putShort(K key, short value);

	short getShort(K key);

	IPrimitiveTypeMap<K> putInt(K key, int value);

	int getInt(K key);

	IPrimitiveTypeMap<K> putLong(K key, long value);

	long getLong(K key);

	IPrimitiveTypeMap<K> putFloat(K key, float value);

	float getFloat(K key);

	IPrimitiveTypeMap<K> putDouble(K key, double value);

	double getDouble(K key);

	IPrimitiveTypeMap<K> putBoolean(K key, boolean value);

	boolean getBoolean(K key);

	IPrimitiveTypeMap<K> putChar(K key, char value);

	char getChar(K key);

	IPrimitiveTypeMap<K> putString(K key, String value);

	String getString(K key);

	IPrimitiveTypeMap<K> putObj(K key, Object value);

	<V> V getObj(K key);
}
