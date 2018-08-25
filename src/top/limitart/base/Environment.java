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
package top.limitart.base;

import java.util.Properties;

/**
 * 环境变量
 *
 * @author hank
 * @version 2018/8/25 0025 20:16
 * @see java.util.Properties
 */
public class Environment extends Properties {
    private static final String DEFAULT_ZERO_STR = "0";

    public int getInt(String key) {
        return Integer.parseInt(getProperty(key, DEFAULT_ZERO_STR));
    }

    public String getString(String key) {
        return getProperty(key, "");
    }

    public long getLong(String key) {
        return Long.parseLong(getProperty(key, DEFAULT_ZERO_STR));
    }

}
