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
import org.slingerxv.limitart.net.protobuf.handler.ProtoBufHandler;

import io.netty.channel.Channel;

/**
 * @author hank
 *
 */
public class HelloToClientHandler implements ProtoBufHandler<HelloToClient> {

	@Override
	public void handle(Channel channel, HelloToClient msg) {
		System.out.println(msg.getContent());
	}

	@Override
	public HelloToClient getDefaultInstance() {
		return HelloToClient.getDefaultInstance();
	}

}
