package com.alibaba.json.bvt.serializer;


import SerializerFeature.QuoteFieldNames;
import SerializerFeature.WriteTabAsSpecial;
import com.alibaba.fastjson.serializer.SerializeWriter;
import junit.framework.TestCase;
import org.junit.Assert;


@SuppressWarnings("deprecation")
public class SerializeWriterTest_4 extends TestCase {
    public void test_0() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.config(WriteTabAsSpecial, true);
        out.writeFieldValue(',', "name", "\t");
        Assert.assertEquals(",\"name\":\"\\t\"", out.toString());
        out.close();
    }

    public void test_1() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.config(WriteTabAsSpecial, true);
        out.writeFieldValue(',', "name", "\t\n");
        Assert.assertEquals(",\"name\":\"\\t\\n\"", out.toString());
        out.close();
    }

    public void test_3() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.config(WriteTabAsSpecial, true);
        out.writeFieldValue(',', "name", "\t\n \b\n\r\f\\ \"");
        Assert.assertEquals(",\"name\":\"\\t\\n \\b\\n\\r\\f\\\\ \\\"\"", out.toString());
        out.close();
    }

    public void test_4() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.config(WriteTabAsSpecial, false);
        out.writeFieldValue(',', "name", "\t\n \b\n\r\f\\ \"");
        Assert.assertEquals(",\"name\":\"\\t\\n \\b\\n\\r\\f\\\\ \\\"\"", out.toString());
        out.close();
    }

    public void test_5() throws Exception {
        SerializeWriter out = new SerializeWriter(1000);
        out.config(QuoteFieldNames, true);
        out.config(WriteTabAsSpecial, true);
        out.writeString("\t\n \b\n\r\f\\ \"");
        Assert.assertEquals("\"\\t\\n \\b\\n\\r\\f\\\\ \\\"\"", out.toString());
        out.close();
    }
}

