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
package org.slingerxv.limitart.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author hank
 *
 */
public final class StringUtil {
	private final static Pattern PHONE_REG = Pattern.compile("^((1[0-9][0-9]))\\d{8}$");
	private final static Pattern EMAIL_REG = Pattern
			.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
	private final static Pattern IP_REG = Pattern
			.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
	private final static Pattern IP_INNER_REG = Pattern.compile(
			"^((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})$");

	private StringUtil() {
	}

	public static boolean isEmptyOrNull(String value) {
		return value == null || value.trim().length() == 0;
	}

	/**
	 * 是否是手机号码
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isPhoneNumber(String value) {
		return matchReg(PHONE_REG, value);
	}

	/**
	 * 是否是邮箱
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isMail(String value) {
		return matchReg(EMAIL_REG, value);
	}

	/**
	 * 是否是ip地址
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isIp4(String value) {
		return matchReg(IP_REG, value);
	}

	public static boolean isInnerIp4(String value) {
		return matchReg(IP_INNER_REG, value);
	}

	/**
	 * 是否匹配正则表达式
	 * 
	 * @param value
	 * @param reg
	 * @return
	 */
	public static boolean matchReg(String value, String reg) {
		Pattern p = Pattern.compile(reg);
		return matchReg(p, value);
	}

	public static boolean matchReg(Pattern p, String target) {
		Matcher m = p.matcher(target);
		return m.matches();
	}

	public static <T> String toCommaList(List<T> list) {
		if (list == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		for (T obj : list) {
			buffer.append(obj.toString()).append(',');
		}
		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		return buffer.toString();
	}

	public static List<String> fromCommaString(String value) {
		List<String> result = new ArrayList<>();
		if (value == null) {
			return result;
		}
		String[] split = value.split(",");
		for (String temp : split) {
			result.add(temp);
		}
		return result;
	}
}
