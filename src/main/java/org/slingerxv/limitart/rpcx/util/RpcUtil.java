/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.rpcx.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slingerxv.limitart.net.binary.message.MessageMeta;
import org.slingerxv.limitart.rpcx.define.ServiceX;
import org.slingerxv.limitart.rpcx.exception.ServiceXProxyException;
import org.slingerxv.limitart.rpcx.struct.RpcProviderName;
import org.slingerxv.limitart.util.StringUtil;

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
