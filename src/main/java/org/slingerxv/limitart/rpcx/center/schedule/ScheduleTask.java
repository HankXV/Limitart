/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.rpcx.center.schedule;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slingerxv.limitart.collections.ConcurrentHashSet;
import org.slingerxv.limitart.rpcx.center.struct.ServiceXServerSession;
import org.slingerxv.limitart.rpcx.message.schedule.TriggerScheduleServiceCenterToProviderServiceCenterMessage;
import org.slingerxv.limitart.util.RandomUtil;
import org.slingerxv.limitart.util.TimeUtil;

public class ScheduleTask implements Job {
	private static Logger log = LogManager.getLogger();
	public static String RPCSERVERS = "RPCSERVERS";
	public static String SCHEDULES = "SCHEDULES";

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext job) throws JobExecutionException {
		TriggerScheduleServiceCenterToProviderServiceCenterMessage msg = new TriggerScheduleServiceCenterToProviderServiceCenterMessage();
		String jobName = job.getJobDetail().getKey().getName();
		JobDataMap jobDataMap = job.getJobDetail().getJobDataMap();
		ConcurrentHashMap<Integer, ServiceXServerSession> rpcServers = (ConcurrentHashMap<Integer, ServiceXServerSession>) jobDataMap
				.get(RPCSERVERS);
		ConcurrentHashMap<String, ConcurrentHashSet<Integer>> schedules = (ConcurrentHashMap<String, ConcurrentHashSet<Integer>>) jobDataMap
				.get(SCHEDULES);
		ConcurrentHashSet<Integer> providerList = schedules.get(jobName);
		if (providerList == null) {
			log.error("Job:" + jobName + "找不到Provider");
			return;
		}
		msg.setJobName(jobName);
		// 查看是否是最有一次执行，并且移除此job
		if (!job.getTrigger().mayFireAgain()) {
			msg.setEnd(true);
			schedules.remove(jobName);
			log.info("任务生命终结，执行删除：" + jobName);
		}
		// 选举式触发
		ArrayList<Integer> arrayList = new ArrayList<>(providerList);
		int providerId = arrayList.get(RandomUtil.randomInt(0, arrayList.size() - 1));
		ServiceXServerSession serviceXServerSession = rpcServers.get(providerId);
		if (serviceXServerSession != null) {
			serviceXServerSession.getSession().writeAndFlush(msg);
			log.info(jobName + "触发！分配的ProviderId为：" + providerId + "，下次触发时间："
					+ TimeUtil.date2Str(job.getTrigger().getNextFireTime().getTime()));
		}
	}
}