package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class ByteArrayFieldTest_7_gzip_hex extends TestCase {
    public void test_0() throws Exception {
        ByteArrayFieldTest_7_gzip_hex.Model model = new ByteArrayFieldTest_7_gzip_hex.Model();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 1000; ++i) {
            buf.append("0123456890");
            buf.append("ABCDEFGHIJ");
        }
        model.value = buf.toString().getBytes();
        String json = JSON.toJSONString(model);
        TestCase.assertEquals("{\"value\":\"H4sIAAAAAAAAAO3IsRGAIBAAsJVeUE5LBBXcfyC3sErKxJLyupX9iHq2ft3PmG8455xzzjnnnHPOOeecc84555xzzjnnnHPOOeecc84555xzzjnnnHPOOeecc84555z7/T6powiAIE4AAA==\"}", json);
        ByteArrayFieldTest_7_gzip_hex.Model model1 = JSON.parseObject(json, ByteArrayFieldTest_7_gzip_hex.Model.class);
        Assert.assertArrayEquals(model.value, model1.value);
    }

    private static class Model {
        @JSONField(format = "gzip,base64")
        public byte[] value;
    }
}

