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
package org.slingerxv.limitart.net.binary.distributed.util;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class InnerServerUtil {
	private final static String INNER_PASS = "INNER_PASS";
	/**
	 * 服务器IdKey
	 */
	private static AttributeKey<Integer> SERVER_ID_KEY = AttributeKey.newInstance("SERVER_ID_KEY");
	/**
	 * 服务器类型Key
	 */
	private static AttributeKey<Integer> SERVER_TYPE_KEY = AttributeKey.newInstance("SERVER_TYPE_KEY");

	/**
	 * 设置服务器类型
	 * 
	 * @param channel
	 * @param serverType
	 */
	public static void setServerType(Channel channel, int serverType) {
		channel.attr(SERVER_TYPE_KEY).set(serverType);
	}

	/**
	 * 获取服务器类型
	 * 
	 * @param channel
	 * @return
	 */
	public static Integer getServerType(Channel channel) {
		return channel.attr(SERVER_TYPE_KEY).get();
	}

	/**
	 * 设置服务器Id
	 * 
	 * @param channel
	 * @param serverId
	 */
	public static void setServerId(Channel channel, int serverId) {
		channel.attr(SERVER_ID_KEY).set(serverId);
	}

	/**
	 * 获取服务器Id
	 * 
	 * @param channel
	 * @return
	 */
	public static Integer getServerId(Channel channel) {
		return channel.attr(SERVER_ID_KEY).get();
	}

	public static String getInnerPass() {
		return INNER_PASS;
	}

}
