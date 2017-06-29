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

public class RpcUtil {
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
	 */
	public static boolean checkParamType(Class<?> type) {
		if (!type.isPrimitive()) {
			if (type.isArray()) {
				return checkParamType(type.getComponentType());
			} else {
				if (!MessageMeta.class.isAssignableFrom(type) && type != String.class && type != List.class
						&& type != ArrayList.class && type != HashMap.class && type != Map.class
						&& type != HashSet.class && type != Set.class) {
					return false;
				}
			}
		}
		return true;
	}
}
