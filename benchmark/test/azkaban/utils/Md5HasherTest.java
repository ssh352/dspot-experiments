package azkaban.utils;


import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class Md5HasherTest {
    private static final File ZIP_FILE = new File("src/test/resources/sample_flow_01.zip");

    @Test
    public void md5Hash() throws Exception {
        Assert.assertThat(Md5Hasher.md5Hash(Md5HasherTest.ZIP_FILE), Matchers.is(new byte[]{ -59, -26, 22, -50, -80, -101, -57, -121, -27, 46, -71, -101, -85, -115, 42, -116 }));
    }
}

