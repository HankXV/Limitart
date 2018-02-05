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
package org.slingerxv.limitart.collections;


import org.slingerxv.limitart.base.ThreadSafe;
import org.slingerxv.limitart.base.ThreadUnsafe;

import java.util.HashMap;

/**
 * 约束型Map
 *
 * @author hank
 */
@ThreadUnsafe
public class ConstraintHashedMap<K> extends HashMap<K, Object> implements ConstraintMap<K> {

    /**
     * 放入Byte
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putByte(K key, byte value) {
        putObj(key, value);
        return this;
    }

    /**
     * 获取Byte
     *
     * @param key
     * @return 返回0或其他
     */
    @Override
    public byte getByte(K key) {
        if (!containsKey(key)) {
            return 0;
        }
        return Byte.parseByte(getObj(key).toString());
    }

    /**
     * 放入Short
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putShort(K key, short value) {
        putObj(key, value);
        return this;
    }

    /**
     * 获取Short
     *
     * @param key
     * @return 返回0或其他
     */
    @Override
    public short getShort(K key) {
        if (!containsKey(key)) {
            return 0;
        }
        return Short.parseShort(getObj(key).toString());
    }

    /**
     * 放入int
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putInt(K key, int value) {
        putObj(key, value);
        return this;
    }

    /**
     * 获取int
     *
     * @param key
     * @return 0或其他int
     */
    @Override
    public int getInt(K key) {
        if (!containsKey(key)) {
            return 0;
        }
        return Integer.parseInt(getObj(key).toString());
    }

    /**
     * 放入long
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putLong(K key, long value) {
        putObj(key, value);
        return this;
    }

    /**
     * 获取long
     *
     * @param key
     * @return 0L或者其他long
     */
    @Override
    public long getLong(K key) {
        if (!containsKey(key)) {
            return 0L;
        }
        return Long.parseLong(getObj(key).toString());
    }

    /**
     * 放入浮点
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putFloat(K key, float value) {
        putObj(key, value);
        return this;
    }

    /**
     * 获取浮点
     *
     * @param key
     * @return 0F或者其他浮点
     */
    @Override
    public float getFloat(K key) {
        if (!containsKey(key)) {
            return 0F;
        }
        return Float.parseFloat(getObj(key).toString());
    }

    /**
     * 放入double
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putDouble(K key, double value) {
        putObj(key, value);
        return this;
    }

    /**
     * 获取double
     *
     * @param key
     * @return 0D或者其他double
     */
    @Override
    public double getDouble(K key) {
        if (!containsKey(key)) {
            return 0D;
        }
        return Double.parseDouble(getObj(key).toString());
    }

    /**
     * 放入char
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putChar(K key, char value) {
        putObj(key, value);
        return this;
    }

    /**
     * 获取char
     *
     * @param key
     * @return 0或其他char
     */
    @Override
    public char getChar(K key) {
        if (!containsKey(key)) {
            return 0;
        }
        return getObj(key);
    }

    /**
     * 写入布尔
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putBoolean(K key, boolean value) {
        putInt(key, value ? 1 : 0);
        return this;
    }

    /**
     * 获取布尔
     *
     * @param key
     * @return
     */
    @Override
    public boolean getBoolean(K key) {
        return getInt(key) == 1;
    }

    /**
     * 放入字符串
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ConstraintMap<K> putString(K key, String value) {
        if (value == null) {
            putObj(key, "");
        } else {
            putObj(key, value);
        }
        return this;
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return ""或其他字符串
     */
    @Override
    public String getString(K key) {
        if (!containsKey(key)) {
            return "";
        }
        return (String) get(key);
    }

    @Override
    public ConstraintMap<K> putObj(K key, Object value) {
        put(key, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getObj(K key) {
        return (V) get(key);
    }
}
