package com.alibaba.json.bvt.serializer;


import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONWriter;
import java.io.StringWriter;
import junit.framework.TestCase;


public class SerializeWriterTest_BrowserSecure_4_script extends TestCase {
    public void test_0() throws Exception {
        JSONObject object = new JSONObject();
        object.put("value", "<script>alert(1);</script>");
        String text = JSON.toJSONString(object, BrowserSecure);
        // assertEquals("{\"value\":\"&lt;script&gt;alert(1);&lt;\\/script&gt;\"}", text);
        TestCase.assertEquals("{\"value\":\"\\u003Cscript\\u003Ealert\\u00281\\u0029;\\u003C/script\\u003E\"}", text);
        JSONObject object1 = JSON.parseObject(text);
        TestCase.assertEquals(object.get("value"), object1.get("value"));
    }

    public void test_1() throws Exception {
        String text = JSON.toJSONString("<", BrowserSecure);
        TestCase.assertEquals("\"\\u003C\"", text);
    }

    public void test_2() throws Exception {
        String text = JSON.toJSONString("<script>", BrowserSecure);
        TestCase.assertEquals("\"\\u003Cscript\\u003E\"", text);
    }

    public void test_3() throws Exception {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            buf.append("<script>");
        }
        StringBuilder buf1 = new StringBuilder();
        buf1.append('"');
        for (int i = 0; i < 500; i++) {
            buf1.append("\\u003Cscript\\u003E");
        }
        buf1.append('"');
        StringWriter out = new StringWriter();
        JSONWriter writer = new JSONWriter(out);
        writer.config(BrowserSecure, true);
        writer.writeObject(buf.toString());
        writer.flush();
        TestCase.assertEquals(buf1.toString(), out.toString());
    }

    public void test_4() throws Exception {
        String text = JSON.toJSONString("(", BrowserSecure);
        TestCase.assertEquals("\"\\u0028\"", text);
    }

    public void test_5() throws Exception {
        String text = JSON.toJSONString(")", BrowserSecure);
        TestCase.assertEquals("\"\\u0029\"", text);
    }
}

