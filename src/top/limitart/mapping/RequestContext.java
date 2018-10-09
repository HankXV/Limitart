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
package top.limitart.mapping;

import top.limitart.base.Conditions;

/**
 * 路由参数上下文
 *
 * @param <MSG> 消息
 * @author hank
 * @version 2018/10/8 0008 20:01
 */
public abstract class RequestContext<MSG extends Request> {
    private final MSG msg;

    public RequestContext(MSG msg) {
        Conditions.notNull(msg, "msg");
        this.msg = msg;
    }

    public <T extends MSG> T msg() {
        return (T) msg;
    }
}
