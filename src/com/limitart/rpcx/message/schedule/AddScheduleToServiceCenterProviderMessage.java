package com.limitart.rpcx.message.schedule;

import com.limitart.net.binary.message.Message;
import com.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * 向服务中心申请调度任务
 * 
 * @author hank
 *
 */
public class AddScheduleToServiceCenterProviderMessage extends Message {
	private String jobName;
	private int providerId;
	private String cronExpression;
	private int intervalInHours;
	private int intervalInMinutes;
	private int intervalInSeconds;
	private int intervalInMillis;
	private int repeatCount;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public int getIntervalInHours() {
		return intervalInHours;
	}

	public void setIntervalInHours(int intervalInHours) {
		this.intervalInHours = intervalInHours;
	}

	public int getIntervalInMinutes() {
		return intervalInMinutes;
	}

	public void setIntervalInMinutes(int intervalInMinutes) {
		this.intervalInMinutes = intervalInMinutes;
	}

	public int getIntervalInSeconds() {
		return intervalInSeconds;
	}

	public void setIntervalInSeconds(int intervalInSeconds) {
		this.intervalInSeconds = intervalInSeconds;
	}

	public int getIntervalInMillis() {
		return intervalInMillis;
	}

	public void setIntervalInMillis(int intervalInMillis) {
		this.intervalInMillis = intervalInMillis;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.AddScheduleToServiceCenterProviderMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putString(this.jobName);
		putInt(this.providerId);
		putString(this.cronExpression);
		putInt(this.intervalInHours);
		putInt(this.intervalInMinutes);
		putInt(this.intervalInSeconds);
		putInt(this.intervalInMillis);
		putInt(this.repeatCount);
	}

	@Override
	public void decode() throws Exception {
		this.jobName = getString();
		this.providerId = getInt();
		this.cronExpression = getString();
		this.intervalInHours = getInt();
		this.intervalInMinutes = getInt();
		this.intervalInSeconds = getInt();
		this.intervalInMillis = getInt();
		this.repeatCount = getInt();
	}

}
