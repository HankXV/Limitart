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
package top.limitart.net;

import top.limitart.base.ImmutableCouple;
import top.limitart.base.Nullable;
import top.limitart.util.StringUtil;

/**
 * IP端口对
 *
 * @author Hank
 */
public class AddressPair extends ImmutableCouple<String, Integer> {
    public static AddressPair withPort(int port) {
        return new AddressPair(port);
    }

    public static AddressPair withIP(String ip, int port) {
        return new AddressPair(ip, port);
    }

    /**
     * 用端口初始化(一般用于服务器)
     *
     * @param port
     */
    public AddressPair(int port) {
        this(null, port);
    }

    /**
     * 用IP和端口初始化
     *
     * @param ip   IP地址
     * @param port 端口
     */
    public AddressPair(@Nullable String ip, int port) {
        super(ip, port);
        if (ip != null && !StringUtil.isIp4(ip)) {
            throw new IllegalArgumentException("ip format error");
        }
    }

    /**
     * 获取IP
     *
     * @return IP地址
     */
    public @Nullable
    String getIp() {
        return get1();
    }

    /**
     * 获取端口
     *
     * @return 端口地址
     */
    public int getPort() {
        return get2();
    }

    @Override
    public String toString() {
        return (StringUtil.empty(getIp()) ? "0.0.0.0" : getIp()) + ":" + getPort();
    }
}
