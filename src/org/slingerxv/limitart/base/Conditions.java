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
package org.slingerxv.limitart.base;


/**
 * 条件判定
 *
 * @author hank
 * @version 2017/11/2 0002 20:46
 */
public class Conditions {
    /**
     * 检测是否为正数
     *
     * @param value
     */
    public static void positive(int value) {
        Conditions.args(value > 0, "value must > 0");
    }

    /**
     * 检测是否为自然数
     *
     * @param value
     */
    public static void natural(int value) {
        Conditions.args(value >= 0, "value must >= 0");
    }

    /**
     * 判断是否为相同线程
     *
     * @param thread
     * @param <T>
     */
    public static <T extends Thread> T sameThread(T thread) {
        notNull(thread, "thread");
        if (thread != Thread.currentThread()) {
            throw new NotSameThreadException("caller must be only one,yours:%s,this:%s", thread, Thread.currentThread());
        }
        return thread;
    }

    /**
     * 判断是否为相同线程
     *
     * @param thread
     * @param template
     * @param params
     * @param <T>
     */
    public static <T extends Thread> T sameThread(T thread, String template, Object... params) {
        notNull(thread, "thread");
        if (thread != Thread.currentThread()) {
            throw new NotSameThreadException(template, params);
        }
        return thread;
    }

    /**
     * 检查元素是否为空，否则抛异常
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> T notNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    /**
     * 检查元素是否为空，否则抛异常
     *
     * @param obj
     * @param template
     * @param params
     * @param <T>
     * @return
     */
    public static <T> T notNull(T obj, String template, Object... params) {
        if (obj == null)
            throw new NullPointerException(String.format(template, params));
        return obj;
    }

    /**
     * 检查参数是否正确，否则抛异常
     *
     * @param isRight
     */
    public static void args(boolean isRight) {
        if (!isRight) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 检查参数是否正确，否则抛异常
     *
     * @param isRight
     * @param template
     * @param params
     */
    public static void args(boolean isRight, String template, Object... params) {
        if (!isRight) {
            throw new IllegalArgumentException(String.format(template, params));
        }
    }

    /**
     * 检查数组边界
     *
     * @param index
     * @param size
     */
    public static int eleIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index:" + index + ",size:" + size);
        }
        return index;
    }

    /**
     * 检查数组边界
     *
     * @param index
     * @param size
     * @param template
     * @param params
     */
    public static int eleIndex(int index, int size, String template, Object... params) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(String.format(template, params));
        }
        return index;
    }
}
