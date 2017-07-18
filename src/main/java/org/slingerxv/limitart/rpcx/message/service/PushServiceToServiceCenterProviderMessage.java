package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * 推送服务到服务中心消息
 * 
 * @author Hank
 *
 */
public class PushServiceToServiceCenterProviderMessage extends Message {
	public String myIp;
	public int myPort;
	public int providerUID;
	public ArrayList<String> services = new ArrayList<>();

	@Override
	public short getMessageId() {
		return RpcMessageEnum.PushServiceToServiceCenterProviderMessage.getValue();
	}
}
