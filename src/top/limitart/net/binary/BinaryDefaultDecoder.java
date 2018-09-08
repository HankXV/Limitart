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
package top.limitart.net.binary;

import io.netty.buffer.ByteBuf;
import top.limitart.net.Session;

/**
 * 默认解码器
 * 
 * @author hank
 *
 */
public class BinaryDefaultDecoder extends BinaryDecoder {
	public final static BinaryDefaultDecoder ME = new BinaryDefaultDecoder();

	public BinaryDefaultDecoder() {
		super(Short.MAX_VALUE, 0, 2, 0, 2);
	}

	@Override
	public short readMessageId(Session session, ByteBuf buffer) {
		return buffer.readShort();
	}

}
