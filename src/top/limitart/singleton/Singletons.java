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
package top.limitart.singleton;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.Conditions;
import top.limitart.base.Proc1;
import top.limitart.util.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例容器入口
 *
 * @author Hank
 */
public class Singletons {
    private static final Logger LOGGER = LoggerFactory.getLogger(Singletons.class);
    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

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
        List<Class<?>> classes;
        try {
            classes = Conditions.notNull(ReflectionUtil.getClasses("", classLoader, null));
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("reflect error", e);
            return this;
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
                LOGGER.error("can not create instance of {} from an empty constructor", clazz.getName());
            }
            Conditions.notNull(singletonInstance);
            instances.put(clazz, singletonInstance);
            LOGGER.trace("find singleton class:{}", clazz.getName());
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
                T t = clazz.newInstance();
                LOGGER.trace("create instance :{}", t);
                injectByFieldAndMethod(t);
                return t;
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("instance error", e);
            }
        }
        return null;
    }

    private void injectByConstructor(Class<?> clazz) {
    }

    private void injectByFieldAndMethod(Object obj) {
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
                LOGGER.error("reflect error", e);
            }
            LOGGER.trace("inject field {} into {}", type.getName(), obj.getClass().getName());
        }
    }

    public void forEach(Proc1<Object> proc) {
        instances.values().forEach(a -> proc.run(a));
    }
}
