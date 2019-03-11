package com.alibaba.json.bvt.parser.deser;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.junit.Assert;


public class BigDecimalTest extends TestCase {
    public void test_null() throws Exception {
        Assert.assertNull(JSON.parseObject("null", BigDecimalTest.VO.class));
        Assert.assertNull(JSON.parseObject("{value:null}", BigDecimalTest.VO.class).getValue());
        Assert.assertNull(JSON.parseObject("{'value':null}", BigDecimalTest.VO.class).getValue());
        Assert.assertNull(JSON.parseObject("{\"value\":null}", BigDecimalTest.VO.class).getValue());
        Assert.assertNull(JSON.parseArray("null", BigDecimal.class));
        Assert.assertNull(JSON.parseObject("null", BigDecimal.class));
    }

    public void test_postfix() throws Exception {
        Assert.assertEquals(new BigDecimal("123"), JSON.parseObject("123L", BigDecimal.class));
        Assert.assertEquals(new BigDecimal("123"), JSON.parseObject("123D", BigDecimal.class));
        Assert.assertEquals(new BigDecimal("123"), JSON.parseObject("123F", BigDecimal.class));
        Assert.assertEquals(new BigDecimal("123"), JSON.parseObject("123S", BigDecimal.class));
        Assert.assertEquals(new BigDecimal("123"), JSON.parseObject("123B", BigDecimal.class));
    }

    public void test_className() throws Exception {
        Assert.assertEquals("123.", JSON.toJSONString(new BigDecimal("123"), WriteClassName));
        Assert.assertEquals("123.00", JSON.toJSONString(new BigDecimal("123.00"), WriteClassName));
        Assert.assertEquals("123.45", JSON.toJSONString(new BigDecimal("123.45"), WriteClassName));
        Assert.assertEquals(new BigDecimal("123"), JSON.parse("123."));
    }

    public static class VO {
        private BigDecimal value;

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }
}

