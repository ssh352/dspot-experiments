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
package org.apache.beam.sdk.io.gcp.storage;


import StandardResolveOptions.RESOLVE_FILE;
import State.DONE;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.PipelineResult.State;
import org.apache.beam.sdk.extensions.gcp.options.GcsOptions;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.io.fs.MatchResult.Metadata;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.testing.FileChecksumMatcher;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.apache.beam.sdk.util.GcsUtil;
import org.apache.beam.sdk.util.gcsfs.GcsPath;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


// Run a specific test using:
// ./gradlew :beam-sdks-java-io-google-cloud-platform:integrationTest --tests
// GcsKmsKeyIT.testGcsWriteWithKmsKey --info
/**
 * Integration test for GCS CMEK support.
 */
@RunWith(JUnit4.class)
public class GcsKmsKeyIT {
    private static final String INPUT_FILE = "gs://dataflow-samples/shakespeare/kinglear.txt";

    private static final String EXPECTED_CHECKSUM = "b9778bfac7fa8b934e42a322ef4bd4706b538fd0";

    /**
     * Tests writing to gcpTempLocation with --dataflowKmsKey set on the command line. Verifies that
     * resulting output uses specified key and is readable. Does not verify any temporary files.
     */
    @Test
    public void testGcsWriteWithKmsKey() {
        TestPipelineOptions options = TestPipeline.testingPipelineOptions().as(TestPipelineOptions.class);
        GcsOptions gcsOptions = options.as(GcsOptions.class);
        final String expectedKmsKey = gcsOptions.getDataflowKmsKey();
        MatcherAssert.assertThat(expectedKmsKey, CoreMatchers.notNullValue());
        ResourceId filenamePrefix = FileSystems.matchNewResource(gcsOptions.getGcpTempLocation(), true).resolve(String.format("GcsKmsKeyIT-%tF-%<tH-%<tM-%<tS-%<tL.output", new Date()), RESOLVE_FILE);
        Pipeline p = Pipeline.create(options);
        p.apply("ReadLines", TextIO.read().from(GcsKmsKeyIT.INPUT_FILE)).apply("WriteLines", TextIO.write().to(filenamePrefix));
        PipelineResult result = p.run();
        State state = result.waitUntilFinish();
        MatcherAssert.assertThat(state, CoreMatchers.equalTo(DONE));
        String filePattern = filenamePrefix + "*-of-*";
        MatcherAssert.assertThat(result, new FileChecksumMatcher(GcsKmsKeyIT.EXPECTED_CHECKSUM, filePattern));
        // Verify objects have KMS key set.
        try {
            MatchResult matchResult = Iterables.getOnlyElement(FileSystems.match(Collections.singletonList(filePattern)));
            GcsUtil gcsUtil = gcsOptions.getGcsUtil();
            for (Metadata metadata : matchResult.metadata()) {
                String kmsKey = gcsUtil.getObject(GcsPath.fromUri(metadata.resourceId().toString())).getKmsKeyName();
                // Returned kmsKey should have a version suffix.
                MatcherAssert.assertThat(metadata.resourceId().toString(), kmsKey, CoreMatchers.startsWith((expectedKmsKey + "/cryptoKeyVersions/")));
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}

