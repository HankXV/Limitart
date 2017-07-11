package com.limitart.net.binary.distributed.util;

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
