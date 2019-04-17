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
package org.apache.camel.component.mina2;


import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("fix me")
public class Mina2ClientModeTcpTextlineDelimiterTest extends BaseMina2Test {
    @Test
    public void testMinaRoute() throws Exception {
        MockEndpoint endpoint = getMockEndpoint("mock:result");
        Object body = "Hello there!";
        endpoint.expectedBodiesReceived(body);
        // need to start the server first
        Mina2ClientModeTcpTextlineDelimiterTest.Server server = new Mina2ClientModeTcpTextlineDelimiterTest.Server(getPort());
        server.startup();
        // start the camel route to connect to the server
        context.getRouteController().startRoute("minaRoute");
        endpoint.assertIsSatisfied();
        server.shutdown();
    }

    private class Server {
        private final int port;

        private IoAcceptor acceptor;

        Server(int port) {
            this.port = port;
        }

        public void startup() throws Exception {
            acceptor = new NioSocketAcceptor();
            Mina2TextLineCodecFactory codecFactory = new Mina2TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.UNIX);
            acceptor.getFilterChain().addLast("codec", new org.apache.mina.filter.codec.ProtocolCodecFilter(codecFactory));
            acceptor.setHandler(new Mina2ClientModeTcpTextlineDelimiterTest.ServerHandler());
            acceptor.bind(new InetSocketAddress("127.0.0.1", port));
        }

        public void shutdown() throws Exception {
            acceptor.unbind();
            acceptor.dispose();
        }
    }

    private class ServerHandler extends IoHandlerAdapter {
        public void sessionOpened(IoSession session) throws Exception {
            session.write("Hello there!\n");
            session.closeNow();
        }
    }
}
