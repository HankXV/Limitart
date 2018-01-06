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
package org.slingerxv.limitart.net.binary;


import org.slingerxv.limitart.base.*;
import org.slingerxv.limitart.net.Session;

/**
 * 消息处理方法参数
 *
 * @author hank
 */
public class BinaryRequestParam {
    private BinaryMessage msg;
    private Session session;
    private Object extra;

    public BinaryRequestParam(@NotNull Session session, @NotNull BinaryMessage msg) {
        Conditions.notNull(session, "session");
        Conditions.notNull(msg, "msg");
        this.session = session;
        this.msg = msg;
    }

    @SuppressWarnings("unchecked")
    public @NotNull
    <T extends BinaryMessage> T msg() {
        return (T) msg;
    }

    public @NotNull
    Session session() {
        return this.session;
    }

    /**
     * @return the extra
     */
    public Object extra() {
        return extra;
    }

    /**
     * @param extra the extra to set
     */
    public void extra(Object extra) {
        this.extra = extra;
    }
}
