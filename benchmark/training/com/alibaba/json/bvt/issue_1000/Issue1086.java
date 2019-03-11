package com.alibaba.json.bvt.issue_1000;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 20/03/2017.
 */
public class Issue1086 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1086.Model model = JSON.parseObject("{\"flag\":1}", Issue1086.Model.class);
        TestCase.assertTrue(model.flag);
    }

    public static class Model {
        public boolean flag;
    }
}

