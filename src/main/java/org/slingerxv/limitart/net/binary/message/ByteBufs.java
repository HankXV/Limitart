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
package org.slingerxv.limitart.net.binary.message;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

/**
 * @author hank
 *
 */
public final class ByteBufs {
	private ByteBufs() {
	}

	public static final void writeRawVarint64(ByteBuf buffer, long value) {
		while (true) {
			if ((value & ~0x7FL) == 0) {
				buffer.writeByte(((byte) value));
				return;
			} else {
				buffer.writeByte((byte) (((int) value & 0x7F) | 0x80));
				value >>>= 7;
			}
		}
	}

	public static final long readRawVarint64(ByteBuf buffer) {
		int shift = 0;
		long result = 0;
		while (shift < 64) {
			final byte b = buffer.readByte();
			result |= (long) (b & 0x7F) << shift;
			if ((b & 0x80) == 0) {
				return result;
			}
			shift += 7;
		}
		throw new CorruptedFrameException("malformed varint.");
	}

	public static final void writeRawVarint32(ByteBuf buffer, int val) {
		while (true) {
			if ((val & ~0x7F) == 0) {
				buffer.writeByte((byte) val);
				return;
			} else {
				buffer.writeByte((byte) ((val & 0x7F) | 0x80));
				val >>>= 7;
			}
		}
	}

	public static int readRawVarint32(ByteBuf buffer) {
		int x;
		if ((x = buffer.readByte()) >= 0) {
			return x;
		} else if ((x ^= (buffer.readByte() << 7)) < 0L) {
			x ^= (~0L << 7);
		} else if ((x ^= (buffer.readByte() << 14)) >= 0L) {
			x ^= (~0L << 7) ^ (~0L << 14);
		} else if ((x ^= (buffer.readByte() << 21)) < 0L) {
			x ^= (~0L << 7) ^ (~0L << 14) ^ (~0L << 21);
		} else {
			int y = buffer.readByte();
			x ^= y << 28;
			x ^= (~0L << 7) ^ (~0L << 14) ^ (~0L << 21) ^ (~0L << 28);
			if (y < 0) {
				throw new CorruptedFrameException("malformed varint.");
			}
		}
		return x;

	}
}
