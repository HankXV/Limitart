package org.slingerxv.limitart.net.binary.message;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slingerxv.limitart.net.binary.message.exception.MessageIOException;
import org.slingerxv.limitart.reflectasm.ConstructorAccess;
import org.slingerxv.limitart.reflectasm.FieldAccess;
import org.slingerxv.limitart.util.filter.FieldFilter;

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
	private static ConcurrentHashMap<Class<? extends MessageMeta>, FieldAccess> messageMetaFieldCache = new ConcurrentHashMap<>();
	private ByteBuf buffer;

	public void encode() throws Exception {
		FieldAccess fieldAccess = getFieldAccess();
		Field[] fields = fieldAccess.getFields();
		for (Field temp : fields) {
			writeField(temp);
		}
	}

	private void writeField(Field field) throws Exception {
		Class<?> type = field.getType();
		Object object = field.get(this);
		if (type.isPrimitive()) {
			if (type == byte.class) {
				putByte(field.getByte(this));
			} else if (type == short.class) {
				putShort(field.getShort(this));
			} else if (type == int.class) {
				putInt(field.getInt(this));
			} else if (type == long.class) {
				putLong(field.getLong(this));
			} else if (type == float.class) {
				putFloat(field.getFloat(this));
			} else if (type == double.class) {
				putDouble(field.getDouble(this));
			} else if (type == char.class) {
				putChar(field.getChar(this));
			} else if (type == boolean.class) {
				putBoolean(field.getBoolean(this));
			}
		} else if (type.isArray()) {
			Class<?> component = type.getComponentType();
			if (component == byte.class) {
				putByteArray((byte[]) object);
			} else if (component == short.class) {
				putShortArray((short[]) object);
			} else if (component == int.class) {
				putIntArray((int[]) object);
			} else if (component == long.class) {
				putLongArray((long[]) object);
			} else if (component == float.class) {
				putFloatArray((float[]) object);
			} else if (component == double.class) {
				putDoubleArray((double[]) object);
			} else if (component == char.class) {
				putCharArray((char[]) object);
			} else if (component == boolean.class) {
				putBooleanArray((boolean[]) object);
			} else if (component == Byte.class) {
				Byte[] temp = (Byte[]) object;
				byte[] temp1 = new byte[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				putByteArray(temp1);
			} else if (component == Short.class) {
				Short[] temp = (Short[]) object;
				short[] temp1 = new short[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				putShortArray(temp1);
			} else if (component == Integer.class) {
				Integer[] temp = (Integer[]) object;
				int[] temp1 = new int[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				putIntArray(temp1);
			} else if (component == Long.class) {
				Long[] temp = (Long[]) object;
				long[] temp1 = new long[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				putLongArray(temp1);
			} else if (component == Float.class) {
				Float[] temp = (Float[]) object;
				float[] temp1 = new float[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				putFloatArray(temp1);
			} else if (component == Double.class) {
				Double[] temp = (Double[]) object;
				double[] temp1 = new double[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				putDoubleArray(temp1);
			} else if (component == Character.class) {
				Character[] temp = (Character[]) object;
				char[] temp1 = new char[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				putCharArray(temp1);
			} else if (component == Boolean.class) {
				Boolean[] temp = (Boolean[]) object;
				boolean[] temp1 = new boolean[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				putBooleanArray(temp1);
			} else if (component.getSuperclass() == MessageMeta.class) {
				putMessageMetaArray((MessageMeta[]) object);
			} else if (component == String.class) {
				putStringArray((String[]) object);
			}
		} else if (List.class.isAssignableFrom(type)) {
			ParameterizedType t = (ParameterizedType) field.getGenericType();
			Class<?> component = (Class<?>) t.getActualTypeArguments()[0];
			if (component == Byte.class) {
				putByteList((ArrayList<Byte>) object);
			} else if (component == Short.class) {
				putShort((short) object);
			} else if (component == Integer.class) {
				putIntList((ArrayList<Integer>) object);
			} else if (component == Long.class) {
				putLongList((ArrayList<Long>) object);
			} else if (component == Float.class) {
				putFloatList((ArrayList<Float>) object);
			} else if (component == Double.class) {
				putDoubleList((ArrayList<Double>) object);
			} else if (component == Character.class) {
				putCharList((ArrayList<Character>) object);
			} else if (component == Boolean.class) {
				putBooleanList((ArrayList<Boolean>) object);
			} else if (component.getSuperclass() == MessageMeta.class) {
				putMessageMetaList((ArrayList<MessageMeta>) object);
			} else if (component == String.class) {
				putStringList((ArrayList<String>) object);
			}
		} else {
			if (type == Byte.class) {
				if (object == null) {
					putByte((byte) 0);
				} else {
					putByte((byte) object);
				}
			} else if (type == Short.class) {
				if (object == null) {
					putShort((short) 0);
				} else {
					putShort((short) object);
				}
			} else if (type == Integer.class) {
				if (object == null) {
					putInt(0);
				} else {
					putInt((int) object);
				}
			} else if (type == Long.class) {
				if (object == null) {
					putLong(0L);
				} else {
					putLong((long) object);
				}
			} else if (type == Float.class) {
				if (object == null) {
					putFloat(0F);
				} else {
					putFloat((float) object);
				}
			} else if (type == Double.class) {
				if (object == null) {
					putDouble(0D);
				} else {
					putDouble((double) object);
				}
			} else if (type == Character.class) {
				if (object == null) {
					putChar((char) 0);
				} else {
					putChar((char) object);
				}
			} else if (type == Boolean.class) {
				putBoolean(field.getBoolean(this));
				if (object == null) {
					putBoolean(false);
				} else {
					putBoolean((boolean) object);
				}
			} else if (type.getSuperclass() == MessageMeta.class) {
				MessageMeta next = (MessageMeta) object;
				putMessageMeta(next);
			} else if (type == String.class) {
				putString((String) object);
			} else {
				throw new MessageIOException(getClass()
						+ " type error(non MessageMeta field must be pritive(or it's box object),array or List. array's component  and List's generic param as the same as non MessageMeta rule ):"
						+ type.getName());
			}
		}
	}

	public void decode() throws Exception {
		FieldAccess fieldAccess = getFieldAccess();
		Field[] fields = fieldAccess.getFields();
		for (Field temp : fields) {
			readField(temp);
		}
	}

	private void readField(Field field) throws IllegalArgumentException, IllegalAccessException, Exception {
		Class<?> type = field.getType();
		if (type.isPrimitive()) {
			if (type == byte.class) {
				field.setByte(this, getByte());
			} else if (type == short.class) {
				field.setShort(this, getShort());
			} else if (type == int.class) {
				field.setInt(this, getInt());
			} else if (type == long.class) {
				field.setLong(this, getLong());
			} else if (type == float.class) {
				putFloat(field.getFloat(this));
				field.setFloat(this, getFloat());
			} else if (type == double.class) {
				field.setDouble(this, getDouble());
			} else if (type == char.class) {
				field.setChar(this, getChar());
			} else if (type == boolean.class) {
				field.setBoolean(this, getBoolean());
			}
		} else if (type.isArray()) {
			Class<?> component = type.getComponentType();
			if (component == byte.class) {
				field.set(this, getByteArray());
			} else if (component == short.class) {
				field.set(this, getShortArray());
			} else if (component == int.class) {
				field.set(this, getIntArray());
			} else if (component == long.class) {
				field.set(this, getLongArray());
			} else if (component == float.class) {
				field.set(this, getFloatArray());
			} else if (component == double.class) {
				field.set(this, getDoubleArray());
			} else if (component == char.class) {
				field.set(this, getCharArray());
			} else if (component == boolean.class) {
				field.set(this, getBooleanArray());
			} else if (component == Byte.class) {
				byte[] temp = getByteArray();
				Byte[] temp1 = new Byte[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				field.set(this, temp1);
			} else if (component == Short.class) {
				short[] temp = getShortArray();
				Short[] temp1 = new Short[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				field.set(this, temp1);
			} else if (component == Integer.class) {
				int[] temp = getIntArray();
				Integer[] temp1 = new Integer[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				field.set(this, temp1);
			} else if (component == Long.class) {
				long[] temp = getLongArray();
				Long[] temp1 = new Long[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				field.set(this, temp1);
			} else if (component == Float.class) {
				float[] temp = getFloatArray();
				Float[] temp1 = new Float[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				field.set(this, temp1);
			} else if (component == Double.class) {
				double[] temp = getDoubleArray();
				Double[] temp1 = new Double[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				field.set(this, temp1);
			} else if (component == Character.class) {
				char[] temp = getCharArray();
				Character[] temp1 = new Character[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				field.set(this, temp1);
			} else if (component == Boolean.class) {
				boolean[] temp = getBooleanArray();
				Boolean[] temp1 = new Boolean[temp.length];
				for (int i = 0; i < temp.length; ++i) {
					temp1[i] = temp[i];
				}
				field.set(this, temp1);
			} else if (component.getSuperclass() == MessageMeta.class) {
				field.set(this, getMessageMetaArray((Class<? extends MessageMeta>) component));
			} else if (component == String.class) {
				field.set(this, getStringArray());
			}
		} else if (List.class.isAssignableFrom(type)) {
			ParameterizedType t = (ParameterizedType) field.getGenericType();
			Class<?> component = (Class<?>) t.getActualTypeArguments()[0];
			if (component == Byte.class) {
				field.set(this, getByteList());
			} else if (component == Short.class) {
				field.set(this, getShortList());
			} else if (component == Integer.class) {
				field.set(this, getIntList());
			} else if (component == Long.class) {
				field.set(this, getLongList());
			} else if (component == Float.class) {
				field.set(this, getFloatList());
			} else if (component == Double.class) {
				field.set(this, getDoubleList());
			} else if (component == Character.class) {
				field.set(this, getCharList());
			} else if (component == Boolean.class) {
				field.set(this, getBooleanList());
			} else if (component.getSuperclass() == MessageMeta.class) {
				field.set(this, getMessageMetaList((Class<? extends MessageMeta>) component));
			} else if (component == String.class) {
				field.set(this, getStringList());
			}
		} else {
			if (type == Byte.class) {
				field.set(this, getByte());
			} else if (type == Short.class) {
				field.set(this, getShort());
			} else if (type == Integer.class) {
				field.set(this, getInt());
			} else if (type == Long.class) {
				field.set(this, getLong());
			} else if (type == Float.class) {
				field.set(this, getFloat());
			} else if (type == Double.class) {
				field.set(this, getDouble());
			} else if (type == Character.class) {
				field.set(this, getChar());
			} else if (type == Boolean.class) {
				field.set(this, getBoolean());
			} else if (type.getSuperclass() == MessageMeta.class) {
				field.set(this, getMessageMeta((Class<? extends MessageMeta>) type));
			} else if (type == String.class) {
				field.set(this, getString());
			} else {
				throw new MessageIOException(getClass()
						+ " type error(non MessageMeta field must be pritive(or it's box object),array or List. array's component  and List's generic param as the same as non MessageMeta rule ):"
						+ type.getName());
			}
		}
	}

	private FieldAccess getFieldAccess() {
		FieldAccess fieldAccess = messageMetaFieldCache.get(getClass());
		if (fieldAccess == null) {
			fieldAccess = FieldAccess.get(getClass(), false, field -> {
				return !(FieldFilter.isStatic(field) || FieldFilter.isTransient(field));
			});
			FieldAccess put = messageMetaFieldCache.putIfAbsent(getClass(), fieldAccess);
			if (put != null) {
				fieldAccess = put;
			}
		}
		return fieldAccess;
	}

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
		newInstance.buffer(null);
		return newInstance;
	}

	/**
	 * 写入二进制元数据列表
	 * 
	 * @param buffer
	 * @param value
	 * @throws Exception
	 */
	protected final <T extends MessageMeta> void putMessageMetaList(ArrayList<T> value) throws Exception {
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
	protected final <T extends MessageMeta> ArrayList<T> getMessageMetaList(Class<T> clazz) throws Exception {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<>();
		} else {
			ArrayList<T> list = new ArrayList<>();
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
	protected final void putStringList(ArrayList<String> value) {
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
	protected final ArrayList<String> getStringList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<String>();
		} else {
			ArrayList<String> list = new ArrayList<String>();
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
	protected final void putLongList(ArrayList<Long> value) {
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
	protected final ArrayList<Long> getLongList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Long>();
		} else {
			ArrayList<Long> list = new ArrayList<Long>();
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
	protected final void putIntList(ArrayList<Integer> value) {
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
	protected final ArrayList<Integer> getIntList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Integer>();
		} else {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < len; ++i) {
				list.add(getInt());
			}
			return list;
		}
	}

	/**
	 * 读取byte列表
	 * 
	 * @param buffer
	 * @return
	 */
	protected final ArrayList<Byte> getByteList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Byte>();
		} else {
			ArrayList<Byte> list = new ArrayList<Byte>();
			for (int i = 0; i < len; ++i) {
				list.add(getByte());
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
	 * 写入byte列表
	 * 
	 * @param buffer
	 * @param value
	 */
	protected final void putByteList(ArrayList<Byte> value) {
		if (value == null) {
			putShort((short) -1);
		} else if (value.isEmpty()) {
			putShort((short) 0);
		} else {
			putShort((short) value.size());
			for (Byte temp : value) {
				putByte(temp);
			}
		}
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
	protected final void putByteArrayList(ArrayList<byte[]> list) {
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
	protected final ArrayList<byte[]> getByteArrayList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<>();
		} else {
			ArrayList<byte[]> list = new ArrayList<>();
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
	protected final void putBooleanList(ArrayList<Boolean> value) {
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
	protected final ArrayList<Boolean> getBooleanList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Boolean>();
		} else {
			ArrayList<Boolean> list = new ArrayList<Boolean>();
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
	protected final void putFloatList(ArrayList<Float> value) {
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
	protected final ArrayList<Float> getFloatList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<>();
		} else {
			ArrayList<Float> list = new ArrayList<>();
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
	protected final void putDoubleList(ArrayList<Double> value) {
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
	protected final ArrayList<Double> getDoubleList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Double>();
		} else {
			ArrayList<Double> list = new ArrayList<Double>();
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
	protected final void putShortList(ArrayList<Short> value) {
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
	protected final ArrayList<Short> getShortList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Short>();
		} else {
			ArrayList<Short> list = new ArrayList<Short>();
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
	protected final void putCharList(ArrayList<Character> value) {
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
	protected final ArrayList<Character> getCharList() {
		short len = getShort();
		if (len == -1) {
			return null;
		} else if (len == 0) {
			return new ArrayList<Character>();
		} else {
			ArrayList<Character> list = new ArrayList<Character>();
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
