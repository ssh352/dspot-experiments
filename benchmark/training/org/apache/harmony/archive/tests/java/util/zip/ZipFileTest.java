/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.archive.tests.java.util.zip;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import junit.framework.TestCase;
import libcore.java.lang.ref.FinalizationTester;
import tests.support.resource.Support_Resources;


public class ZipFileTest extends TestCase {
    // the file hyts_zipFile.zip in setup must be included as a resource
    private String tempFileName;

    private ZipFile zfile;

    // custom security manager
    SecurityManager sm = new SecurityManager() {
        final String forbidenPermissionAction = "read";

        public void checkPermission(Permission perm) {
            // only check if it's a FilePermission because Locale checks
            // for a PropertyPermission with action"read" to get system props.
            if ((perm instanceof FilePermission) && (perm.getActions().equals(forbidenPermissionAction))) {
                throw new SecurityException();
            }
        }
    };

    /**
     * java.util.zip.ZipFile#ZipFile(java.io.File)
     */
    public void test_ConstructorLjava_io_File() {
        // Test for method java.util.zip.ZipFile(java.io.File)
        TestCase.assertTrue("Used to test", true);
    }

    /**
     * java.util.zip.ZipFile#ZipFile(java.io.File, int)
     */
    public void test_ConstructorLjava_io_FileI() throws IOException {
        zfile.close();// about to reopen the same temp file

        File file = new File(tempFileName);
        ZipFile zip = new ZipFile(file, ((ZipFile.OPEN_DELETE) | (ZipFile.OPEN_READ)));
        zip.close();
        TestCase.assertTrue("Zip should not exist", (!(file.exists())));
        file = new File(tempFileName);
        file.delete();
        try {
            zip = new ZipFile(file, ZipFile.OPEN_READ);
            TestCase.fail("IOException expected");
        } catch (IOException ee) {
            // expected
        }
        file = new File(tempFileName);
        try {
            zip = new ZipFile(file, (-1));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ee) {
            // expected
        }
    }

    /**
     *
     *
     * @throws IOException
    java.util.zip.ZipFile#ZipFile(java.lang.String)
     * 		
     */
    public void test_ConstructorLjava_lang_String() throws IOException {
        System.setProperty("user.dir", System.getProperty("java.io.tmpdir"));
        zfile.close();// about to reopen the same temp file

        ZipFile zip = new ZipFile(tempFileName);
        zip.close();
        File file = File.createTempFile("zip", "tmp");
        try {
            zip = new ZipFile(file.getName());
            TestCase.fail("ZipException expected");
        } catch (ZipException ee) {
            // expected
        }
        file.delete();
    }

    /**
     * java.util.zip.ZipFile#finalize()
     */
    public void test_finalize() throws IOException {
        InputStream in = Support_Resources.getStream("hyts_ZipFile.zip");
        File file = Support_Resources.createTempFile(".jar");
        OutputStream out = new FileOutputStream(file);
        int result;
        byte[] buf = new byte[4096];
        while ((result = in.read(buf)) != (-1)) {
            out.write(buf, 0, result);
        } 
        in.close();
        out.close();
        /* ZipFile zip = new ZipFile(file); ZipEntry entry1 =
        zip.getEntry("File1.txt"); assertNotNull("Did not find entry",
        entry1); entry1 = null; zip = null;
         */
        TestCase.assertNotNull("Did not find entry", test_finalize1(test_finalize2(file)));
        FinalizationTester.induceFinalization();
        file.delete();
        TestCase.assertTrue("Zip should not exist", (!(file.exists())));
    }

    /**
     *
     *
     * @throws IOException
    java.util.zip.ZipFile#close()
     * 		
     */
    public void test_close() throws IOException {
        // Test for method void java.util.zip.ZipFile.close()
        File fl = new File(tempFileName);
        ZipFile zf = new ZipFile(fl);
        InputStream is1 = zf.getInputStream(zf.getEntry("File1.txt"));
        InputStream is2 = zf.getInputStream(zf.getEntry("File2.txt"));
        is1.read();
        is2.read();
        zf.close();
        try {
            is1.read();
            TestCase.fail("IOException expected");
        } catch (IOException ee) {
            // expected
        }
        try {
            is2.read();
            TestCase.fail("IOException expected");
        } catch (IOException ee) {
            // expected
        }
    }

