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
package org.slingerxv.limitart.game.bag;

/**
 * 物品
 *
 * @author hank
 */
public abstract class BaseBagItem implements Comparable<BaseBagItem> {
    // 堆叠数量
    private int num;

    /**
     * 堆叠数量
     *
     * @return
     */
    public int getNum() {
        return num;
    }

    /**
     * 堆叠数量
     *
     * @param num
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * 最大堆叠数量
     *
     * @return
     */
    public abstract int getMaxStackNumber();

    /**
     * 是否为同种物品
     *
     * @param another
     * @return
     */
    public abstract boolean isSameType(BaseBagItem another);

    /**
     * 拷贝物品
     *
     * @return
     */
    public abstract BaseBagItem copy();
}
