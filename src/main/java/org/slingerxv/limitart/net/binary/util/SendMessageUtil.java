package org.slingerxv.limitart.net.binary.util;

import java.io.IOException;
import java.util.List;

import org.slingerxv.limitart.net.binary.codec.AbstractBinaryEncoder;
import org.slingerxv.limitart.net.binary.listener.SendMessageListener;
import org.slingerxv.limitart.net.binary.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public final class SendMessageUtil {

    private SendMessageUtil() {
    }

    public static void sendMessage(AbstractBinaryEncoder encoder, Channel channel, Message msg,
                                   SendMessageListener listener) throws Exception {
        if (channel == null) {
            if (listener != null) {
                listener.onComplete(false, new NullPointerException("channel"), null);
            }
            return;
        }
        if (!channel.isWritable()) {
            if (listener != null) {
                listener.onComplete(false, new IOException(" channel " + channel.remoteAddress() + " is unwritable"),
                        channel);
            }
            return;
        }
        ByteBuf buffer = channel.alloc().ioBuffer();
        encoder.beforeWriteBody(buffer, msg.getMessageId());
        msg.buffer(buffer);
        msg.encode();
        msg.buffer(null);
        encoder.afterWriteBody(buffer);
        ChannelFuture writeAndFlush = channel.writeAndFlush(buffer);
        writeAndFlush.addListener((ChannelFutureListener) arg0 -> {
            if (listener != null) {
                listener.onComplete(arg0.isSuccess(), arg0.cause(), arg0.channel());
            }
        });
    }

    public static void sendMessage(AbstractBinaryEncoder encoder, List<Channel> channels, Message msg,
                                   SendMessageListener listener) throws Exception {
        if (channels == null || channels.isEmpty()) {
            if (listener != null) {
                listener.onComplete(false, new IOException(" channel list  is null"), null);
            }
            return;
        }
        ByteBuf buffer = Unpooled.directBuffer(256);
        encoder.beforeWriteBody(buffer, msg.getMessageId());
        msg.buffer(buffer);
        msg.encode();
        msg.buffer(null);
        encoder.afterWriteBody(buffer);
        for (int i = 0; i < channels.size(); ++i) {
            Channel channel = channels.get(i);
            if (!channel.isWritable()) {
                if (listener != null) {
                    listener.onComplete(false,
                            new IOException(" channel " + channel.remoteAddress() + " is unwritable"), channel);
                }
                continue;
            }
            ChannelFuture writeAndFlush = channel.writeAndFlush(buffer.retainedSlice());
            if (listener != null) {
                writeAndFlush.addListener((ChannelFutureListener) arg0 -> listener.onComplete(arg0.isSuccess(), arg0.cause(), arg0.channel()));
            }
            if (i == channels.size() - 1) {
                buffer.release();
            }
        }
    }
}
