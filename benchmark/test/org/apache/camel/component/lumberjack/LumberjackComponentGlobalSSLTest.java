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
package org.apache.camel.component.lumberjack;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class LumberjackComponentGlobalSSLTest extends CamelTestSupport {
    private static int port;

    @Test
    public void shouldListenToMessagesOverSSL() throws Exception {
        // cannot test on java 1.9
        if (isJava19()) {
            return;
        }
        // We're expecting 25 messages with Maps
        MockEndpoint mock = getMockEndpoint("mock:output");
        mock.expectedMessageCount(25);
        mock.allMessages().body().isInstanceOf(Map.class);
        // When sending messages
        List<Integer> responses = LumberjackUtil.sendMessages(LumberjackComponentGlobalSSLTest.port, createClientSSLContextParameters());
        // Then we should have the messages we're expecting
        mock.assertIsSatisfied();
        // And we should have replied with 2 acknowledgments for each window frame
        assertEquals(Arrays.asList(10, 15), responses);
    }
}
