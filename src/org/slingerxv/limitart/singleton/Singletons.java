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
package org.slingerxv.limitart.singleton;


import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.logging.Logger;
import org.slingerxv.limitart.logging.Loggers;
import org.slingerxv.limitart.util.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 单例容器入口
 *
 * @author Hank
 */
public class Singletons {
    private static Logger log = Loggers.create();
    private Map<Class<?>, Object> instances = new HashMap<>();

    private Singletons() {
    }

    /**
     * 创建实例
     *
     * @return
     */
    public static Singletons create() {
        return new Singletons();
    }


    /**
     * 寻找单例
     *
     * @return
     */
    public Singletons search(ClassLoader classLoader) {
        //寻找所有有@Singleton注解的类
        List<Class<?>> classes = null;
        try {
            classes = Conditions.notNull(ReflectionUtil.getClasses("", classLoader, null));
        } catch (IOException | ClassNotFoundException e) {
            log.error(e);
        }
        List<Class<?>> needRefs = new LinkedList<>();
        for (Class<?> clazz : classes) {
            Singleton annotation = clazz.getAnnotation(Singleton.class);
            if (annotation == null) {
                continue;
            }
            Object singletonInstance = null;
            try {
                singletonInstance = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("can not create instance of {} from an empty constructor", clazz.getName());
            }
            Conditions.notNull(singletonInstance);
            instances.put(clazz, singletonInstance);
            log.trace("find singleton class:{}", clazz.getName());
        }
        for (Object obj : instances.values()) {
            injectByFieldAndMethod(obj);
        }
        return this;
    }

    /**
     * 获取实例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T instance(Class<T> clazz) {
        if (instances.containsKey(clazz)) {
            return (T) instances.get(clazz);
        } else {
            try {
                //TODO 修改为reflectasm 并且检测和支持构造函数和方法注入
                T t = clazz.newInstance();
                log.trace("create instance :{}", t);
                injectByFieldAndMethod(t);
                return t;
            } catch (InstantiationException | IllegalAccessException e) {
                log.error(e);
            }
        }
        return null;
    }

    private void injectByConstructor(Class<?> clazz) {
    }

    private void injectByFieldAndMethod(Object obj) {
        //TODO 修改为reflectasm 并且检测和支持构造函数和方法注入
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Ref annotation = field.getAnnotation(Ref.class);
            if (annotation == null) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Class<?> type = field.getType();
            if (!instances.containsKey(type)) {
                throw new NullPointerException("can not find singleton :" + type.getName());
            }
            try {
                field.set(obj, instances.get(type));
            } catch (IllegalAccessException e) {
                log.error(e);
            }
            log.trace("inject field {} into {}", type.getName(), obj.getClass().getName());
        }
    }
}
