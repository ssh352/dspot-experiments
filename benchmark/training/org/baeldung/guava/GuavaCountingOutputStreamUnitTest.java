package org.baeldung.guava;


import com.google.common.io.CountingOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Test;


public class GuavaCountingOutputStreamUnitTest {
    public static final int MAX = 5;

    @Test(expected = RuntimeException.class)
    public void givenData_whenCountReachesLimit_thenThrowException() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CountingOutputStream cos = new CountingOutputStream(out);
        byte[] data = new byte[1024];
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        int b;
        while ((b = in.read()) != (-1)) {
            cos.write(b);
            if ((cos.getCount()) >= (GuavaCountingOutputStreamUnitTest.MAX)) {
                throw new RuntimeException("Write limit reached");
            }
        } 
    }
}

