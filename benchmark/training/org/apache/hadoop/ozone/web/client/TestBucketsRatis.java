/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.web.client;


import java.io.IOException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.RatisTestHelper;
import org.apache.hadoop.ozone.client.protocol.ClientProtocol;
import org.apache.hadoop.ozone.client.rest.OzoneException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * The same as {@link TestBuckets} except that this test is Ratis enabled.
 */
@Ignore("Disabling Ratis tests for pipeline work.")
@RunWith(Parameterized.class)
public class TestBucketsRatis {
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static RatisTestHelper.RatisTestSuite suite;

    private static ClientProtocol client;

    private static OzoneConfiguration conf;

    @Parameterized.Parameter
    @SuppressWarnings("visibilitymodifier")
    public static Class clientProtocol;

    @Test
    public void testCreateBucket() throws Exception {
        TestBuckets.runTestCreateBucket(TestBucketsRatis.client);
    }

    @Test
    public void testAddBucketAcls() throws Exception {
        TestBuckets.runTestAddBucketAcls(TestBucketsRatis.client);
    }

    @Test
    public void testRemoveBucketAcls() throws Exception {
        TestBuckets.runTestRemoveBucketAcls(TestBucketsRatis.client);
    }

    @Test
    public void testDeleteBucket() throws IOException, OzoneException {
        TestBuckets.runTestDeleteBucket(TestBucketsRatis.client);
    }

    @Test
    public void testListBucket() throws Exception {
        TestBuckets.runTestListBucket(TestBucketsRatis.client);
    }
}

