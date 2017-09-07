/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.net.protobuf;

import org.slingerxv.limitart.net.protobuf.HelloProtoBuf.HelloToClient;
import org.slingerxv.limitart.net.protobuf.HelloProtoBuf.HelloToServer;
import org.slingerxv.limitart.net.protobuf.handler.ProtoBufHandler;

import io.netty.channel.Channel;

/**
 * @author hank
 *
 */
public class HelloToServerHandler implements ProtoBufHandler<HelloToServer> {

	@Override
	public void handle(Channel channel, HelloToServer msg) {
		System.out.println(msg.getContent());
		HelloToClient result = HelloToClient.newBuilder().setContent("hello client!").build();
		channel.writeAndFlush(result);
	}

	@Override
	public HelloToServer getDefaultInstance() {
		return HelloToServer.getDefaultInstance();
	}

}
