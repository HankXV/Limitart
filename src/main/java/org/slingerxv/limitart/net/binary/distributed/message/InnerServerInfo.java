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
package org.slingerxv.limitart.net.binary.distributed.message;

import org.slingerxv.limitart.net.binary.message.MessageMeta;

public class InnerServerInfo extends MessageMeta {
	public int serverType;
	public int serverId;
	public String outIp;
	public int outPort;
	public int innerPort;
	public String outPass;

	@Override
	public String toString() {
		return "InnerServerInfo [serverType=" + serverType + ", serverId=" + serverId + ", outIp=" + outIp
				+ ", outPort=" + outPort + ", innerPort=" + innerPort + ", outPass=" + outPass + "]";
	}
}
