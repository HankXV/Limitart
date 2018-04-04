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
 * 不可变的三个
 *
 * @author hank
 */
@ThreadUnsafe
public class ImmutableTriple<A, B, C> implements Triple<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    public ImmutableTriple(@Nullable A a, @Nullable B b, @Nullable C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public @Nullable
    A getA() {
        return a;
    }

    @Override
    public @Nullable
    B getB() {
        return b;
    }

    @Override
    public @Nullable
    C getC() {
        return c;
    }
}
