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
package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * 推送服务到服务中心消息
 * 
 * @author Hank
 *
 */
public class PushServiceToServiceCenterProviderMessage extends Message {
	public String myIp;
	public int myPort;
	public int providerUID;
	public ArrayList<String> services = new ArrayList<>();

	@Override
	public short getMessageId() {
		return RpcMessageEnum.PushServiceToServiceCenterProviderMessage.getValue();
	}
}
