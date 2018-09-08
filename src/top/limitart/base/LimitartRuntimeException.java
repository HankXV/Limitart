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

/**
 * Limitart运行时异常
 *
 * @author hank
 */
public class LimitartRuntimeException extends RuntimeException {

    public LimitartRuntimeException() {
        super();
    }

    public LimitartRuntimeException(String template, Object... params) {
        super(String.format(template, params));
    }

    public LimitartRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public LimitartRuntimeException(String info, Throwable throwable) {
        super(info, throwable);
    }
}
