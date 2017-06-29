package com.limitart.rpcx.consumerx.selector.define;

import java.util.List;

import com.limitart.rpcx.consumerx.selector.impl.RoundRobinProviderSelector;


public interface IProviderSelector {
	public final static IProviderSelector DEFAULT = new RoundRobinProviderSelector();

	public Integer selectServer(String serviceName, String methodOverloadName, Object[] args, List<Integer> serverList);
}
