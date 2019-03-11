package com.alibaba.json.bvt.feature;


import Feature.DisableFieldSmartMatch;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import junit.framework.TestCase;


/**
 * Created by wenshao on 17/03/2017.
 */
public class DisableFieldSmartMatchTest extends TestCase {
    public void test_feature() throws Exception {
        TestCase.assertEquals(123, JSON.parseObject("{\"person_id\":123}", DisableFieldSmartMatchTest.Model_for_disableFieldSmartMatchMask.class).personId);
        TestCase.assertEquals(0, JSON.parseObject("{\"person_id\":123}", DisableFieldSmartMatchTest.Model_for_disableFieldSmartMatchMask.class, DisableFieldSmartMatch).personId);
        TestCase.assertEquals(123, JSON.parseObject("{\"personId\":123}", DisableFieldSmartMatchTest.Model_for_disableFieldSmartMatchMask.class, DisableFieldSmartMatch).personId);
    }

    public void test_feature2() throws Exception {
        TestCase.assertEquals(0, JSON.parseObject("{\"person_id\":123}", DisableFieldSmartMatchTest.Model_for_disableFieldSmartMatchMask2.class).personId);
        TestCase.assertEquals(123, JSON.parseObject("{\"personId\":123}", DisableFieldSmartMatchTest.Model_for_disableFieldSmartMatchMask2.class).personId);
    }

    public static class Model_for_disableFieldSmartMatchMask {
        public int personId;
    }

    @JSONType(parseFeatures = Feature.DisableFieldSmartMatch)
    public static class Model_for_disableFieldSmartMatchMask2 {
        public int personId;
    }
}

