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
package org.apache.hadoop.hdfs.server.namenode.web.resources;


import java.net.HttpURLConnection;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.log4j.Level;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test WebHDFS files/directories creation to make sure it follows same rules
 * from dfs CLI for specifying files/directories permissions.
 */
public class TestWebHdfsCreatePermissions {
    static final Logger LOG = LoggerFactory.getLogger(TestWebHdfsCreatePermissions.class);

    {
        DFSTestUtil.setNameNodeLogLevel(Level.ALL);
    }

    private MiniDFSCluster cluster;

    @Test
    public void testCreateDirNoPermissions() throws Exception {
        testPermissions(HttpURLConnection.HTTP_OK, "rwxr-xr-x", "/path", "op=MKDIRS");
    }

    @Test
    public void testCreateDir777Permissions() throws Exception {
        testPermissions(HttpURLConnection.HTTP_OK, "rwxrwxrwx", "/test777", "op=MKDIRS&permission=777");
    }

    @Test
    public void testCreateFileNoPermissions() throws Exception {
        testPermissions(HttpURLConnection.HTTP_CREATED, "rw-r--r--", "/test-file", "op=CREATE");
    }

    @Test
    public void testCreateFile666Permissions() throws Exception {
        testPermissions(HttpURLConnection.HTTP_CREATED, "rw-rw-rw-", "/test-file", "op=CREATE&permission=666");
    }
}

