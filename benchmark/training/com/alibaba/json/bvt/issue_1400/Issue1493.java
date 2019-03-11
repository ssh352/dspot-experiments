package com.alibaba.json.bvt.issue_1400;


import SerializerFeature.WriteDateUseDateFormat;
import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue1493 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1493.TestBean test = new Issue1493.TestBean();
        String stime2 = "2017-09-22T15:08:56";
        LocalDateTime time1 = LocalDateTime.now();
        time1 = time1.minusNanos(10L);
        System.out.println(time1.getNano());
        LocalDateTime time2 = LocalDateTime.parse(stime2);
        test.setTime1(time1);
        test.setTime2(time2);
        String t1 = JSON.toJSONString(time1, WriteDateUseDateFormat);
        String json = JSON.toJSONString(test, WriteDateUseDateFormat);
        Assert.assertEquals((((("{\"time1\":" + t1) + ",\"time2\":\"") + stime2) + "\"}"), json);
        // String default_format = JSON.DEFFAULT_LOCAL_DATE_TIME_FORMAT;
        // JSON.DEFFAULT_LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        // String stime1 = DateTimeFormatter.ofPattern(JSON.DEFFAULT_LOCAL_DATE_TIME_FORMAT, Locale.CHINA).format(time1);
        json = JSON.toJSONString(test, WriteDateUseDateFormat);
        Assert.assertEquals((((("{\"time1\":" + (JSON.toJSONString(time1, WriteDateUseDateFormat))) + ",\"time2\":\"") + stime2) + "\"}"), json);
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        String stime1 = DateTimeFormatter.ofPattern(pattern, Locale.CHINA).format(time1);
        json = JSON.toJSONStringWithDateFormat(test, "yyyy-MM-dd'T'HH:mm:ss", WriteDateUseDateFormat);
        Assert.assertEquals((((("{\"time1\":\"" + stime1) + "\",\"time2\":\"") + stime2) + "\"}"), json);
        // JSON.DEFFAULT_LOCAL_DATE_TIME_FORMAT = default_format;
    }

    public static class TestBean {
        LocalDateTime time1;

        LocalDateTime time2;

        public LocalDateTime getTime1() {
            return time1;
        }

        public void setTime1(LocalDateTime time1) {
            this.time1 = time1;
        }

        public LocalDateTime getTime2() {
            return time2;
        }

        public void setTime2(LocalDateTime time2) {
            this.time2 = time2;
        }
    }
}

