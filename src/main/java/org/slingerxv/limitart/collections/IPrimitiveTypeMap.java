package org.slingerxv.limitart.collections;

/**
 * 基础类型操作Map
 * 
 * @author hank
 *
 * @param <K>
 */
public interface IPrimitiveTypeMap<K> {

	void putByte(K key, byte value);

	byte getByte(K key);

	void putShort(K key, short value);

	short getShort(K key);

	void putInt(K key, int value);

	int getInt(K key);

	void putLong(K key, long value);

	long getLong(K key);

	void putFloat(K key, float value);

	float getFloat(K key);

	void putDouble(K key, double value);

	double getDouble(K key);

	void putBoolean(K key, boolean value);

	boolean getBoolean(K key);

	void putChar(K key, char value);

	char getChar(K key);

	void putString(K key, String value);

	String getString(K key);
}
