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
