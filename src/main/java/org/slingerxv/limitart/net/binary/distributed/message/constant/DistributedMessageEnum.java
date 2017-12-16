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
package org.slingerxv.limitart.net.binary.distributed.message.constant;

public enum DistributedMessageEnum {
	ReqConnectionReportSlave2MasterMessage((short)-101),
	ReqServerLoadSlave2MasterMessage((short)-102),
	ResServerJoinMaster2SlaveMessage((short)-103),
	ResServerQuitMaster2SlaveMessage((short)-104);
	private short messageId;

	DistributedMessageEnum(short messageId) {
		this.messageId = messageId;
	}

	public short getValue() {
		return this.messageId;
	}
}
