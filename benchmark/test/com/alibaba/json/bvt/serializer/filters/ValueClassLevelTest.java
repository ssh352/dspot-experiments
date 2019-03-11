package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.ValueFilter;
import junit.framework.TestCase;
import org.junit.Assert;


public class ValueClassLevelTest extends TestCase {
    public void test_0() throws Exception {
        Object[] array = new Object[]{ new ValueClassLevelTest.ModelA(), new ValueClassLevelTest.ModelB() };
        SerializeConfig config = new SerializeConfig();
        // 
        config.addFilter(ValueClassLevelTest.ModelA.class, new ValueFilter() {
            @Override
            public Object process(Object object, String name, Object value) {
                return 30001;
            }
        });
        // 
        config.addFilter(ValueClassLevelTest.ModelB.class, new ValueFilter() {
            @Override
            public Object process(Object object, String name, Object value) {
                return 20001;
            }
        });
        String text2 = JSON.toJSONString(array, config);
        Assert.assertEquals("[{\"id\":30001},{\"id\":20001}]", text2);
        String text = JSON.toJSONString(array);
        Assert.assertEquals("[{\"id\":1001},{\"id\":1002}]", text);
    }

    public static class ModelA {
        public int id = 1001;
    }

    public static class ModelB {
        public int id = 1002;
    }
}

