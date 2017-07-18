package org.slingerxv.limitart.rpcx.message.service;

import org.slingerxv.limitart.net.binary.message.Message;
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
}
