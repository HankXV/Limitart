/*
 *
 *  * Copyright (c) 2016-present The Limitart Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.limitart.concurrent;

import top.limitart.base.NotNull;
import top.limitart.base.Nullable;
import top.limitart.base.Proc;
import top.limitart.base.Proc1;


/**
 * 资源占有者 Actor模型
 * 弱引用的持有某个对象资源
 * 这种角色同时只能强制性的占用一个资源或不占有
 * 可以把{@code Actor}比作棋盘上的棋子，{@code Place}比作格子
 * @param <T> 资源实体
 * @param <R> 资源区域类型
 * @author hank
 * @
 */
public interface Actor<T, R extends Actor.Place<T>> {
    /**
     * 离开
     *
     * @param r 资源类型
     * @return
     */
    void leave(@NotNull R r);

    /**
     * 占有
     *
     * @param r 新资源
     * @return
     */
    void join(@NotNull R r, Proc onSuccess, Proc1<Exception> onFail);

    /**
     * 当前占用者在哪个资源域上(获取当前占有的资源)
     *
     * @return 资源实体
     */
    @Nullable
    R where();

    /**
     * 资源区域
     * 用于资源占有者获取资源的场地
     *
     * @param <T> 所拥有的资源类型
     * @author hank
     */
    interface Place<T> {

        /**
         * 获取资源实体
         *
         * @return
         */
        @NotNull
        T res();
    }
}
