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
package org.slingerxv.limitart.taskqueuegroup.struct;

import org.slingerxv.limitart.collections.ConcurrentHashSet;
import org.slingerxv.limitart.taskqueue.define.ITaskQueue;

/**
 * 线程容器
 * 
 * @author Hank
 *
 */
public class AutoGrowthSegment<T> {
	private int threadIndex;
	private ITaskQueue<T> thread;
	private ConcurrentHashSet<AutoGrowthEntity> entities = new ConcurrentHashSet<>();

	public ConcurrentHashSet<AutoGrowthEntity> getEntities() {
		return entities;
	}

	public void setEntities(ConcurrentHashSet<AutoGrowthEntity> entities) {
		this.entities = entities;
	}

	public int getThreadIndex() {
		return threadIndex;
	}

	public void setThreadIndex(int threadIndex) {
		this.threadIndex = threadIndex;
	}

	public ITaskQueue<T> getThread() {
		return thread;
	}

	public void setThread(ITaskQueue<T> thread) {
		this.thread = thread;
	}

}
