package com.limitart.rpcx.message.service;

import com.limitart.net.binary.message.Message;
import com.limitart.rpcx.message.constant.RpcMessageEnum;

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

	@Override
	public void encode() throws Exception {

	}

	@Override
	public void decode() throws Exception {

	}

}
