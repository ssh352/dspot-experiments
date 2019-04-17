/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.swift;


import SwiftProtocolConstants.SWIFT_PARTITION_SIZE;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.swift.snative.SwiftNativeFileSystem;
import org.apache.hadoop.fs.swift.util.SwiftTestUtils;
import org.apache.hadoop.fs.swift.util.SwiftUtils;
import org.apache.hadoop.io.IOUtils;
import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;


/**
 * Test partitioned uploads.
 * This is done by forcing a very small partition size and verifying that it
 * is picked up.
 */
public class TestSwiftFileSystemPartitionedUploads extends SwiftFileSystemBaseTest {
    public static final String WRONG_PARTITION_COUNT = "wrong number of partitions written into ";

    public static final int PART_SIZE = 1;

    public static final int PART_SIZE_BYTES = (TestSwiftFileSystemPartitionedUploads.PART_SIZE) * 1024;

    public static final int BLOCK_SIZE = 1024;

    private URI uri;

    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testPartitionPropertyPropagatesToConf() throws Throwable {
        Assert.assertEquals(1, getConf().getInt(SWIFT_PARTITION_SIZE, 0));
    }

    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testPartionPropertyPropagatesToStore() throws Throwable {
        Assert.assertEquals(1, fs.getStore().getPartsizeKB());
    }

    /**
     * tests functionality for big files ( > 5Gb) upload
     */
    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testFilePartUpload() throws Throwable {
        final Path path = new Path("/test/testFilePartUpload");
        int len = 8192;
        final byte[] src = SwiftTestUtils.dataset(len, 32, 144);
        FSDataOutputStream out = fs.create(path, false, getBufferSize(), ((short) (1)), TestSwiftFileSystemPartitionedUploads.BLOCK_SIZE);
        try {
            int totalPartitionsToWrite = len / (TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES);
            assertPartitionsWritten("Startup", out, 0);
            // write 2048
            int firstWriteLen = 2048;
            out.write(src, 0, firstWriteLen);
            // assert
            long expected = getExpectedPartitionsWritten(firstWriteLen, TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES, false);
            SwiftUtils.debug(SwiftFileSystemBaseTest.LOG, "First write: predict %d partitions written", expected);
            assertPartitionsWritten("First write completed", out, expected);
            // write the rest
            int remainder = len - firstWriteLen;
            SwiftUtils.debug(SwiftFileSystemBaseTest.LOG, "remainder: writing: %d bytes", remainder);
            out.write(src, firstWriteLen, remainder);
            expected = getExpectedPartitionsWritten(len, TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES, false);
            assertPartitionsWritten("Remaining data", out, expected);
            out.close();
            expected = getExpectedPartitionsWritten(len, TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES, true);
            assertPartitionsWritten("Stream closed", out, expected);
            Header[] headers = fs.getStore().getObjectHeaders(path, true);
            for (Header header : headers) {
                SwiftFileSystemBaseTest.LOG.info(header.toString());
            }
            byte[] dest = readDataset(fs, path, len);
            SwiftFileSystemBaseTest.LOG.info(((("Read dataset from " + path) + ": data length =") + len));
            // compare data
            SwiftTestUtils.compareByteArrays(src, dest, len);
            FileStatus status;
            final Path qualifiedPath = fs.makeQualified(path);
            status = fs.getFileStatus(qualifiedPath);
            // now see what block location info comes back.
            // This will vary depending on the Swift version, so the results
            // aren't checked -merely that the test actually worked
            BlockLocation[] locations = fs.getFileBlockLocations(status, 0, len);
            Assert.assertNotNull("Null getFileBlockLocations()", locations);
            Assert.assertTrue("empty array returned for getFileBlockLocations()", ((locations.length) > 0));
            // last bit of test -which seems to play up on partitions, which we download
            // to a skip
            try {
                validatePathLen(path, len);
            } catch (AssertionError e) {
                // downgrade to a skip
                throw new AssumptionViolatedException(e, null);
            }
        } finally {
            IOUtils.closeStream(out);
        }
    }

