package com.limitart.rpcx.consumerx.selector.define;

import java.util.List;

import com.limitart.rpcx.consumerx.selector.impl.RoundRobinProviderSelector;


public interface IProviderSelector {
	IProviderSelector DEFAULT = new RoundRobinProviderSelector();

	Integer selectServer(String serviceName, String methodOverloadName, Object[] args, List<Integer> serverList);
}
