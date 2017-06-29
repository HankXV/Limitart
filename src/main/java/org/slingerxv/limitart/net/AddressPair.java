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
package org.slingerxv.limitart.net;

import org.slingerxv.limitart.util.StringUtil;

/**
 * IP端口对
 * 
 * @author Hank
 *
 */
public class AddressPair {
	private String ip;
	private int port;

	public AddressPair(int port) {
		this(null, port);
	}

	public AddressPair(String ip, int port) {
		if (!StringUtil.isIp4(ip)) {
			throw new IllegalArgumentException("ip format error");
		}
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

}
