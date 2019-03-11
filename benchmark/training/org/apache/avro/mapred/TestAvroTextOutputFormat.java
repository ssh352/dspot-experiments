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
package org.apache.avro.mapred;


import Schema.Type.BYTES;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.RecordWriter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestAvroTextOutputFormat {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testAvroTextRecordWriter() throws Exception {
        File file = new File(tmpFolder.getRoot().getPath(), "writer");
        Schema schema = Schema.create(BYTES);
        DatumWriter<ByteBuffer> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema);
        DataFileWriter<ByteBuffer> fileWriter = new DataFileWriter(datumWriter);
        fileWriter.create(schema, file);
        RecordWriter<Object, Object> rw = new AvroTextOutputFormat().new AvroTextRecordWriter(fileWriter, "\t".getBytes(StandardCharsets.UTF_8));
        rw.write(null, null);
        rw.write(null, NullWritable.get());
        rw.write(NullWritable.get(), null);
        rw.write(NullWritable.get(), NullWritable.get());
        rw.write("k1", null);
        rw.write("k2", NullWritable.get());
        rw.write(null, "v1");
        rw.write(NullWritable.get(), "v2");
        rw.write("k3", "v3");
        rw.write(new Text("k4"), new Text("v4"));
        rw.close(null);
        DatumReader<ByteBuffer> reader = new org.apache.avro.generic.GenericDatumReader();
        DataFileReader<ByteBuffer> fileReader = new DataFileReader(file, reader);
        Assert.assertEquals("k1", asString(fileReader.next()));
        Assert.assertEquals("k2", asString(fileReader.next()));
        Assert.assertEquals("v1", asString(fileReader.next()));
        Assert.assertEquals("v2", asString(fileReader.next()));
        Assert.assertEquals("k3\tv3", asString(fileReader.next()));
        Assert.assertEquals("k4\tv4", asString(fileReader.next()));
        Assert.assertFalse("End", fileReader.hasNext());
    }
}

