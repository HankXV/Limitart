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
