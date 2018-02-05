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

import java.lang.ref.WeakReference;

/**
 * 弱引用
 *
 * @author hank
 * @version 2018/2/5 0005 21:16
 * @see WeakReference
 */
@ThreadUnsafe
public class WeakRefHolder<T> {
    private WeakReference<T> ref;

    public static <T> WeakRefHolder<T> of(T t) {
        Conditions.notNull(t);
        return new WeakRefHolder(t);
    }

    public static <T> WeakRefHolder<T> empty() {
        return new WeakRefHolder();
    }

    private WeakRefHolder(T t) {
        ref = new WeakReference<>(t);
    }

    private WeakRefHolder() {}

    public @Nullable
    T get() {
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    public WeakRefHolder set(@Nullable T t) {
        T tmp = get();
        if (tmp != null && tmp == t) {
            return this;
        }
        if (ref != null) {
            ref.clear();
            ref = null;
        }
        if (t != null) {
            ref = new WeakReference<>(t);
        }
        return this;
    }
}
