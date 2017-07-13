package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;
import java.util.List;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;
import org.slingerxv.limitart.rpcx.message.service.meta.ProviderServiceMeta;


/**
 * 向服务中心订阅服务
 * 
 * @author Hank
 *
 */
public class SubscribeServiceResultServiceCenterMessage extends Message {
	private List<ProviderServiceMeta> services = new ArrayList<>();

	public List<ProviderServiceMeta> getServices() {
		return services;
	}

	public void setServices(List<ProviderServiceMeta> services) {
		this.services = services;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.SubscribeServiceResultServiceCenterMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putMessageMetaList(this.services);
	}

	@Override
	public void decode() throws Exception {
		this.services = getMessageMetaList(ProviderServiceMeta.class);
	}
}
