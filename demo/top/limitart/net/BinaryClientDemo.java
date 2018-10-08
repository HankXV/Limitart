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

import io.netty.channel.EventLoop;
import top.limitart.mapping.Router;
import top.limitart.net.binary.BinaryEndPoint;
import top.limitart.net.binary.BinaryMessage;

/**
 * @author hank
 */
public class BinaryClientDemo {
    public static void main(String[] args) throws Exception {
        new BinaryEndPoint.Builder(false)
                .router(Router.empty().registerMapperClass(BinaryManagerDemo.class)).onConnected((s, state) -> {
            if (state) {
                try {
                    s.writeNow(new BinaryMessageDemo());
                } catch (Exception ignored) {
                }
            }
        }).build().start(new AddressPair("127.0.0.1", 8888));
    }
}
