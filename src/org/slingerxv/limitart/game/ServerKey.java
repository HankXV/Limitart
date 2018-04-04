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
package org.slingerxv.limitart.game;


import org.slingerxv.limitart.base.Conditions;

/**
 * 区服和平台生成的一个KEY(满足以下条件的应用即可)
 * {@code 1<=server<=0xFFFF}
 * {@code 1<=platform<=0x7F}
 *
 * @author hank
 * @version 2018/3/17 0017 12:03
 */
public interface ServerKey {
    /**
     * 区服最大
     */
    int SERVER_MAX = 0xFFFF;
    /**
     * 区服最小
     */
    int SERVER_MIN = 1;
    /**
     * 平台最小
     */
    byte PLATFORM_MIN = 1;

    int serverKey();

    static int serverKey(int server, byte platform) {
        Conditions.args(server >= SERVER_MIN && server <= SERVER_MAX, "%s<=server<=%s,yours:%s", SERVER_MIN, SERVER_MAX, server);
        Conditions.args(
                platform >= PLATFORM_MIN, "platform>=%s,yours:%s", PLATFORM_MIN, platform);
        return ((platform & 0XFF) << 16) | ((server & 0xFFFF));
    }

    static int server(ServerKey key) {
        return server(key.serverKey());
    }

    static byte platform(ServerKey key) {
        return platform(key.serverKey());
    }

    static int server(int key) {
        return key & 0X00FFFF;
    }

    static byte platform(int key) {
        return (byte) (key >> 16);
    }
}
