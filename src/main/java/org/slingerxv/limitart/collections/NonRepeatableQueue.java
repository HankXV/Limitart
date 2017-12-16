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
package org.slingerxv.limitart.collections;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 不重复放入队列
 * 
 * @author hank
 *
 */
public class NonRepeatableQueue<V> {
	private Set<V> waitUpdateSet = new ConcurrentHashSet<>();
	private Queue<V> waitUpdateQueue = new ConcurrentLinkedQueue<>();

	public int size() {
		return waitUpdateSet.size();
	}

	public synchronized void offer(V player) {
		if (!waitUpdateSet.contains(player)) {
			waitUpdateSet.add(player);
			waitUpdateQueue.offer(player);
		}
	}

	public synchronized V poll() {
		V poll = waitUpdateQueue.poll();
		if (poll != null) {
			waitUpdateSet.remove(poll);
		}
		return poll;
	}
}
