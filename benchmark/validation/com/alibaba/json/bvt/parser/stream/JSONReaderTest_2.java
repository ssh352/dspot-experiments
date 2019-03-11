package com.alibaba.json.bvt.parser.stream;


import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderTest_2 extends TestCase {
    public void test_read_integer() throws Exception {
        String text = "1001";
        JSONReader reader = new JSONReader(new StringReader(text));
        Assert.assertEquals(Integer.valueOf(1001), reader.readInteger());
        reader.close();
    }

    public void test_read_Long() throws Exception {
        String text = "1001";
        JSONReader reader = new JSONReader(new StringReader(text));
        Assert.assertEquals(Long.valueOf(1001), reader.readLong());
        reader.close();
    }
}

