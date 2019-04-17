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
package org.apache.flink.runtime.fs.hdfs;


import java.io.IOException;
import java.net.URI;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that validate the behavior of the Hadoop File System Factory.
 */
public class HadoopFsFactoryTest extends TestLogger {
    @Test
    public void testCreateHadoopFsWithoutConfig() throws Exception {
        final URI uri = URI.create("hdfs://localhost:12345/");
        HadoopFsFactory factory = new HadoopFsFactory();
        FileSystem fs = factory.create(uri);
        Assert.assertEquals(uri.getScheme(), fs.getUri().getScheme());
        Assert.assertEquals(uri.getAuthority(), fs.getUri().getAuthority());
        Assert.assertEquals(uri.getPort(), fs.getUri().getPort());
    }

    @Test
    public void testCreateHadoopFsWithMissingAuthority() throws Exception {
        final URI uri = URI.create("hdfs:///my/path");
        HadoopFsFactory factory = new HadoopFsFactory();
        try {
            factory.create(uri);
            Assert.fail("should have failed with an exception");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("authority"));
        }
    }
}
