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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.apache.avro.mapred;


import AvroInputFormat.IGNORE_FILES_WITHOUT_EXTENSION_KEY;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestAvroInputFormat {
    @Rule
    public TemporaryFolder DIR = new TemporaryFolder();

    private JobConf conf;

    private FileSystem fs;

    private Path inputDir;

    @SuppressWarnings("rawtypes")
    @Test
    public void testIgnoreFilesWithoutExtension() throws Exception {
        fs.mkdirs(inputDir);
        Path avroFile = new Path(inputDir, "somefile.avro");
        Path textFile = new Path(inputDir, "someotherfile.txt");
        fs.create(avroFile).close();
        fs.create(textFile).close();
        FileInputFormat.setInputPaths(conf, inputDir);
        AvroInputFormat inputFormat = new AvroInputFormat();
        FileStatus[] statuses = inputFormat.listStatus(conf);
        Assert.assertEquals(1, statuses.length);
        Assert.assertEquals("somefile.avro", statuses[0].getPath().getName());
        conf.setBoolean(IGNORE_FILES_WITHOUT_EXTENSION_KEY, false);
        statuses = inputFormat.listStatus(conf);
        Assert.assertEquals(2, statuses.length);
        Set<String> names = new HashSet<>();
        names.add(statuses[0].getPath().getName());
        names.add(statuses[1].getPath().getName());
        Assert.assertTrue(names.contains("somefile.avro"));
        Assert.assertTrue(names.contains("someotherfile.txt"));
    }
}

