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


import Schema.Type.LONG;
import Schema.Type.STRING;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.avro.Schema;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestSequenceFileReader {
    private static final int COUNT = Integer.parseInt(System.getProperty("test.count", "10"));

    @ClassRule
    public static TemporaryFolder INPUT_DIR = new TemporaryFolder();

    @Rule
    public TemporaryFolder OUTPUT_DIR = new TemporaryFolder();

    private static final Schema SCHEMA = Pair.getPairSchema(Schema.create(LONG), Schema.create(STRING));

    @Test
    public void testReadSequenceFile() throws Exception {
        checkFile(new SequenceFileReader(TestSequenceFileReader.file()));
    }

    @Test
    public void testSequenceFileInputFormat() throws Exception {
        JobConf job = new JobConf();
        Path outputPath = new Path(OUTPUT_DIR.getRoot().getPath());
        outputPath.getFileSystem(job).delete(outputPath);
        // configure input for Avro from sequence file
        AvroJob.setInputSequenceFile(job);
        FileInputFormat.setInputPaths(job, TestSequenceFileReader.file().toURI().toString());
        AvroJob.setInputSchema(job, TestSequenceFileReader.SCHEMA);
        // mapper is default, identity
        // reducer is default, identity
        // configure output for avro
        AvroJob.setOutputSchema(job, TestSequenceFileReader.SCHEMA);
        FileOutputFormat.setOutputPath(job, outputPath);
        JobClient.runJob(job);
        checkFile(new org.apache.avro.file.DataFileReader(new File(((outputPath.toString()) + "/part-00000.avro")), new org.apache.avro.specific.SpecificDatumReader()));
    }

    private static class NonAvroMapper extends MapReduceBase implements Mapper<LongWritable, Text, AvroKey<Long>, AvroValue<Utf8>> {
        public void map(LongWritable key, Text value, OutputCollector<AvroKey<Long>, AvroValue<Utf8>> out, Reporter reporter) throws IOException {
            out.collect(new AvroKey(key.get()), new AvroValue(new Utf8(value.toString())));
        }
    }

    @Test
    public void testNonAvroMapper() throws Exception {
        JobConf job = new JobConf();
        Path outputPath = new Path(OUTPUT_DIR.getRoot().getPath());
        outputPath.getFileSystem(job).delete(outputPath);
        // configure input for non-Avro sequence file
        job.setInputFormat(SequenceFileInputFormat.class);
        FileInputFormat.setInputPaths(job, TestSequenceFileReader.file().toURI().toString());
        // use a hadoop mapper that emits Avro output
        job.setMapperClass(TestSequenceFileReader.NonAvroMapper.class);
        // reducer is default, identity
        // configure output for avro
        FileOutputFormat.setOutputPath(job, outputPath);
        AvroJob.setOutputSchema(job, TestSequenceFileReader.SCHEMA);
        JobClient.runJob(job);
        checkFile(new org.apache.avro.file.DataFileReader(new File(((outputPath.toString()) + "/part-00000.avro")), new org.apache.avro.specific.SpecificDatumReader()));
    }

    private static class NonAvroOnlyMapper extends MapReduceBase implements Mapper<LongWritable, Text, AvroWrapper<Pair<Long, Utf8>>, NullWritable> {
        public void map(LongWritable key, Text value, OutputCollector<AvroWrapper<Pair<Long, Utf8>>, NullWritable> out, Reporter reporter) throws IOException {
            out.collect(new AvroWrapper(new Pair(key.get(), new Utf8(value.toString()))), NullWritable.get());
        }
    }

    @Test
    public void testNonAvroMapOnly() throws Exception {
        JobConf job = new JobConf();
        Path outputPath = new Path(OUTPUT_DIR.getRoot().getPath());
        outputPath.getFileSystem(job).delete(outputPath);
        // configure input for non-Avro sequence file
        job.setInputFormat(SequenceFileInputFormat.class);
        FileInputFormat.setInputPaths(job, TestSequenceFileReader.file().toURI().toString());
        // use a hadoop mapper that emits Avro output
        job.setMapperClass(TestSequenceFileReader.NonAvroOnlyMapper.class);
        // configure output for avro
        job.setNumReduceTasks(0);
        // map-only
        FileOutputFormat.setOutputPath(job, outputPath);
        AvroJob.setOutputSchema(job, TestSequenceFileReader.SCHEMA);
        JobClient.runJob(job);
        checkFile(new org.apache.avro.file.DataFileReader(new File(((outputPath.toString()) + "/part-00000.avro")), new org.apache.avro.specific.SpecificDatumReader()));
    }

    private static class NonAvroReducer extends MapReduceBase implements Reducer<AvroKey<Long>, AvroValue<Utf8>, LongWritable, Text> {
        public void reduce(AvroKey<Long> key, Iterator<AvroValue<Utf8>> values, OutputCollector<LongWritable, Text> out, Reporter reporter) throws IOException {
            while (values.hasNext()) {
                AvroValue<Utf8> value = values.next();
                out.collect(new LongWritable(key.datum()), new Text(value.datum().toString()));
            } 
        }
    }

    @Test
    public void testNonAvroReducer() throws Exception {
        JobConf job = new JobConf();
        Path outputPath = new Path(OUTPUT_DIR.getRoot().getPath());
        outputPath.getFileSystem(job).delete(outputPath);
        // configure input for Avro from sequence file
        AvroJob.setInputSequenceFile(job);
        AvroJob.setInputSchema(job, TestSequenceFileReader.SCHEMA);
        FileInputFormat.setInputPaths(job, TestSequenceFileReader.file().toURI().toString());
        // mapper is default, identity
        // use a hadoop reducer that consumes Avro input
        AvroJob.setMapOutputSchema(job, TestSequenceFileReader.SCHEMA);
        job.setReducerClass(TestSequenceFileReader.NonAvroReducer.class);
        // configure outputPath for non-Avro SequenceFile
        job.setOutputFormat(SequenceFileOutputFormat.class);
        FileOutputFormat.setOutputPath(job, outputPath);
        // output key/value classes are default, LongWritable/Text
        JobClient.runJob(job);
        checkFile(new SequenceFileReader(new File(((outputPath.toString()) + "/part-00000"))));
    }
}

