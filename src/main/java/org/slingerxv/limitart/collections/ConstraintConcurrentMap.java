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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 并发String约束性Map
 * 
 * @author hank
 *
 * @param <K>
 */
public class ConstraintConcurrentMap<K> extends ConstraintHashMap<K> {
	/**
	 * 构造函数
	 */
	protected ConstraintConcurrentMap() {
		super(new ConcurrentHashMap<>());
	}

	/**
	 * 构造一个空的对象
	 * 
	 * @return
	 */
	public static <K> ConstraintConcurrentMap<K> empty() {
		return new ConstraintConcurrentMap<K>();
	}

	/**
	 * 通过键值对数组构造
	 * 
	 * @param kvs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K> ConstraintConcurrentMap<K> just(Object... kvs) {
		Objects.requireNonNull(kvs, "kvs");
		ConstraintConcurrentMap<K> empty = ConstraintConcurrentMap.empty();
		for (int i = 0; i < kvs.length; i += 2) {
			Objects.requireNonNull(kvs[i], "key");
			Objects.requireNonNull(kvs[i + 1], "value");
			empty.putObj((K) kvs[i], kvs[i + 1]);
		}
		return empty;
	}

	/**
	 * 从一个Map构造出此对象
	 * 
	 * @param map
	 * @return
	 */
	public static <K> ConstraintConcurrentMap<K> from(Map<? extends K, ? extends Object> map) {
		ConstraintConcurrentMap<K> empty = ConstraintConcurrentMap.empty();
		empty.putAll(map);
		return empty;
	}
}
