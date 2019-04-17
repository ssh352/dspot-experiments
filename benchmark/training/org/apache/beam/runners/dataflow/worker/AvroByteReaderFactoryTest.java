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
package org.apache.beam.runners.dataflow.worker;


import GlobalWindow.Coder.INSTANCE;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.util.WindowedValue;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link AvroByteReaderFactory}.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("rawtypes")
public class AvroByteReaderFactoryTest {
    private final String pathToAvroFile = "/path/to/file.avro";

    @Test
    public void testCreatePlainAvroByteReader() throws Exception {
        Coder<?> coder = WindowedValue.getFullCoder(BigEndianIntegerCoder.of(), INSTANCE);
        NativeReader<?> reader = runTestCreateAvroReader(pathToAvroFile, null, null, /* sdkComponents= */
        CloudObjects.asCloudObject(coder, null));
        Assert.assertThat(reader, new IsInstanceOf(AvroByteReader.class));
        AvroByteReader avroReader = ((AvroByteReader) (reader));
        Assert.assertEquals(pathToAvroFile, avroReader.avroSource.getFileOrPatternSpec());
        Assert.assertEquals(0L, avroReader.startPosition);
        Assert.assertEquals(Long.MAX_VALUE, avroReader.endPosition);
        Assert.assertEquals(coder, avroReader.coder);
    }

    @Test
    public void testCreateRichAvroByteReader() throws Exception {
        Coder<?> coder = WindowedValue.getFullCoder(BigEndianIntegerCoder.of(), INSTANCE);
        NativeReader<?> reader = runTestCreateAvroReader(pathToAvroFile, 200L, 500L, /* sdkComponents= */
        CloudObjects.asCloudObject(coder, null));
        Assert.assertThat(reader, new IsInstanceOf(AvroByteReader.class));
        AvroByteReader avroReader = ((AvroByteReader) (reader));
        Assert.assertEquals(pathToAvroFile, avroReader.avroSource.getFileOrPatternSpec());
        Assert.assertEquals(200L, avroReader.startPosition);
        Assert.assertEquals(500L, avroReader.endPosition);
        Assert.assertEquals(coder, avroReader.coder);
    }
}
