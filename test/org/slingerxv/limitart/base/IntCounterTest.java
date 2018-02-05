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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hank
 * @version 2017/11/2 0002 20:43
 */
public class IntCounterTest {
    private IntCounter counter;

    @Before
    public void setUp() {
        counter =
                new IntCounter() {
                    @Override
                    public int high() {
                        return 10;
                    }

                    @Override
                    public int low() {
                        return 0;
                    }
                };
    }

    @Test
    public void proc() {
        // 0
        Assert.assertEquals(counter.getCount(), 0);
        // 0
        Assert.assertEquals(counter.decrementAndGet(), 0);
        // 1
        Assert.assertEquals(counter.incrementAndGet(), 1);
        // 3
        Assert.assertEquals(counter.addAndGet(2), 3);
        // 5
        Assert.assertEquals(counter.getAndAdd(2), 3);
        // 4
        Assert.assertEquals(counter.getAndDecrement(), 5);
        // 5
        Assert.assertEquals(counter.getAndIncrement(), 4);
        // 10
        Assert.assertEquals(counter.addAndGet(1000), 10);
    }
}
