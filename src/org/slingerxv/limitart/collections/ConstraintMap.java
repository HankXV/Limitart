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


import java.util.Map;

/**
 * 约束型Map
 *
 * @param <K>
 * @author Hank
 */
public interface ConstraintMap<K> extends Map<K, Object> {
    static <K> ConstraintMap<K> empty() {
        return new ConstraintHashedMap<>();
    }

    /**
     * 放入Byte
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putByte(K key, byte value);

    /**
     * 获取Byte
     *
     * @param key
     * @return 返回0或其他
     */
    byte getByte(K key);

    /**
     * 放入Short
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putShort(K key, short value);

    /**
     * 获取Short
     *
     * @param key
     * @return 返回0或其他
     */
    short getShort(K key);

    /**
     * 放入int
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putInt(K key, int value);

    /**
     * 获取int
     *
     * @param key
     * @return 0或其他int
     */
    int getInt(K key);

    /**
     * 放入long
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putLong(K key, long value);

    /**
     * 获取long
     *
     * @param key
     * @return 0L或者其他long
     */
    long getLong(K key);

    /**
     * 放入浮点
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putFloat(K key, float value);

    /**
     * 获取浮点
     *
     * @param key
     * @return 0F或者其他浮点
     */
    float getFloat(K key);

    /**
     * 放入double
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putDouble(K key, double value);

    /**
     * 获取double
     *
     * @param key
     * @return 0D或者其他double
     */
    double getDouble(K key);

    /**
     * 放入char
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putChar(K key, char value);

    /**
     * 获取char
     *
     * @param key
     * @return 0或其他char
     */
    char getChar(K key);

    /**
     * 写入布尔
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putBoolean(K key, boolean value);

    /**
     * 获取布尔
     *
     * @param key
     * @return
     */
    boolean getBoolean(K key);

    /**
     * 放入字符串
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putString(K key, String value);

    /**
     * 获取字符串
     *
     * @param key
     * @return ""或其他字符串
     */
    String getString(K key);

    /**
     * 放入原始对象
     *
     * @param key
     * @param value
     * @return
     */
    ConstraintMap<K> putObj(K key, Object value);

    /**
     * 获取原始对象
     *
     * @param key
     * @return null或对象
     */
    <V> V getObj(K key);
}