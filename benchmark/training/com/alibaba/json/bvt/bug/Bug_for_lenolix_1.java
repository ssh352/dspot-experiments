package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_lenolix_1 extends TestCase {
    public void test_0() throws Exception {
        Map<String, Bug_for_lenolix_1.User> matcherMap = new HashMap<String, Bug_for_lenolix_1.User>();
        String matcherMapString = JSON.toJSONString(matcherMap, WriteClassName, WriteMapNullValue);
        System.out.println(matcherMapString);
        matcherMap = JSONObject.parseObject(matcherMapString, new com.alibaba.fastjson.TypeReference<Map<String, Bug_for_lenolix_1.User>>() {});
    }

    public static class User {}
}

