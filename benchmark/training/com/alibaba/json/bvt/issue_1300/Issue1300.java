package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.TypeUtils;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by wenshao on 01/07/2017.
 */
public class Issue1300 extends TestCase {
    public void testFullJSON() {
        JSONObject data = new JSONObject();
        data.put("name", "string");
        data.put("code", 1);
        data.put("pinyin", "pinyin");
        Issue1300.City object = TypeUtils.castToJavaBean(data, Issue1300.City.class);
        TestCase.assertEquals("string", object.name);
        TestCase.assertEquals(1, object.code);
        TestCase.assertEquals("pinyin", object.pinyin);
    }

    public void testEmptyJSON() {
        Issue1300.City object = TypeUtils.castToJavaBean(new JSONObject(), Issue1300.City.class);
        Assert.assertEquals(null, object.name);
        Assert.assertEquals(0, object.code);
    }

    public static class City implements Issue1300.Parcelable {
        public final int code;

        public final String name;

        public final String pinyin;

        @JSONCreator
        public City(@JSONField(name = "code")
        int code, @JSONField(name = "name")
        String name, @JSONField(name = "pinyin")
        String pinyin) {
            this.code = code;
            this.name = name;
            this.pinyin = pinyin;
        }
    }

    public static interface Parcelable {}
}

