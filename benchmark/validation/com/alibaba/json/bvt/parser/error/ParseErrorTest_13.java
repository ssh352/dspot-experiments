package com.alibaba.json.bvt.parser.error;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class ParseErrorTest_13 extends TestCase {
    public void test_for_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":Set[]:");
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }
}

