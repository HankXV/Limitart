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

import java.util.ArrayList;

import org.slingerxv.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;
import org.slingerxv.limitart.net.binary.message.Message;

public class ResServerJoinMaster2SlaveMessage extends Message {
	public ArrayList<InnerServerInfo> infos = new ArrayList<>();

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ResServerJoinMaster2SlaveMessage.getValue();
	}
}
