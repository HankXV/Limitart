package org.slingerxv.limitart.game.innerserver.config;

import org.slingerxv.limitart.net.binary.message.MessageFactory;

public class InnerPublicServerConfig {
	private int masterPort;
	private MessageFactory factory;

	private InnerPublicServerConfig(InnerPublicServerConfigBuilder builder) {
		this.masterPort = builder.masterPort;
		if (builder.factory == null) {
			throw new NullPointerException("factory");
		}
		this.factory = builder.factory;
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

		public InnerPublicServerConfigBuilder() {
		}

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
