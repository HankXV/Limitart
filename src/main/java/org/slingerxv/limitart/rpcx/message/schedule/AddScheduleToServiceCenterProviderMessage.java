package org.slingerxv.limitart.rpcx.message.schedule;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * 向服务中心申请调度任务
 * 
 * @author hank
 *
 */
public class AddScheduleToServiceCenterProviderMessage extends Message {
	public String jobName;
	public int providerId;
	public String cronExpression;
	public int intervalInHours;
	public int intervalInMinutes;
	public int intervalInSeconds;
	public int intervalInMillis;
	public int repeatCount;

	@Override
	public short getMessageId() {
		return RpcMessageEnum.AddScheduleToServiceCenterProviderMessage.getValue();
	}
}
