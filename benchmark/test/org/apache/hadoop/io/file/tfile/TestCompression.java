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
package org.apache.hadoop.io.file.tfile;


import Compression.Algorithm.CONF_LZO_CLASS;
import Compression.Algorithm.LZO;
import Compression.Algorithm.conf;
import java.io.IOException;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestCompression {
    /**
     * Regression test for HADOOP-11418.
     * Verify we can set a LZO codec different from default LZO codec.
     */
    @Test
    public void testConfigureLZOCodec() throws IOException {
        // Dummy codec
        String defaultCodec = "org.apache.hadoop.io.compress.DefaultCodec";
        conf.set(CONF_LZO_CLASS, defaultCodec);
        Assert.assertEquals(defaultCodec, LZO.getCodec().getClass().getName());
    }

    @Test
    public void testMisconfiguredLZOCodec() throws Exception {
        // Dummy codec
        String defaultCodec = "org.apache.hadoop.io.compress.InvalidLzoCodec";
        conf.set(CONF_LZO_CLASS, defaultCodec);
        IOException ioEx = LambdaTestUtils.intercept(IOException.class, defaultCodec, () -> LZO.getCodec());
        if (!((ioEx.getCause()) instanceof ClassNotFoundException)) {
            throw ioEx;
        }
    }
}

