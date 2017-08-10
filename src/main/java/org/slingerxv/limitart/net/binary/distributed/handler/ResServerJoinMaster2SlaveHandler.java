/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.net.binary.distributed.handler;

import org.slingerxv.limitart.net.binary.distributed.InnerSlaveServer;
import org.slingerxv.limitart.net.binary.distributed.message.ResServerJoinMaster2SlaveMessage;
import org.slingerxv.limitart.net.binary.handler.IHandler;

public class ResServerJoinMaster2SlaveHandler implements IHandler<ResServerJoinMaster2SlaveMessage> {

	@Override
	public void handle(ResServerJoinMaster2SlaveMessage msg) {
		((InnerSlaveServer) msg.getExtra()).ResServerJoinMaster2Slave(msg);
	}

}
