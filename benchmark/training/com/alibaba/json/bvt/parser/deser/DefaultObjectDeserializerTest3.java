package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.Assert;


@SuppressWarnings("rawtypes")
public class DefaultObjectDeserializerTest3 extends TestCase {
    public void test_0() throws Exception {
        HashMap o = ((HashMap) (JSON.parse("{\"@type\":\"java.lang.Cloneable\"}")));
        Assert.assertEquals(0, o.size());
    }
}

