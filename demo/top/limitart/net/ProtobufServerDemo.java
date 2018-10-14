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

import com.google.protobuf.Message;
import top.limitart.mapping.Router;
import top.limitart.net.protobuf.ProtobufEndPoint;
import top.limitart.net.protobuf.ProtobufRequestParam;

/**
 * @author hank
 */
public class ProtobufServerDemo {
    static final ProtobufSessionRole role = new ProtobufSessionRole();

    public static void main(String[] args)
            throws Exception {
        ProtobufEndPoint.builder(true)
                .router(Router.empty(Message.class, ProtobufRequestParam.class).registerMapperClass(MessageMapper.class)).onConnected((s, b) -> {
            if (b) {
                role.join(s, () -> System.out.println("join session success!"), Throwable::printStackTrace);
            } else {
                role.leave(s);
                System.out.println("leave session success");
            }
        }).build().start(AddressPair.withPort(7878));
    }
}
