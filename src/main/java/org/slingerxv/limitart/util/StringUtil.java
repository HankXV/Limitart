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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public final class StringUtil {
	public static final String BUDDHA = "\n                   _ooOoo_" + "\n" + "                  o8888888o" + "\n"
			+ "                  88\" . \"88" + "\n" + "                  (| -_- |)" + "\n"
			+ "                  O\\  =  /O" + "\n" + "               ____/`---'\\____" + "\n"
			+ "             .'  \\\\|     |//  `." + "\n" + "            /  \\\\|||  :  |||//  \\" + "\n"
			+ "           /  _||||| -:- |||||-  \\" + "\n" + "           |   | \\\\\\  -  /// |   |" + "\n"
			+ "           | \\_|  ''\\---/''  |   |" + "\n" + "           \\  .-\\__  `-`  ___/-. /" + "\n"
			+ "         ___`. .'  /--.--\\  `. . __" + "\n" + "      .\"\" '<  `.___\\_<|>_/___.'  >'\"\"." + "\n"
			+ "     | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |" + "\n" + "     \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /"
			+ "\n" + "======`-.____`-.___\\_____/___.-`____.-'======" + "\n" + "                   `=---='" + "\n"
			+ "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^" + "\n";
	private static String PHONE_REG = "^((1[0-9][0-9]))\\d{8}$";
	private static String EMAIL_REG = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	private final static String IP_REG = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
	private static String IP_INNER_REG = "^((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})$";

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
		return matchReg(value, PHONE_REG);
	}

	/**
	 * 是否是邮箱
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isMail(String value) {
		return matchReg(value, EMAIL_REG);
	}

	/**
	 * 是否是ip地址
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isIp4(String value) {
		return matchReg(value, IP_REG);
	}

	public static boolean isInnerIp4(String value) {
		return matchReg(value, IP_INNER_REG);
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
		Matcher m = p.matcher(value);
		return m.matches();
	}

	public static String toJSONWithClassInfo(Object obj) {
		if (obj == null) {
			return null;
		}
		return JSON.toJSONString(obj, SerializerFeature.WriteClassName, SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.WriteNullNumberAsZero);
	}

	public static String toJSON(Object obj) {
		if (obj == null) {
			return null;
		}
		return JSON.toJSONString(obj);
	}

	public static <T> T toObject(String text, Class<T> clazz) {
		if (text == null) {
			return null;
		}
		return JSON.parseObject(text, clazz);
	}

	public static <T> List<T> toArray(String text, Class<T> clazz) {
		if (text == null) {
			return new ArrayList<>();
		}
		return JSON.parseArray(text, clazz);
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

	public static String toShortStr(double num) {
		if (num < 100000L)// 十万
			return String.format("%.2f", num) + "";
		else if (num < 1000000L)
			return String.format("%.2f", num / 1000L) + "K";
		else if (num < 1000000000L)
			return String.format("%.2f", num / 1000000L) + "M";
		else if (num < 1000000000000L)
			return String.format("%.2f", num / 1000000000L) + "B";
		else if (num < 1000000000000000L)
			return String.format("%.2f", num / 1000000000000L) + "T";
		else
			return String.format("%.2f", num / 1000000000000000L) + "P";
	}
}
