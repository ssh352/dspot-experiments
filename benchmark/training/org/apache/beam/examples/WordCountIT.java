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
package org.apache.beam.examples;


import StandardResolveOptions.RESOLVE_DIRECTORY;
import StandardResolveOptions.RESOLVE_FILE;
import java.util.Date;
import org.apache.beam.examples.WordCount.WordCountOptions;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * End-to-end tests of WordCount.
 */
@RunWith(JUnit4.class)
public class WordCountIT {
    private static final String DEFAULT_INPUT = "gs://apache-beam-samples/shakespeare/winterstale-personae";

    private static final String DEFAULT_OUTPUT_CHECKSUM = "ebf895e7324e8a3edc72e7bcc96fa2ba7f690def";

    /**
     * Options for the WordCount Integration Test.
     *
     * <p>Define expected output file checksum to verify WordCount pipeline result with customized
     * input.
     */
    public interface WordCountITOptions extends WordCountOptions , TestPipelineOptions {}

    @Test
    public void testE2EWordCount() throws Exception {
        WordCountIT.WordCountITOptions options = TestPipeline.testingPipelineOptions().as(WordCountIT.WordCountITOptions.class);
        setInputFile(WordCountIT.DEFAULT_INPUT);
        options.setOutput(FileSystems.matchNewResource(getTempRoot(), true).resolve(String.format("WordCountIT-%tF-%<tH-%<tM-%<tS-%<tL", new Date()), RESOLVE_DIRECTORY).resolve("output", RESOLVE_DIRECTORY).resolve("results", RESOLVE_FILE).toString());
        setOnSuccessMatcher(new org.apache.beam.sdk.testing.FileChecksumMatcher(WordCountIT.DEFAULT_OUTPUT_CHECKSUM, ((getOutput()) + "*-of-*")));
        WordCount.runWordCount(options);
    }
}
