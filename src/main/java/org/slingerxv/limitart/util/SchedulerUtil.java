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

import java.util.Collection;
import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务作业
 * 
 * @author hank
 *
 */
public final class SchedulerUtil {
	private static final Logger log = LoggerFactory.getLogger(SchedulerUtil.class);
	private static SchedulerUtil INSTANCE = new SchedulerUtil();
	private SchedulerFactory SF;

	public static SchedulerUtil self() {
		return INSTANCE;
	}

	private SchedulerUtil() {
		Properties prop = new Properties();
		prop.setProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, "LimitartScheduler");
		prop.setProperty(StdSchedulerFactory.PROP_SCHED_THREAD_NAME, "LimitartQuartz");
		// 这里是阻止并发执行，当执行时间超过间隔时间，会开线程执行，这里不允许
		prop.setProperty("org.quartz.threadPool.threadCount", 1 + "");
		try {
			SF = new StdSchedulerFactory(prop);
		} catch (SchedulerException e) {
			log.error("init StdSchedulerFactory error", e);
		}
	}

	/**
	 * 增加一个调度任务(cron版)
	 * 
	 * @param name
	 *            任务名称
	 * @param task
	 *            执行内容
	 * @param cronExpression
	 *            cron表达式
	 * @throws SchedulerException
	 */
	public Trigger addSchedule(String name, Class<? extends Job> task, String cronExpression, JobDataMap param)
			throws SchedulerException {
		Scheduler sched = SF.getScheduler();
		JobBuilder builder = JobBuilder.newJob(task);
		builder.withIdentity(name, Scheduler.DEFAULT_GROUP);
		if (param != null) {
			builder.usingJobData(param);
		}
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, Scheduler.DEFAULT_GROUP)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
		sched.scheduleJob(builder.build(), trigger);
		if (!sched.isShutdown())
			sched.start();
		return trigger;
	}

	/**
	 * 增加一个调度任务
	 * 
	 * @param name
	 *            任务名称
	 * @param task
	 *            执行内容
	 * @param intervalInHours
	 *            间隔小时
	 * @param intervalInMinutes
	 *            间隔分钟
	 * @param intervalInSeconds
	 *            间隔秒
	 * @param intervalInMillis
	 *            间隔毫秒
	 * @param repeatCount
	 *            重复次数
	 * @throws SchedulerException
	 */
	public Trigger addSchedule(String name, Class<? extends Job> task, int intervalInHours, int intervalInMinutes,
			int intervalInSeconds, int intervalInMillis, int repeatCount, JobDataMap param) throws SchedulerException {
		Scheduler sched = SF.getScheduler();
		JobBuilder builder = JobBuilder.newJob(task);
		builder.withIdentity(name, Scheduler.DEFAULT_GROUP);
		if (param != null) {
			builder.usingJobData(param);
		}
		SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule();
		if (intervalInHours > 0) {
			simpleSchedule.withIntervalInHours(intervalInHours);
		}
		if (intervalInMinutes > 0) {
			simpleSchedule.withIntervalInMinutes(intervalInMinutes);
		}
		if (intervalInSeconds > 0) {
			simpleSchedule.withIntervalInSeconds(intervalInSeconds);
		}
		if (intervalInMillis > 0) {
			simpleSchedule.withIntervalInMilliseconds(intervalInMillis);
		}
		if (repeatCount >= 0) {
			simpleSchedule.withRepeatCount(repeatCount);
		} else {
			simpleSchedule.repeatForever();
		}
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, Scheduler.DEFAULT_GROUP)
				.withSchedule(simpleSchedule).build();
		sched.scheduleJob(builder.build(), trigger);
		if (!sched.isShutdown())
			sched.start();
		return trigger;
	}

	/**
	 * 增加一个调度任务
	 * 
	 * @param name
	 *            任务名称
	 * @param task
	 *            执行内容
	 * @param intervalInSeconds
	 *            间隔秒
	 * @throws SchedulerException
	 */
	public Trigger addSchedule(String name, Class<? extends Job> task, int intervalInSeconds, JobDataMap param)
			throws SchedulerException {
		return addSchedule(name, task, 0, 0, intervalInSeconds, 0, -1, param);
	}

	/**
	 * 是否有这个任务
	 * 
	 * @param name
	 * @return
	 * @throws SchedulerException
	 */
	public boolean hasSchedule(String name) throws SchedulerException {
		Scheduler scheduler = SF.getScheduler();
		if (scheduler == null) {
			return false;
		}
		return scheduler.checkExists(new JobKey(name, Scheduler.DEFAULT_GROUP));
	}

	/**
	 * 关闭一个调度任务
	 * 
	 * @param name
	 * @throws SchedulerException
	 */
	public boolean removeSchedule(String name) throws SchedulerException {
		Scheduler scheduler = SF.getScheduler();
		JobKey key = new JobKey(name, Scheduler.DEFAULT_GROUP);
		return scheduler.deleteJob(key);
	}

	/**
	 * 关闭所有调度任务
	 */
	public void shutdown() {
		try {
			Collection<Scheduler> allSchedulers = SF.getAllSchedulers();
			for (Scheduler s : allSchedulers) {
				s.shutdown();
			}
		} catch (SchedulerException e) {
			log.error("shut down error!", e);
		}
	}
}
