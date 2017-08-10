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
package org.slingerxv.limitart.net.binary.codec;

import io.netty.buffer.ByteBuf;

/**
 * 二进制编码器
 * 
 * @author hank
 *
 */
public abstract class AbstractBinaryEncoder {
	public static final AbstractBinaryEncoder DEFAULT_ENCODER = new AbstractBinaryEncoder() {

		@Override
		public void beforeWriteBody(ByteBuf buf, short messageId) {
			// 消息长度(包括消息Id)
			buf.writeShort(0);
			// 消息Id
			buf.writeShort(messageId);
		}

		@Override
		public void afterWriteBody(ByteBuf buf) {
			// 重设消息长度
			buf.setShort(0, buf.readableBytes() - Short.BYTES);
		}

	};

	public abstract void beforeWriteBody(ByteBuf buf, short messageId);

	public abstract void afterWriteBody(ByteBuf buf);
}
