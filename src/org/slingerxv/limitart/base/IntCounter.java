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

import org.slingerxv.limitart.util.GameMathUtil;


/**
 * int计数器
 *
 * @author hank
 * @version 2017/11/2 0002 20:41
 */
@ThreadUnsafe
public class IntCounter {
    private int count;

    public IntCounter() {
        //DO NOTHING 为了正常序列化
    }

    public IntCounter(int initVal) {
        Conditions.args(initVal >= low() && initVal <= high(), "low<=initVal<=high");
        setCount(initVal);
    }

    /**
     * 归零
     */
    public void zero() {
        setCount(0);
    }

    /**
     * 设置为最大值
     */
    public void setHigh() {
        setCount(high());
    }

    /**
     * 设置为最小值
     */
    public void setLow() {
        setCount(low());
    }

    /**
     * 最大限制
     *
     * @return
     */
    protected int high() {
        return Integer.MAX_VALUE;
    }

    /**
     * 最小限制
     *
     * @return
     */
    protected int low() {
        return 0;
    }

    /**
     * 获取当前数值
     *
     * @return
     */
    public int getCount() {
        return this.count;
    }

    /**
     * 直接设置当前值
     *
     * @param value
     * @return
     */
    protected int setCount(int value) {
        return this.count = GameMathUtil.fixedBetween(value, low(), high());
    }

    /**
     * +1并获取
     *
     * @return
     */
    public int incrementAndGet() {
        return addAndGet(1);
    }

    /**
     * -1并获取
     *
     * @return
     */
    public int decrementAndGet() {
        return addAndGet(-1);
    }

    /**
     * 增加并获取
     *
     * @param delta
     * @return
     */
    public int addAndGet(int delta) {
        return setCount(getCount() + delta);
    }

    /**
     * 获取并+1
     *
     * @return
     */
    public int getAndIncrement() {
        return getAndAdd(1);
    }

    /**
     * 获取并-1
     *
     * @return
     */
    public int getAndDecrement() {
        return getAndAdd(-1);
    }

    /**
     * 获取并增加
     *
     * @param delta
     * @return
     */
    public int getAndAdd(int delta) {
        int old = getCount();
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
}
