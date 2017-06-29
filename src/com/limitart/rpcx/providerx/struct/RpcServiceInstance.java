package com.limitart.rpcx.providerx.struct;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * RPC服务实现类
 * 
 * @author ank
 *
 */
public class RpcServiceInstance {
	private Object instance;
	private HashMap<String, Method> methods = new HashMap<>();

	public Object self() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public HashMap<String, Method> getMethods() {
		return methods;
	}

	public void setMethods(HashMap<String, Method> methods) {
		this.methods = methods;
	}
}
