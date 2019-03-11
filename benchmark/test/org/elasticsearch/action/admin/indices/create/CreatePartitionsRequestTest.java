/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package org.elasticsearch.action.admin.indices.create;


import java.util.Arrays;
import java.util.UUID;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CreatePartitionsRequestTest {
    @Test
    public void testSerialization() throws Exception {
        UUID jobId = UUID.randomUUID();
        CreatePartitionsRequest request = new CreatePartitionsRequest(Arrays.asList("a", "b", "c"), jobId);
        BytesStreamOutput out = new BytesStreamOutput();
        request.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        CreatePartitionsRequest requestDeserialized = new CreatePartitionsRequest();
        requestDeserialized.readFrom(in);
        MatcherAssert.assertThat(requestDeserialized.indices(), Matchers.contains("a", "b", "c"));
        MatcherAssert.assertThat(requestDeserialized.jobId(), Matchers.is(jobId));
        jobId = UUID.randomUUID();
        request = new CreatePartitionsRequest(Arrays.asList("a", "b", "c"), jobId);
        out = new BytesStreamOutput();
        request.writeTo(out);
        in = out.bytes().streamInput();
        requestDeserialized = new CreatePartitionsRequest();
        requestDeserialized.readFrom(in);
        MatcherAssert.assertThat(requestDeserialized.indices(), Matchers.contains("a", "b", "c"));
        MatcherAssert.assertThat(requestDeserialized.jobId(), Matchers.is(jobId));
    }
}

