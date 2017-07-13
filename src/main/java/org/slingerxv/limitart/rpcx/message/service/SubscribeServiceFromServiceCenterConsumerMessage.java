package org.slingerxv.limitart.rpcx.message.service;

import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

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
