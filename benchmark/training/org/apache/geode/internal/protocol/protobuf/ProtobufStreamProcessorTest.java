/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.protocol.protobuf;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.geode.internal.cache.InternalCacheForClientAccess;
import org.apache.geode.internal.protocol.TestExecutionContext;
import org.apache.geode.internal.protocol.protobuf.v1.ProtobufStreamProcessor;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class ProtobufStreamProcessorTest {
    @Test(expected = IOException.class)
    public void receiveMessage() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        OutputStream outputStream = new ByteArrayOutputStream(2);
        ProtobufStreamProcessor protobufStreamProcessor = new ProtobufStreamProcessor();
        InternalCacheForClientAccess mockInternalCache = Mockito.mock(InternalCacheForClientAccess.class);
        Mockito.when(mockInternalCache.getCacheForProcessingClientRequests()).thenReturn(mockInternalCache);
        protobufStreamProcessor.receiveMessage(inputStream, outputStream, TestExecutionContext.getNoAuthCacheExecutionContext(mockInternalCache));
    }
}

