package com.hankcs.hanlp.corpus.dictionary.item;


import junit.framework.TestCase;


public class ItemTest extends TestCase {
    public void testCreate() throws Exception {
        TestCase.assertEquals("?? v 7685 vn 616", Item.create("?? v 7685 vn 616").toString());
    }

    public void testCombine() throws Exception {
        SimpleItem itemA = SimpleItem.create("A 1 B 2");
        SimpleItem itemB = SimpleItem.create("B 1 C 2 D 3");
        itemA.combine(itemB);
        TestCase.assertEquals("B 3 D 3 C 2 A 1 ", itemA.toString());
    }
}

