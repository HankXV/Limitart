package org.slingerxv.limitart.net.binary.distributed.config;

import org.slingerxv.limitart.net.binary.message.MessageFactory;

/**
 * 二进制服务器配置
 * 
 * @author hank
 *
 */
public final class InnerSlaveServerConfig {
	private String slaveName;
	private int myServerId;
	private String myIp;
	private int myServerPort;
	private String myServerPass;
	private int myInnerServerPort;
	private String myInnerServerPass;
	private String masterIp;
	private int masterServerPort;
	private String masterServerPass;
	private int masterInnerPort;
	private String masterInnerPass;
	private MessageFactory factory;

	private InnerSlaveServerConfig(InnerSlaveServerConfigBuilder builder) {
		this.slaveName = builder.slaveName;
		this.myServerId = builder.myServerId;
		this.myIp = builder.myServerIp;
		this.myServerPort = builder.myServerPort;
		this.myServerPass = builder.myServerPass;
		this.myInnerServerPort = builder.myInnerServerPort;
		this.myInnerServerPass = builder.myInnerServerPass;
		this.masterIp = builder.masterIp;
		this.masterServerPort = builder.masterServerPort;
		this.masterServerPass = builder.masterServerPass;
		this.masterInnerPort = builder.masterInnerPort;
		this.masterInnerPass = builder.masterInnerPass;
		if (builder.factory == null) {
			throw new NullPointerException("factory");
		}
		this.factory = builder.factory;
	}

	public String getSlaveName() {
		return slaveName;
	}

	public int getMyServerId() {
		return myServerId;
	}

	public String getMyIp() {
		return myIp;
	}

	public int getMyServerPort() {
		return myServerPort;
	}

	public String getMyServerPass() {
		return myServerPass;
	}

	public int getMyInnerServerPort() {
		return myInnerServerPort;
	}

	public String getMyInnerServerPass() {
		return myInnerServerPass;
	}

	public String getMasterIp() {
		return masterIp;
	}

	public int getMasterServerPort() {
		return masterServerPort;
	}

	public String getMasterServerPass() {
		return masterServerPass;
	}

	public int getMasterInnerPort() {
		return masterInnerPort;
	}

	public String getMasterInnerPass() {
		return masterInnerPass;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	public static class InnerSlaveServerConfigBuilder {
		private String slaveName;
		private int myServerId;
		private String myServerIp;
		private int myServerPort;
		private String myServerPass;
		private int myInnerServerPort;
		private String myInnerServerPass;
		private String masterIp;
		private int masterServerPort;
		private String masterServerPass;
		private int masterInnerPort;
		private String masterInnerPass;
		private MessageFactory factory;

		public InnerSlaveServerConfigBuilder() {
			this.slaveName = "Inner-Slave-Server";
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public InnerSlaveServerConfig build() {
			return new InnerSlaveServerConfig(this);
		}

		public InnerSlaveServerConfigBuilder slaveName(String slaveName) {
			this.slaveName = slaveName;
			return this;
		}

		public InnerSlaveServerConfigBuilder myServerId(int myServerId) {
			this.myServerId = myServerId;
			return this;
		}

		public InnerSlaveServerConfigBuilder myServerIp(String myServerIp) {
			this.myServerIp = myServerIp;
			return this;
		}

		public InnerSlaveServerConfigBuilder myServerPort(int myServerPort) {
			if (myServerPort >= 1024) {
				this.myServerPort = myServerPort;
			}
			return this;
		}

		public InnerSlaveServerConfigBuilder myServerPass(String myServerPass) {
			this.myServerPass = myServerPass;
			return this;
		}

		public InnerSlaveServerConfigBuilder myInnerServerPort(int myInnerServerPort) {
			if (myInnerServerPort >= 1024) {
				this.myInnerServerPort = myInnerServerPort;
			}
			return this;
		}

		public InnerSlaveServerConfigBuilder myInnerServerPass(String myInnerServerPass) {
			this.myInnerServerPass = myInnerServerPass;
			return this;
		}

		public InnerSlaveServerConfigBuilder masterIp(String masterIp) {
			this.masterIp = masterIp;
			return this;
		}

		public InnerSlaveServerConfigBuilder masterServerPort(int masterServerPort) {
			if (masterServerPort >= 1024) {
				this.masterServerPort = masterServerPort;
			}
			return this;
		}

		public InnerSlaveServerConfigBuilder masterInnerPass(String masterInnerPass) {
			this.masterInnerPass = masterInnerPass;
			return this;
		}

		public InnerSlaveServerConfigBuilder facotry(MessageFactory factory) {
			this.factory = factory;
			return this;
		}
	}
}
