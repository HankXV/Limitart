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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slingerxv.limitart.base.*;
import org.slingerxv.limitart.logging.Logger;
import org.slingerxv.limitart.logging.Loggers;
import org.slingerxv.limitart.net.Session;

import java.io.IOException;
import java.util.List;

/**
 * 消息发送工具
 *
 * @author hank
 */
public class BinarySendMessageUtil {
    private static Logger log = Loggers.create();

    /**
     * 发送消息
     *
     * @param encoder
     * @param session
     * @param msg
     * @param listener
     * @throws BinaryMessageCodecException
     */
    public static void sendMessage(@NotNull BinaryEncoder encoder, @NotNull Session session, @NotNull BinaryMessage msg,
                                   @Nullable Proc2<Boolean, Throwable> listener) {
        ByteBuf buffer = Unpooled.buffer();
        encoder.beforeWriteBody(buffer, msg.messageID());
        msg.buffer(buffer);
        try {
            msg.encode();
        } catch (IllegalAccessException | BinaryMessageCodecException e) {
            log.error("message encode error!", e);
        }
        encoder.afterWriteBody(buffer);
        session.writeNow(buffer, listener);
        msg.buffer(null);
    }

    /**
     * 发送消息
     *
     * @param encoder
     * @param sessions
     * @param msg
     * @param listener
     * @throws BinaryMessageCodecException
     */
    public static void sendMessage(@NotNull BinaryEncoder encoder, @NotNull List<Session> sessions, @NotNull BinaryMessage msg,
                                   @Nullable Proc3<Session, Boolean, Throwable> listener) {
        if (sessions == null || sessions.isEmpty()) {
            Procs.invoke(listener, null, false, new IOException(" channel list  is null"));
            return;
        }
        ByteBuf buffer = Unpooled.buffer();
        encoder.beforeWriteBody(buffer, msg.messageID());
        msg.buffer(buffer);
        try {
            msg.encode();
        } catch (IllegalAccessException | BinaryMessageCodecException e) {
            log.error("message encode error!", e);
        }
        encoder.afterWriteBody(buffer);
        for (int i = 0; i < sessions.size(); ++i) {
            Session session = sessions.get(i);
            ByteBuf retainedSlice = buffer.retainedSlice();
            session.writeNow(retainedSlice, (suc, cause) -> Procs.invoke(listener, session, suc, cause));
            if (i == sessions.size() - 1) {
                buffer.release();
            }
        }
        msg.buffer(null);
    }
}
