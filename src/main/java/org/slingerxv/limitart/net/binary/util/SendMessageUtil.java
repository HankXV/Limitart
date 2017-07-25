package org.slingerxv.limitart.net.binary.util;

import java.io.IOException;
import java.util.List;

import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryEncoder;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
		ByteBuf buffer = channel.alloc().ioBuffer();
		encoder.beforeWriteBody(buffer, msg.getMessageId());
		msg.buffer(buffer);
		try {
			msg.encode();
		} catch (Exception e) {
			throw new MessageCodecException(e);
		}
		msg.buffer(null);
		encoder.afterWriteBody(buffer);
		ChannelFuture writeAndFlush = channel.writeAndFlush(buffer);
		writeAndFlush.addListener((ChannelFutureListener) arg0 -> {
			Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
		});
	}

	public static void sendMessage(AbstractBinaryEncoder encoder, List<Channel> channels, Message msg,
			Proc3<Boolean, Throwable, Channel> listener) throws MessageCodecException {
		if (channels == null || channels.isEmpty()) {
			Procs.invoke(listener, false, new IOException(" channel list  is null"), null);
			return;
		}
		ByteBuf buffer = Unpooled.directBuffer(256);
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
			ChannelFuture writeAndFlush = channel.writeAndFlush(buffer.retainedSlice());
			writeAndFlush.addListener((ChannelFutureListener) arg0 -> {
				Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
			});
			if (i == channels.size() - 1) {
				buffer.release();
			}
		}
	}
}
