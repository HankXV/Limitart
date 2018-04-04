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
package org.slingerxv.limitart.base;


import java.util.Comparator;

/**
 * 比较链
 *
 * @author hank
 * @version 2018/4/4 0004 15:35
 * @see Comparator
 * @see Comparable
 */
public final class CompareChain {
    private int result;

    public static CompareChain empty() {
        return new CompareChain();
    }

    public static <T> Comparator<T> build(Func2<T, T, CompareChain> func) {
        return (o1, o2) -> {
            CompareChain compareChain = func.run(o1, o2);
            return compareChain.compare();
        };
    }

    public static CompareChain start(byte a, byte b) {
        return empty().then(a, b);
    }

    public static CompareChain start(char a, char b) {
        return empty().then(a, b);
    }

    public static CompareChain start(short a, short b) {
        return empty().then(a, b);
    }

    public static CompareChain start(int a, int b) {
        return empty().then(a, b);
    }

    public static CompareChain start(long a, long b) {
        return empty().then(a, b);
    }

    public static CompareChain start(float a, float b) {
        return empty().then(a, b);
    }

    public static CompareChain start(double a, double b) {
        return empty().then(a, b);
    }

    public static CompareChain start(boolean a, boolean b) {
        return empty().then(a, b);
    }

    public static <T> CompareChain start(Comparator<T> comparator, T a, T b) {
        return empty().then(comparator, a, b);
    }

    public static <T> CompareChain start(Comparable<T> comparable, T another) {
        return empty().then(comparable, another);
    }

    private CompareChain() {
    }

    public CompareChain then(byte a, byte b) {
        if (result == 0) {
            this.result = Byte.compare(a, b);
        }
        return this;
    }

    public CompareChain then(char a, char b) {
        if (result == 0) {
            this.result = Character.compare(a, b);
        }
        return this;
    }

    public CompareChain then(short a, short b) {
        if (result == 0) {
            this.result = Short.compare(a, b);
        }
        return this;
    }

    public CompareChain then(int a, int b) {
        if (result == 0) {
            this.result = Integer.compare(a, b);
        }
        return this;
    }

    public CompareChain then(long a, long b) {
        if (result == 0) {
            this.result = Long.compare(a, b);
        }
        return this;
    }

    public CompareChain then(float a, float b) {
        if (result == 0) {
            this.result = Float.compare(a, b);
        }
        return this;
    }

    public CompareChain then(double a, double b) {
        if (result == 0) {
            this.result = Double.compare(a, b);
        }
        return this;
    }

    public CompareChain then(boolean a, boolean b) {
        if (result == 0) {
            this.result = Boolean.compare(a, b);
        }
        return this;
    }

    public <T> CompareChain then(Comparator<T> comparator, T a, T b) {
        if (result == 0) {
            this.result = comparator.compare(a, b);
        }
        return this;
    }

    public <T> CompareChain then(Comparable<T> comparable, T another) {
        if (result == 0) {
            this.result = comparable.compareTo(another);
        }
        return this;
    }

    public int compare() {
        return this.result;
    }
}
