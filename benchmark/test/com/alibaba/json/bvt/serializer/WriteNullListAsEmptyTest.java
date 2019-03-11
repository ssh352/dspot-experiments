package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteNullListAsEmptyTest extends TestCase {
    public void test_nullList() {
        SerializerFeature[] features = new SerializerFeature[]{ SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty };
        Assert.assertEquals("{\"names\":[]}", JSON.toJSONString(new WriteNullListAsEmptyTest.VO1(), features));
        Assert.assertEquals("{\"names\":[]}", JSON.toJSONString(new WriteNullListAsEmptyTest.VO2(), features));
        Assert.assertEquals("{\"names\":[]}", JSON.toJSONString(new WriteNullListAsEmptyTest.VO3(), features));
        Assert.assertEquals("{\"names\":[]}", JSON.toJSONString(new WriteNullListAsEmptyTest.VO4(), features));
    }

    public static class VO1 {
        private List<Object> names = null;

        public List<Object> getNames() {
            return names;
        }

        public void setNames(List<Object> names) {
            this.names = names;
        }
    }

    public static class VO2 {
        private List<String> names = null;

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }
    }

    public static class VO3 {
        private List<Integer> names = null;

        public List<Integer> getNames() {
            return names;
        }

        public void setNames(List<Integer> names) {
            this.names = names;
        }
    }

    public static class VO4 {
        private List<Long> names = null;

        public List<Long> getNames() {
            return names;
        }

        public void setNames(List<Long> names) {
            this.names = names;
        }
    }
}

