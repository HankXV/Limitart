package org.slingerxv.limitart.rpcx.message.schedule;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * 服务中心任务触发
 * 
 * @author hank
 *
 */
public class TriggerScheduleServiceCenterToProviderServiceCenterMessage extends Message {
	private String jobName;
	private boolean end;

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.TriggerScheduleServiceCenterToProviderServiceCenterMessage.getValue();
	}
}
