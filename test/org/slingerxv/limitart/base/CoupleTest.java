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
public class CoupleTest {
    private Couple<Integer, String> couple;

    @Before
    public void setUp() {
        couple = Couple.ofImmutable(10, "wife");
    }

    @Test
    public void proc() {
        Assert.assertTrue(couple.getHusband() == 10);
        Assert.assertTrue(couple.getWife().equals("wife"));
    }
}
