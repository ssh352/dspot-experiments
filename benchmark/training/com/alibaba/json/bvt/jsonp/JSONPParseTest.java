package com.alibaba.json.bvt.jsonp;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import junit.framework.TestCase;


/**
 * Created by wenshao on 21/02/2017.
 */
public class JSONPParseTest extends TestCase {
    public void test_f() throws Exception {
        String text = "callback ({'id':1, 'name':'idonans'} );";
        JSONPObject jsonpObject = JSON.parseObject(text, JSONPObject.class);
        TestCase.assertEquals("callback", jsonpObject.getFunction());
        TestCase.assertEquals(1, jsonpObject.getParameters().size());
        JSONObject param = ((JSONObject) (jsonpObject.getParameters().get(0)));
        TestCase.assertEquals(1, param.get("id"));
        TestCase.assertEquals("idonans", param.get("name"));
        String json = JSON.toJSONString(jsonpObject);
        System.out.println(json);
    }
}

