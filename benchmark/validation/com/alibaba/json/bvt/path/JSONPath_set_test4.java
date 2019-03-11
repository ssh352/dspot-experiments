package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_set_test4 extends TestCase {
    public void test_jsonpath_1() throws Exception {
        Map<String, Object> root = new HashMap<String, Object>();
        JSONPath.set(root, "/a[0]/b", 1001);
        Assert.assertEquals("{\"a\":[{\"b\":1001}]}", JSON.toJSONString(root));
        Assert.assertEquals(1001, JSONPath.eval(root, "/a[0]/b"));
    }
}

