package org.slingerxv.limitart.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class TimeUtil {
	private static final Logger log = LogManager.getLogger();
	private static final long ONE_MINUTE = 60000L;
	private static final long ONE_HOUR = 3600000L;
	private static final long ONE_DAY = 86400000L;
	private static final long ONE_WEEK = 604800000L;
	private static final String ONE_SECOND_AGO = "秒前";
	private static final String ONE_MINUTE_AGO = "分钟前";
	private static final String ONE_HOUR_AGO = "小时前";
	private static final String ONE_DAY_AGO = "天前";
	private static final String ONE_MONTH_AGO = "月前";
	private static final String ONE_YEAR_AGO = "年前";

	private TimeUtil() {
	}

	/**
	 * 获取短时间
	 * 
	 * @param time
	 * @return
	 */
	public static String date2ShortStr(long time) {
		long delta = Math.max(System.currentTimeMillis() - time, 0);
		if (delta < ONE_MINUTE) {
			long seconds = toSeconds(delta);
			return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
		}
		if (delta < 45L * ONE_MINUTE) {
			long minutes = toMinutes(delta);
			return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
		}
		if (delta < 24L * ONE_HOUR) {
			long hours = toHours(delta);
			return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
		}
		if (delta < 48L * ONE_HOUR) {
			return "昨天";
		}
		if (delta < 30L * ONE_DAY) {
			long days = toDays(delta);
			return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
		}
		if (delta < 12L * 4L * ONE_WEEK) {
			long months = toMonths(delta);
			return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
		} else {
			long years = toYears(delta);
			return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
		}
	}

	private static long toSeconds(long date) {
		return date / 1000L;
	}

	private static long toMinutes(long date) {
		return toSeconds(date) / 60L;
	}

	private static long toHours(long date) {
		return toMinutes(date) / 60L;
	}

	private static long toDays(long date) {
		return toHours(date) / 24L;
	}

	private static long toMonths(long date) {
		return toDays(date) / 30L;
	}

	private static long toYears(long date) {
		return toMonths(date) / 365L;
	}

	/**
	 * 是否是同一天
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isSameDay(long time) {
		Calendar nowCal = Calendar.getInstance();
		Date date = new Date(time);
		Calendar herCal = Calendar.getInstance();
		herCal.setTime(date);
		int nowYear = nowCal.get(Calendar.YEAR);
		int nowMonth = nowCal.get(Calendar.MONTH);
		int nowDay = nowCal.get(Calendar.DAY_OF_MONTH);
		int herYear = herCal.get(Calendar.YEAR);
		int herMonth = herCal.get(Calendar.MONTH);
		int herDay = herCal.get(Calendar.DAY_OF_MONTH);
        return nowYear == herYear && nowMonth == herMonth && nowDay == herDay;

    }

	/**
	 * 是否是同一个月
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isSameMonth(long time) {
		Calendar nowCal = Calendar.getInstance();
		Date date = new Date(time);
		Calendar herCal = Calendar.getInstance();
		herCal.setTime(date);
		int nowYear = nowCal.get(Calendar.YEAR);
		int nowMonth = nowCal.get(Calendar.MONTH);
		int herYear = herCal.get(Calendar.YEAR);
		int herMonth = herCal.get(Calendar.MONTH);
        return nowYear == herYear && nowMonth == herMonth;
    }

	/**
	 * 是否是同一年
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isSameYear(long time) {
		Calendar nowCal = Calendar.getInstance();
		Date date = new Date(time);
		Calendar herCal = Calendar.getInstance();
		herCal.setTime(date);
		int nowYear = nowCal.get(Calendar.YEAR);
		int herYear = herCal.get(Calendar.YEAR);
        return nowYear == herYear;
    }

	public static long str2Date(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
		Date parse = null;
		try {
			parse = format.parse(str);
		} catch (ParseException e) {
			log.error(e, e);
			return 0;
		}
		return parse.getTime();
	}

	public static String date2Str(long date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
		Date temp = new Date(date);
		String format2 = format.format(temp);
		return format2;
	}
}
