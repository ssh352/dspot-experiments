/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.dimension;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class LegacyDimensionSpecTest {
    private final ObjectMapper mapper = new DefaultObjectMapper();

    @Test
    public void testEqualsSerde() throws IOException {
        final String dimension = "testDimension";
        final List<DimensionSpec> deserializedSpecs = mapper.readValue(StringUtils.format("[\"%s\"]", dimension), new TypeReference<List<DimensionSpec>>() {});
        Assert.assertEquals(dimension, deserializedSpecs.get(0).getDimension());
        Assert.assertEquals(dimension, deserializedSpecs.get(0).getOutputName());
        Assert.assertEquals(new LegacyDimensionSpec(dimension), deserializedSpecs.get(0));
    }
}

