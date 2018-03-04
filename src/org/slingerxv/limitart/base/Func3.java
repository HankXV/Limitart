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
package org.slingerxv.limitart.base;

/**
 * 三参返回接口
 *
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <R>
 * @author hank
 */
@FunctionalInterface
public interface Func3<T1, T2, T3, R> {
    R run(T1 t1, T2 t2, T3 t3);
}
