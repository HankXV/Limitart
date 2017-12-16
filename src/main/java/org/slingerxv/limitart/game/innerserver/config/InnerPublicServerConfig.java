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
package org.slingerxv.limitart.game.innerserver.config;

import java.util.Objects;

import org.slingerxv.limitart.net.binary.message.MessageFactory;

public class InnerPublicServerConfig {
	private int masterPort;
	private MessageFactory factory;

	private InnerPublicServerConfig(InnerPublicServerConfigBuilder builder) {
		this.masterPort = builder.masterPort;
		this.factory = Objects.requireNonNull(builder.factory, "factory");
	}

	public int getMasterPort() {
		return masterPort;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	public static class InnerPublicServerConfigBuilder {
		private int masterPort;
		private MessageFactory factory;

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public InnerPublicServerConfig build() {
			return new InnerPublicServerConfig(this);
		}

		public InnerPublicServerConfigBuilder masterPort(int masterPort) {
			if (masterPort >= 1024) {
				this.masterPort = masterPort;
			}
			return this;
		}

		public InnerPublicServerConfigBuilder factory(MessageFactory factory) {
			this.factory = factory;
			return this;
		}
	}
}
