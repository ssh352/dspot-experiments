package com.alibaba.json.bvt.bug;


import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue62 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue62.A a = new Issue62.A();
        a.setA("aaaaaaaaaa".getBytes());
        a.setB(1);
        a.setC("aaaa");
        String jsonData = JSON.toJSONString(a, UseSingleQuotes);
        Assert.assertEquals("{'a':'YWFhYWFhYWFhYQ==','b':1,'c':'aaaa'}", jsonData);
        JSON.parse(jsonData);
    }

    static class A {
        private byte[] a;

        private int b;

        private String c;

        public byte[] getA() {
            return a;
        }

        public void setA(byte[] a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }
    }
}

