package com.limitart.rpcx.consumerx.struct;

/**
 * RPC提供者远程地址
 * 
 * @author hank
 *
 */
public class ProviderRemote {
	private String providerIp;
	private int providerPort;

	public ProviderRemote(String providerIp, int providerPort) {
		this.providerIp = providerIp;
		this.providerPort = providerPort;
	}

	public String getProviderIp() {
		return providerIp;
	}

	public int getProviderPort() {
		return providerPort;
	}

}
