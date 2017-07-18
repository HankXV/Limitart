package org.slingerxv.limitart.rpcx.message.service;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * 直接拉取RPC服务器服务列表
 * 
 * @author hank
 *
 */
public class DirectFetchProviderServicesMessage extends Message {

	@Override
	public short getMessageId() {
		return RpcMessageEnum.DirectFetchProviderServicesMessage.getValue();
	}
}
