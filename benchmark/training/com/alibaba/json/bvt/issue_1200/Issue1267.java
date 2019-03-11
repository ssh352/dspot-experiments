package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.springframework.util.LinkedMultiValueMap;


/**
 * Created by kimmking on 15/06/2017.
 */
public class Issue1267 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"message\":{\"refund_fee\":[\"0.01\"],\"pay_fee\":[\"0.01\"]},\"url\":\"http://localhost:8080\"}";
        LinkedMultiValueMap message = JSON.parseObject(JSON.parseObject(json).getString("message"), LinkedMultiValueMap.class);// ???????????

        TestCase.assertEquals("0.01", message.get("pay_fee").get(0));
        Issue1267.PushHttpMessage pushHttpMessage = JSON.parseObject(json, Issue1267.PushHttpMessage.class);
        TestCase.assertEquals("0.01", pushHttpMessage.getMessage().get("pay_fee").get(0));
    }

    public static class PushHttpMessage {
        private LinkedMultiValueMap<String, String> message;

        private String url;

        public LinkedMultiValueMap<String, String> getMessage() {
            return message;
        }

        public void setMessage(LinkedMultiValueMap<String, String> message) {
            this.message = message;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

