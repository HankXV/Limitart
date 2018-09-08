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
package top.limitart.base;

/**
 * 持有器
 *
 * @author hank
 * @version 2017/12/18 0018 19:38
 */
@ThreadUnsafe
public class Holder<T> {
    private T t;

    public static <T> Holder of(@Nullable T t) {
        return empty().set(t);
    }

    public static Holder empty() {
        return new Holder();
    }

    private Holder() {
    }

    public @Nullable
    T get() {
        return this.t;
    }

    public Holder set(@Nullable T t) {
        this.t = t;
        return this;
    }
}
