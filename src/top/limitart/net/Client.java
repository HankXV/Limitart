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

/**
 * 客户端接口
 *
 * @author hank
 */
public interface Client {
    /**
     * 连接
     */
    void connect();

    /**
     * 断开链接
     */
    void disConnect();

    /**
     * 获取客户端名称
     *
     * @return 客户端名称
     */
    String clientName();
}
