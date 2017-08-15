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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slingerxv.limitart.collections.FrequencyReadRankMap;
import org.slingerxv.limitart.collections.define.IRankMap;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.binary.codec.AbstractBinaryEncoder;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.string.LineSeparator;

public final class SendMessageUtil {
	public static boolean IS_FLOW = true;
	private static FlowComparator COMPARATOR = new FlowComparator();
	private static ConcurrentHashMap<Class<? extends Message>, Integer> FLOW_MIN = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Class<? extends Message>, Integer> FLOW_MAX = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Class<? extends Message>, Integer> FLOW_COUNT = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Class<? extends Message>, Long> FLOW_SIZE = new ConcurrentHashMap<>();

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
		flow(msg.getClass(), buffer);
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
		for (int i = 0; i < channels.size(); ++i) {
			Channel channel = channels.get(i);
			if (!channel.isWritable()) {
				Procs.invoke(listener, false, new IOException(" channel " + channel.remoteAddress() + " is unwritable"),
						channel);
				continue;
			}
			ByteBuf retainedSlice = buffer.retainedSlice();
			flow(msg.getClass(), retainedSlice);
			channel.writeAndFlush(retainedSlice).addListener((ChannelFutureListener) arg0 -> {
				Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
			});
			if (i == channels.size() - 1) {
				buffer.release();
			}
		}
	}

	/**
	 * 消息统计
	 * 
	 * @param class1
	 * @param retainedSlice
	 */
	private static void flow(Class<? extends Message> clazz, ByteBuf buf) {
		if (!IS_FLOW) {
			return;
		}
		FLOW_MIN.putIfAbsent(clazz, Integer.MAX_VALUE);
		FLOW_MAX.putIfAbsent(clazz, 0);
		FLOW_COUNT.putIfAbsent(clazz, 0);
		FLOW_SIZE.putIfAbsent(clazz, 0L);
		int readableBytes = buf.readableBytes();
		FLOW_MIN.put(clazz, Math.min(FLOW_MIN.get(clazz), readableBytes));
		FLOW_MAX.put(clazz, Math.max(FLOW_MAX.get(clazz), readableBytes));
		FLOW_COUNT.put(clazz, FLOW_COUNT.get(clazz) + 1);
		FLOW_SIZE.put(clazz, FLOW_SIZE.get(clazz) + readableBytes);
	}

	/**
	 * 生成流量报告
	 * 
	 * @return
	 */
	public static String reportFlow(int top) {
		if (!IS_FLOW) {
			return "no flow to report";
		}
		if (top < 1) {
			return "top error!";
		}
		IRankMap<Class<? extends Message>, FlowMeta> min = new FrequencyReadRankMap<>(COMPARATOR, top);
		IRankMap<Class<? extends Message>, FlowMeta> max = new FrequencyReadRankMap<>(COMPARATOR, top);
		IRankMap<Class<? extends Message>, FlowMeta> count = new FrequencyReadRankMap<>(COMPARATOR, top);
		IRankMap<Class<? extends Message>, FlowMeta> size = new FrequencyReadRankMap<>(COMPARATOR, top);
		for (Entry<Class<? extends Message>, Integer> entry : FLOW_MIN.entrySet()) {
			FlowMeta meta = new FlowMeta();
			meta.setClazz(entry.getKey());
			meta.setValue(entry.getValue());
			min.put(entry.getKey(), meta);
		}
		for (Entry<Class<? extends Message>, Integer> entry : FLOW_MAX.entrySet()) {
			FlowMeta meta = new FlowMeta();
			meta.setClazz(entry.getKey());
			meta.setValue(entry.getValue());
			max.put(entry.getKey(), meta);
		}
		for (Entry<Class<? extends Message>, Integer> entry : FLOW_COUNT.entrySet()) {
			FlowMeta meta = new FlowMeta();
			meta.setClazz(entry.getKey());
			meta.setValue(entry.getValue());
			count.put(entry.getKey(), meta);
		}
		for (Entry<Class<? extends Message>, Long> entry : FLOW_SIZE.entrySet()) {
			FlowMeta meta = new FlowMeta();
			meta.setClazz(entry.getKey());
			meta.setValue(entry.getValue());
			size.put(entry.getKey(), meta);
		}
		List<FlowMeta> minRange = min.getRange(0, top);
		List<FlowMeta> maxRange = max.getRange(0, top);
		List<FlowMeta> countRange = count.getRange(0, top);
		List<FlowMeta> sizeRange = size.getRange(0, top);
		StringBuilder sb = new StringBuilder();
		sb.append("=======min:").append(LineSeparator.DEFAULT.value());
		for (FlowMeta meta : minRange) {
			sb.append(meta.toString()).append(LineSeparator.DEFAULT.value());
		}

		sb.append("=======max:").append(LineSeparator.DEFAULT.value());
		for (FlowMeta meta : maxRange) {
			sb.append(meta.toString()).append(LineSeparator.DEFAULT.value());
		}

		sb.append("=======count:").append(LineSeparator.DEFAULT.value());
		for (FlowMeta meta : countRange) {
			sb.append(meta.toString()).append(LineSeparator.DEFAULT.value());
		}

		sb.append("=======size:").append(LineSeparator.DEFAULT.value());
		for (FlowMeta meta : sizeRange) {
			sb.append(meta.toString()).append(LineSeparator.DEFAULT.value());
		}
		return sb.toString();
	}
}
