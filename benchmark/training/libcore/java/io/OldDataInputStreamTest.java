/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.java.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import junit.framework.TestCase;
import tests.support.Support_ASimpleInputStream;


public class OldDataInputStreamTest extends TestCase {
    private DataOutputStream os;

    private DataInputStream dis;

    private ByteArrayOutputStream bos;

    String unihw = "Hello World";

    public String fileString = "Test_All_Tests\nTest_java_io_BufferedInputStream\nTest_java_io_BufferedOutputStream\nTest_java_io_ByteArrayInputStream\nTest_java_io_ByteArrayOutputStream\nTest_DataInputStream\n";

    private final int testLength = fileString.length();

    public void test_ConstructorLjava_io_InputStream() {
        try {
            os.writeChar('t');
            os.close();
            openDataInputStream();
        } catch (IOException e) {
            TestCase.fail(("IOException during constructor test : " + (e.getMessage())));
        } finally {
            try {
                dis.close();
            } catch (IOException e) {
                TestCase.fail(("IOException during constructor test : " + (e.getMessage())));
            }
        }
    }

    public void test_read$B() throws IOException {
        byte[] rbytes = new byte[(testLength) - 5];
        Support_ASimpleInputStream sis = new Support_ASimpleInputStream();
        int r;
        os.write(fileString.getBytes());
        os.close();
        openDataInputStream();
        r = dis.read(rbytes);
        TestCase.assertEquals("Test 1: Incorrect number of bytes read;", ((testLength) - 5), r);
        TestCase.assertTrue("Test 2: Incorrect data written or read.", new String(rbytes).equals(fileString.substring(0, ((testLength) - 5))));
        r = dis.read(rbytes);
        TestCase.assertEquals("Test 3: Incorrect number of bytes read;", 5, r);
        TestCase.assertTrue("Test 4: Incorrect data written or read.", new String(rbytes, 0, 5).equals(fileString.substring(((testLength) - 5))));
        dis.close();
        sis.throwExceptionOnNextUse = true;
        dis = new DataInputStream(sis);
        try {
            dis.read(rbytes);
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_read$BII() throws IOException {
        byte[] rbytes = new byte[(testLength) - 5];
        Support_ASimpleInputStream sis = new Support_ASimpleInputStream();
        int r;
        os.write(fileString.getBytes());
        os.close();
        openDataInputStream();
        r = dis.read(rbytes, 1, ((testLength) - 10));
        TestCase.assertEquals("Test 1: Incorrect number of bytes read;", ((testLength) - 10), r);
        TestCase.assertEquals("Test 2: Incorrect data read.", 0, rbytes[0]);
        TestCase.assertTrue("Test 3: Incorrect data written or read.", new String(rbytes, 1, r).equals(fileString.substring(0, r)));
        r = dis.read(rbytes, 0, 15);
        TestCase.assertEquals("Test 3: Incorrect number of bytes read;", 10, r);
        TestCase.assertTrue("Test 4: Incorrect data written or read.", new String(rbytes, 0, r).equals(fileString.substring(((testLength) - 10))));
        dis.close();
        sis.throwExceptionOnNextUse = true;
        dis = new DataInputStream(sis);
        try {
            dis.read(rbytes, 1, 5);
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_read$BII_Exception() throws IOException {
        byte[] rbytes = new byte[(testLength) - 5];
        os.write(fileString.getBytes());
        os.close();
        openDataInputStream();
        try {
            dis.read(rbytes, (-1), 1);
            TestCase.fail("IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            dis.read(rbytes, 0, (-1));
            TestCase.fail("IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            dis.read(rbytes, rbytes.length, 1);
            TestCase.fail("IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void test_readFully$B() throws IOException {
        byte[] rbytes = new byte[testLength];
        os.write(fileString.getBytes());
        os.close();
        openDataInputStream();
        dis.readFully(rbytes);
        TestCase.assertTrue("Test 1: Incorrect data written or read.", new String(rbytes, 0, testLength).equals(fileString));
        dis.close();
        try {
            dis.readFully(rbytes);
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        openDataInputStream();
        dis.readByte();
        try {
            dis.readFully(rbytes);
            TestCase.fail("Test 3: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
    }

    public void test_readFully$BII() throws IOException {
        byte[] rbytes = new byte[testLength];
        os.write(fileString.getBytes());
        os.close();
        openDataInputStream();
        dis.readFully(rbytes, 2, ((testLength) - 4));
        TestCase.assertTrue("Test 1: Incorrect data written or read.", new String(rbytes, 2, ((testLength) - 4)).equals(fileString.substring(0, ((testLength) - 4))));
        dis.close();
        try {
            dis.readFully(rbytes, 0, testLength);
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        openDataInputStream();
        dis.readByte();
        try {
            dis.readFully(rbytes, 0, testLength);
            TestCase.fail("Test 3: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
    }

    public void test_readFully$BII_Exception() throws IOException {
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(new byte[testLength]));
        byte[] byteArray = new byte[testLength];
        try {
            is.readFully(byteArray, 0, (-1));
            TestCase.fail("Test 1: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.readFully(byteArray, 0, ((byteArray.length) + 1));
            TestCase.fail("Test 2: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.readFully(byteArray, 1, byteArray.length);
            TestCase.fail("Test 3: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.readFully(byteArray, (-1), byteArray.length);
            TestCase.fail("Test 4: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.readFully(null, 0, 1);
            TestCase.fail("Test 5: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
        is = new DataInputStream(null);
        try {
            is.readFully(byteArray, 0, 1);
            TestCase.fail("Test 6: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
    }

    public void test_readUnsignedByte() throws IOException {
        os.writeByte(((byte) (-127)));
        os.close();
        openDataInputStream();
        TestCase.assertEquals("Test 1: Incorrect byte written or read;", 129, dis.readUnsignedByte());
        try {
            dis.readUnsignedByte();
            TestCase.fail("Test 2: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        dis.close();
        try {
            dis.readUnsignedByte();
            TestCase.fail("Test 3: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_readUnsignedShort() throws IOException {
        os.writeShort(Short.MIN_VALUE);
        os.close();
        openDataInputStream();
        TestCase.assertEquals("Test 1: Incorrect short written or read;", ((Short.MAX_VALUE) + 1), dis.readUnsignedShort());
        try {
            dis.readUnsignedShort();
            TestCase.fail("Test 2: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        dis.close();
        try {
            dis.readUnsignedShort();
            TestCase.fail("Test 3: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_readUTFLjava_io_DataInput() throws IOException {
        os.writeUTF(unihw);
        os.close();
        openDataInputStream();
        TestCase.assertTrue("Test 1: Incorrect UTF-8 string written or read.", DataInputStream.readUTF(dis).equals(unihw));
        try {
            DataInputStream.readUTF(dis);
            TestCase.fail("Test 2: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        dis.close();
        try {
            DataInputStream.readUTF(dis);
            TestCase.fail("Test 3: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }
}

