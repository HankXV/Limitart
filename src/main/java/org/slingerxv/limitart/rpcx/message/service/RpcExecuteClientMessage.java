package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;

import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

public class RpcExecuteClientMessage extends RPCMeta {
	public int requestId;
	public String moduleName;
	public String methodName;
	public ArrayList<String> paramTypes = new ArrayList<>();
	public ArrayList<Object> params = new ArrayList<>();

	@Override
	public String toString() {
		return moduleName + "@" + methodName;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.RpcExecuteClientMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.requestId);
		putString(this.moduleName);
		putString(this.methodName);
		putStringList(this.paramTypes);
		if (this.paramTypes != null) {
			for (Object object : params) {
				encodeObj(object);
			}
		}
	}

	@Override
	public void decode() throws Exception {
		this.requestId = getInt();
		this.moduleName = getString();
		this.methodName = getString();
		this.paramTypes = getStringList();
		// 这里特殊处理，因为服务器要知道参数类型去寻找具体的类
		for (String type : this.paramTypes) {
			this.params.add(decodeObj(type));
		}
	}
}
