package org.slingerxv.limitart.rpcx.message.service.meta;

import java.util.ArrayList;
import java.util.List;

import org.slingerxv.limitart.net.binary.message.MessageMeta;


/**
 * 服务信息
 * 
 * @author hank
 *
 */
public class ProviderServiceMeta extends MessageMeta {
	private String serviceName;
	private List<ProviderHostMeta> hostInfos = new ArrayList<>();

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<ProviderHostMeta> getHostInfos() {
		return hostInfos;
	}

	public void setHostInfos(List<ProviderHostMeta> hostInfos) {
		this.hostInfos = hostInfos;
	}

	@Override
	public void encode() throws Exception {
		putString(this.serviceName);
		putMessageMetaList(this.hostInfos);
	}

	@Override
	public void decode() throws Exception {
		this.serviceName = getString();
		this.hostInfos = getMessageMetaList(ProviderHostMeta.class);
	}
}
