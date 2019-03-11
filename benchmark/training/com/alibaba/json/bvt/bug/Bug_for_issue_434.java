package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_434 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{value:[\"null\"]}";
        JSONObject parse = JSONObject.parseObject(json);
        JSONArray jsonArray = parse.getJSONArray("value");
        Assert.assertEquals(1, jsonArray.size());
    }
}

