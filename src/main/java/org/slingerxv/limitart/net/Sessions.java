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

import java.io.IOException;
import java.util.List;

import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * @author Hank
 *
 */
public class Sessions {
	public static void sendMessage(AbstractBinaryEncoder encoder, Session session, Message msg,
			Proc3<Boolean, Throwable, Session> listener) throws MessageCodecException {
		ByteBuf buffer = Unpooled.buffer();
		encoder.beforeWriteBody(buffer, msg.getMessageId());
		msg.buffer(buffer);
		try {
			msg.encode();
		} catch (Exception e) {
			throw new MessageCodecException(e);
		}
		msg.buffer(null);
		encoder.afterWriteBody(buffer);
		session.writeNow(buffer, resultCallback);(buffer).addListener((ChannelFutureListener) arg0 -> {
			Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
		});
	}

	public static void sendMessage(AbstractBinaryEncoder encoder, List<Session> sessions, Message msg,
			Proc3<Boolean, Throwable, Channel> listener) throws MessageCodecException {
		if (sessions == null || sessions.isEmpty()) {
			Procs.invoke(listener, false, new IOException(" channel list  is null"), null);
			return;
		}
		ByteBuf buffer = Unpooled.buffer();
		encoder.beforeWriteBody(buffer, msg.getMessageId());
		msg.buffer(buffer);
		try {
			msg.encode();
		} catch (Exception e) {
			throw new MessageCodecException(e);
		}
		msg.buffer(null);
		encoder.afterWriteBody(buffer);
		for (int i = 0; i < sessions.size(); ++i) {
			Session session = sessions.get(i);
			if (!session.getChannel().isWritable()) {
				Procs.invoke(listener, false,
						new IOException(" channel " + session.getChannel().remoteAddress() + " is unwritable"),
						session.getChannel());
				continue;
			}
			ByteBuf retainedSlice = buffer.retainedSlice();
			session.getChannel().writeAndFlush(retainedSlice).addListener((ChannelFutureListener) arg0 -> {
				Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
			});
			if (i == sessions.size() - 1) {
				buffer.release();
			}
		}
	}
}
