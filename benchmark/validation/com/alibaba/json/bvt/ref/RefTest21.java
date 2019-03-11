package com.alibaba.json.bvt.ref;


import Feature.DisableSpecialKeyDetect;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/23.
 */
public class RefTest21 extends TestCase {
    public void test_ref() throws Exception {
        String jsonTest = "{\"details\":{\"type\":{\"items\":{\"allOf\":[{\"$ref\":\"title\",\"required\":[\"iconImg\"]}]}}}}";
        JSONObject object = JSON.parseObject(jsonTest, DisableSpecialKeyDetect);
        System.out.println(object.get("details"));
    }
}

