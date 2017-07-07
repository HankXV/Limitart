package com.limitart.net.binary.message;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.limitart.reflectasm.ConstructorAccess;
import com.limitart.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;

/**
 * 二进制元数据
 * 
 * @author Hank
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class MessageMeta {
	private static final boolean COMPRESS_INT = false;
	private static ConcurrentHashMap<Class<? extends MessageMeta>, ConstructorAccess> messageMetaCache = new ConcurrentHashMap<>();
	private ByteBuf buffer;

	public abstract void encode() throws Exception;

	public abstract void decode() throws Exception;

	public void buffer(ByteBuf buffer) {
		this.buffer = buffer;
	}

	public ByteBuf buffer() {
		return this.buffer;
	}

	/**
	 * 写入二进制元数据
	 * 
	 * @param buffer
	 * @param meta
	 * @throws Exception
	 */
	protected final void putMessageMeta(MessageMeta meta) throws Exception {
		if (meta == null) {
			putByte((byte) 0);
		} else {
			putByte((byte) 1);
			meta.buffer = this.buffer;
			meta.encode();
		}
	}

	/**
	 * 读取二进制元数据
	 * 
	 * @param buffer
	 * @param out
	 * @throws Exception
	 */
	protected final <T extends MessageMeta> T getMessageMeta(Class<T> clazz) throws Exception {
		byte len = getByte();
		if (len == 0) {
			return null;
		}
		T newInstance = createInstance(clazz);
		newInstance.buffer(this.buffer);
		newInstance.decode();
		return newInstance;
	}

	/**
	 * 写入二进制元数据列表
	 * 
	 * @param buffer
	 * @param value
	 * @throws Exception
	 */
	protected final <T extends MessageMeta> void putMessageMetaList(List<T> value) throws Exception {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (MessageMeta temp : value) {
				putMessageMeta(temp);
			}
		}
	}

	/**
	 * 读取二进制元数据列表
	 * 
	 * @param buffer
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	protected final <T extends MessageMeta> List<T> getMessageMetaList(Class<T> clazz) throws Exception {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<>();
		} else {
			List<T> list = new ArrayList<>();
			for (int i = 0; i < len; ++i) {
				T messageMeta = getMessageMeta(clazz);
				list.add(messageMeta);
			}
			return list;
		}
	}

	/**
	 * 写入二进制元数据数组
	 * 
	 * @param buffer
	 * @param value
	 * @throws Exception
	 */
	protected final <T extends MessageMeta> void putMessageMetaArray(T[] value) throws Exception {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (T t : value) {
				putMessageMeta(t);
			}
		}
	}

	/**
	 * 读取二进制元数据数组
	 * 
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	protected final <T extends MessageMeta> T[] getMessageMetaArray(Class<T> clazz) throws Exception {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return (T[]) Array.newInstance(clazz, 0);
		} else {
			T[] result = (T[]) Array.newInstance(clazz, length);
			for (int i = 0; i < length; ++i) {
				result[i] = getMessageMeta(clazz);
			}
			return result;
		}
	}

	/**
	 * 写入String类型
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putString(String value) {
		if (value == null) {
			putByteArray(null);
		} else if ("".equals(value)) {
			putByteArray(new byte[0]);
		} else {
			byte[] bytes = value.getBytes(CharsetUtil.UTF_8);
			putByteArray(bytes);
		}
	}

	/**
	 * 读取String类型
	 * 
	 * @param buffer
	 * @return
	 */
	protected final String getString() {
		byte[] bytes = getByteArray();
		if (bytes == null) {
			return null;
		} else if (bytes.length == 0) {
			return "";
		} else {
			return new String(bytes, CharsetUtil.UTF_8);
		}
	}

	/**
	 * 写入String列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putStringList(List<String> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (String temp : value) {
				putString(temp);
			}
		}
	}

	/**
	 * 读取String列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<String> getStringList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<String>();
		} else {
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < len; ++i) {
				list.add(getString());
			}
			return list;
		}
	}

	/**
	 * 写入字符串数组
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putStringArray(String[] value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (String temp : value) {
				putString(temp);
			}
		}
	}

	/**
	 * 读取字符串数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final String[] getStringArray() {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return new String[0];
		} else {
			String[] result = new String[length];
			for (int i = 0; i < length; ++i) {
				result[i] = getString();
			}
			return result;
		}
	}

	/**
	 * 写入long数据
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putLong(long value) {
		buffer.writeLong(value);
	}

	/**
	 * 读取long数据
	 * 
	 * @param buffer
	 * @return
	 */
	protected final long getLong() {
		return buffer.readLong();
	}

	/**
	 * 写入long列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putLongList(List<Long> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (Long temp : value) {
				putLong(temp);
			}
		}
	}

	/**
	 * 读取long列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<Long> getLongList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Long>();
		} else {
			List<Long> list = new ArrayList<Long>();
			for (int i = 0; i < len; ++i) {
				list.add(getLong());
			}
			return list;
		}
	}

	/**
	 * 写入long数组
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putLongArray(long[] value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (long temp : value) {
				putLong(temp);
			}
		}
	}

	/**
	 * 读取long数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final long[] getLongArray() {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return new long[0];
		} else {
			long[] result = new long[length];
			for (int i = 0; i < length; ++i) {
				result[i] = getLong();
			}
			return result;
		}
	}

	/**
	 * 写入int数据
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putInt(int value) {
		if (COMPRESS_INT) {
			writeRawVarint32(value);
		} else {
			this.buffer.writeInt(value);
		}
	}

	/**
	 * 读取int数据
	 * 
	 * @param buffer
	 * @return
	 */
	protected final int getInt() {
		if (COMPRESS_INT) {
			return readRawVarint32();
		} else {
			return this.buffer.readInt();
		}
	}

	/**
	 * 写入int列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putIntList(List<Integer> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (Integer temp : value) {
				putInt(temp);
			}
		}
	}

	/**
	 * 读取int列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<Integer> getIntList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Integer>();
		} else {
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < len; ++i) {
				list.add(getInt());
			}
			return list;
		}
	}

	/**
	 * 写入int数组
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putIntArray(int[] value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (int temp : value) {
				putInt(temp);
			}
		}
	}

	/**
	 * 读取int数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final int[] getIntArray() {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return new int[0];
		} else {
			int[] result = new int[length];
			for (int i = 0; i < length; ++i) {
				result[i] = getInt();
			}
			return result;
		}
	}

	/**
	 * 写入byte数据
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putByte(byte value) {
		buffer.writeByte(value);
	}

	/**
	 * 读取byte数据
	 * 
	 * @param buffer
	 * @return
	 */
	protected final byte getByte() {
		return buffer.readByte();
	}

	/**
	 * 写入byte[]列表
	 * 
	 * @param buffer
	 * @param list
	 */
	protected final void putByteArrayList(List<byte[]> list) {
		if (list == null) {
			putShort((short) -1);
		} else if (list.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) list.size());
			for (byte[] bt : list) {
				putByteArray(bt);
			}
		}
	}

	/**
	 * 读取byte[]列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<byte[]> getByteArrayList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<>();
		} else {
			List<byte[]> list = new ArrayList<>();
			list.add(getByteArray());
			return list;
		}
	}

	/**
	 * 写入byte数组
	 * 
	 * @param buffer
	 * @param bytes
	 */
	protected final void putByteArray(byte[] bytes) {
		if (bytes == null) {
			putShort((short) -1);
		} else if (bytes.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) bytes.length);
			buffer.writeBytes(bytes);
		}
	}

	/**
	 * 读取byte数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final byte[] getByteArray() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new byte[0];
		} else {
			byte[] bytes = new byte[len];
			buffer.readBytes(bytes, 0, len);
			return bytes;
		}
	}

	/**
	 * 写入bool数据
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putBoolean(boolean value) {
		buffer.writeBoolean(value);
	}

	/**
	 * 读取bool数据
	 * 
	 * @param buffer
	 * @return
	 */
	protected final boolean getBoolean() {
		return buffer.readBoolean();
	}

	/**
	 * 写入bool列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putBooleanList(List<Boolean> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (boolean temp : value) {
				putBoolean(temp);
			}
		}
	}

	/**
	 * 读取bool列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<Boolean> getBooleanList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Boolean>();
		} else {
			List<Boolean> list = new ArrayList<Boolean>();
			for (int i = 0; i < len; ++i) {
				list.add(getBoolean());
			}
			return list;
		}
	}

	/**
	 * 写入bool数组
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putBooleanArray(boolean[] value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (boolean temp : value) {
				putBoolean(temp);
			}
		}
	}

	/**
	 * 读取bool数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final boolean[] getBooleanArray() {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return new boolean[0];
		} else {
			boolean[] result = new boolean[length];
			for (int i = 0; i < length; ++i) {
				result[i] = getBoolean();
			}
			return result;
		}
	}

	/**
	 * 写入float数据
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putFloat(float value) {
		buffer.writeFloat(value);
	}

	/**
	 * 读取float数据
	 * 
	 * @param buffer
	 * @return
	 */
	protected final float getFloat() {
		return buffer.readFloat();
	}

	/**
	 * 写入float列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putFloatList(List<Float> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (Float temp : value) {
				putFloat(temp);
			}
		}
	}

	/**
	 * 读取float列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<Float> getFloatList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<>();
		} else {
			List<Float> list = new ArrayList<>();
			for (int i = 0; i < len; ++i) {
				list.add(getFloat());
			}
			return list;
		}
	}

	/**
	 * 写入float数组
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putFloatArray(float[] value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (float temp : value) {
				putFloat(temp);
			}
		}
	}

	/**
	 * 读取float数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final float[] getFloatArray() {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return new float[0];
		} else {
			float[] result = new float[length];
			for (int i = 0; i < length; ++i) {
				result[i] = getFloat();
			}
			return result;
		}
	}

	/**
	 * 写入double数据
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putDouble(double value) {
		buffer.writeDouble(value);
	}

	/**
	 * 读取double数据
	 * 
	 * @param buffer
	 * @return
	 */
	protected final double getDouble() {
		return buffer.readDouble();
	}

	/**
	 * 写入double列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putDoubleList(List<Double> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (Double temp : value) {
				putDouble(temp);
			}
		}
	}

	/**
	 * 读取double列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<Double> getDoubleList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Double>();
		} else {
			List<Double> list = new ArrayList<Double>();
			for (int i = 0; i < len; ++i) {
				list.add(getDouble());
			}
			return list;
		}
	}

	/**
	 * 写入double数组
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putDoubleArray(double[] value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (double temp : value) {
				putDouble(temp);
			}
		}
	}

	/**
	 * 读取double数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final double[] getDoubleArray() {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return new double[0];
		} else {
			double[] result = new double[length];
			for (int i = 0; i < length; ++i) {
				result[i] = getDouble();
			}
			return result;
		}
	}

	/**
	 * 写入short数据
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putShort(short value) {
		buffer.writeShort(value);
	}

	/**
	 * 读取short数据
	 * 
	 * @param buffer
	 * @return
	 */
	protected final short getShort() {
		return buffer.readShort();
	}

	/**
	 * 写入short列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putShortList(List<Short> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (Short temp : value) {
				putShort(temp);
			}
		}
	}

	/**
	 * 读取short列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<Short> getShortList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Short>();
		} else {
			List<Short> list = new ArrayList<Short>();
			for (int i = 0; i < len; ++i) {
				list.add(getShort());
			}
			return list;
		}
	}

	/**
	 * 写入short数组
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putShortArray(short[] value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (short temp : value) {
				putShort(temp);
			}
		}
	}

	/**
	 * 读取short数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final short[] getShortArray() {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return new short[0];
		} else {
			short[] result = new short[length];
			for (int i = 0; i < length; ++i) {
				result[i] = getShort();
			}
			return result;
		}
	}

	/**
	 * 写入char数据
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putChar(char value) {
		buffer.writeChar(value);
	}

	/**
	 * 读取char数据
	 * 
	 * @param buffer
	 * @return
	 */
	protected final char getChar() {
		return buffer.readChar();
	}

	/**
	 * 写入char列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putCharList(List<Character> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (Character temp : value) {
				putChar(temp);
			}
		}
	}

	/**
	 * 读取char列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final List<Character> getCharList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Character>();
		} else {
			List<Character> list = new ArrayList<Character>();
			for (int i = 0; i < len; ++i) {
				list.add(getChar());
			}
			return list;
		}
	}

	/**
	 * 写入char数组
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putCharArray(char[] value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.length == 0) {
			putShort((short) 0);
		} else {
			putShort((short) value.length);
			for (char temp : value) {
				putChar(temp);
			}
		}
	}

	/**
	 * 读取char数组
	 * 
	 * @param buffer
	 * @return
	 */
	protected final char[] getCharArray() {
		short length = getShort();
		if (length == -1) {
			return null;
		} else if (length == 0) {
			return new char[0];
		} else {
			char[] result = new char[length];
			for (int i = 0; i < length; ++i) {
				result[i] = getChar();
			}
			return result;
		}
	}

	protected final Object decodeObj(Class<?> type) throws Exception {
		return decodeObj(type.getName());
	}

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
			Class forName = Class.forName(type);
			return getMessageMetaArray(forName.getComponentType());
		} else {
			Class forName = Class.forName(type);
			return getMessageMeta(forName);
		}
	}

	protected final void encodeObj(Object object) throws Exception {
		encodeObj(object, null);
	}

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

	private void writeRawVarint32(int value) {
		while (true) {
			if ((value & 0xFFFFFF80) == 0) {
				this.buffer.writeByte(value);
				return;
			}
			this.buffer.writeByte(value & 0x7F | 0x80);
			value >>>= 7;
		}
	}

	private int readRawVarint32() {
		if (!(buffer.isReadable())) {
			return 0;
		}
		buffer.markReaderIndex();
		byte tmp = buffer.readByte();
		if (tmp >= 0) {
			return tmp;
		}
		int result = tmp & 0x7F;
		if (!(buffer.isReadable())) {
			buffer.resetReaderIndex();
			return 0;
		}
		if ((tmp = buffer.readByte()) >= 0) {
			result |= tmp << 7;
		} else {
			result |= (tmp & 0x7F) << 7;
			if (!(buffer.isReadable())) {
				buffer.resetReaderIndex();
				return 0;
			}
			if ((tmp = buffer.readByte()) >= 0) {
				result |= tmp << 14;
			} else {
				result |= (tmp & 0x7F) << 14;
				if (!(buffer.isReadable())) {
					buffer.resetReaderIndex();
					return 0;
				}
				if ((tmp = buffer.readByte()) >= 0) {
					result |= tmp << 21;
				} else {
					result |= (tmp & 0x7F) << 21;
					if (!(buffer.isReadable())) {
						buffer.resetReaderIndex();
						return 0;
					}
					result |= (tmp = buffer.readByte()) << 28;
					if (tmp < 0) {
						throw new CorruptedFrameException("malformed varint.");
					}
				}
			}
		}
		return result;
	}

	private <T extends MessageMeta> T createInstance(Class<T> clazz) {
		ConstructorAccess constructorAccess = messageMetaCache.get(clazz);
		if (constructorAccess == null) {
			constructorAccess = ConstructorAccess.get(clazz);
			ConstructorAccess putIfAbsent = messageMetaCache.putIfAbsent(clazz, constructorAccess);
			if (putIfAbsent != null) {
				constructorAccess = putIfAbsent;
			}
		}
		return (T) constructorAccess.newInstance();
	}
}
