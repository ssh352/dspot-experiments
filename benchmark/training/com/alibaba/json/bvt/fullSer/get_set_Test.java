package com.alibaba.json.bvt.fullSer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class get_set_Test extends TestCase {
    public void test_codec() throws Exception {
        get_set_Test.VO vo = new get_set_Test.VO();
        vo.set_id(123);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"id\":123}", text);
        get_set_Test.VO vo1 = JSON.parseObject(text, get_set_Test.VO.class);
        Assert.assertEquals(123, vo1.get_id());
    }

    public static class VO {
        private int id;

        public int get_id() {
            return id;
        }

        public void set_id(int id) {
            this.id = id;
        }
    }
}

