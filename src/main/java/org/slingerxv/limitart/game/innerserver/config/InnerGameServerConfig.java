package org.slingerxv.limitart.game.innerserver.config;

import java.util.Objects;

import org.slingerxv.limitart.net.binary.message.MessageFactory;

public class InnerGameServerConfig {
	private int serverId;
	private String gameServerIp;
	private int gameServerPort;
	private String gameServerPass;
	private String publicIp;
	private int publicPort;
	private MessageFactory factory;

	private InnerGameServerConfig(InnerGameServerConfigBuilder builder) {
		this.serverId = builder.serverId;
		this.gameServerIp = builder.gameServerIp;
		this.gameServerPort = builder.gameServerPort;
		this.gameServerPass = builder.gameServerPass;
		this.publicIp = builder.publicIp;
		this.publicPort = builder.publicPort;
		this.factory = Objects.requireNonNull(builder.factory, "factory");
	}

	public int getServerId() {
		return serverId;
	}

	public String getGameServerIp() {
		return gameServerIp;
	}

	public int getGameServerPort() {
		return gameServerPort;
	}

	public String getGameServerPass() {
		return gameServerPass;
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

	public static class InnerGameServerConfigBuilder {
		private int serverId;
		private String gameServerIp;
		private int gameServerPort;
		private String gameServerPass;
		private String publicIp;
		private int publicPort;
		private MessageFactory factory;

		public InnerGameServerConfigBuilder() {
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public InnerGameServerConfig build() {
			return new InnerGameServerConfig(this);
		}

		public InnerGameServerConfigBuilder serverId(int serverId) {
			this.serverId = serverId;
			return this;
		}

		public InnerGameServerConfigBuilder gameServerIp(String gameServerIp) {
			this.gameServerIp = gameServerIp;
			return this;
		}

		public InnerGameServerConfigBuilder gameServerPort(int gameServerPort) {
			if (gameServerPort >= 1024) {
				this.gameServerPort = gameServerPort;
			}
			return this;
		}

		public InnerGameServerConfigBuilder gameServerPass(String gameServerPass) {
			this.gameServerPass = gameServerPass;
			return this;
		}

		public InnerGameServerConfigBuilder publicIp(String publicIp) {
			this.publicIp = publicIp;
			return this;
		}

		public InnerGameServerConfigBuilder publicPort(int publicPort) {
			if (publicPort >= 1024) {
				this.publicPort = publicPort;
			}
			return this;
		}

		public InnerGameServerConfigBuilder factory(MessageFactory factory) {
			this.factory = factory;
			return this;
		}
	}
}
