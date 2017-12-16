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
package org.slingerxv.limitart.rpcx.message.constant;

/**
 * RPC相关消息Id
 * 
 * @author hank
 *
 */
public enum RpcMessageEnum {
	/**
	 * 链接验证服务器
	 */
	RpcExecuteClientMessage((short) -101),
	/**
	 * 链接验证客户端
	 */
	RpcResultServerMessage((short) -102),
	/**
	 * 直接拉取RPC服务器服务列表
	 */
	DirectFetchProviderServicesMessage((short) -103),
	/**
	 * RPC服务器服务列表拉取结果
	 */
	DirectFetchProviderServicesResultMessage((short) -104),
	/**
	 * 推送服务到服务中心消息
	 */
	PushServiceToServiceCenterProviderMessage((short) -105),
	/**
	 * 向服务中心订阅服务
	 */
	SubscribeServiceFromServiceCenterConsumerMessage((short) -106),
	/**
	 * 服务中心订阅服务返回
	 */
	SubscribeServiceResultServiceCenterMessage((short) -107),
	/**
	 * 通知断开链接
	 */
	NoticeProviderDisconnectedServiceCenterMessage((short) -108),
	/**
	 * 向服务中心申请调度任务
	 */
	AddScheduleToServiceCenterProviderMessage((short) -109),
	/**
	 * 服务中心任务触发
	 */
	TriggerScheduleServiceCenterToProviderServiceCenterMessage((short) -110),;
	private short messageId;

	RpcMessageEnum(short messageId) {
		this.messageId = messageId;
	}

	public short getValue() {
		return this.messageId;
	}
}
