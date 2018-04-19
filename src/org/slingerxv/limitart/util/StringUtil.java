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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author hank
 */
public final class StringUtil {
    private final static Pattern PHONE_REG = Pattern.compile("^((1[0-9][0-9]))\\d{8}$");
    private final static Pattern EMAIL_REG = Pattern
            .compile("^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
    private final static Pattern IP_REG = Pattern
            .compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
    private final static Pattern IP_INNER_REG = Pattern.compile(
            "^((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})$");
    private final static Pattern SQL_KEY_WORD =
            Pattern.compile(
                    "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
                            + "(\\b(select|update|and|or|delete|insert|trancate|char|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)",
                    Pattern.CASE_INSENSITIVE);

    private StringUtil() {
    }

    /**
     * 字符串是否为""或null
     *
     * @param value
     * @return
     */
    public static boolean empty(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * 字符串不为""或null
     *
     * @param value
     * @return
     */
    public static boolean notEmpty(String value) {
        return !empty(value);
    }

    /**
     * 是否是手机号码
     *
     * @param value
     * @return
     */
    public static boolean isPhoneNumber(String value) {
        return !empty(value) && matchReg(PHONE_REG, value);
    }

    /**
     * 是否是邮箱
     *
     * @param value
     * @return
     */
    public static boolean isMail(String value) {
        return !empty(value) && matchReg(EMAIL_REG, value);
    }

    /**
     * 是否是IP地址
     *
     * @param value
     * @return
     */
    public static boolean isIp4(String value) {
        return !empty(value) && matchReg(IP_REG, value);
    }

    /**
     * 是否是内网IP
     *
     * @param value
     * @return
     */
    public static boolean isInnerIp4(String value) {
        return !empty(value) && matchReg(IP_INNER_REG, value);
    }

    /**
     * 是否与SQL相关
     *
     * @param value
     * @return
     */
    public static boolean isSQLRelative(String value) {
        return !empty(value) && SQL_KEY_WORD.matcher(value).find();
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

    /**
     * 是否匹配正则表达式
     *
     * @param p
     * @param target
     * @return
     */
    public static boolean matchReg(Pattern p, String target) {
        Matcher m = p.matcher(target);
        return m.matches();
    }
}
