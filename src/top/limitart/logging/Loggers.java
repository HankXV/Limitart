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
package top.limitart.logging;


import top.limitart.logging.impl.Slf4JLoggers;

@Deprecated
public abstract class Loggers {
    private volatile static Loggers DEFAULT;


    public synchronized static void setDefault(Loggers factory) {
        DEFAULT = factory;
    }

    public static Logger create() {
        return create(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public static Logger create(Class<?> clazz) {
        return create(clazz.getName());
    }

    public static Logger create(String name) {
        if (DEFAULT == null) {
            DEFAULT = new Slf4JLoggers();
        }
        return DEFAULT.instance(name);
    }

    protected abstract Logger instance(String name);
}
