package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;

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
	public ArrayList<ProviderServiceMeta> services = new ArrayList<>();

	@Override
	public short getMessageId() {
		return RpcMessageEnum.SubscribeServiceResultServiceCenterMessage.getValue();
	}
}
