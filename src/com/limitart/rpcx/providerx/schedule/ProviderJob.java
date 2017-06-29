package com.limitart.rpcx.providerx.schedule;

import com.limitart.util.StringUtil;

/**
 * Provider调度任务
 * 
 * @author hank
 *
 */
public final class ProviderJob {
	private String jobName;
	private String cronExpression;
	private int intervalInHours;
	private int intervalInMinutes;
	private int intervalInSeconds;
	private int intervalInMillis;
	private int repeatCount;
	private IProviderScheduleListener listener;

	private ProviderJob(ProviderJobBuilder builder) {
		if (StringUtil.isEmptyOrNull(builder.jobName)) {
			throw new NullPointerException("jobName");
		}
		if (StringUtil.isEmptyOrNull(builder.cronExpression) && builder.intervalInHours <= 0 && builder.intervalInMinutes <= 0
				&& builder.intervalInSeconds <= 0 && builder.intervalInMillis <= 0) {
			throw new NullPointerException("schedule time");
		}
		if (builder.listener == null) {
			throw new NullPointerException("listener");
		}
		this.jobName = builder.jobName;
		this.cronExpression = builder.cronExpression;
		this.intervalInHours = builder.intervalInHours;
		this.intervalInMinutes = builder.intervalInMinutes;
		this.intervalInSeconds = builder.intervalInSeconds;
		this.intervalInMillis = builder.intervalInMillis;
		this.repeatCount = builder.repeatCount;
		this.listener = builder.listener;
	}

	@Override
	public String toString() {
		return "ProviderJob [jobName=" + jobName + ", cronExpression=" + cronExpression + ", intervalInHours="
				+ intervalInHours + ", intervalInMinutes=" + intervalInMinutes + ", intervalInSeconds="
				+ intervalInSeconds + ", intervalInMillis=" + intervalInMillis + ", repeatCount=" + repeatCount
				+ ", listener=" + listener + "]";
	}

	public String getJobName() {
		return jobName;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public int getIntervalInHours() {
		return intervalInHours;
	}

	public int getIntervalInMinutes() {
		return intervalInMinutes;
	}

	public int getIntervalInSeconds() {
		return intervalInSeconds;
	}

	public int getIntervalInMillis() {
		return intervalInMillis;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public IProviderScheduleListener getListener() {
		return listener;
	}

	public static class ProviderJobBuilder {
		private String jobName;
		private String cronExpression;
		private int intervalInHours;
		private int intervalInMinutes;
		private int intervalInSeconds;
		private int intervalInMillis;
		private int repeatCount;
		private IProviderScheduleListener listener;

		public ProviderJob build() {
			return new ProviderJob(this);
		}

		/**
		 * 任务名称，一组Provider的唯一标识
		 * 
		 * @param jobName
		 * @return
		 */
		public ProviderJobBuilder jobName(String jobName) {
			if (StringUtil.isEmptyOrNull(jobName)) {
				throw new NullPointerException("jobName");
			}
			this.jobName = jobName;
			return this;
		}

		/**
		 * 使用cron表达式制定任务计划(如果有cron表达式，其他时间配置失效)
		 * 
		 * @param cronExpression
		 * @return
		 */
		public ProviderJobBuilder cronExpression(String cronExpression) {
			if (StringUtil.isEmptyOrNull(cronExpression)) {
				throw new NullPointerException("cronExpression");
			}
			this.cronExpression = cronExpression;
			return this;
		}

		/**
		 * 触发间隔：小时
		 * 
		 * @param intervalInHours
		 * @return
		 */
		public ProviderJobBuilder intervalInHours(int intervalInHours) {
			this.intervalInHours = intervalInHours < 0 ? 0 : intervalInHours;
			return this;
		}

		/**
		 * 触发间隔：分钟
		 * 
		 * @param intervalInHours
		 * @return
		 */
		public ProviderJobBuilder intervalInMinutes(int intervalInMinutes) {
			this.intervalInMinutes = intervalInMinutes < 0 ? 0 : intervalInMinutes;
			return this;
		}

		/**
		 * 触发间隔：秒
		 * 
		 * @param intervalInHours
		 * @return
		 */
		public ProviderJobBuilder intervalInSeconds(int intervalInSeconds) {
			this.intervalInSeconds = intervalInSeconds < 0 ? 0 : intervalInSeconds;
			return this;
		}

		/**
		 * 触发间隔：毫秒
		 * 
		 * @param intervalInHours
		 * @return
		 */
		public ProviderJobBuilder intervalInMillis(int intervalInMillis) {
			this.intervalInMillis = intervalInMillis < 0 ? 0 : intervalInMillis;
			return this;
		}

		/**
		 * 重复执行次数
		 * 
		 * @param repeatCount
		 * @return
		 */
		public ProviderJobBuilder repeatCount(int repeatCount) {
			this.repeatCount = repeatCount < 0 ? 0 : repeatCount;
			return this;
		}

		/**
		 * 永久执行
		 * 
		 * @return
		 */
		public ProviderJobBuilder repeatForever() {
			this.repeatCount = -1;
			return this;
		}

		/**
		 * 任务触发回调
		 * 
		 * @param listener
		 * @return
		 */
		public ProviderJobBuilder listener(IProviderScheduleListener listener) {
			if (listener == null) {
				throw new NullPointerException("IProviderScheduleListener");
			}
			this.listener = listener;
			if(StringUtil.isEmptyOrNull(this.jobName)){
				return jobName(listener.getClass().getName());
			}
			return this;
		}
	}
}
