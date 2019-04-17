package com.hankcs.hanlp.mining.word;


import junit.framework.TestCase;


public class TfIdfCounterTest extends TestCase {
    public void testGetKeywords() throws Exception {
        TfIdfCounter counter = new TfIdfCounter();
        counter.add("??????", "?????????");
        counter.add("???????", "?????????????");
        counter.add("????", "?????????????????????????????");
        counter.compute();
        for (Object id : counter.documents()) {
            System.out.println(((id + " : ") + (counter.getKeywordsOf(id, 3))));
        }
        System.out.println(counter.getKeywords("???????", 2));
    }
}
