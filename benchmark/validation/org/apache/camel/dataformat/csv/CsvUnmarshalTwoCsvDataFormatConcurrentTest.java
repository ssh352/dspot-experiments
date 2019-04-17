/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.dataformat.csv;


import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CsvUnmarshalTwoCsvDataFormatConcurrentTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @EndpointInject(uri = "mock:result2")
    private MockEndpoint result2;

    @Test
    public void testCsvUnMarshal() throws Exception {
        result.expectedMessageCount(1);
        result2.expectedMessageCount(1);
        sendAndVerify("|", result);
        resetMocks();
        result.expectedMessageCount(1);
        result2.expectedMessageCount(1);
        sendAndVerify(";", result2);
    }
}
