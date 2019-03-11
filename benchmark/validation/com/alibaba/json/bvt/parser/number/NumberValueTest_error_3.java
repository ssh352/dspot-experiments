package com.alibaba.json.bvt.parser.number;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class NumberValueTest_error_3 extends TestCase {
    public void test_0() throws Exception {
        Exception error = null;
        try {
            String text = "{\"value\":33e+}";
            JSON.parse(text);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}

