package org.slingerxv.limitart.rpcx.message.service.meta;

import java.util.ArrayList;

import org.slingerxv.limitart.net.binary.message.MessageMeta;

/**
 * 服务信息
 * 
 * @author hank
 *
 */
public class ProviderServiceMeta extends MessageMeta {
	public String serviceName;
	public ArrayList<ProviderHostMeta> hostInfos = new ArrayList<>();
}
