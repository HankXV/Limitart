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
package org.slingerxv.limitart.util;


/**
 * 时间工具
 *
 * @author hank
 * @version 2018-02-26
 */
public final class TimeUtil {
    /**
     * 秒
     */
    public static final int SECOND_IN_MILLIS = 1000;
    /**
     * 分
     */
    public static final int MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;
    /**
     * 时
     */
    public static final int HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;

    /**
     * 获取当前时间戳：毫秒
     *
     * @return
     */
    public long millis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳：秒
     *
     * @return
     */
    public int seconds() {
        return (int) (millis() / SECOND_IN_MILLIS);
    }

    /**
     * 构建一个用毫秒表示的时长
     *
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    public int buildMillis(int hours, int minutes, int seconds) {
        return hours * HOUR_IN_MILLIS + minutes * MINUTE_IN_MILLIS + seconds * SECOND_IN_MILLIS;
    }

    /**
     * @param minutes
     * @param seconds
     * @return
     */
    public int buildMillis(int minutes, int seconds) {
        return buildMillis(0, minutes, seconds);
    }

    /**
     * @param seconds
     * @return
     */
    public int buildMillis(int seconds) {
        return buildMillis(0, 0, seconds);
    }
}
