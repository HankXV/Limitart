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
