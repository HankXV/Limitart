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


public class Procs {
    public static void invoke(@Nullable Proc proc) {
        if (proc != null)
            proc.run();
    }

    public static <T1> void invoke(@Nullable Proc1<T1> proc, T1 t1) {
        if (proc != null)
            proc.run(t1);
    }

    public static <T1, T2> void invoke(@Nullable Proc2<T1, T2> proc, T1 t1, T2 t2) {
        if (proc != null)
            proc.run(t1, t2);
    }

    public static <T1, T2, T3> void invoke(@Nullable Proc3<T1, T2, T3> proc, T1 t1, T2 t2, T3 t3) {
        if (proc != null)
            proc.run(t1, t2, t3);
    }

    public static <T1, T2, T3, T4> void invoke(@Nullable Proc4<T1, T2, T3, T4> proc, T1 t1, T2 t2, T3 t3, T4 t4) {
        if (proc != null)
            proc.run(t1, t2, t3, t4);
    }

    public static <T1, T2, T3, T4, T5> void invoke(@Nullable Proc5<T1, T2, T3, T4, T5> proc, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        if (proc != null)
            proc.run(t1, t2, t3, t4, t5);
    }
}
