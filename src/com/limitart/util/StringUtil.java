package com.limitart.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public final class StringUtil {
	private static String PHONE_REG = "^((1[0-9][0-9]))\\d{8}$";
	private static String EMAIL_REG = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

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

	public static final <T> T toObject(String text, Class<T> clazz) {
		if (text == null) {
			return null;
		}
		return JSON.parseObject(text, clazz);
	}

	public static final <T> List<T> toArray(String text, Class<T> clazz) {
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
}
