package org.slingerxv.limitart.net.binary.server.config;

import java.util.HashSet;

import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryDecoder;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryEncoder;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.struct.AddressPair;
import org.slingerxv.limitart.util.StringUtil;

import io.netty.channel.Channel;

/**
 * 二进制服务器配置
 * 
 * @author hank
 *
 */
public final class BinaryServerConfig {
	private String serverName;
	private AddressPair addressPair;
	private int connectionValidateTimeInSec;
	private AbstractBinaryDecoder decoder;
	private AbstractBinaryEncoder encoder;
	private HashSet<String> whiteList;
	private MessageFactory factory;

	// ---listener
	private Proc2<Channel, Boolean> onChannelStateChanged;
	private Proc2<Channel, Throwable> onExceptionCaught;
	private Proc1<Channel> onServerBind;
	private Proc1<Channel> onConnectionEffective;
	private Proc2<Message, IHandler<Message>> dispatchMessage;

	private BinaryServerConfig(BinaryServerConfigBuilder builder) {
		this.serverName = builder.serverName;
		if (builder.addressPair == null) {
			throw new NullPointerException("addressPair");
		}
		this.addressPair = builder.addressPair;
		this.connectionValidateTimeInSec = builder.connectionValidateTimeInSec;
		if (builder.decoder == null) {
			throw new NullPointerException("decoder");
		}
		this.decoder = builder.decoder;
		if (builder.encoder == null) {
			throw new NullPointerException("encoder");
		}
		this.encoder = builder.encoder;
		if (builder.whiteList == null) {
			throw new NullPointerException("whiteList");
		}
		this.whiteList = builder.whiteList;
		if (builder.factory == null) {
			throw new NullPointerException("factory");
		}
		this.factory = builder.factory;
		this.onChannelStateChanged = builder.onChannelStateChanged;
		this.onExceptionCaught = builder.onExceptionCaught;
		this.onServerBind = builder.onServerBind;
		this.onConnectionEffective = builder.onConnectionEffective;
		this.dispatchMessage = builder.dispatchMessage;
	}

	public String getServerName() {
		return this.serverName;
	}

	public int getConnectionValidateTimeInSec() {
		return connectionValidateTimeInSec;
	}

	public Proc2<Channel, Boolean> getOnChannelStateChanged() {
		return onChannelStateChanged;
	}

	public Proc2<Channel, Throwable> getOnExceptionCaught() {
		return onExceptionCaught;
	}

	public Proc1<Channel> getOnServerBind() {
		return onServerBind;
	}

	public Proc1<Channel> getOnConnectionEffective() {
		return onConnectionEffective;
	}

	public Proc2<Message, IHandler<Message>> getDispatchMessage() {
		return dispatchMessage;
	}

	public AddressPair getAddressPair() {
		return addressPair;
	}

	public AbstractBinaryDecoder getDecoder() {
		return decoder;
	}

	public AbstractBinaryEncoder getEncoder() {
		return encoder;
	}

	public HashSet<String> getWhiteList() {
		return whiteList;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	public static class BinaryServerConfigBuilder {
		private String serverName;
		private AddressPair addressPair;
		private int connectionValidateTimeInSec;
		private AbstractBinaryDecoder decoder;
		private AbstractBinaryEncoder encoder;
		private HashSet<String> whiteList;
		private MessageFactory factory;
		// ---listener
		private Proc2<Channel, Boolean> onChannelStateChanged;
		private Proc2<Channel, Throwable> onExceptionCaught;
		private Proc1<Channel> onServerBind;
		private Proc1<Channel> onConnectionEffective;
		private Proc2<Message, IHandler<Message>> dispatchMessage;

		public BinaryServerConfigBuilder() {
			this.serverName = "Binary-Server";
			this.addressPair = new AddressPair(8888, "limitart-core");
			this.connectionValidateTimeInSec = 20;
			this.decoder = AbstractBinaryDecoder.DEFAULT_DECODER;
			this.encoder = AbstractBinaryEncoder.DEFAULT_ENCODER;
			this.whiteList = new HashSet<>();
			this.dispatchMessage = new Proc2<Message, IHandler<Message>>() {

				@Override
				public void run(Message t1, IHandler<Message> t2) {
					t2.handle(t1);
				}
			};
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public BinaryServerConfig build() {
			return new BinaryServerConfig(this);
		}

		/**
		 * 自定义解码器
		 * 
		 * @param decoder
		 * @return
		 */
		public BinaryServerConfigBuilder decoder(AbstractBinaryDecoder decoder) {
			this.decoder = decoder;
			return this;
		}

		public BinaryServerConfigBuilder encoder(AbstractBinaryEncoder encoder) {
			this.encoder = encoder;
			return this;
		}

		public BinaryServerConfigBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		/**
		 * 绑定端口
		 * 
		 * @param port
		 * @return
		 */
		public BinaryServerConfigBuilder addressPair(AddressPair addressPair) {
			this.addressPair = addressPair;
			return this;
		}

		public BinaryServerConfigBuilder factory(MessageFactory factory) {
			this.factory = factory;
			return this;
		}

		/**
		 * 链接验证超时(秒)
		 * 
		 * @param connectionValidateTimeInSec
		 * @return
		 */
		public BinaryServerConfigBuilder connectionValidateTimeInSec(int connectionValidateTimeInSec) {
			this.connectionValidateTimeInSec = connectionValidateTimeInSec;
			return this;
		}

		public BinaryServerConfigBuilder whiteList(String... remoteAddress) {
			for (String ip : remoteAddress) {
				if (StringUtil.isIp(ip)) {
					this.whiteList.add(ip);
				}
			}
			return this;
		}

		public BinaryServerConfigBuilder onChannelStateChanged(Proc2<Channel, Boolean> onChannelStateChanged) {
			this.onChannelStateChanged = onChannelStateChanged;
			return this;
		}

		public BinaryServerConfigBuilder onExceptionCaught(Proc2<Channel, Throwable> onExceptionCaught) {
			this.onExceptionCaught = onExceptionCaught;
			return this;
		}

		public BinaryServerConfigBuilder onServerBind(Proc1<Channel> onServerBind) {
			this.onServerBind = onServerBind;
			return this;
		}

		public BinaryServerConfigBuilder onConnectionEffective(Proc1<Channel> onConnectionEffective) {
			this.onConnectionEffective = onConnectionEffective;
			return this;
		}

		public BinaryServerConfigBuilder dispatchMessage(Proc2<Message, IHandler<Message>> dispatchMessage) {
			this.dispatchMessage = dispatchMessage;
			return this;
		}
	}
}
