package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;


public class Bug_for_liuwanzhen_ren extends TestCase {
    public void test_0() throws Exception {
        Bug_for_liuwanzhen_ren.Bean bean = new Bug_for_liuwanzhen_ren.Bean();
        bean.setAction("123");
        HashMap paramMap = new HashMap();
        paramMap.put("url1", "123");
        paramMap.put("url2", "456");
        bean.setParamMap(paramMap);
        String str = JSON.toJSONString(bean);
        System.out.println(str);
        Bug_for_liuwanzhen_ren.Bean bean2 = JSON.parseObject(str, Bug_for_liuwanzhen_ren.Bean.class);
        System.out.println(bean2.getAction());
        System.out.println(bean2.getParamMap());
    }

    public static class Bean {
        private String action;

        private HashMap<String, String> paramMap;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public HashMap<String, String> getParamMap() {
            return paramMap;
        }

        public void setParamMap(HashMap<String, String> paramMap) {
            this.paramMap = paramMap;
        }
    }
}

