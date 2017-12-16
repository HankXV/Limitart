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
package org.slingerxv.limitart.logging;


public interface Logger {

    String name();

    boolean isTraceEnabled();

    void trace(String msg);

    void trace(String format, Object... args);

    void trace(String msg, Throwable t);

    void trace(Throwable t);

    boolean isDebugEnabled();

    void debug(String msg);

    void debug(String format, Object... args);

    void debug(String msg, Throwable t);

    void debug(Throwable t);

    boolean isInfoEnabled();

    void info(String msg);

    void info(String format, Object... args);

    void info(String msg, Throwable t);

    void info(Throwable t);

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String format, Object... args);

    void warn(String msg, Throwable t);

    void warn(Throwable t);

    boolean isErrorEnabled();

    void error(String msg);

    void error(String format, Object... args);

    void error(String msg, Throwable t);

    void error(Throwable t);

    boolean isEnabled(LogLevel level);

    void log(LogLevel level, String msg);

    void log(LogLevel level, String format, Object... args);

    void log(LogLevel level, String msg, Throwable t);

    void log(LogLevel level, Throwable t);
}
