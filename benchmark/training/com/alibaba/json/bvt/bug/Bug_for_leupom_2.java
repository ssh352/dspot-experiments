package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_leupom_2 extends TestCase {
    public void test_0() throws Exception {
        Bug_for_leupom_2.Time time = new Bug_for_leupom_2.Time(1000, TimeUnit.MILLISECONDS);
        String text = JSON.toJSONString(time);
        System.out.println(text);
        Bug_for_leupom_2.Time time2 = JSON.parseObject(text, Bug_for_leupom_2.Time.class);
        Assert.assertEquals(time2.getValue(), time.getValue());
        Assert.assertEquals(time2.getUnit(), time.getUnit());
    }

    public static class Time {
        private long value;

        private TimeUnit unit;

        public Time() {
            super();
        }

        public Time(long value, TimeUnit unit) {
            super();
            this.value = value;
            this.unit = unit;
        }

        public long getValue() {
            return value;
        }

        @JSONField(serialzeFeatures = { SerializerFeature.WriteEnumUsingToString })
        public TimeUnit getUnit() {
            return unit;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }
    }
}

