package com.alibaba.json.bvt.parser.fieldTypeResolver;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.deserializer.FieldTypeResolver;
import java.lang.reflect.Type;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldTypeResolverTest extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"item_0\":{},\"item_1\":{}}";
        FieldTypeResolver fieldResolver = new FieldTypeResolver() {
            public Type resolve(Object object, String fieldName) {
                if (fieldName.startsWith("item_")) {
                    return FieldTypeResolverTest.Item.class;
                }
                return null;
            }
        };
        JSONObject jsonObject = JSON.parseObject(text, JSONObject.class, fieldResolver);
        Assert.assertTrue(((jsonObject.get("item_0")) instanceof FieldTypeResolverTest.Item));
    }

    public static class Item {}
}

