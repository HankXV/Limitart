package org.slingerxv.limitart.collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EnumListTest {
    private EnumList<EnumListElement, Integer> list;

    @Before
    public void setUp() {
        list = EnumList.create(EnumListElement.class);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test() {
        Assert.assertNull(list.get(EnumListElement.A));
        Assert.assertNull(list.put(EnumListElement.A, 1));
        Assert.assertTrue(1 == list.put(EnumListElement.A, 2));
    }

}
