package com.limitart.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 大概计时管理(更精确计时请用SchedulerUtil) 用计时器的时候请注意线程问题
 * 
 * @author hank
 * @see SchedulerUtil
 *
 */
public final class TimerUtil {
	private static Timer timer = new Timer("Limitart-Timer");

	private TimerUtil() {
	}

	public static void unScheduleGlobal(TimerTask listener) {
		listener.cancel();
		timer.purge();
	}

	public static void scheduleGlobal(long delay, long interval, TimerTask listener) {
		timer.schedule(listener, delay, interval);
	}

	public static void scheduleGlobal(long delay, TimerTask listener) {
		timer.schedule(listener, delay);
	}

	public static void shutdown() {
		timer.cancel();
	}
}
