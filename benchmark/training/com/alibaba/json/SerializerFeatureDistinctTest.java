package com.alibaba.json;


import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


/**
 * Created by wenshao on 24/06/2017.
 */
public class SerializerFeatureDistinctTest extends TestCase {
    public void test_allfeatures() throws Exception {
        Set<Object> masks = new HashSet<Object>();
        for (SerializerFeature feature : SerializerFeature.values()) {
            Object mask = feature.getMask();
            TestCase.assertFalse(masks.contains(mask));
            masks.add(mask);
        }
        TestCase.assertEquals(masks.size(), SerializerFeature.values().length);
        System.out.println(SerializerFeature.values().length);
    }
}

