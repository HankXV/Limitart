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
package top.limitart.net.binary.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.net.Session;
import top.limitart.net.binary.BinaryMessage;
import top.limitart.net.binary.BinaryServerInterceptor;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 白名单拦截器
 *
 * @author hank
 */
public class BinaryServerWhiteListInterceptor implements BinaryServerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinaryServerWhiteListInterceptor.class);
    private final Set<String> whiteList = new HashSet<>();

    public BinaryServerWhiteListInterceptor(String... ips) {
        Collections.addAll(whiteList, ips);
    }

    public BinaryServerWhiteListInterceptor(Collection<String> ips) {
        whiteList.addAll(ips);
    }

    @Override
    public boolean onConnected(Session session) {
        InetSocketAddress insocket = (InetSocketAddress) session.remoteAddress();
        String remoteAddress = insocket.getAddress().getHostAddress();
        if (!whiteList.contains(remoteAddress)) {
            LOGGER.info("ip: " + remoteAddress + " rejected link!");
            return true;
        }
        return false;
    }

    @Override
    public boolean onMessageIn(Session session, BinaryMessage msg) {
        return false;
    }

    @Override
    public void onMessageOut(Session session, BinaryMessage msg) {

    }
}
