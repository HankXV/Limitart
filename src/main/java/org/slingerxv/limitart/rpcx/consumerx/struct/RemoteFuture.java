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
