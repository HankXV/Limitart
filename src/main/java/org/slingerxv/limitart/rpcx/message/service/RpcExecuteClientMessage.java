package org.slingerxv.limitart.rpcx.message.service;

import java.util.ArrayList;
import java.util.List;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.rpcx.message.constant.RpcMessageEnum;

public class RpcExecuteClientMessage extends Message {
	private int requestId;
	private String moduleName;
	private String methodName;
	private List<String> paramTypes = new ArrayList<>();
	private List<Object> params = new ArrayList<>();

	@Override
	public String toString() {
		return moduleName + "@" + methodName;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

	public List<String> getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(List<String> paramTypes) {
		this.paramTypes = paramTypes;
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
