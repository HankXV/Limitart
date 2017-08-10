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
package org.slingerxv.limitart.net.binary.util;

import java.io.IOException;
import java.util.List;

import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryEncoder;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public final class SendMessageUtil {

	private SendMessageUtil() {
	}

	public static void sendMessage(AbstractBinaryEncoder encoder, Channel channel, Message msg,
			Proc3<Boolean, Throwable, Channel> listener) throws MessageCodecException {
		if (channel == null) {
			Procs.invoke(listener, false, new NullPointerException("channel"), null);
			return;
		}
		if (!channel.isWritable()) {
			Procs.invoke(listener, false, new IOException(" channel " + channel.remoteAddress() + " is unwritable"),
					channel);
			return;
		}
		ByteBuf buffer = PooledByteBufAllocator.DEFAULT.ioBuffer();
		encoder.beforeWriteBody(buffer, msg.getMessageId());
		msg.buffer(buffer);
		try {
			msg.encode();
		} catch (Exception e) {
			throw new MessageCodecException(e);
		}
		msg.buffer(null);
		encoder.afterWriteBody(buffer);
		channel.writeAndFlush(buffer).addListener((ChannelFutureListener) arg0 -> {
			Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
		});
	}

	public static void sendMessage(AbstractBinaryEncoder encoder, List<Channel> channels, Message msg,
			Proc3<Boolean, Throwable, Channel> listener) throws MessageCodecException {
		if (channels == null || channels.isEmpty()) {
			Procs.invoke(listener, false, new IOException(" channel list  is null"), null);
			return;
		}
		ByteBuf buffer = PooledByteBufAllocator.DEFAULT.ioBuffer();
		encoder.beforeWriteBody(buffer, msg.getMessageId());
		msg.buffer(buffer);
		try {
			msg.encode();
		} catch (Exception e) {
			throw new MessageCodecException(e);
		}
		msg.buffer(null);
		encoder.afterWriteBody(buffer);
		for (int i = 0; i < channels.size(); ++i) {
			Channel channel = channels.get(i);
			if (!channel.isWritable()) {
				Procs.invoke(listener, false, new IOException(" channel " + channel.remoteAddress() + " is unwritable"),
						channel);
				continue;
			}
			channel.writeAndFlush(buffer.retainedSlice()).addListener((ChannelFutureListener) arg0 -> {
				Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
			});
			if (i == channels.size() - 1) {
				buffer.release();
			}
		}
	}
}
