package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;
import java.util.List;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

/**
 * 推送服务到服务中心消息
 * 
 * @author Hank
 *
 */
public class PushServiceToServiceCenterProviderMessage extends Message {
	private String myIp;
	private int myPort;
	private int providerUID;
	private List<String> services = new ArrayList<>();

	public String getMyIp() {
		return myIp;
	}

	public void setMyIp(String myIp) {
		this.myIp = myIp;
	}

	public int getMyPort() {
		return myPort;
	}

	public void setMyPort(int myPort) {
		this.myPort = myPort;
	}

	public int getProviderUID() {
		return providerUID;
	}

	public void setProviderUID(int providerUID) {
		this.providerUID = providerUID;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.PushServiceToServiceCenterProviderMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putString(this.myIp);
		putInt(this.myPort);
		putInt(this.providerUID);
		putStringList(this.services);
	}

	@Override
	public void decode() throws Exception {
		this.myIp = getString();
		this.myPort = getInt();
		this.providerUID = getInt();
		this.services = getStringList();
	}
}