    /**
     * tests functionality for big files ( > 5Gb) upload
     */
    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testFilePartUploadNoLengthCheck() throws IOException, URISyntaxException {
        final Path path = new Path("/test/testFilePartUploadLengthCheck");
        int len = 8192;
        final byte[] src = SwiftTestUtils.dataset(len, 32, 144);
        FSDataOutputStream out = fs.create(path, false, getBufferSize(), ((short) (1)), TestSwiftFileSystemPartitionedUploads.BLOCK_SIZE);
        try {
            int totalPartitionsToWrite = len / (TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES);
            assertPartitionsWritten("Startup", out, 0);
            // write 2048
            int firstWriteLen = 2048;
            out.write(src, 0, firstWriteLen);
            // assert
            long expected = getExpectedPartitionsWritten(firstWriteLen, TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES, false);
            SwiftUtils.debug(SwiftFileSystemBaseTest.LOG, "First write: predict %d partitions written", expected);
            assertPartitionsWritten("First write completed", out, expected);
            // write the rest
            int remainder = len - firstWriteLen;
            SwiftUtils.debug(SwiftFileSystemBaseTest.LOG, "remainder: writing: %d bytes", remainder);
            out.write(src, firstWriteLen, remainder);
            expected = getExpectedPartitionsWritten(len, TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES, false);
            assertPartitionsWritten("Remaining data", out, expected);
            out.close();
            expected = getExpectedPartitionsWritten(len, TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES, true);
            assertPartitionsWritten("Stream closed", out, expected);
            Header[] headers = fs.getStore().getObjectHeaders(path, true);
            for (Header header : headers) {
                SwiftFileSystemBaseTest.LOG.info(header.toString());
            }
            byte[] dest = readDataset(fs, path, len);
            SwiftFileSystemBaseTest.LOG.info(((("Read dataset from " + path) + ": data length =") + len));
            // compare data
            SwiftTestUtils.compareByteArrays(src, dest, len);
            FileStatus status = fs.getFileStatus(path);
            // now see what block location info comes back.
            // This will vary depending on the Swift version, so the results
            // aren't checked -merely that the test actually worked
            BlockLocation[] locations = fs.getFileBlockLocations(status, 0, len);
            Assert.assertNotNull("Null getFileBlockLocations()", locations);
            Assert.assertTrue("empty array returned for getFileBlockLocations()", ((locations.length) > 0));
        } finally {
            IOUtils.closeStream(out);
        }
    }

    /**
     * Test sticks up a very large partitioned file and verifies that
     * it comes back unchanged.
     *
     * @throws Throwable
     * 		
     */
    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testManyPartitionedFile() throws Throwable {
        final Path path = new Path("/test/testManyPartitionedFile");
        int len = (TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES) * 15;
        final byte[] src = SwiftTestUtils.dataset(len, 32, 144);
        FSDataOutputStream out = fs.create(path, false, getBufferSize(), ((short) (1)), TestSwiftFileSystemPartitionedUploads.BLOCK_SIZE);
        out.write(src, 0, src.length);
        int expected = getExpectedPartitionsWritten(len, TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES, true);
        out.close();
        assertPartitionsWritten("write completed", out, expected);
        Assert.assertEquals("too few bytes written", len, SwiftNativeFileSystem.getBytesWritten(out));
        Assert.assertEquals("too few bytes uploaded", len, SwiftNativeFileSystem.getBytesUploaded(out));
        // now we verify that the data comes back. If it
        // doesn't, it means that the ordering of the partitions
        // isn't right
        byte[] dest = readDataset(fs, path, len);
        // compare data
        SwiftTestUtils.compareByteArrays(src, dest, len);
        // finally, check the data
        FileStatus[] stats = fs.listStatus(path);
        Assert.assertEquals(("wrong entry count in " + (SwiftTestUtils.dumpStats(path.toString(), stats))), expected, stats.length);
    }

