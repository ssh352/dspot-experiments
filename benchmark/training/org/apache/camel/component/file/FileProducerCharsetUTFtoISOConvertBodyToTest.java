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
package org.apache.camel.component.file;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class FileProducerCharsetUTFtoISOConvertBodyToTest extends ContextTestSupport {
    private byte[] utf;

    private byte[] iso;

    @Test
    public void testFileProducerCharsetUTFtoISOConvertBodyTo() throws Exception {
        oneExchangeDone.matchesMockWaitTime();
        File file = new File("target/data/charset/output.txt");
        Assert.assertTrue("File should exist", file.exists());
        InputStream fis = Files.newInputStream(Paths.get(file.getAbsolutePath()));
        byte[] buffer = new byte[100];
        int len = fis.read(buffer);
        Assert.assertTrue(("Should read data: " + len), (len != (-1)));
        byte[] data = new byte[len];
        System.arraycopy(buffer, 0, data, 0, len);
        fis.close();
        for (byte b : data) {
            log.info("loaded byte: {}", b);
        }
        // data should be in iso, where the danish ae is -26
        Assert.assertEquals(4, data.length);
        Assert.assertEquals(65, data[0]);
        Assert.assertEquals(66, data[1]);
        Assert.assertEquals(67, data[2]);
        Assert.assertEquals((-26), data[3]);
    }
}
