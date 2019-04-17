/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor.aggregate.zipfile;


import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.IOHelper;
import org.junit.Test;


public class ZipAggregationStrategyTest extends CamelTestSupport {
    private static final int EXPECTED_NO_FILES = 3;

    @Test
    public void testSplitter() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:aggregateToZipEntry");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived("foo", "bar");
        assertMockEndpointsSatisfied();
        Thread.sleep(500);
        File[] files = new File("target/out").listFiles();
        assertTrue((files != null));
        assertTrue("Should be a file in target/out directory", ((files.length) > 0));
        File resultFile = files[0];
        ZipInputStream zin = new ZipInputStream(new FileInputStream(resultFile));
        try {
            int fileCount = 0;
            for (ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()) {
                fileCount = fileCount + 1;
            }
            assertEquals((("Zip file should contains " + (ZipAggregationStrategyTest.EXPECTED_NO_FILES)) + " files"), ZipAggregationStrategyTest.EXPECTED_NO_FILES, fileCount);
        } finally {
            IOHelper.close(zin);
        }
    }
}
