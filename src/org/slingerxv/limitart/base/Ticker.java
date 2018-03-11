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

import java.util.concurrent.TimeUnit;

/**
 * 时间触发器(自己决定触发频率)
 *
 * @author hank
 * @version 2018/3/8 0008 21:42
 */
@ThreadUnsafe
public class Ticker {
    private final long interval;
    private final boolean strict;
    private long deadline;

    public Ticker(int interval, TimeUnit unit) {
        this(interval, unit, false);
    }

    /**
     * @param interval 间隔时间
     * @param strict   严格模式(假设时间差远远大于执行间隔,不会跳过任何一次执行)
     */
    public Ticker(int interval, TimeUnit unit, boolean strict) {
        this.interval = unit.toMillis(interval);
        this.strict = strict;
    }

    public boolean tick(long now) {
        if (now < deadline) {
            return false;
        }
        if (strict && deadline > 0) {
            deadline += interval;
        } else {
            deadline = now + interval;
        }
        return true;
    }

    public long getInterval() {
        return interval;
    }

    public boolean isStrict() {
        return strict;
    }
}
