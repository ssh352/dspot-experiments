package com.alibaba.json.bvt.serializer.filters;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class ClassLevelFeatureConfigTest_private extends TestCase {
    public void test_0() throws Exception {
        SerializeConfig config = new SerializeConfig();
        ClassLevelFeatureConfigTest_private.Model model = new ClassLevelFeatureConfigTest_private.Model();
        model.id = 1001;
        Assert.assertEquals("{\"id\":1001}", JSON.toJSONString(model, config));
        config.config(ClassLevelFeatureConfigTest_private.Model.class, BeanToArray, true);
        Assert.assertEquals("[1001]", JSON.toJSONString(model, config));
    }

    private static class Model {
        public int id;
    }
}

