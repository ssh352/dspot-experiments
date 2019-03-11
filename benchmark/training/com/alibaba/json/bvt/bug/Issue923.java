package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 05/12/2016.
 */
public class Issue923 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"res\": \"00000\",\"version\": \"1.8.0\",\"des\":\"\u7248\u672c\u66f4\u65b0\uff1a\n" + (((("1\u3001\u9080\u8bf7\u6709\u793c\uff1a\u65b0\u529f\u80fd\uff0c\u65b0\u73a9\u6cd5\uff0c\u5feb\u53bb\u4f53\u9a8c\u5427~\n" + "2\u3001\u76f4\u64ad\u7981\u8a00\uff1a\u4e3b\u64ad\u518d\u4e5f\u4e0d\u7528\u62c5\u5fc3\u5c0f\u9ed1\u7c89\u5566~\n") + "3\u3001\u84dd\u9cb8\u5e01\u5145\u503c\uff1a\u591a\u79cd\u6a21\u5757\u4efb\u4f60\u9009\uff0c\u591a\u5145\u591a\u9001\uff01\n") + "4\u3001\u4f18\u5316\u6392\u884c\u699c\uff1a\u4fee\u590d\u76f4\u64ad\u9875\u9762\u7684\u6392\u884c\u699c\uff0c\u8ba9\u5927\u5bb6\u7b2c\u4e00\u65f6\u95f4\u770b\u5230\u4ed8\u51fa\u7684\u4f60~\n") + "5\u3001\u4fee\u590d\u76f4\u64ad\u804a\u5929\u533a\uff1a\u518d\u4e5f\u4e0d\u62c5\u5fc3\u4e3b\u64ad\u770b\u4e0d\u5230\u4f60\u9001\u7684\u793c\u7269\u548c\u5c0f\u661f\u661f\u5566~\",\"download\":\"http://xxx/android/x/x.apk\"}");
        JSON.parse(text);
    }
}

