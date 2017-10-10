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

/**
 * @author hank
 *
 */
public interface LongValueMap<K> extends Map<K, Long> {
	public long getValue(K key);

	public long setValue(K key, long value);

	public long getAndIncrement(K key);

	public long incrementAndGet(K key);

	public long getAndDecrement(K key);

	public long decrementAndGet(K key);

	public long getAndAdd(K key, long value);

	public long addAndGet(K key, long value);

	public long sum();
}
