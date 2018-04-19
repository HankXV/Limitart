/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.net.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;
import org.slingerxv.limitart.reflectasm.ConstructorAccess;
import org.slingerxv.limitart.reflectasm.FieldAccess;
import org.slingerxv.limitart.util.ReflectionUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 二进制元数据
 *
 * @author Hank
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BinaryMeta {
    private static boolean COMPRESS_INT32_64 = true;
    private final static Map<Class<? extends BinaryMeta>, ConstructorAccess> messageMetaCache = new ConcurrentHashMap<>();
    private final static Map<Class<? extends BinaryMeta>, FieldAccess> messageMetaFieldCache = new ConcurrentHashMap<>();
    private ByteBuf buffer;

    /**
     * 设置是否开启int和long压缩
     *
     * @param open
     */
    public static void compressOpen(boolean open) {
        COMPRESS_INT32_64 = open;
    }

    public void encode() throws IllegalArgumentException, IllegalAccessException, BinaryMessageCodecException {
        FieldAccess fieldAccess = getFieldAccess();
        Field[] fields = fieldAccess.getFields();
        for (Field temp : fields) {
            writeField(temp);
        }
    }

    private void writeField(Field field)
            throws BinaryMessageCodecException, IllegalArgumentException, IllegalAccessException {
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
                if (object == null) {
                    putByteArray(null);
                } else {
                    Byte[] temp = (Byte[]) object;
                    byte[] temp1 = new byte[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i] == null ? 0 : temp[i];
                    }
                    putByteArray(temp1);
                }
            } else if (component == Short.class) {
                if (object == null) {
                    putShortArray(null);
                } else {
                    Short[] temp = (Short[]) object;
                    short[] temp1 = new short[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i] == null ? 0 : temp[i];
                    }
                    putShortArray(temp1);
                }
            } else if (component == Integer.class) {
                if (object == null) {
                    putIntArray(null);
                } else {
                    Integer[] temp = (Integer[]) object;
                    int[] temp1 = new int[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i] == null ? 0 : temp[i];
                    }
                    putIntArray(temp1);
                }
            } else if (component == Long.class) {
                if (object == null) {
                    putLongArray(null);
                } else {
                    Long[] temp = (Long[]) object;
                    long[] temp1 = new long[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i] == null ? 0 : temp[i];
                    }
                    putLongArray(temp1);
                }
            } else if (component == Float.class) {
                if (object == null) {
                    putFloatArray(null);
                } else {
                    Float[] temp = (Float[]) object;
                    float[] temp1 = new float[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i] == null ? 0 : temp[i];
                    }
                    putFloatArray(temp1);
                }
            } else if (component == Double.class) {
                if (object == null) {
                    putDoubleArray(null);
                } else {
                    Double[] temp = (Double[]) object;
                    double[] temp1 = new double[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i] == null ? 0 : temp[i];
                    }
                    putDoubleArray(temp1);
                }
            } else if (component == Character.class) {
                if (object == null) {
                    putCharArray(null);
                } else {
                    Character[] temp = (Character[]) object;
                    char[] temp1 = new char[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i] == null ? 0 : temp[i];
                    }
                    putCharArray(temp1);
                }
            } else if (component == Boolean.class) {
                if (object == null) {
                    putBooleanArray(null);
                } else {
                    Boolean[] temp = (Boolean[]) object;
                    boolean[] temp1 = new boolean[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i] == null ? false : temp[i];
                    }
                    putBooleanArray(temp1);
                }
            } else if (component.getSuperclass() == BinaryMeta.class) {
                putMessageMetaArray((BinaryMeta[]) object);
            } else if (component == String.class) {
                putStringArray((String[]) object);
            }
        } else if (List.class.isAssignableFrom(type)) {
            ParameterizedType t = (ParameterizedType) field.getGenericType();
            Class<?> component = (Class<?>) t.getActualTypeArguments()[0];
            if (component == Byte.class) {
                putByteList((List<Byte>) object);
            } else if (component == Short.class) {
                putShortList((List<Short>) object);
            } else if (component == Integer.class) {
                putIntList((List<Integer>) object);
            } else if (component == Long.class) {
                putLongList((List<Long>) object);
            } else if (component == Float.class) {
                putFloatList((List<Float>) object);
            } else if (component == Double.class) {
                putDoubleList((List<Double>) object);
            } else if (component == Character.class) {
                putCharList((List<Character>) object);
            } else if (component == Boolean.class) {
                putBooleanList((List<Boolean>) object);
            } else if (component.getSuperclass() == BinaryMeta.class) {
                putMessageMetaList((List<BinaryMeta>) object);
            } else if (component == String.class) {
                putStringList((List<String>) object);
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
                if (object == null) {
                    putBoolean(false);
                } else {
                    putBoolean((boolean) object);
                }
            } else if (type.getSuperclass() == BinaryMeta.class) {
                BinaryMeta next = (BinaryMeta) object;
                putMessageMeta(next);
            } else if (type == String.class) {
                putString((String) object);
            } else {
                throw new BinaryMessageCodecException(getClass()
                        + " type error(non MessageMeta field must be primitive(or it's box object),array or List. array's component  and List's generic param as the same as non MessageMeta rule ):"
                        + type.getName());
            }
        }
    }

    public void decode() throws IllegalArgumentException, IllegalAccessException, BinaryMessageCodecException {
        FieldAccess fieldAccess = getFieldAccess();
        Field[] fields = fieldAccess.getFields();
        for (Field temp : fields) {
            readField(temp);
        }
    }

    private void readField(Field field)
            throws IllegalArgumentException, IllegalAccessException, BinaryMessageCodecException {
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
                if (temp != null) {
                    Byte[] temp1 = new Byte[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i];
                    }
                    field.set(this, temp1);
                }
            } else if (component == Short.class) {
                short[] temp = getShortArray();
                if (temp != null) {
                    Short[] temp1 = new Short[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i];
                    }
                    field.set(this, temp1);
                }
            } else if (component == Integer.class) {
                int[] temp = getIntArray();
                if (temp != null) {
                    Integer[] temp1 = new Integer[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i];
                    }
                    field.set(this, temp1);
                }
            } else if (component == Long.class) {
                long[] temp = getLongArray();
                if (temp != null) {
                    Long[] temp1 = new Long[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i];
                    }
                    field.set(this, temp1);
                }
            } else if (component == Float.class) {
                float[] temp = getFloatArray();
                if (temp != null) {
                    Float[] temp1 = new Float[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i];
                    }
                    field.set(this, temp1);
                }
            } else if (component == Double.class) {
                double[] temp = getDoubleArray();
                if (temp != null) {
                    Double[] temp1 = new Double[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i];
                    }
                    field.set(this, temp1);
                }
            } else if (component == Character.class) {
                char[] temp = getCharArray();
                if (temp != null) {
                    Character[] temp1 = new Character[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i];
                    }
                    field.set(this, temp1);
                }
            } else if (component == Boolean.class) {
                boolean[] temp = getBooleanArray();
                if (temp != null) {
                    Boolean[] temp1 = new Boolean[temp.length];
                    for (int i = 0; i < temp.length; ++i) {
                        temp1[i] = temp[i];
                    }
                    field.set(this, temp1);
                }
            } else if (component.getSuperclass() == BinaryMeta.class) {
                field.set(this, getMessageMetaArray((Class<? extends BinaryMeta>) component));
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
            } else if (component.getSuperclass() == BinaryMeta.class) {
                field.set(this, getMessageMetaList((Class<? extends BinaryMeta>) component));
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
            } else if (type.getSuperclass() == BinaryMeta.class) {
                field.set(this, getMessageMeta((Class<? extends BinaryMeta>) type));
            } else if (type == String.class) {
                field.set(this, getString());
            } else {
                throw new BinaryMessageCodecException(getClass()
                        + " type error(non MessageMeta field must be primitive(or it's box object),array or List. array's component  and List's generic param as the same as non MessageMeta rule ):"
                        + type.getName());
            }
        }
    }

    private FieldAccess getFieldAccess() {
        FieldAccess fieldAccess = messageMetaFieldCache.get(getClass());
        if (fieldAccess == null) {
            fieldAccess = FieldAccess.get(getClass(), false, field -> !(ReflectionUtil.isStatic(field) || ReflectionUtil.isTransient(field)
                    || ReflectionUtil.isFinal(field)));
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
     * @param meta
     * @throws BinaryMessageCodecException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws Exception
     */
    public final void putMessageMeta(BinaryMeta meta)
            throws IllegalArgumentException, IllegalAccessException, BinaryMessageCodecException {
        if (meta == null) {
            putByte(0);
        } else {
            putByte(1);
            meta.buffer(this.buffer);
            meta.encode();
        }
    }

    /**
     * 读取二进制元数据
     *
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws BinaryMessageCodecException
     */
    public final <T extends BinaryMeta> T getMessageMeta(Class<T> clazz)
            throws IllegalArgumentException, IllegalAccessException, BinaryMessageCodecException {
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
     * @param value
     * @throws BinaryMessageCodecException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public final <T extends BinaryMeta> void putMessageMetaList(List<T> value)
            throws BinaryMessageCodecException, IllegalArgumentException, IllegalAccessException {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (BinaryMeta temp : value) {
                putMessageMeta(temp);
            }
        }
    }

    /**
     * 读取二进制元数据列表
     *
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws BinaryMessageCodecException
     */
    public final <T extends BinaryMeta> List<T> getMessageMetaList(Class<T> clazz)
            throws IllegalArgumentException, IllegalAccessException, BinaryMessageCodecException {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
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
     * @param value
     * @throws BinaryMessageCodecException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public final <T extends BinaryMeta> void putMessageMetaArray(T[] value)
            throws IllegalArgumentException, IllegalAccessException, BinaryMessageCodecException {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (T t : value) {
                putMessageMeta(t);
            }
        }
    }

    /**
     * 读取二进制元数据数组
     *
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws BinaryMessageCodecException
     */
    public final <T extends BinaryMeta> T[] getMessageMetaArray(Class<T> clazz)
            throws IllegalArgumentException, IllegalAccessException, BinaryMessageCodecException {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return (T[]) Array.newInstance(clazz, 0);
            default:
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
     * @param value
     */
    public final void putString(String value) {
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
     * @return
     */
    public final String getString() {
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
     * @param value
     */
    public final void putStringList(List<String> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (String temp : value) {
                putString(temp);
            }
        }
    }

    /**
     * 读取String列表
     *
     * @return
     */
    public final List<String> getStringList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<String> list = new ArrayList<>();
                for (int i = 0; i < len; ++i) {
                    list.add(getString());
                }
                return list;
        }
    }

    /**
     * 写入字符串数组
     *
     * @param value
     */
    public final void putStringArray(String[] value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (String temp : value) {
                putString(temp);
            }
        }
    }

    /**
     * 读取字符串数组
     *
     * @return
     */
    public final String[] getStringArray() {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return new String[0];
            default:
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
     * @param value
     */
    public final void putLong(long value) {
        if (COMPRESS_INT32_64) {
            writeRawVarint64(buffer, value);
        } else {
            buffer.writeLong(value);
        }
    }

    /**
     * 读取long数据
     *
     * @return
     */
    public final long getLong() {
        if (COMPRESS_INT32_64) {
            return readRawVarint64(buffer);
        } else {
            return buffer.readLong();
        }
    }

    /**
     * 写入long列表
     *
     * @param value
     */
    public final void putLongList(List<Long> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (Long temp : value) {
                putLong(temp);
            }
        }
    }

    /**
     * 读取long列表
     *
     * @return
     */
    public final List<Long> getLongList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<Long> list = new ArrayList<>();
                for (int i = 0; i < len; ++i) {
                    list.add(getLong());
                }
                return list;
        }
    }

    /**
     * 写入long数组
     *
     * @param value
     */
    public final void putLongArray(long[] value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (long temp : value) {
                putLong(temp);
            }
        }
    }

    /**
     * 读取long数组
     *
     * @return
     */
    public final long[] getLongArray() {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return new long[0];
            default:
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
     * @param value
     */
    public final void putInt(int value) {
        if (COMPRESS_INT32_64) {
            writeRawVarint32(buffer, value);
        } else {
            this.buffer.writeInt(value);
        }
    }

    /**
     * 读取int数据
     *
     * @return
     */
    public final int getInt() {
        if (COMPRESS_INT32_64) {
            return readRawVarint32(buffer);
        } else {
            return this.buffer.readInt();
        }
    }

    /**
     * 写入int列表
     *
     * @param value
     */
    public final void putIntList(List<Integer> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (Integer temp : value) {
                putInt(temp);
            }
        }
    }

    /**
     * 读取int列表
     *
     * @return
     */
    public final List<Integer> getIntList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < len; ++i) {
                    list.add(getInt());
                }
                return list;
        }
    }

    /**
     * 读取byte列表
     *
     * @return
     */
    public final List<Byte> getByteList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<Byte> list = new ArrayList<>();
                for (int i = 0; i < len; ++i) {
                    list.add(getByte());
                }
                return list;
        }
    }

    /**
     * 写入int数组
     *
     * @param value
     */
    public final void putIntArray(int[] value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (int temp : value) {
                putInt(temp);
            }
        }
    }

    /**
     * 读取int数组
     *
     * @return
     */
    public final int[] getIntArray() {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return new int[0];
            default:
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
     * @param value
     */
    public final void putByte(int value) {
        buffer.writeByte(value);
    }

    /**
     * 写入byte列表
     *
     * @param value
     */
    public final void putByteList(List<Byte> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (Byte temp : value) {
                putByte(temp);
            }
        }
    }

    /**
     * 读取byte数据
     *
     * @return
     */
    public final byte getByte() {
        return buffer.readByte();
    }

    /**
     * 写入byte[]列表
     *
     * @param list
     */
    public final void putByteArrayList(List<byte[]> list) {
        if (list == null) {
            putShort(-1);
        } else {
            putShort(list.size());
            for (byte[] bt : list) {
                putByteArray(bt);
            }
        }
    }

    /**
     * 读取byte[]列表
     *
     * @return
     */
    public final List<byte[]> getByteArrayList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<byte[]> list = new ArrayList<>();
                list.add(getByteArray());
                return list;
        }
    }

    /**
     * 写入byte数组
     *
     * @param bytes
     */
    public final void putByteArray(byte[] bytes) {
        if (bytes == null) {
            putShort(-1);
        } else {
            putShort(bytes.length);
            buffer.writeBytes(bytes);
        }
    }

    /**
     * 读取byte数组
     *
     * @return
     */
    public final byte[] getByteArray() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new byte[0];
            default:
                byte[] bytes = new byte[len];
                buffer.readBytes(bytes, 0, len);
                return bytes;
        }
    }

    /**
     * 写入bool数据
     *
     * @param value
     */
    public final void putBoolean(boolean value) {
        buffer.writeBoolean(value);
    }

    /**
     * 读取bool数据
     *
     * @return
     */
    public final boolean getBoolean() {
        return buffer.readBoolean();
    }

    /**
     * 写入bool列表
     *
     * @param value
     */
    public final void putBooleanList(List<Boolean> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (boolean temp : value) {
                putBoolean(temp);
            }
        }
    }

    /**
     * 读取bool列表
     *
     * @return
     */
    public final List<Boolean> getBooleanList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<Boolean> list = new ArrayList<>();
                for (int i = 0; i < len; ++i) {
                    list.add(getBoolean());
                }
                return list;
        }
    }

    /**
     * 写入bool数组
     *
     * @param value
     */
    public final void putBooleanArray(boolean[] value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (boolean temp : value) {
                putBoolean(temp);
            }
        }
    }

    /**
     * 读取bool数组
     *
     * @return
     */
    public final boolean[] getBooleanArray() {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return new boolean[0];
            default:
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
     * @param value
     */
    public final void putFloat(float value) {
        buffer.writeFloat(value);
    }

    /**
     * 读取float数据
     *
     * @return
     */
    public final float getFloat() {
        return buffer.readFloat();
    }

    /**
     * 写入float列表
     *
     * @param value
     */
    public final void putFloatList(List<Float> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (Float temp : value) {
                putFloat(temp);
            }
        }
    }

    /**
     * 读取float列表
     *
     * @return
     */
    public final List<Float> getFloatList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
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
     * @param value
     */
    public final void putFloatArray(float[] value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (float temp : value) {
                putFloat(temp);
            }
        }
    }

    /**
     * 读取float数组
     *
     * @return
     */
    public final float[] getFloatArray() {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return new float[0];
            default:
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
     * @param value
     */
    public final void putDouble(double value) {
        buffer.writeDouble(value);
    }

    /**
     * 读取double数据
     *
     * @return
     */
    public final double getDouble() {
        return buffer.readDouble();
    }

    /**
     * 写入double列表
     *
     * @param value
     */
    public final void putDoubleList(List<Double> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (Double temp : value) {
                putDouble(temp);
            }
        }
    }

    /**
     * 读取double列表
     *
     * @return
     */
    public final List<Double> getDoubleList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<Double> list = new ArrayList<>();
                for (int i = 0; i < len; ++i) {
                    list.add(getDouble());
                }
                return list;
        }
    }

    /**
     * 写入double数组
     *
     * @param value
     */
    public final void putDoubleArray(double[] value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (double temp : value) {
                putDouble(temp);
            }
        }
    }

    /**
     * 读取double数组
     *
     * @return
     */
    public final double[] getDoubleArray() {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return new double[0];
            default:
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
     * @param value
     */
    public final void putShort(int value) {
        if (COMPRESS_INT32_64) {
            writeRawVarint32(buffer, value);
        } else {
            buffer.writeShort(value);
        }
    }

    /**
     * 读取short数据
     *
     * @return
     */
    public final short getShort() {
        if (COMPRESS_INT32_64) {
            return (short) readRawVarint32(buffer);
        } else {
            return buffer.readShort();
        }
    }

    /**
     * 写入short列表
     *
     * @param value
     */
    public final void putShortList(List<Short> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (Short temp : value) {
                putShort(temp);
            }
        }
    }

    /**
     * 读取short列表
     *
     * @return
     */
    public final List<Short> getShortList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<Short> list = new ArrayList<>();
                for (int i = 0; i < len; ++i) {
                    list.add(getShort());
                }
                return list;
        }
    }

    /**
     * 写入short数组
     *
     * @param value short数组
     */
    public final void putShortArray(short[] value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (short temp : value) {
                putShort(temp);
            }
        }
    }

    /**
     * 读取short数组
     *
     * @return short数组
     */
    public final short[] getShortArray() {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return new short[0];
            default:
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
     * @param value char值
     */
    public final void putChar(int value) {
        buffer.writeChar(value);
    }

    /**
     * 读取char数据
     *
     * @return char
     */
    public final char getChar() {
        return buffer.readChar();
    }

    /**
     * 写入char列表
     *
     * @param value char列表
     */
    public final void putCharList(List<Character> value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.size());
            for (Character temp : value) {
                putChar(temp);
            }
        }
    }

    /**
     * 读取char列表
     *
     * @return char列表
     */
    public final List<Character> getCharList() {
        short len = getShort();
        switch (len) {
            case -1:
                return null;
            case 0:
                return new ArrayList<>();
            default:
                List<Character> list = new ArrayList<>();
                for (int i = 0; i < len; ++i) {
                    list.add(getChar());
                }
                return list;
        }
    }

    /**
     * 写入char数组
     *
     * @param value char数组
     */
    public final void putCharArray(char[] value) {
        if (value == null) {
            putShort(-1);
        } else {
            putShort(value.length);
            for (char temp : value) {
                putChar(temp);
            }
        }
    }

    /**
     * 读取char数组
     *
     * @return char数组
     */
    public final char[] getCharArray() {
        short length = getShort();
        switch (length) {
            case -1:
                return null;
            case 0:
                return new char[0];
            default:
                char[] result = new char[length];
                for (int i = 0; i < length; ++i) {
                    result[i] = getChar();
                }
                return result;
        }
    }

    private <T extends BinaryMeta> T createInstance(Class<T> clazz) {
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

    public static void writeRawVarint64(ByteBuf buffer, long value) {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                buffer.writeByte(((byte) value));
                return;
            } else {
                buffer.writeByte((byte) (((int) value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }

    public static long readRawVarint64(ByteBuf buffer) {
        int shift = 0;
        long result = 0;
        while (shift < 64) {
            final byte b = buffer.readByte();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw new CorruptedFrameException("malformed varint.");
    }

    public static void writeRawVarint32(ByteBuf buffer, int val) {
        while (true) {
            if ((val & ~0x7F) == 0) {
                buffer.writeByte((byte) val);
                return;
            } else {
                buffer.writeByte((byte) ((val & 0x7F) | 0x80));
                val >>>= 7;
            }
        }
    }

    public static int readRawVarint32(ByteBuf buffer) {
        int x;
        if ((x = buffer.readByte()) >= 0) {
            return x;
        } else if ((x ^= (buffer.readByte() << 7)) < 0L) {
            x ^= (~0L << 7);
        } else if ((x ^= (buffer.readByte() << 14)) >= 0L) {
            x ^= (~0L << 7) ^ (~0L << 14);
        } else if ((x ^= (buffer.readByte() << 21)) < 0L) {
            x ^= (~0L << 7) ^ (~0L << 14) ^ (~0L << 21);
        } else {
            int y = buffer.readByte();
            x ^= y << 28;
            x ^= (~0L << 7) ^ (~0L << 14) ^ (~0L << 21) ^ (~0L << 28);
            if (y < 0) {
                throw new CorruptedFrameException("malformed varint.");
            }
        }
        return x;
    }
}
