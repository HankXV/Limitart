package com.limitart.net.binary;

import com.limitart.net.binary.client.BinaryClient;
import com.limitart.net.binary.client.config.BinaryClientConfig;
import com.limitart.net.binary.client.config.BinaryClientConfig.BinaryClientConfigBuilder;
import com.limitart.net.binary.client.listener.BinaryClientEventListener;
import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.MessageFactory;

public class BinaryClientDemo {
	public static void main(String[] args) throws Exception {
		BinaryClientConfig build = new BinaryClientConfigBuilder().remoteIp("127.0.0.1").remotePort(8888)
				.clientName("BinaryClientDemo").build();
		MessageFactory factory = new MessageFactory();
		BinaryClientEventListener binaryClientEventListener = new BinaryClientEventListener() {

			@Override
			public void onExceptionCaught(BinaryClient client, Throwable cause) {
				cause.printStackTrace();
			}

			@Override
			public void onConnectionEffective(BinaryClient client) {
				BinaryMessageDemo message = new BinaryMessageDemo();
				message.info = "Hello Limitart!";
				try {
					client.sendMessage(message, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onChannelInactive(BinaryClient client) {

			}

			@Override
			public void onChannelActive(BinaryClient client) {

			}

			@Override
			public void dispatchMessage(Message message) {
				message.getHandler().handle(message);
			}
		};
		BinaryClient client = new BinaryClient(build, binaryClientEventListener, factory);
		client.connect();
	}
}
