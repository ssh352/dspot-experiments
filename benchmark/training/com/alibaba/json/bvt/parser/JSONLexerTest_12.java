package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_12 extends TestCase {
    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"type\":92233720368547758071}", JSONLexerTest_12.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_a() throws Exception {
        Assert.assertEquals(123L, JSON.parseObject("{\"vo\":{\"type\":123}}", JSONLexerTest_12.A.class).getVo().getType());
    }

    public void test_error_2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"vo\":{\"type\":123}[", JSONLexerTest_12.A.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"vo\":{\"type\":123]", JSONLexerTest_12.A.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class A {
        private JSONLexerTest_12.VO vo;

        public JSONLexerTest_12.VO getVo() {
            return vo;
        }

        public void setVo(JSONLexerTest_12.VO vo) {
            this.vo = vo;
        }
    }

    public static class VO {
        public VO() {
        }

        private long type;

        public long getType() {
            return type;
        }

        public void setType(long type) {
            this.type = type;
        }
    }
}

