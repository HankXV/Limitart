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


import org.slingerxv.limitart.base.Proc;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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
    public static final long SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);
    /**
     * 分
     */
    public static final long MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1);
    /**
     * 时
     */
    public static final long HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1);

    private TimeUtil() {
    }

    /**
     * 获取当前时间戳：毫秒
     *
     * @return
     */
    public static long millis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳：毫秒
     *
     * @return
     */
    public static long now() {
        return millis();
    }

    /**
     * 获取当前时间戳：秒
     *
     * @return
     */
    public static int seconds() {
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
    public static long buildMillis(int hours, int minutes, int seconds) {
        return hours * HOUR_IN_MILLIS + minutes * MINUTE_IN_MILLIS + seconds * SECOND_IN_MILLIS;
    }

    /**
     * @param minutes
     * @param seconds
     * @return
     */
    public static long buildMillis(int minutes, int seconds) {
        return buildMillis(0, minutes, seconds);
    }

    /**
     * @param seconds
     * @return
     */
    public static long buildMillis(int seconds) {
        return buildMillis(0, 0, seconds);
    }

    /**
     * 是否为今天
     *
     * @param time
     * @return
     */
    public static boolean today(long time) {
        return sameDay(time, millis());
    }

    /**
     * 是否为同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean sameDay(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取所在天的凌晨时间
     *
     * @param time
     * @return
     */
    public long zeroStamp(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取UTC(世界标准时间)时间戳
     *
     * @return
     */
    public static long utc() {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return cal.getTimeInMillis();
    }

    public static void performanceTest(Proc proc1, Proc proc2, int count) {
        long now = now();
        for (int i = 0; i < count; ++i) {
            proc1.run();
        }
        System.out.println((now() - now) + "/ms");
        now = now();
        for (int i = 0; i < count; ++i) {
            proc2.run();
        }
        System.out.println((now() - now) + "/ms");
    }
}
