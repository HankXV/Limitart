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


import top.limitart.base.Proc2;
import top.limitart.collections.ConstraintMap;
import top.limitart.concurrent.Actor;

import java.net.SocketAddress;

/**
 * 会话
 *
 * @param <B> 会话传递消息的介质
 * @param <T> 会话占用的线程
 * @author Hank
 */
public interface Session<B, T> extends Actor.Place<T> {
    /**
     * 立即写出数据
     *
     * @param buf
     * @param resultCallback
     */
    void writeNow(B buf, Proc2<Boolean, Throwable> resultCallback);

    /**
     * 立即写出数据
     *
     * @param buf
     */
    void writeNow(B buf);

    /**
     * 是否可写
     *
     * @return
     */
    boolean writable();

    /**
     * 关闭会话
     *
     * @param resultCallback
     */
    void close(Proc2<Boolean, Throwable> resultCallback);

    /**
     * 关闭会话
     */
    void close();

    /**
     * 远程地址
     *
     * @return
     */
    SocketAddress remoteAddress();

    /**
     * 本地地址
     *
     * @return
     */
    SocketAddress localAddress();

    /**
     * 获取自定义参数列表
     *
     * @return the params
     */
    ConstraintMap<Integer> params();

    /**
     * 当前会话所处线程
     *
     * @return
     */
    T thread();
}
