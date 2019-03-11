package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import junit.framework.TestCase;
import org.junit.Assert;


public class PropertyPreFilterClassLevelTest_private extends TestCase {
    public void test_0() throws Exception {
        Object[] array = new Object[]{ new PropertyPreFilterClassLevelTest_private.ModelA(), new PropertyPreFilterClassLevelTest_private.ModelB() };
        SerializeConfig config = new SerializeConfig();
        // 
        config.addFilter(PropertyPreFilterClassLevelTest_private.ModelA.class, new SimplePropertyPreFilter("name"));
        // 
        config.addFilter(PropertyPreFilterClassLevelTest_private.ModelB.class, new SimplePropertyPreFilter("id"));
        String text2 = JSON.toJSONString(array, config);
        Assert.assertEquals("[{},{\"id\":1002}]", text2);
        String text = JSON.toJSONString(array);
        Assert.assertEquals("[{\"id\":1001},{\"id\":1002}]", text);
    }

    private static class ModelA {
        public int id = 1001;
    }

    private static class ModelB {
        public int id = 1002;
    }
}

