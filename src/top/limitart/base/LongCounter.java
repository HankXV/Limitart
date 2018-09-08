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


import top.limitart.util.GameMathUtil;

/**
 * 计数器
 *
 * @author hank
 * @version 2017/10/31 0031 10:35
 */
@ThreadUnsafe
public class LongCounter {
    private long count;

    public LongCounter() {
        // DO NOTHING
    }

    public LongCounter(long initVal) {
        Conditions.args(initVal >= low() && initVal <= high(), "low<=initVal<=high");
        setCount(initVal);
    }

    public void zero() {
        setCount(0);
    }

    public void setHigh() {
        setCount(high());
    }

    public void setLow() {
        setCount(low());
    }

    /**
     * 最大限制
     *
     * @return
     */
    protected long high() {
        return Long.MAX_VALUE;
    }

    /**
     * 最小限制
     *
     * @return
     */
    protected long low() {
        return 0L;
    }

    /**
     * 获取当前数值
     *
     * @return
     */
    public long getCount() {
        return this.count;
    }

    /**
     * 直接设置当前值
     *
     * @param value
     * @return
     */
    public long setCount(long value) {
        return this.count = GameMathUtil.fixedBetween(value, low(), high());
    }

    /**
     * +1并获取
     *
     * @return
     */
    public long incrementAndGet() {
        return addAndGet(1L);
    }

    /**
     * -1并获取
     *
     * @return
     */
    public long decrementAndGet() {
        return addAndGet(-1L);
    }

    /**
     * 增加并获取
     *
     * @param delta
     * @return
     */
    public long addAndGet(long delta) {
        return setCount(GameMathUtil.safeAdd(getCount(), delta));
    }

    /**
     * 获取并+1
     *
     * @return
     */
    public long getAndIncrement() {
        return getAndAdd(1L);
    }

    /**
     * 获取并-1
     *
     * @return
     */
    public long getAndDecrement() {
        return getAndAdd(-1L);
    }

    /**
     * 获取并增加
     *
     * @param delta
     * @return
     */
    public long getAndAdd(long delta) {
        long old = getCount();
        setCount(GameMathUtil.safeAdd(old, delta));
        return old;
    }

    /**
     * 是否到达下边界
     *
     * @return
     */
    public boolean reachLow() {
        return count == low();
    }

    /**
     * 是否到达上边界
     *
     * @return
     */
    public boolean reachHigh() {
        return count == high();
    }

    @Override
    public String toString() {
        return count + "";
    }
}
