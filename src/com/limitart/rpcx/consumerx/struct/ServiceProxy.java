package com.limitart.rpcx.consumerx.struct;

import com.limitart.rpcx.struct.RpcProviderName;

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
