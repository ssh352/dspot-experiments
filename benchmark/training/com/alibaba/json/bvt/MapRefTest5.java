package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class MapRefTest5 extends TestCase {
    public void test_0() throws Exception {
        String text = "[{\"u1\":{\"id\":123,\"name\":\"wenshao\"},\"u2\":{\"$ref\":\"$\"}}]";
        List<Map<String, Object>> list = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<Map<String, Object>>>() {});
        // Assert.assertEquals(map, map.get("this"));
        Assert.assertSame(list, list.get(0).get("u2"));
    }
}

