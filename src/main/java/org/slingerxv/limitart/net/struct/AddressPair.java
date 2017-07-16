package org.slingerxv.limitart.net.struct;

import io.netty.util.internal.StringUtil;

public class AddressPair {
	private String ip;
	private int port;
	private String pass = "limitart-core";

	public AddressPair(String ip, int port, String pass) {
		this.ip = ip;
		this.port = port;
		if (!StringUtil.isNullOrEmpty(pass)) {
			this.pass = pass;
		}
	}

	public AddressPair(int port) {
		this(null, port);
	}

	public AddressPair(int port, String pass) {
		this(null, port, pass);
	}

	public AddressPair(String ip, int port) {
		this(ip, port, null);
	}

	public String getPass() {
		return pass;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

}
