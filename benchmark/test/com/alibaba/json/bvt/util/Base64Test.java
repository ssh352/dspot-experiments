package com.alibaba.json.bvt.util;


import com.alibaba.json.test.Base64;
import junit.framework.TestCase;


public class Base64Test extends TestCase {
    public void test_base64() throws Exception {
        String str = "????????????????????????????????????????????????????????????????????www.alibaba.com????????????????www.1688.com??????????????????????www.aliexpress.com?????????????????????????????>>";
        byte[] bytes = str.getBytes("UTF8");
        String base64Str = Base64.encodeToString(bytes, false);
        {
            byte[] bytes2 = com.alibaba.fastjson.util.Base64.decodeFast(base64Str);
            TestCase.assertEquals(str, new String(bytes2));
        }
        {
            byte[] bytes2 = com.alibaba.fastjson.util.Base64.decodeFast(base64Str, 0, base64Str.length());
            TestCase.assertEquals(str, new String(bytes2));
        }
        {
            byte[] bytes2 = com.alibaba.fastjson.util.Base64.decodeFast(base64Str.toCharArray(), 0, base64Str.length());
            TestCase.assertEquals(str, new String(bytes2));
        }
    }
}
