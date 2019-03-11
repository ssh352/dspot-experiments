package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ShortArraySerializerTest extends TestCase {
    public void test_0() {
        Assert.assertEquals("[]", JSON.toJSONString(new short[0]));
        Assert.assertEquals("[1,2]", JSON.toJSONString(new short[]{ 1, 2 }));
        Assert.assertEquals("[1,2,3]", JSON.toJSONString(new short[]{ 1, 2, 3 }));
    }
}

