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

import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

public class RpcResultServerMessage extends RPCMeta {
	private int requestId;
	private int errorCode;
	private String returnType;
	private Object returnVal;

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public Object getReturnVal() {
		return returnVal;
	}

	public void setReturnVal(Object returnVal) {
		this.returnVal = returnVal;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.RpcResultServerMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.requestId);
		putInt(this.errorCode);
		putString(this.returnType);
		encodeObj(this.returnVal);
	}

	@Override
	public void decode() throws Exception {
		this.requestId = getInt();
		this.errorCode = getInt();
		this.returnType = getString();
		this.returnVal = decodeObj(this.returnType);
	}
}
