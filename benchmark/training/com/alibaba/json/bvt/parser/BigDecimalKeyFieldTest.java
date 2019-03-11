package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class BigDecimalKeyFieldTest extends TestCase {
    public void test_for_bigdecimal_key() throws Exception {
        Map<?, ?> obj = JSON.parseObject("{1234.56:\"abc\"}", HashMap.class);
        Map.Entry<?, ?> entry = obj.entrySet().iterator().next();
        Assert.assertEquals("abc", entry.getValue());
        Assert.assertEquals(new BigDecimal("1234.56"), entry.getKey());
    }
}

