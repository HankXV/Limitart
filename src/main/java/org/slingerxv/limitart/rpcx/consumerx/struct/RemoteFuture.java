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
package org.slingerxv.limitart.rpcx.consumerx.struct;

import java.util.concurrent.CountDownLatch;

import org.slingerxv.limitart.rpcx.consumerx.define.IServiceAsyncCallback;
import org.slingerxv.limitart.rpcx.message.service.RpcResultServerMessage;

/**
 * 远程调用回调
 * 
 * @author hank
 *
 */
public class RemoteFuture {
	private int requestId;
	private int providerId;
	private volatile RpcResultServerMessage responseResult;
	private IServiceAsyncCallback callback;
	private CountDownLatch countDownLatch = new CountDownLatch(1);

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public RpcResultServerMessage getResponseResult() {
		return responseResult;
	}

	public void setResponseResult(RpcResultServerMessage responseResult) {
		this.responseResult = responseResult;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public IServiceAsyncCallback getCallback() {
		return callback;
	}

	public void setCallback(IServiceAsyncCallback callback) {
		this.callback = callback;
	}

	public CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}

	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

}
