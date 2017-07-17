package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.binary.server.BinaryServer;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig.BinaryServerConfigBuilder;
import org.slingerxv.limitart.net.binary.server.listener.BinaryServerEventListener;
import org.slingerxv.limitart.net.struct.AddressPair;

import io.netty.channel.Channel;

public class BinaryServerDemo {
	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, MessageIDDuplicatedException {
		BinaryServerConfig build = new BinaryServerConfigBuilder().addressPair(new AddressPair(8888))
				.serverName("BinaryServerDemo").factory(new MessageFactory().registerMsg(BinaryHandlerDemo.class))
				.build();
		BinaryServerEventListener binaryServerEventListener = new BinaryServerEventListener() {
			// 当网络模块发生错误时
			@Override
			public void onExceptionCaught(Channel channel, Throwable cause) {
				cause.printStackTrace();
			}

			@Override
			public void onChannelStateChanged(Channel channel, boolean active) {

			}

			// 当服务器完成绑定时
			@Override
			public void onServerBind(Channel channel) {

			}

			// 当链接有效时(通常在这里我们认为一个链接是有效的，因为它经过了验证)
			@Override
			public void onConnectionEffective(Channel channel) {

			}

			// 当接收到消息时
			@Override
			public void dispatchMessage(Message message) {
				message.getHandler().handle(message);
			}
		};
		BinaryServer server = new BinaryServer(build, binaryServerEventListener);
		server.startServer();
	}
}
