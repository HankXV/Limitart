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
package org.slingerxv.limitart.rpcx.center.config;

/**
 * 服务中心配置
 * 
 * @author Hank
 *
 */
public final class ServiceCenterXConfig {
	private int port;

	private ServiceCenterXConfig(ServiceCenterXConfigBuilder builder) {
		this.port = builder.port;
	}

	public int getPort() {
		return port;
	}

	public static class ServiceCenterXConfigBuilder {
		private int port;

		public ServiceCenterXConfigBuilder() {
			this.port = 9000;
		}

		public ServiceCenterXConfig build() {
			return new ServiceCenterXConfig(this);
		}

		public ServiceCenterXConfigBuilder port(int port) {
			if (port >= 1024) {
				this.port = port;
			}
			return this;
		}
	}
}
