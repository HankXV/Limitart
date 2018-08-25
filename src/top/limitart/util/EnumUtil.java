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
package top.limitart.util;

import top.limitart.base.Nullable;

/**
 * 枚举工具
 *
 * @author hank
 * @version 2018/2/12 0012 20:22
 */
public final class EnumUtil {
    /**
     * 获取序号对应的枚举类型
     *
     * @param c
     * @param ordinal
     * @param <E>
     * @return
     */
    @Nullable
    public static <E extends Enum<E>> E byOrdinal(Class<E> c, int ordinal) {
        E[] enumConstants = c.getEnumConstants();
        return (ordinal > (enumConstants.length - 1) || ordinal < 0) ? null : enumConstants[ordinal];
    }

    /**
     * 获取枚举个数
     *
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> int length(Class<E> c) {
        return c.getEnumConstants().length;
    }
}
