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
package org.apache.hadoop.hbase.io.hfile;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing writing a version 3 {@link HFile}.
 */
@RunWith(Parameterized.class)
@Category({ IOTests.class, SmallTests.class })
public class TestHFileWriterV3 {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFileWriterV3.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileWriterV3.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Configuration conf;

    private FileSystem fs;

    private boolean useTags;

    public TestHFileWriterV3(boolean useTags) {
        this.useTags = useTags;
    }

    @Test
    public void testHFileFormatV3() throws IOException {
        testHFileFormatV3Internals(useTags);
    }

    @Test
    public void testMidKeyInHFile() throws IOException {
        testMidKeyInHFileInternals(useTags);
    }
}
