package org.slingerxv.limitart.rpcx.message.service.meta;

import org.slingerxv.limitart.net.binary.message.MessageMeta;

/**
 * RPC提供者自身主机信息
 * 
 * @author hank
 *
 */
public class ProviderHostMeta extends MessageMeta {
	private int providerId;
	private String ip;
	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}
}
