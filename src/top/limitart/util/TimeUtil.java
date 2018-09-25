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
package top.limitart.util;


import top.limitart.base.Proc;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具
 *
 * @author hank
 * @version 2018-02-26
 */
public final class TimeUtil {
    //系统内置时钟，跟系统时间不挂钩
    private static Clock clock = Clock.systemDefaultZone();
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
        return clock.millis();
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
     * 是否为同一月
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean sameMonth(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * 是否为同一周
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean sameWeek(LocalDate d1, LocalDate d2) {
        return sameWeek(d1, d2, DayOfWeek.MONDAY);
    }

    /**
     * 是否为同一周
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean sameWeek(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal1.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time2);
        cal2.setFirstDayOfWeek(Calendar.MONDAY);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 是否为同一周
     *
     * @param d1
     * @param d2
     * @param dayOfWeek
     * @return
     */
    public static boolean sameWeek(LocalDate d1, LocalDate d2, DayOfWeek dayOfWeek) {
        WeekFields weekFields = WeekFields.of(dayOfWeek, 1);
        int week1 = d1.get(weekFields.weekOfWeekBasedYear());
        int week2 = d2.get(weekFields.weekOfWeekBasedYear());
        return week1 == week2 && ChronoUnit.WEEKS.between(d1, d2) == 0;
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

    public static LocalDate localDate() {
        return LocalDate.now(clock);
    }

    public static LocalDateTime localDateTime() {
        return LocalDateTime.now(clock);
    }

    public static LocalDateTime localDateTime(String date) {
        return LocalDateTime.parse(date.replaceAll(" ", "T"));
    }

    public static Date date() {
        return new Date(clock.millis());
    }

    public static long localDateTime2Mills(LocalDateTime time) {
        return time.toInstant(ZoneOffset.systemDefault().getRules().getOffset(time)).toEpochMilli();
    }

    public static LocalDateTime mills2LocalDateTime(long mills) {
        Instant instant = Instant.ofEpochMilli(mills);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static int betweenDays(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    public static int betweenWeeks(LocalDate start, LocalDate end) {
        int week1 = toEpochWeek(start);
        int week2 = toEpochWeek(end);
        return week2 - week1;
    }

    public static int toEpochWeek(LocalDate localDate) {
        double day = localDate.toEpochDay();
        Double weekDouble = Math.ceil((day - 3.0) / 7.0 + 1.0);
        int week = weekDouble.intValue();
        return week;
    }

    /**
     * 获取当前周星期1
     *
     * @return
     */
    public static Date getThisWeekMonday() {
        return getThisWeekMonday(millis());
    }

    /**
     * 获取当前周的星期1
     *
     * @return
     */
    public static Date getThisWeekMonday(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
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

    /**
     * 毫秒转时间格式(小时：分：秒) 不满一单位时不显示相应单位
     *
     * @param mills
     * @return
     */
    public static String shortTime(long mills) {
        String str = "";
        long sec = mills / 1000;
        long day = sec / (24 * 3600);
        long hours = sec % (24 * 3600) / 3600;
        long minutes = sec % (24 * 3600) % 3600 / 60;
        long second = sec % (24 * 3600) % 3600 % 60;
        str = second + "秒";
        if (hours != 0 || day != 0) {
            str = minutes + "分" + str;
        } else {
            if (minutes != 0) str = minutes + "分" + str;
        }
        if (day != 0) {
            str = hours + "小时" + str;
        } else {
            if (hours != 0) str = hours + "小时" + str;
        }
        if (day != 0) str = day + "天" + str;
        return str;
    }
}