    /**
     * java.util.zip.ZipFile#entries()
     */
    public void test_entries() throws Exception {
        // Test for method java.util.Enumeration java.util.zip.ZipFile.entries()
        Enumeration<? extends ZipEntry> enumer = zfile.entries();
        int c = 0;
        while (enumer.hasMoreElements()) {
            ++c;
            enumer.nextElement();
        } 
        TestCase.assertTrue(("Incorrect number of entries returned: " + c), (c == 6));
        Enumeration<? extends ZipEntry> enumeration = zfile.entries();
        zfile.close();
        try {
            enumeration.nextElement();
            TestCase.fail("did not detect closed file");
        } catch (IllegalStateException expected) {
        }
        try {
            enumeration.hasMoreElements();
            TestCase.fail("did not detect closed file");
        } catch (IllegalStateException expected) {
        }
        try {
            zfile.entries();
            TestCase.fail("did not detect closed file");
        } catch (IllegalStateException expected) {
        }
    }

    /**
     * java.util.zip.ZipFile#getEntry(java.lang.String)
     */
    public void test_getEntryLjava_lang_String() throws IOException {
        // Test for method java.util.zip.ZipEntry
        // java.util.zip.ZipFile.getEntry(java.lang.String)
        ZipEntry zentry = zfile.getEntry("File1.txt");
        TestCase.assertNotNull("Could not obtain ZipEntry", zentry);
        int r;
        InputStream in;
        zentry = zfile.getEntry("testdir1/File1.txt");
        TestCase.assertNotNull("Could not obtain ZipEntry: testdir1/File1.txt", zentry);
        zentry = zfile.getEntry("testdir1/");
        TestCase.assertNotNull("Could not obtain ZipEntry: testdir1/", zentry);
        in = zfile.getInputStream(zentry);
        TestCase.assertNotNull("testdir1/ should not have null input stream", in);
        r = in.read();
        in.close();
        TestCase.assertEquals("testdir1/ should not contain data", (-1), r);
        zentry = zfile.getEntry("testdir1/testdir1");
        TestCase.assertNotNull("Could not obtain ZipEntry: testdir1/testdir1", zentry);
        in = zfile.getInputStream(zentry);
        byte[] buf = new byte[256];
        r = in.read(buf);
        in.close();
        TestCase.assertEquals("incorrect contents", "This is also text", new String(buf, 0, r));
    }

    public void test_getEntryLjava_lang_String_AndroidOnly() throws IOException {
        ZipEntry zentry = zfile.getEntry("File1.txt");
        TestCase.assertNotNull("Could not obtain ZipEntry", zentry);
        int r;
        InputStream in;
        zentry = zfile.getEntry("testdir1");
        TestCase.assertNotNull("Must be able to obtain ZipEntry: testdir1", zentry);
        in = zfile.getInputStream(zentry);
        /* Android delivers empty InputStream, RI no InputStream at all. The
        spec doesn't clarify this, so we need to deal with both situations.
         */
        int data = -1;
        if (in != null) {
            data = in.read();
            in.close();
        }
        TestCase.assertEquals("Must not be able to read directory data", (-1), data);
    }

    public void test_getEntryLjava_lang_String_Ex() throws IOException {
        ZipEntry zentry = zfile.getEntry("File1.txt");
        TestCase.assertNotNull("Could not obtain ZipEntry", zentry);
        zfile.close();
        try {
            zfile.getEntry("File2.txt");
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException ee) {
        }
    }

