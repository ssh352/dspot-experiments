package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_262 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"$\":\"zhugw\"}";
        Assert.assertEquals("zhugw", JSONPath.read(json, "/\\$"));
    }

    public static class Model {
        public String name;
    }
}

