package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * RPC服务器服务列表拉取结果
 * 
 * @author hank
 *
 */
public class DirectFetchProviderServicesResultMessage extends Message {
	public int providerId;
	public ArrayList<String> services = new ArrayList<>();

	@Override
	public short getMessageId() {
		return RpcMessageEnum.DirectFetchProviderServicesResultMessage.getValue();
	}
}