    /**
     *
     *
     * @throws IOException
    java.util.zip.ZipFile#getInputStream(java.util.zip.ZipEntry)
     * 		
     */
    public void test_getInputStreamLjava_util_zip_ZipEntry() throws IOException {
        // Test for method java.io.InputStream
        // java.util.zip.ZipFile.getInputStream(java.util.zip.ZipEntry)
        ZipEntry zentry = null;
        InputStream is = null;
        try {
            zentry = zfile.getEntry("File1.txt");
            is = zfile.getInputStream(zentry);
            byte[] rbuf = new byte[1000];
            int r;
            is.read(rbuf, 0, (r = ((int) (zentry.getSize()))));
            TestCase.assertEquals("getInputStream read incorrect data", "This is text", new String(rbuf, 0, r));
        } catch (IOException e) {
            TestCase.fail("IOException during getInputStream");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                TestCase.fail("Failed to close input stream");
            }
        }
        zentry = zfile.getEntry("File2.txt");
        zfile.close();
        try {
            is = zfile.getInputStream(zentry);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException ee) {
            // expected
        }
        // ZipException can not be checked. Stream object returned or null.
    }

    /**
     * java.util.zip.ZipFile#getName()
     */
    public void test_getName() {
        // Test for method java.lang.String java.util.zip.ZipFile.getName()
        TestCase.assertTrue(("Returned incorrect name: " + (zfile.getName())), zfile.getName().equals(tempFileName));
    }

    /**
     *
     *
     * @throws IOException
    java.util.zip.ZipFile#size()
     * 		
     */
    public void test_size() throws IOException {
        TestCase.assertEquals(6, zfile.size());
        zfile.close();
        try {
            zfile.size();
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }

    /**
     * java.io.InputStream#reset()
     */
    public void test_reset() throws IOException {
        // read an uncompressed entry
        ZipEntry zentry = zfile.getEntry("File1.txt");
        InputStream is = zfile.getInputStream(zentry);
        byte[] rbuf1 = new byte[6];
        byte[] rbuf2 = new byte[6];
        int r1;
        int r2;
        r1 = is.read(rbuf1);
        TestCase.assertEquals(rbuf1.length, r1);
        r2 = is.read(rbuf2);
        TestCase.assertEquals(rbuf2.length, r2);
        try {
            is.reset();
            TestCase.fail();
        } catch (IOException expected) {
        }
        is.close();
        // read a compressed entry
        byte[] rbuf3 = new byte[4185];
        ZipEntry zentry2 = zfile.getEntry("File3.txt");
        is = zfile.getInputStream(zentry2);
        r1 = is.read(rbuf3);
        TestCase.assertEquals(4183, r1);
        try {
            is.reset();
            TestCase.fail();
        } catch (IOException expected) {
        }
        is.close();
        is = zfile.getInputStream(zentry2);
        r1 = is.read(rbuf3, 0, 3000);
        TestCase.assertEquals(3000, r1);
        try {
            is.reset();
            TestCase.fail();
        } catch (IOException expected) {
        }
        is.close();
    }

    /**
     * java.io.InputStream#reset()
     */
    public void test_reset_subtest0() throws IOException {
        // read an uncompressed entry
        ZipEntry zentry = zfile.getEntry("File1.txt");
        InputStream is = zfile.getInputStream(zentry);
        byte[] rbuf1 = new byte[12];
        byte[] rbuf2 = new byte[12];
        int r = is.read(rbuf1, 0, 4);
        TestCase.assertEquals(4, r);
        is.mark(0);
        r = is.read(rbuf1);
        TestCase.assertEquals(8, r);
        TestCase.assertEquals((-1), is.read());
        try {
            is.reset();
            TestCase.fail();
        } catch (IOException expected) {
        }
        is.close();
        // read a compressed entry
        byte[] rbuf3 = new byte[4185];
        ZipEntry zentry2 = zfile.getEntry("File3.txt");
        is = zfile.getInputStream(zentry2);
        r = is.read(rbuf3, 0, 3000);
        TestCase.assertEquals(3000, r);
        is.mark(0);
        r = is.read(rbuf3);
        TestCase.assertEquals(1183, r);
        TestCase.assertEquals((-1), is.read());
        try {
            is.reset();
            TestCase.fail();
        } catch (IOException expected) {
        }
        is.close();
    }
}

