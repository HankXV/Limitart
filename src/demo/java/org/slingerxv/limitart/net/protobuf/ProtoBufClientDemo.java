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

import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.protobuf.HelloProtoBuf.HelloToServer;
import org.slingerxv.limitart.net.protobuf.message.ProtoBufFactory;

/**
 * @author hank
 *
 */
public class ProtoBufClientDemo {
	public static void main(String[] args) throws Exception {
		ProtoBufFactory factory = new ProtoBufFactory();
		factory.registerHandler(new HelloToClientHandler());
		ProtoBufClient client = new ProtoBufClient.ProtoBufClientBuilder().factory(factory)
				.remoteAddress(new AddressPair("127.0.0.1", 8888)).onChannelStateChanged((c, f) -> {
					if (f) {
						HelloToServer build = HelloToServer.newBuilder().setContent("hello limitart!").build();
						c.sendMessage(build);
					}
				}).build();
		client.connect();
	}
}