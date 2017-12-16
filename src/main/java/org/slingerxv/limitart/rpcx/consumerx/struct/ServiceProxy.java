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

import org.slingerxv.limitart.rpcx.struct.RpcProviderName;

/**
 * 服务代理
 * 
 * @author hank
 *
 */
public class ServiceProxy {
	private String serviceName;
	private RpcProviderName providerName;
	private Object instance;

	public Object self() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public RpcProviderName getProviderName() {
		return providerName;
	}

	public void setProviderName(RpcProviderName providerName) {
		this.providerName = providerName;
	}

}
