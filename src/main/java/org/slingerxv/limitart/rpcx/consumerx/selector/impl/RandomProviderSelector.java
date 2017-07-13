package org.slingerxv.limitart.rpcx.consumerx.selector.impl;

import java.util.List;

import org.slingerxv.limitart.rpcx.consumerx.selector.define.IProviderSelector;
import org.slingerxv.limitart.util.RandomUtil;


/**
 * 随机服务器选择器
 * 
 * @author hank
 *
 */
public class RandomProviderSelector implements IProviderSelector {

	@Override
	public Integer selectServer(String serviceName, String methodOverloadName, Object[] args,
			List<Integer> serverList) {
		return serverList.get(RandomUtil.randomInt(0, serverList.size() - 1));
	}

}
