package com.alibaba.json.bvt.serializer.stream;


import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class StreamWriterTest_writeArray2 extends TestCase {
    public void test_0() throws Exception {
        StringWriter out = new StringWriter();
        SerializeWriter writer = new SerializeWriter(out, 10);
        Assert.assertEquals(10, writer.getBufferLength());
        int blockSize = 21;
        for (char ch = 'a'; ch <= 'z'; ++ch) {
            char[] chars = new char[blockSize];
            for (int i = 0; i < blockSize; ++i) {
                chars[i] = ch;
            }
            writer.write(chars, 0, chars.length);
        }
        writer.close();
        String text = out.toString();
        Assert.assertEquals((26 * blockSize), text.length());
        for (int i = 0; i < 26; ++i) {
            for (int j = 0; j < blockSize; ++j) {
                Assert.assertEquals(text.charAt(((i * blockSize) + j)), ((char) ('a' + i)));
            }
        }
    }
}
