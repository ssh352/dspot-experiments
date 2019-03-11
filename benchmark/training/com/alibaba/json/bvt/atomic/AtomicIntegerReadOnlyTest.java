package com.alibaba.json.bvt.atomic;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class AtomicIntegerReadOnlyTest extends TestCase {
    public void test_codec_null() throws Exception {
        AtomicIntegerReadOnlyTest.V0 v = new AtomicIntegerReadOnlyTest.V0(123);
        String text = JSON.toJSONString(v);
        Assert.assertEquals("{\"value\":123}", text);
        AtomicIntegerReadOnlyTest.V0 v1 = JSON.parseObject(text, AtomicIntegerReadOnlyTest.V0.class);
        Assert.assertEquals(v1.getValue().intValue(), v.getValue().intValue());
    }

    public static class V0 {
        private final AtomicInteger value;

        public V0() {
            this(0);
        }

        public V0(int value) {
            this.value = new AtomicInteger(value);
        }

        public AtomicInteger getValue() {
            return value;
        }
    }
}