    /**
     * Test that when a partitioned file is overwritten by a smaller one,
     * all the old partitioned files go away
     *
     * @throws Throwable
     * 		
     */
    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testOverwritePartitionedFile() throws Throwable {
        final Path path = new Path("/test/testOverwritePartitionedFile");
        final int len1 = 8192;
        final byte[] src1 = SwiftTestUtils.dataset(len1, 'A', 'Z');
        FSDataOutputStream out = fs.create(path, false, getBufferSize(), ((short) (1)), 1024);
        out.write(src1, 0, len1);
        out.close();
        long expected = getExpectedPartitionsWritten(len1, TestSwiftFileSystemPartitionedUploads.PART_SIZE_BYTES, false);
        assertPartitionsWritten("initial upload", out, expected);
        assertExists("Exists", path);
        FileStatus status = fs.getFileStatus(path);
        Assert.assertEquals("Length", len1, status.getLen());
        // now write a shorter file with a different dataset
        final int len2 = 4095;
        final byte[] src2 = SwiftTestUtils.dataset(len2, 'a', 'z');
        out = fs.create(path, true, getBufferSize(), ((short) (1)), 1024);
        out.write(src2, 0, len2);
        out.close();
        status = fs.getFileStatus(path);
        Assert.assertEquals("Length", len2, status.getLen());
        byte[] dest = readDataset(fs, path, len2);
        // compare data
        SwiftTestUtils.compareByteArrays(src2, dest, len2);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testDeleteSmallPartitionedFile() throws Throwable {
        final Path path = new Path("/test/testDeleteSmallPartitionedFile");
        final int len1 = 1024;
        final byte[] src1 = SwiftTestUtils.dataset(len1, 'A', 'Z');
        SwiftTestUtils.writeDataset(fs, path, src1, len1, 1024, false);
        assertExists("Exists", path);
        Path part_0001 = new Path(path, SwiftUtils.partitionFilenameFromNumber(1));
        Path part_0002 = new Path(path, SwiftUtils.partitionFilenameFromNumber(2));
        String ls = SwiftTestUtils.ls(fs, path);
        assertExists(("Partition 0001 Exists in " + ls), part_0001);
        assertPathDoesNotExist(("partition 0002 found under " + ls), part_0002);
        assertExists(("Partition 0002 Exists in " + ls), part_0001);
        fs.delete(path, false);
        assertPathDoesNotExist("deleted file still there", path);
        ls = SwiftTestUtils.ls(fs, path);
        assertPathDoesNotExist(("partition 0001 file still under " + ls), part_0001);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testDeletePartitionedFile() throws Throwable {
        final Path path = new Path("/test/testDeletePartitionedFile");
        SwiftTestUtils.writeDataset(fs, path, data, data.length, 1024, false);
        assertExists("Exists", path);
        Path part_0001 = new Path(path, SwiftUtils.partitionFilenameFromNumber(1));
        Path part_0002 = new Path(path, SwiftUtils.partitionFilenameFromNumber(2));
        String ls = SwiftTestUtils.ls(fs, path);
        assertExists(("Partition 0001 Exists in " + ls), part_0001);
        assertExists(("Partition 0002 Exists in " + ls), part_0001);
        fs.delete(path, false);
        assertPathDoesNotExist("deleted file still there", path);
        ls = SwiftTestUtils.ls(fs, path);
        assertPathDoesNotExist(("partition 0001 file still under " + ls), part_0001);
        assertPathDoesNotExist(("partition 0002 file still under " + ls), part_0002);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_BULK_IO_TEST_TIMEOUT)
    public void testRenamePartitionedFile() throws Throwable {
        Path src = new Path("/test/testRenamePartitionedFileSrc");
        int len = data.length;
        SwiftTestUtils.writeDataset(fs, src, data, len, 1024, false);
        assertExists("Exists", src);
        String partOneName = SwiftUtils.partitionFilenameFromNumber(1);
        Path srcPart = new Path(src, partOneName);
        Path dest = new Path("/test/testRenamePartitionedFileDest");
        Path destPart = new Path(src, partOneName);
        assertExists("Partition Exists", srcPart);
        fs.rename(src, dest);
        assertPathExists(fs, "dest file missing", dest);
        FileStatus status = fs.getFileStatus(dest);
        Assert.assertEquals("Length of renamed file is wrong", len, status.getLen());
        byte[] destData = readDataset(fs, dest, len);
        // compare data
        SwiftTestUtils.compareByteArrays(data, destData, len);
        String srcLs = SwiftTestUtils.ls(fs, src);
        String destLs = SwiftTestUtils.ls(fs, dest);
        assertPathDoesNotExist(("deleted file still found in " + srcLs), src);
        assertPathDoesNotExist(("partition file still found in " + srcLs), srcPart);
    }
}
