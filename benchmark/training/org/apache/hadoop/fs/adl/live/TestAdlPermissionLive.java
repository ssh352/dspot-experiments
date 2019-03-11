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
package org.apache.hadoop.fs.adl.live;


import CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
import java.io.IOException;
import java.util.UUID;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.adl.common.Parallelized;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test ACL permission on file/folder on Adl file system.
 */
@RunWith(Parallelized.class)
public class TestAdlPermissionLive {
    private static Path testRoot = new Path("/test");

    private FsPermission permission;

    private Path path;

    private FileSystem adlStore;

    public TestAdlPermissionLive(FsPermission testPermission) {
        permission = testPermission;
    }

    @Test
    public void testFilePermission() throws IOException {
        path = new Path(TestAdlPermissionLive.testRoot, UUID.randomUUID().toString());
        adlStore.getConf().set(FS_PERMISSIONS_UMASK_KEY, "000");
        adlStore.mkdirs(path.getParent(), new FsPermission(FsAction.ALL, FsAction.WRITE, FsAction.NONE));
        adlStore.removeDefaultAcl(path.getParent());
        adlStore.create(path, permission, true, 1024, ((short) (1)), 1023, null);
        FileStatus status = adlStore.getFileStatus(path);
        Assert.assertEquals(permission, status.getPermission());
    }

    @Test
    public void testFolderPermission() throws IOException {
        path = new Path(TestAdlPermissionLive.testRoot, UUID.randomUUID().toString());
        adlStore.getConf().set(FS_PERMISSIONS_UMASK_KEY, "000");
        adlStore.mkdirs(path.getParent(), new FsPermission(FsAction.ALL, FsAction.WRITE, FsAction.NONE));
        adlStore.removeDefaultAcl(path.getParent());
        adlStore.mkdirs(path, permission);
        FileStatus status = adlStore.getFileStatus(path);
        Assert.assertEquals(permission, status.getPermission());
    }
}

