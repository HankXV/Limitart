package org.slingerxv.limitart.rpcx.consumerx.selector.define;

import java.util.List;

import org.slingerxv.limitart.rpcx.consumerx.selector.impl.RoundRobinProviderSelector;


public interface IProviderSelector {
	IProviderSelector DEFAULT = new RoundRobinProviderSelector();

	Integer selectServer(String serviceName, String methodOverloadName, Object[] args, List<Integer> serverList);
}
