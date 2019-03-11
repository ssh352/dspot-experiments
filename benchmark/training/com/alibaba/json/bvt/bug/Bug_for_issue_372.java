package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_372 extends TestCase {
    public void test_for_issue() throws Exception {
        ParserConfig config = new ParserConfig();
        ObjectDeserializer deser = config.getDeserializer(Bug_for_issue_372.Model.class);
        Assert.assertEquals(JavaBeanDeserializer.class, deser.getClass());
    }

    @JSONType(asm = false)
    public static class Model {}
}

