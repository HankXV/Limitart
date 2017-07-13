package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;
import java.util.List;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * RPC服务器服务列表拉取结果
 * 
 * @author hank
 *
 */
public class DirectFetchProviderServicesResultMessage extends Message {
	private int providerId;
	private List<String> services = new ArrayList<>();

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.DirectFetchProviderServicesResultMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.providerId);
		putStringList(this.services);
	}

	@Override
	public void decode() throws Exception {
		this.providerId = getInt();
		this.services = getStringList();
	}
}
