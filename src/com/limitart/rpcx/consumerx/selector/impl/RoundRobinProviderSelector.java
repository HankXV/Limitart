package com.limitart.rpcx.consumerx.selector.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.limitart.rpcx.consumerx.selector.define.IProviderSelector;


/**
 * 服务器轮询选择器
 * 
 * @author hank
 *
 */
public class RoundRobinProviderSelector implements IProviderSelector {
	private AtomicInteger now = new AtomicInteger(0);

	@Override
	public Integer selectServer(String serviceName, String methodOverloadName, Object[] args,
			List<Integer> serverList) {
		int incrementAndGet = now.getAndIncrement();
		if (incrementAndGet >= serverList.size()) {
			incrementAndGet = 0;
			now.set(incrementAndGet);
		}
		return serverList.get(incrementAndGet);
	}

}
