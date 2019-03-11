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
package org.apache.geode.internal.protocol.protobuf.v1.operations;


import BasicTypes.EncodedValue;
import BasicTypes.Entry;
import RegionAPI.PutRequest;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.internal.protocol.TestExecutionContext;
import org.apache.geode.internal.protocol.protobuf.v1.BasicTypes;
import org.apache.geode.internal.protocol.protobuf.v1.ProtobufRequestUtilities;
import org.apache.geode.internal.protocol.protobuf.v1.ProtobufSerializationService;
import org.apache.geode.internal.protocol.protobuf.v1.RegionAPI;
import org.apache.geode.internal.protocol.protobuf.v1.Result;
import org.apache.geode.internal.protocol.protobuf.v1.Success;
import org.apache.geode.internal.protocol.protobuf.v1.serialization.exception.DecodingException;
import org.apache.geode.internal.protocol.protobuf.v1.utilities.ProtobufUtilities;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class PutRequestOperationHandlerJUnitTest extends OperationHandlerJUnitTest {
    private final String TEST_KEY = "my key";

    private final String TEST_VALUE = "99";

    private final String TEST_REGION = "test region";

    private Region regionMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_puttingTheEncodedEntryIntoRegion() throws Exception {
        PutRequestOperationHandler operationHandler = new PutRequestOperationHandler();
        Result result = operationHandler.process(serializationService, generateTestRequest(), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        Mockito.verify(regionMock).put(TEST_KEY, TEST_VALUE);
        Mockito.verify(regionMock, Mockito.times(1)).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test(expected = DecodingException.class)
    public void processThrowsExceptionWhenUnableToDecode() throws Exception {
        String exceptionText = "unsupported type!";
        Exception exception = new DecodingException(exceptionText);
        ProtobufSerializationService serializationServiceStub = Mockito.mock(ProtobufSerializationService.class);
        Mockito.when(serializationServiceStub.decode(ArgumentMatchers.any())).thenThrow(exception);
        BasicTypes.EncodedValue encodedKey = EncodedValue.newBuilder().setJsonObjectResult("{\"someKey\":\"someValue\"}").build();
        PutRequestOperationHandler operationHandler = new PutRequestOperationHandler();
        BasicTypes.EncodedValue testValue = serializationService.encode(TEST_VALUE);
        BasicTypes.Entry testEntry = ProtobufUtilities.createEntry(encodedKey, testValue);
        RegionAPI.PutRequest putRequest = ProtobufRequestUtilities.createPutRequest(TEST_REGION, testEntry).getPutRequest();
        operationHandler.process(serializationServiceStub, putRequest, TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
    }

    @Test
    public void test_RegionNotFound() throws Exception {
        Mockito.when(cacheStub.getRegion(TEST_REGION)).thenReturn(null);
        PutRequestOperationHandler operationHandler = new PutRequestOperationHandler();
        expectedException.expect(RegionDestroyedException.class);
        Result result = operationHandler.process(serializationService, generateTestRequest(), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
    }
}

