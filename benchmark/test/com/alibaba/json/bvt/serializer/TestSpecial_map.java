package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestSpecial_map extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals("{\"name\":\"\\u0000\"}", JSON.toJSONString(Collections.singletonMap("name", "\u0000")));
    }

    public void test_1() throws Exception {
        Assert.assertEquals("{\"name\":\"\\u0001\"}", JSON.toJSONString(Collections.singletonMap("name", "\u0001")));
    }

    public void test_2() throws Exception {
        Assert.assertEquals("{\"name\":\"\\u0002\"}", JSON.toJSONString(Collections.singletonMap("name", "\u0002")));
    }

    public void test_3() throws Exception {
        Assert.assertEquals("{\"name\":\"\\u0003\"}", JSON.toJSONString(Collections.singletonMap("name", "\u0003")));
    }

    public void test_4() throws Exception {
        Assert.assertEquals("{\"name\":\"\\u0004\"}", JSON.toJSONString(Collections.singletonMap("name", "\u0004")));
    }

    public void test_5() throws Exception {
        Assert.assertEquals("{\"name\":\"\\u0005\"}", JSON.toJSONString(Collections.singletonMap("name", "\u0005")));
    }

    public void test_6() throws Exception {
        Assert.assertEquals("{\"name\":\"\\u0006\"}", JSON.toJSONString(Collections.singletonMap("name", "\u0006")));
    }

    public void test_7() throws Exception {
        Assert.assertEquals("{\"name\":\"\\u0007\"}", JSON.toJSONString(Collections.singletonMap("name", "\u0007")));
    }

    public void test_8() throws Exception {
        Assert.assertEquals("{\"name\":\"\\b\"}", JSON.toJSONString(Collections.singletonMap("name", "\b")));
    }

    public void test_9() throws Exception {
        Assert.assertEquals("{\"name\":\"\\t\"}", JSON.toJSONString(Collections.singletonMap("name", "\t")));
    }

    public void test_10() throws Exception {
        Assert.assertEquals("{\"name\":\"\\n\"}", JSON.toJSONString(Collections.singletonMap("name", "\n")));
    }
}

