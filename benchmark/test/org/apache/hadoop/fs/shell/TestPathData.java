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
package org.apache.hadoop.fs.shell;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;


public class TestPathData {
    private static final String TEST_ROOT_DIR = GenericTestUtils.getTestDir("testPD").getAbsolutePath();

    protected Configuration conf;

    protected FileSystem fs;

    protected Path testDir;

    @Test(timeout = 30000)
    public void testWithDirStringAndConf() throws Exception {
        String dirString = "d1";
        PathData item = new PathData(dirString, conf);
        checkPathData(dirString, item);
        // properly implementing symlink support in various commands will require
        // trailing slashes to be retained
        dirString = "d1/";
        item = new PathData(dirString, conf);
        checkPathData(dirString, item);
    }

    @Test(timeout = 30000)
    public void testUnqualifiedUriContents() throws Exception {
        String dirString = "d1";
        PathData item = new PathData(dirString, conf);
        PathData[] items = item.getDirectoryContents();
        Assert.assertEquals(TestPathData.sortedString("d1/f1", "d1/f1.1", "d1/f2"), TestPathData.sortedString(items));
    }

    @Test(timeout = 30000)
    public void testQualifiedUriContents() throws Exception {
        String dirString = fs.makeQualified(new Path("d1")).toString();
        PathData item = new PathData(dirString, conf);
        PathData[] items = item.getDirectoryContents();
        Assert.assertEquals(TestPathData.sortedString((dirString + "/f1"), (dirString + "/f1.1"), (dirString + "/f2")), TestPathData.sortedString(items));
    }

    @Test(timeout = 30000)
    public void testCwdContents() throws Exception {
        String dirString = Path.CUR_DIR;
        PathData item = new PathData(dirString, conf);
        PathData[] items = item.getDirectoryContents();
        Assert.assertEquals(TestPathData.sortedString("d1", "d2"), TestPathData.sortedString(items));
    }

    @Test(timeout = 30000)
    public void testToFile() throws Exception {
        PathData item = new PathData(".", conf);
        Assert.assertEquals(new File(testDir.toString()), item.toFile());
        item = new PathData("d1/f1", conf);
        Assert.assertEquals(new File(((testDir) + "/d1/f1")), item.toFile());
        item = new PathData(((testDir) + "/d1/f1"), conf);
        Assert.assertEquals(new File(((testDir) + "/d1/f1")), item.toFile());
    }

    @Test(timeout = 5000)
    public void testToFileRawWindowsPaths() throws Exception {
        PlatformAssumptions.assumeWindows();
        // Can we handle raw Windows paths? The files need not exist for
        // these tests to succeed.
        String[] winPaths = new String[]{ "n:\\", "N:\\", "N:\\foo", "N:\\foo\\bar", "N:/", "N:/foo", "N:/foo/bar" };
        PathData item;
        for (String path : winPaths) {
            item = new PathData(path, conf);
            Assert.assertEquals(new File(path), item.toFile());
        }
        item = new PathData("foo\\bar", conf);
        Assert.assertEquals(new File(((testDir) + "\\foo\\bar")), item.toFile());
    }

    @Test(timeout = 5000)
    public void testInvalidWindowsPath() throws Exception {
        PlatformAssumptions.assumeWindows();
        // Verify that the following invalid paths are rejected.
        String[] winPaths = new String[]{ "N:\\foo/bar" };
        for (String path : winPaths) {
            try {
                PathData item = new PathData(path, conf);
                Assert.fail(("Did not throw for invalid path " + path));
            } catch (IOException ioe) {
            }
        }
    }

    @Test(timeout = 30000)
    public void testAbsoluteGlob() throws Exception {
        PathData[] items = PathData.expandAsGlob(((testDir) + "/d1/f1*"), conf);
        Assert.assertEquals(TestPathData.sortedString(((testDir) + "/d1/f1"), ((testDir) + "/d1/f1.1")), TestPathData.sortedString(items));
        String absolutePathNoDriveLetter = (testDir) + "/d1/f1";
        if (Shell.WINDOWS) {
            // testDir is an absolute path with a drive letter on Windows, i.e.
            // c:/some/path
            // and for the test we want something like the following
            // /some/path
            absolutePathNoDriveLetter = absolutePathNoDriveLetter.substring(2);
        }
        items = PathData.expandAsGlob(absolutePathNoDriveLetter, conf);
        Assert.assertEquals(TestPathData.sortedString(absolutePathNoDriveLetter), TestPathData.sortedString(items));
        items = PathData.expandAsGlob(".", conf);
        Assert.assertEquals(TestPathData.sortedString("."), TestPathData.sortedString(items));
    }

    @Test(timeout = 30000)
    public void testRelativeGlob() throws Exception {
        PathData[] items = PathData.expandAsGlob("d1/f1*", conf);
        Assert.assertEquals(TestPathData.sortedString("d1/f1", "d1/f1.1"), TestPathData.sortedString(items));
    }

    @Test(timeout = 30000)
    public void testRelativeGlobBack() throws Exception {
        fs.setWorkingDirectory(new Path("d1"));
        PathData[] items = PathData.expandAsGlob("../d2/*", conf);
        Assert.assertEquals(TestPathData.sortedString("../d2/f3"), TestPathData.sortedString(items));
    }

    @Test
    public void testGlobThrowsExceptionForUnreadableDir() throws Exception {
        Path obscuredDir = new Path("foo");
        Path subDir = new Path(obscuredDir, "bar");// so foo is non-empty

        fs.mkdirs(subDir);
        fs.setPermission(obscuredDir, new FsPermission(((short) (0))));// no access

        try {
            PathData.expandAsGlob("foo/*", conf);
            Assert.fail("Should throw IOException");
        } catch (IOException ioe) {
            // expected
        } finally {
            // make sure the test directory can be deleted
            fs.setPermission(obscuredDir, new FsPermission(((short) (493))));// default

        }
    }

    @Test(timeout = 30000)
    public void testWithStringAndConfForBuggyPath() throws Exception {
        String dirString = "file:///tmp";
        Path tmpDir = new Path(dirString);
        PathData item = new PathData(dirString, conf);
        // this may fail some day if Path is fixed to not crunch the uri
        // if the authority is null, however we need to test that the PathData
        // toString() returns the given string, while Path toString() does
        // the crunching
        Assert.assertEquals("file:/tmp", tmpDir.toString());
        checkPathData(dirString, item);
    }
}

