package liquibase.util;


import java.io.ByteArrayInputStream;
import org.junit.Assert;
import org.junit.Test;


public class MD5UtilTest {
    private static final String TEST_STRING = "foo";

    private static final String TEST_STRING_MD5_HASH = "acbd18db4cc2f85cedef654fccc4a4d8";

    private static final String TEST_STRING2 = "abc";

    private static final String TEST_STRING2_MD5_HASH = "900150983cd24fb0d6963f7d28e17f72";

    private static final String TEST_STRING3 = "bbb";

    private static final String TEST_STRING3_MD5_HASH = "08f8e0260c64418510cefb2b06eee5cd";

    @Test
    public void testComputeMD5() throws Exception {
        String hash = MD5Util.computeMD5(MD5UtilTest.TEST_STRING);
        Assert.assertEquals(MD5UtilTest.TEST_STRING_MD5_HASH, hash);
        String hash2 = MD5Util.computeMD5(MD5UtilTest.TEST_STRING2);
        Assert.assertEquals(MD5UtilTest.TEST_STRING2_MD5_HASH, hash2);
        String hash3 = MD5Util.computeMD5(MD5UtilTest.TEST_STRING3);
        Assert.assertEquals(MD5UtilTest.TEST_STRING3_MD5_HASH, hash3);
    }

    @Test
    public void testComputeMD5InputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(MD5UtilTest.TEST_STRING.getBytes());
        String hexString = MD5Util.computeMD5(bais);
        Assert.assertEquals(MD5UtilTest.TEST_STRING_MD5_HASH, hexString);
    }
}

