package com.limitart.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.util.listener.ITimerListener;

/**
 * 大概计时管理(更精确计时请用SchedulerUtil) 用计时器的时候请注意线程问题
 * 
 * @author hank
 * @see SchedulerUtil
 *
 */
public class TimerUtil {
	private static Logger log = LogManager.getLogger();
	private static ScheduledThreadPoolExecutor globalTimer = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setName("Global-Timer");
			return thread;
		}
	});

	public static void scheduleGlobal(long delay, long interval, ITimerListener listener) {
		globalTimer.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					listener.action();
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}, delay, interval, TimeUnit.MILLISECONDS);
	}

	public static void scheduleGlobal(long delay, ITimerListener listener) {
		globalTimer.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					listener.action();
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}, delay, TimeUnit.MILLISECONDS);
	}

	public static void shutdown() {
		globalTimer.shutdown();
	}
}
