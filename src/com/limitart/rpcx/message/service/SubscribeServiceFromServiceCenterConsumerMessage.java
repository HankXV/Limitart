package com.limitart.rpcx.message.service;

import com.limitart.net.binary.message.Message;
import com.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * 向服务中心订阅服务
 * 
 * @author Hank
 *
 */
public class SubscribeServiceFromServiceCenterConsumerMessage extends Message {

	@Override
	public short getMessageId() {
		return RpcMessageEnum.SubscribeServiceFromServiceCenterConsumerMessage.getValue();
	}

	@Override
	public void encode() throws Exception {

	}

	@Override
	public void decode() throws Exception {

	}
}
