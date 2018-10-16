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
package top.limitart.dat;


import top.limitart.base.Conditions;
import top.limitart.base.NotNull;
import top.limitart.base.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 数据容器(相当于Excel的一张Sheet)
 *
 * @author hank
 * @version 2018/10/16 0016 14:51
 */
public class DataContainer<T extends DataMeta> {
    private List<T> list = new ArrayList<>();
    private Map<Object, T> map = new HashMap<>();

    public void putIfAbsent(Object p,@NotNull T v) throws Exception {
        Conditions.args(!map.containsKey(p), "{},primary key duplicated：%s", v.getClass(), p);
        list.add(v);
        map.put(p, v);
    }

    public void forEach(Consumer<T> consumer) {
        this.list.forEach(consumer);
    }

    public List<T> copyList() {
        return new ArrayList<>(list);
    }

    public @Nullable
    T get(@NotNull Object primary) {
        return map.get(primary);
    }
}