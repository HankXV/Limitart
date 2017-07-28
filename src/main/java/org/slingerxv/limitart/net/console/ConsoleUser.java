package org.slingerxv.limitart.net.console;

import io.netty.channel.Channel;

public class ConsoleUser {
	private String username;
	private String pass;
	private Channel channel;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
}
