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

public abstract class AbstractLogger implements Logger {

    private static final String EXCEPTION_STR = "exception:";

    private final String name;

    protected AbstractLogger(String name) {
        this.name = name;
    }

    protected AbstractLogger(Class<?> clazz) {
        this(clazz.getSimpleName());
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        switch (level) {
            case TRACE:
                return isTraceEnabled();
            case DEBUG:
                return isDebugEnabled();
            case INFO:
                return isInfoEnabled();
            case WARN:
                return isWarnEnabled();
            case ERROR:
                return isErrorEnabled();
            default:
                throw new Error();
        }
    }

    @Override
    public void trace(Throwable t) {
        trace(EXCEPTION_STR, t);
    }

    @Override
    public void debug(Throwable t) {
        debug(EXCEPTION_STR, t);
    }

    @Override
    public void info(Throwable t) {
        info(EXCEPTION_STR, t);
    }

    @Override
    public void warn(Throwable t) {
        warn(EXCEPTION_STR, t);
    }

    @Override
    public void error(Throwable t) {
        error(EXCEPTION_STR, t);
    }

    @Override
    public void log(LogLevel level, String msg, Throwable t) {
        log0(level, t, msg);
    }

    @Override
    public void log(LogLevel level, Throwable t) {
        log0(level, t, null);
    }

    @Override
    public void log(LogLevel level, Object msg) {
        log0(level, null, msg);
    }


    @Override
    public void log(LogLevel level, String format, Object... args) {
        log0(level, null, format, args);
    }

    private void log0(LogLevel level, Throwable t, Object msg, Object... args) {
        switch (level) {
            case TRACE:
                if (t != null) {
                    if (msg == null) {
                        trace(t);
                    } else {
                        trace(msg.toString(), t);
                    }
                } else {
                    trace(msg.toString(), args);
                }
                break;
            case DEBUG:
                if (t != null) {
                    if (msg == null) {
                        debug(t);
                    } else {
                        debug(msg.toString(), t);
                    }
                } else {
                    debug(msg.toString(), args);
                }
                break;
            case INFO:
                if (t != null) {
                    if (msg == null) {
                        info(t);
                    } else {
                        info(msg.toString(), t);
                    }
                } else {
                    info(msg.toString(), args);
                }
                break;
            case WARN:
                if (t != null) {
                    if (msg == null) {
                        warn(t);
                    } else {
                        warn(msg.toString(), t);
                    }
                } else {
                    warn(msg.toString(), args);
                }
                break;
            case ERROR:
                if (t != null) {
                    if (msg == null) {
                        error(t);
                    } else {
                        error(msg.toString(), t);
                    }
                } else {
                    error(msg.toString(), args);
                }
                break;
            default:
                throw new Error();
        }
    }
}
