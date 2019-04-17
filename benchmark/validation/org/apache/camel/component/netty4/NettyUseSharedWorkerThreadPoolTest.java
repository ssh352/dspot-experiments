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
package org.apache.camel.component.netty4;


import io.netty.channel.EventLoopGroup;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Test;


public class NettyUseSharedWorkerThreadPoolTest extends BaseNettyTest {
    private JndiRegistry jndi;

    private EventLoopGroup sharedWorkerServerGroup;

    private EventLoopGroup sharedWorkerClientGroup;

    private int port;

    private int port2;

    private int port3;

    @Test
    public void testSharedThreadPool() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(30);
        for (int i = 0; i < 10; i++) {
            String reply = template.requestBody((("netty4:tcp://localhost:" + (port)) + "?textline=true&sync=true&workerGroup=#sharedClientPool"), "Hello World", String.class);
            assertEquals("Bye World", reply);
            reply = template.requestBody((("netty4:tcp://localhost:" + (port2)) + "?textline=true&sync=true&workerGroup=#sharedClientPool"), "Hello Camel", String.class);
            assertEquals("Hi Camel", reply);
            reply = template.requestBody((("netty4:tcp://localhost:" + (port3)) + "?textline=true&sync=true&workerGroup=#sharedClientPool"), "Hello Claus", String.class);
            assertEquals("Hej Claus", reply);
        }
        assertMockEndpointsSatisfied();
        sharedWorkerServerGroup.shutdownGracefully().sync().await();
        sharedWorkerClientGroup.shutdownGracefully().sync().await();
    }
}
