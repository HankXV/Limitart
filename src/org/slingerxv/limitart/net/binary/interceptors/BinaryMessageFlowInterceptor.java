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
package org.slingerxv.limitart.net.binary.interceptors;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slingerxv.limitart.base.Func;
import org.slingerxv.limitart.collections.FrequencyReadRankMap;
import org.slingerxv.limitart.collections.RankMap;
import org.slingerxv.limitart.net.Session;
import org.slingerxv.limitart.net.binary.BinaryMessage;
import org.slingerxv.limitart.net.binary.BinaryServerInterceptor;

import io.netty.buffer.ByteBuf;

/**
 * 服务器发送消息流量统计
 * 
 * @author hank
 *
 */
public class BinaryMessageFlowInterceptor implements BinaryServerInterceptor {
	private static FlowComparator COMPARATOR = new FlowComparator();
	private Map<Class<? extends BinaryMessage>, Integer> FLOW_MIN = new ConcurrentHashMap<>();
	private Map<Class<? extends BinaryMessage>, Integer> FLOW_MAX = new ConcurrentHashMap<>();
	private Map<Class<? extends BinaryMessage>, Long> FLOW_COUNT = new ConcurrentHashMap<>();
	private Map<Class<? extends BinaryMessage>, Long> FLOW_SIZE = new ConcurrentHashMap<>();

	/**
	 * 消息统计
	 * 
	 * @param clazz
	 * @param buf
	 */
	private void flow(Class<? extends BinaryMessage> clazz, ByteBuf buf) {
		FLOW_MIN.putIfAbsent(clazz, Integer.MAX_VALUE);
		FLOW_MAX.putIfAbsent(clazz, 0);
		FLOW_COUNT.putIfAbsent(clazz, 0L);
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
	public String reportFlow(int top) {
		if (top < 1) {
			return "top error!";
		}
		RankMap<Class<? extends BinaryMessage>, FlowMeta> min = new FrequencyReadRankMap<>(COMPARATOR, top);
		RankMap<Class<? extends BinaryMessage>, FlowMeta> max = new FrequencyReadRankMap<>(COMPARATOR, top);
		RankMap<Class<? extends BinaryMessage>, FlowMeta> count = new FrequencyReadRankMap<>(COMPARATOR, top);
		RankMap<Class<? extends BinaryMessage>, FlowMeta> size = new FrequencyReadRankMap<>(COMPARATOR, top);
		for (Entry<Class<? extends BinaryMessage>, Integer> entry : FLOW_MIN.entrySet()) {
			FlowMeta meta = new FlowMeta();
			meta.clazz = entry.getKey();
			meta.value = entry.getValue();
			min.put(entry.getKey(), meta);
		}
		for (Entry<Class<? extends BinaryMessage>, Integer> entry : FLOW_MAX.entrySet()) {
			FlowMeta meta = new FlowMeta();
			meta.clazz = entry.getKey();
			meta.value = entry.getValue();
			max.put(entry.getKey(), meta);
		}
		for (Entry<Class<? extends BinaryMessage>, Long> entry : FLOW_COUNT.entrySet()) {
			FlowMeta meta = new FlowMeta();
			meta.clazz = entry.getKey();
			meta.value = entry.getValue();
			count.put(entry.getKey(), meta);
		}
		for (Entry<Class<? extends BinaryMessage>, Long> entry : FLOW_SIZE.entrySet()) {
			FlowMeta meta = new FlowMeta();
			meta.clazz = entry.getKey();
			meta.value = entry.getValue();
			size.put(entry.getKey(), meta);
		}
		List<FlowMeta> minRange = min.getRange(0, top);
		List<FlowMeta> maxRange = max.getRange(0, top);
		List<FlowMeta> countRange = count.getRange(0, top);
		List<FlowMeta> sizeRange = size.getRange(0, top);
		StringBuilder sb = new StringBuilder();
		sb.append("=======min:").append("\r\n");
		for (FlowMeta meta : minRange) {
			sb.append(meta.toString()).append("\r\n");
		}

		sb.append("=======max:").append("\r\n");
		for (FlowMeta meta : maxRange) {
			sb.append(meta.toString()).append("\r\n");
		}

		sb.append("=======count:").append("\r\n");
		for (FlowMeta meta : countRange) {
			sb.append(meta.toString()).append("\r\n");
		}

		sb.append("=======size:").append("\r\n");
		for (FlowMeta meta : sizeRange) {
			sb.append(meta.toString()).append("\r\n");
		}
		return sb.toString();
	}

	@Override
	public boolean onConnected(Session session) {
		return false;
	}

	@Override
	public boolean onMessageIn(Session session, BinaryMessage msg) {
		return false;
	}

	@Override
	public void onMessageOut(Session session, BinaryMessage msg) {
		flow(msg.getClass(), msg.buffer());
	}

	private static class FlowMeta implements Func<Class<? extends BinaryMessage>> {
		private Class<? extends BinaryMessage> clazz;
		private long value;

		@Override
		public Class<? extends BinaryMessage> run() {
			return clazz;
		}

		@Override
		public String toString() {
			return "FlowMeta [clazz=" + clazz + ", value=" + value + "]";
		}
	}

	private static class FlowComparator implements Comparator<FlowMeta>,Serializable {

		@Override
		public int compare(FlowMeta o1, FlowMeta o2) {
			if (o1.value > o2.value) {
				return -1;
			} else if (o1.value < o2.value) {
				return 1;
			}
			return 0;
		}

	}
}
