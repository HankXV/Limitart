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
package org.slingerxv.limitart.json;

import org.slingerxv.limitart.json.impl.Jackson;

import java.util.List;

/**
 * JSON接口定义
 *
 * @author hank
 * @version 2018/3/6 0006 20:35
 */
public abstract class JSON {
    private static JSON DEFAULT = new Jackson();

    /**
     * 获取默认实例(Jackson)
     *
     * @return
     */
    public static JSON getDefault() {
        return DEFAULT;
    }

    /**
     * 将对象转化为JSON格式
     *
     * @param object
     * @return
     */
    public abstract String toStr(Object object) throws JSONException;

    /**
     * 将JSON转化为对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public abstract <T> T toObj(String json, Class<T> clazz) throws JSONException;

    /**
     * 将对象转化为列表
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public abstract <T> List<T> toList(String json, Class<T> clazz) throws JSONException;
}
