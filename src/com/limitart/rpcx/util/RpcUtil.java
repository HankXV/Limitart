package com.limitart.rpcx.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.limitart.net.binary.message.MessageMeta;
import com.limitart.rpcx.define.ServiceX;
import com.limitart.rpcx.exception.ServiceXProxyException;
import com.limitart.rpcx.struct.RpcProviderName;
import com.limitart.util.StringUtil;

public final class RpcUtil {
	private RpcUtil() {
	}

	/**
	 * 获取服务名称
	 * 
	 * @param providerName
	 * @param serviceClass
	 * @return
	 * @throws ServiceXProxyException
	 */
	public static String getServiceName(RpcProviderName providerName, Class<?> serviceClass)
			throws ServiceXProxyException {
		ServiceX annotation = serviceClass.getAnnotation(ServiceX.class);
		if (annotation == null) {
			throw new ServiceXProxyException("not a ServiceX class(annotation)");
		}
		String modole = annotation.module();
		if (StringUtil.isEmptyOrNull(modole)) {
			modole = serviceClass.getSimpleName();
		}
		return providerName.getName() + "@" + modole;
	}

	/**
	 * 检查RPC参数类型是否符合标准
	 * 
	 * @param type
	 * @return
	 * @throws ServiceXProxyException
	 */
	public static void checkParamType(Class<?> type) throws ServiceXProxyException {
		if (!type.isPrimitive()) {
			if (type.isArray()) {
				checkParamType(type.getComponentType());
			} else {
				if (!MessageMeta.class.isAssignableFrom(type) && type != String.class && type != List.class
						&& type != ArrayList.class && type != HashMap.class && type != Map.class
						&& type != HashSet.class && type != Set.class) {
					throw new ServiceXProxyException(type.getName() + "必须是基础类型（包括其数组）或" + MessageMeta.class.getName()
							+ "的子类，或者为上述类型的java.util.List或java.util.ArrayList");
				}
			}
		}
	}
}
