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
package org.slingerxv.limitart.net.binary.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.net.Session;
import org.slingerxv.limitart.net.binary.BinaryMessage;
import org.slingerxv.limitart.net.binary.BinaryServerInterceptor;

/**
 * 接收消息速度拦截器
 *
 * @author hank
 */
public class BinaryMessageReceivedTimeInterceptor implements BinaryServerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinaryMessageReceivedTimeInterceptor.class);
    private final static int LAST_RECEIVE_MSG_TIME = 1;
    private final int millsInterval;

    /**
     * 消息接收间隔
     *
     * @param millsInterval
     */
    public BinaryMessageReceivedTimeInterceptor(int millsInterval) {
        this.millsInterval = millsInterval;
    }

    @Override
    public boolean onConnected(Session session) {
        return false;
    }

    @Override
    public boolean onMessageIn(Session session, BinaryMessage msg) {
        long now = System.currentTimeMillis();
        if (session.params().containsKey(LAST_RECEIVE_MSG_TIME)) {
            Long lastReceiveTime = session.params().getLong(LAST_RECEIVE_MSG_TIME);
            if (now - lastReceiveTime < millsInterval) {
                LOGGER.info(session + " send message too fast,close!");
                session.close();
                return true;
            }
        }
        session.params().putLong(LAST_RECEIVE_MSG_TIME, now);
        return false;
    }

    @Override
    public void onMessageOut(Session session, BinaryMessage msg) {

    }

}
