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
package org.slingerxv.limitart.util;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.collections.ConcurrentHashSet;

public class UniqueIDUtilTest {
    private Set<Long> sets;
    private LongAdder adder;
    private CountDownLatch count;
    private static int THREAD_COUNT = 100;

    @Before
    public void setUp() {
        adder = new LongAdder();
        sets = new ConcurrentHashSet<>();
        count = new CountDownLatch(THREAD_COUNT);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createUUIDLong() {
        for (int i = 0; i < THREAD_COUNT; ++i) {
            new Thread(this::insert).start();
        }
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private void insert() {
        for (int i = 0; i < 1000000; ++i) {
            long createUUID = UniqueIDUtil.nextID(24, adder);
            if (sets.contains(createUUID)) {
                Assert.fail();
            }
        }
        // System.out.println(Thread.currentThread().getName() + " ok!");
        count.countDown();
    }
}
