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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameMathUtilTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void safeTest() {
        Assert.assertTrue(GameMathUtil.safeAdd(10, 11) == 21);
        Assert.assertTrue(GameMathUtil.safeAdd(Integer.MAX_VALUE, 1) == Integer.MAX_VALUE);
        Assert.assertTrue(GameMathUtil.safeSub(10, 11) == -1);
        Assert.assertTrue(GameMathUtil.safeSub(Integer.MIN_VALUE, 1) == Integer.MIN_VALUE);
        Assert.assertTrue(GameMathUtil.safelyMulti(10, 11) == 110);
        Assert.assertTrue(GameMathUtil.safelyMulti(Integer.MAX_VALUE, 10) == Integer.MAX_VALUE);
    }
}
