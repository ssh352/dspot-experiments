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
package org.apache.camel.component.file.remote;


import FileComponent.FILE_EXCHANGE_FILE;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class FtpConsumerWithCharsetTest extends FtpServerTestSupport {
    private final String payload = "\u00e6\u00f8\u00e5 \u00a9";

    @Test
    public void testConsumerWithCharset() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(payload);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getExchanges().get(0);
        RemoteFile<?> file = ((RemoteFile<?>) (exchange.getProperty(FILE_EXCHANGE_FILE)));
        assertNotNull(file);
        assertEquals("iso-8859-1", file.getCharset());
        // The String will be encoded with UTF-8 by default
        byte[] data = exchange.getIn().getBody(String.class).getBytes("UTF-8");
        // data should be in iso, where the danish ae is -61 -90, oe is -61 -72
        // aa is -61 -91
        // and copyright is -62 -87
        assertEquals(9, data.length);
        assertEquals((-61), data[0]);
        assertEquals((-90), data[1]);
        assertEquals((-61), data[2]);
        assertEquals((-72), data[3]);
        assertEquals((-61), data[4]);
        assertEquals((-91), data[5]);
        assertEquals(32, data[6]);
        assertEquals((-62), data[7]);
        assertEquals((-87), data[8]);
    }
}
