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

import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

public class RpcExecuteClientMessage extends RPCMeta {
	public int requestId;
	public String moduleName;
	public String methodName;
	public ArrayList<String> paramTypes = new ArrayList<>();
	public ArrayList<Object> params = new ArrayList<>();

	@Override
	public String toString() {
		return moduleName + "@" + methodName;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.RpcExecuteClientMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.requestId);
		putString(this.moduleName);
		putString(this.methodName);
		putStringList(this.paramTypes);
		if (this.paramTypes != null) {
			for (Object object : params) {
				encodeObj(object);
			}
		}
	}

	@Override
	public void decode() throws Exception {
		this.requestId = getInt();
		this.moduleName = getString();
		this.methodName = getString();
		this.paramTypes = getStringList();
		// 这里特殊处理，因为服务器要知道参数类型去寻找具体的类
		for (String type : this.paramTypes) {
			this.params.add(decodeObj(type));
		}
	}
}
