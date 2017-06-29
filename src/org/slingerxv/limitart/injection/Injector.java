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
package org.slingerxv.limitart.injection;

/**
 * 注射器
 *
 * @author Hank
 * @version 2017/11/11 21:37
 */
public class Injector {
    private Injector() {
    }

    public static Injector create() {
        return new Injector();
    }

    public <T> Register<T> reg(Class<T> clazz) {
        return null;
    }

    public <T> T get(Class<?> mod) {
        return null;
    }

    public <T> T get(Class<?> mod, Class<?> as) {
        return null;
    }
}
