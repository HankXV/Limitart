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

public class InnerFightServerConfig {
	private int serverId;
	private String fightServerIp;
	private int fightServerPort;
	private int fightServerInnerPort;
	private String fightServerPass;
	private String publicIp;
	private int publicPort;
	private MessageFactory factory;

	private InnerFightServerConfig(InnerFightServerConfigBuilder builder) {
		this.serverId = builder.serverId;
		this.fightServerIp = builder.fightServerIp;
		this.fightServerPort = builder.fightServerPort;
		this.fightServerInnerPort = builder.fightServerInnerPort;
		this.fightServerPass = builder.fightServerPass;
		this.publicIp = builder.publicIp;
		this.publicPort = builder.publicPort;
		this.factory = Objects.requireNonNull(builder.factory, "factory");
	}

	public int getServerId() {
		return serverId;
	}

	public String getFightServerIp() {
		return fightServerIp;
	}

	public int getFightServerPort() {
		return fightServerPort;
	}

	public int getFightServerInnerPort() {
		return fightServerInnerPort;
	}

	public String getFightServerPass() {
		return fightServerPass;
	}

	public String getPublicIp() {
		return publicIp;
	}

	public int getPublicPort() {
		return publicPort;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	public static class InnerFightServerConfigBuilder {
		private int serverId;
		private String fightServerIp;
		private int fightServerPort;
		private int fightServerInnerPort;
		private String fightServerPass;
		private String publicIp;
		private int publicPort;
		private MessageFactory factory;

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public InnerFightServerConfig build() {
			return new InnerFightServerConfig(this);
		}

		public InnerFightServerConfigBuilder serverId(int serverId) {
			this.serverId = serverId;
			return this;
		}

		public InnerFightServerConfigBuilder fightServerIp(String fightServerIp) {
			this.fightServerIp = fightServerIp;
			return this;
		}

		public InnerFightServerConfigBuilder fightServerPort(int fightServerPort) {
			if (fightServerPort >= 1024) {
				this.fightServerPort = fightServerPort;
			}
			return this;
		}

		public InnerFightServerConfigBuilder fightServerInnerPort(int fightServerInnerPort) {
			if (fightServerInnerPort >= 1024) {
				this.fightServerInnerPort = fightServerInnerPort;
			}
			return this;
		}

		public InnerFightServerConfigBuilder fightServerPass(String fightServerPass) {
			this.fightServerPass = fightServerPass;
			return this;
		}

		public InnerFightServerConfigBuilder publicIp(String publicIp) {
			this.publicIp = publicIp;
			return this;
		}

		public InnerFightServerConfigBuilder publicPort(int publicPort) {
			if (publicPort >= 1024) {
				this.publicPort = publicPort;
			}
			return this;
		}

		public InnerFightServerConfigBuilder factory(MessageFactory factory) {
			this.factory = factory;
			return this;
		}
	}
}
