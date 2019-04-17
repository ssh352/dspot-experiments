/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util.zip;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import junit.framework.TestCase;


public final class ZipFileTest extends TestCase {
    /**
     * Exercise Inflater's ability to refill the zlib's input buffer. As of this
     * writing, this buffer's max size is 64KiB compressed bytes. We'll write a
     * full megabyte of uncompressed data, which should be sufficient to exhaust
     * the buffer. http://b/issue?id=2734751
     */
    public void testInflatingFilesRequiringZipRefill() throws IOException {
        int originalSize = 1024 * 1024;
        byte[] readBuffer = new byte[8192];
        ZipFile zipFile = new ZipFile(createZipFile(1, originalSize));
        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
            ZipEntry zipEntry = e.nextElement();
            TestCase.assertTrue("This test needs >64 KiB of compressed data to exercise Inflater", ((zipEntry.getCompressedSize()) > (64 * 1024)));
            InputStream is = zipFile.getInputStream(zipEntry);
            while ((is.read(readBuffer, 0, readBuffer.length)) != (-1)) {
            } 
            is.close();
        }
        zipFile.close();
    }

    /**
     * Make sure we don't fail silently for duplicate entries.
     * b/8219321
     */
    public void testDuplicateEntries() throws Exception {
        String name1 = "test_file_name1";
        String name2 = "test_file_name2";
        // Create the good zip file.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(baos);
        out.putNextEntry(new ZipEntry(name2));
        out.closeEntry();
        out.putNextEntry(new ZipEntry(name1));
        out.closeEntry();
        out.close();
        // Rewrite one of the filenames.
        byte[] buffer = baos.toByteArray();
        ZipFileTest.replaceBytes(buffer, name2.getBytes(), name1.getBytes());
        // Write the result to a file.
        File badZip = createTemporaryZipFile();
        ZipFileTest.writeBytes(badZip, buffer);
        // Check that we refuse to load the modified file.
        try {
            ZipFile bad = new ZipFile(badZip);
            TestCase.fail();
        } catch (ZipException expected) {
        }
    }

    /**
     * Make sure the size used for stored zip entires is the uncompressed size.
     * b/10227498
     */
    public void testStoredEntrySize() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(baos);
        // Set up a single stored entry.
        String name = "test_file";
        int expectedLength = 5;
        ZipEntry outEntry = new ZipEntry(name);
        byte[] buffer = new byte[expectedLength];
        outEntry.setMethod(ZipEntry.STORED);
        CRC32 crc = new CRC32();
        crc.update(buffer);
        outEntry.setCrc(crc.getValue());
        outEntry.setSize(buffer.length);
        out.putNextEntry(outEntry);
        out.write(buffer);
        out.closeEntry();
        out.close();
        // Write the result to a file.
        byte[] outBuffer = baos.toByteArray();
        File zipFile = createTemporaryZipFile();
        ZipFileTest.writeBytes(zipFile, outBuffer);
        ZipFile zip = new ZipFile(zipFile);
        // Set up the zip entry to have different compressed/uncompressed sizes.
        ZipEntry ze = zip.getEntry(name);
        ze.setCompressedSize((expectedLength - 1));
        // Read the contents of the stream and verify uncompressed size was used.
        InputStream stream = zip.getInputStream(ze);
        int count = 0;
        int read;
        while ((read = stream.read(buffer)) != (-1)) {
            count += read;
        } 
        TestCase.assertEquals(expectedLength, count);
    }

    public void testInflatingStreamsRequiringZipRefill() throws IOException {
        int originalSize = 1024 * 1024;
        byte[] readBuffer = new byte[8192];
        ZipInputStream in = new ZipInputStream(new FileInputStream(createZipFile(1, originalSize)));
        while ((in.getNextEntry()) != null) {
            while ((in.read(readBuffer, 0, readBuffer.length)) != (-1)) {
            } 
        } 
        in.close();
    }

    public void testZipFileWithLotsOfEntries() throws IOException {
        int expectedEntryCount = (64 * 1024) - 1;
        File f = createZipFile(expectedEntryCount, 0);
        ZipFile zipFile = new ZipFile(f);
        int entryCount = 0;
        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
            ZipEntry zipEntry = e.nextElement();
            ++entryCount;
        }
        TestCase.assertEquals(expectedEntryCount, entryCount);
        zipFile.close();
    }

    // http://code.google.com/p/android/issues/detail?id=36187
    public void testZipFileLargerThan2GiB() throws IOException {
        if (false) {
            // TODO: this test requires too much time and too much disk space!
            File f = createZipFile(1024, ((3 * 1024) * 1024));
            ZipFile zipFile = new ZipFile(f);
            int entryCount = 0;
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry zipEntry = e.nextElement();
                ++entryCount;
            }
            TestCase.assertEquals(1024, entryCount);
            zipFile.close();
        }
    }

    public void testZip64Support() throws IOException {
        try {
            createZipFile((64 * 1024), 0);
            TestCase.fail();// Make this test more like testHugeZipFile when we have Zip64 support.

        } catch (ZipException expected) {
        }
    }

    public void testSTORED() throws IOException {
        ZipOutputStream out = createZipOutputStream(createTemporaryZipFile());
        CRC32 crc = new CRC32();
        // Missing CRC, size, and compressed size => failure.
        try {
            ZipEntry ze = new ZipEntry("a");
            ze.setMethod(ZipEntry.STORED);
            out.putNextEntry(ze);
            TestCase.fail();
        } catch (ZipException expected) {
        }
        // Missing CRC and compressed size => failure.
        try {
            ZipEntry ze = new ZipEntry("a");
            ze.setMethod(ZipEntry.STORED);
            ze.setSize(0);
            out.putNextEntry(ze);
            TestCase.fail();
        } catch (ZipException expected) {
        }
        // Missing CRC and size => failure.
        try {
            ZipEntry ze = new ZipEntry("a");
            ze.setMethod(ZipEntry.STORED);
            ze.setSize(0);
            ze.setCompressedSize(0);
            out.putNextEntry(ze);
            TestCase.fail();
        } catch (ZipException expected) {
        }
        // Missing size and compressed size => failure.
        try {
            ZipEntry ze = new ZipEntry("a");
            ze.setMethod(ZipEntry.STORED);
            ze.setCrc(crc.getValue());
            out.putNextEntry(ze);
            TestCase.fail();
        } catch (ZipException expected) {
        }
        // Missing size is copied from compressed size.
        {
            ZipEntry ze = new ZipEntry("okay1");
            ze.setMethod(ZipEntry.STORED);
            ze.setCrc(crc.getValue());
            TestCase.assertEquals((-1), ze.getSize());
            TestCase.assertEquals((-1), ze.getCompressedSize());
            ze.setCompressedSize(0);
            TestCase.assertEquals((-1), ze.getSize());
            TestCase.assertEquals(0, ze.getCompressedSize());
            out.putNextEntry(ze);
            TestCase.assertEquals(0, ze.getSize());
            TestCase.assertEquals(0, ze.getCompressedSize());
        }
        // Missing compressed size is copied from size.
        {
            ZipEntry ze = new ZipEntry("okay2");
            ze.setMethod(ZipEntry.STORED);
            ze.setCrc(crc.getValue());
            TestCase.assertEquals((-1), ze.getSize());
            TestCase.assertEquals((-1), ze.getCompressedSize());
            ze.setSize(0);
            TestCase.assertEquals(0, ze.getSize());
            TestCase.assertEquals((-1), ze.getCompressedSize());
            out.putNextEntry(ze);
            TestCase.assertEquals(0, ze.getSize());
            TestCase.assertEquals(0, ze.getCompressedSize());
        }
        // Mismatched size and compressed size => failure.
        try {
            ZipEntry ze = new ZipEntry("a");
            ze.setMethod(ZipEntry.STORED);
            ze.setCrc(crc.getValue());
            ze.setCompressedSize(1);
            ze.setSize(0);
            out.putNextEntry(ze);
            TestCase.fail();
        } catch (ZipException expected) {
        }
        // Everything present => success.
        ZipEntry ze = new ZipEntry("okay");
        ze.setMethod(ZipEntry.STORED);
        ze.setCrc(crc.getValue());
        ze.setSize(0);
        ze.setCompressedSize(0);
        out.putNextEntry(ze);
        out.close();
    }

    public void testComments() throws Exception {
        String expectedFileComment = "1 \u0666 2";
        String expectedEntryComment = "a \u0666 b";
        File file = createTemporaryZipFile();
        ZipOutputStream out = createZipOutputStream(file);
        // Is file comment length checking done on bytes or characters? (Should be bytes.)
        out.setComment(null);
        out.setComment(makeString(65535, "a"));
        try {
            out.setComment(makeString((65535 + 1), "a"));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            out.setComment(makeString(65535, "\u0666"));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        ZipEntry ze = new ZipEntry("a");
        // Is entry comment length checking done on bytes or characters? (Should be bytes.)
        ze.setComment(null);
        ze.setComment(makeString(65535, "a"));
        try {
            ze.setComment(makeString((65535 + 1), "a"));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ze.setComment(makeString(65535, "\u0666"));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        ze.setComment(expectedEntryComment);
        out.putNextEntry(ze);
        out.closeEntry();
        out.setComment(expectedFileComment);
        out.close();
        ZipFile zipFile = new ZipFile(file);
        TestCase.assertEquals(expectedFileComment, zipFile.getComment());
        TestCase.assertEquals(expectedEntryComment, zipFile.getEntry("a").getComment());
        zipFile.close();
    }

    public void test_getComment_unset() throws Exception {
        File file = createTemporaryZipFile();
        ZipOutputStream out = createZipOutputStream(file);
        ZipEntry ze = new ZipEntry("test entry");
        ze.setComment("per-entry comment");
        out.putNextEntry(ze);
        out.close();
        ZipFile zipFile = new ZipFile(file);
        TestCase.assertEquals(null, zipFile.getComment());
    }

    // https://code.google.com/p/android/issues/detail?id=58465
    public void test_NUL_in_filename() throws Exception {
        File file = createTemporaryZipFile();
        // We allow creation of a ZipEntry whose name contains a NUL byte,
        // mainly because it's not likely to happen by accident and it's useful for testing.
        ZipOutputStream out = createZipOutputStream(file);
        out.putNextEntry(new ZipEntry("hello"));
        out.putNextEntry(new ZipEntry("hello\u0000"));
        out.close();
        // But you can't open a ZIP file containing such an entry, because we reject it
        // when we find it in the central directory.
        try {
            ZipFile zipFile = new ZipFile(file);
            TestCase.fail();
        } catch (ZipException expected) {
        }
    }

    public void testCrc() throws IOException {
        ZipEntry ze = new ZipEntry("test");
        ze.setMethod(ZipEntry.STORED);
        ze.setSize(4);
        // setCrc takes a long, not an int, so -1 isn't a valid CRC32 (because it's 64 bits).
        try {
            ze.setCrc((-1));
        } catch (IllegalArgumentException expected) {
        }
        // You can set the CRC32 to 0xffffffff if you're slightly more careful though...
        ze.setCrc(4294967295L);
        TestCase.assertEquals(4294967295L, ze.getCrc());
        // And it actually works, even though we use -1L to mean "no CRC set"...
        ZipOutputStream out = createZipOutputStream(createTemporaryZipFile());
        out.putNextEntry(ze);
        out.write((-1));
        out.write((-1));
        out.write((-1));
        out.write((-1));
        out.closeEntry();
        out.close();
    }
}
