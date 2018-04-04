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
 * 可变的一对
 *
 * @author hank
 */
@ThreadUnsafe
public class MutableCouple<H, W> implements Couple<H, W> {
    private H h;
    private W w;


    public MutableCouple(@Nullable H h, @Nullable W w) {
        this.h = h;
        this.w = w;
    }

    public MutableCouple() {
    }

    @Override
    public @Nullable
    H get1() {
        return this.h;
    }

    @Override
    public @Nullable
    W get2() {
        return this.w;
    }


    public MutableCouple set1(@Nullable H h) {
        this.h = h;
        return this;
    }

    public MutableCouple set2(@Nullable W w) {
        this.w = w;
        return this;
    }
}
