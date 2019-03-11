package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 05/05/2017.
 */
public class Issue1177_4 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"models\":[{\"x\":\"y\"},{\"x\":\"y\"}]}";
        Issue1177_4.Root root = JSONObject.parseObject(text, Issue1177_4.Root.class);
        System.out.println(JSON.toJSONString(root));
        String jsonpath = "$..x";
        String value = "y2";
        JSONPath.set(root, jsonpath, value);
        TestCase.assertEquals("{\"models\":[{\"x\":\"y2\"},{\"x\":\"y2\"}]}", JSON.toJSONString(root));
    }

    public static class Root {
        public List<Issue1177_4.Model> models;
    }

    public static class Model {
        public String x;
    }
}

