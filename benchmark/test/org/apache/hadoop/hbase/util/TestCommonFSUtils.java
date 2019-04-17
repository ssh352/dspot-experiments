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
package org.apache.hadoop.hbase.util;


import CommonFSUtils.HBASE_WAL_DIR;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test {@link CommonFSUtils}.
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestCommonFSUtils {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCommonFSUtils.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCommonFSUtils.class);

    private HBaseCommonTestingUtility htu;

    private Configuration conf;

    /**
     * Test path compare and prefix checking.
     */
    @Test
    public void testMatchingTail() throws IOException {
        Path rootdir = htu.getDataTestDir();
        final FileSystem fs = rootdir.getFileSystem(conf);
        Assert.assertTrue(((rootdir.depth()) > 1));
        Path partPath = new Path("a", "b");
        Path fullPath = new Path(rootdir, partPath);
        Path fullyQualifiedPath = fs.makeQualified(fullPath);
        Assert.assertFalse(CommonFSUtils.isMatchingTail(fullPath, partPath));
        Assert.assertFalse(CommonFSUtils.isMatchingTail(fullPath, partPath.toString()));
        Assert.assertTrue(CommonFSUtils.isStartingWithPath(rootdir, fullPath.toString()));
        Assert.assertTrue(CommonFSUtils.isStartingWithPath(fullyQualifiedPath, fullPath.toString()));
        Assert.assertFalse(CommonFSUtils.isStartingWithPath(rootdir, partPath.toString()));
        Assert.assertFalse(CommonFSUtils.isMatchingTail(fullyQualifiedPath, partPath));
        Assert.assertTrue(CommonFSUtils.isMatchingTail(fullyQualifiedPath, fullPath));
        Assert.assertTrue(CommonFSUtils.isMatchingTail(fullyQualifiedPath, fullPath.toString()));
        Assert.assertTrue(CommonFSUtils.isMatchingTail(fullyQualifiedPath, fs.makeQualified(fullPath)));
        Assert.assertTrue(CommonFSUtils.isStartingWithPath(rootdir, fullyQualifiedPath.toString()));
        Assert.assertFalse(CommonFSUtils.isMatchingTail(fullPath, new Path("x")));
        Assert.assertFalse(CommonFSUtils.isMatchingTail(new Path("x"), fullPath));
    }

    @Test
    public void testSetWALRootDir() throws Exception {
        Path p = new Path("file:///hbase/root");
        CommonFSUtils.setWALRootDir(conf, p);
        Assert.assertEquals(p.toString(), conf.get(HBASE_WAL_DIR));
    }

    @Test
    public void testGetWALRootDir() throws IOException {
        Path root = new Path("file:///hbase/root");
        Path walRoot = new Path("file:///hbase/logroot");
        CommonFSUtils.setRootDir(conf, root);
        Assert.assertEquals(root, CommonFSUtils.getRootDir(conf));
        Assert.assertEquals(root, CommonFSUtils.getWALRootDir(conf));
        CommonFSUtils.setWALRootDir(conf, walRoot);
        Assert.assertEquals(walRoot, CommonFSUtils.getWALRootDir(conf));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetWALRootDirIllegalWALDir() throws IOException {
        Path root = new Path("file:///hbase/root");
        Path invalidWALDir = new Path("file:///hbase/root/logroot");
        CommonFSUtils.setRootDir(conf, root);
        CommonFSUtils.setWALRootDir(conf, invalidWALDir);
        CommonFSUtils.getWALRootDir(conf);
    }

    @Test
    public void testRemoveWALRootPath() throws Exception {
        CommonFSUtils.setRootDir(conf, new Path("file:///user/hbase"));
        Path testFile = new Path(CommonFSUtils.getRootDir(conf), "test/testfile");
        Path tmpFile = new Path("file:///test/testfile");
        Assert.assertEquals("test/testfile", CommonFSUtils.removeWALRootPath(testFile, conf));
        Assert.assertEquals(tmpFile.toString(), CommonFSUtils.removeWALRootPath(tmpFile, conf));
        CommonFSUtils.setWALRootDir(conf, new Path("file:///user/hbaseLogDir"));
        Assert.assertEquals(testFile.toString(), CommonFSUtils.removeWALRootPath(testFile, conf));
        Path logFile = new Path(CommonFSUtils.getWALRootDir(conf), "test/testlog");
        Assert.assertEquals("test/testlog", CommonFSUtils.removeWALRootPath(logFile, conf));
    }

    @Test(expected = NullPointerException.class)
    public void streamCapabilitiesDoesNotAllowNullStream() {
        CommonFSUtils.hasCapability(null, "hopefully any string");
    }

    private static final boolean STREAM_CAPABILITIES_IS_PRESENT;

    static {
        boolean tmp = false;
        try {
            Class.forName("org.apache.hadoop.fs.StreamCapabilities");
            tmp = true;
            TestCommonFSUtils.LOG.debug("Test thought StreamCapabilities class was present.");
        } catch (ClassNotFoundException exception) {
            TestCommonFSUtils.LOG.debug("Test didn't think StreamCapabilities class was present.");
        } finally {
            STREAM_CAPABILITIES_IS_PRESENT = tmp;
        }
    }

    @Test
    public void checkStreamCapabilitiesOnKnownNoopStream() throws IOException {
        FSDataOutputStream stream = new FSDataOutputStream(new ByteArrayOutputStream(), null);
        Assert.assertNotEquals(("We expect our dummy FSDOS to claim capabilities iff the StreamCapabilities " + "class is not defined."), TestCommonFSUtils.STREAM_CAPABILITIES_IS_PRESENT, CommonFSUtils.hasCapability(stream, "hsync"));
        Assert.assertNotEquals(("We expect our dummy FSDOS to claim capabilities iff the StreamCapabilities " + "class is not defined."), TestCommonFSUtils.STREAM_CAPABILITIES_IS_PRESENT, CommonFSUtils.hasCapability(stream, "hflush"));
        Assert.assertNotEquals(("We expect our dummy FSDOS to claim capabilities iff the StreamCapabilities " + "class is not defined."), TestCommonFSUtils.STREAM_CAPABILITIES_IS_PRESENT, CommonFSUtils.hasCapability(stream, ("a capability that hopefully no filesystem will " + "implement.")));
    }
}
