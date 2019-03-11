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
package org.apache.druid.segment.loading;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class LoadSpecTest {
    private final String value;

    private final String expectedId;

    public LoadSpecTest(String value, String expectedId) {
        this.value = value;
        this.expectedId = expectedId;
    }

    private static ObjectMapper mapper;

    @Test
    public void testStringResolve() throws IOException {
        LoadSpec loadSpec = LoadSpecTest.mapper.readValue(value, LoadSpec.class);
        Assert.assertEquals(expectedId, loadSpec.getClass().getAnnotation(com.fasterxml.jackson.annotation.JsonTypeName.class).value());
    }
}

