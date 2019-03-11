/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ipc;


import RPC.Server;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;


public class TestMultipleProtocolServer extends TestRpcBase {
    private static Server server;

    // Now test a PB service - a server  hosts both PB and Writable Rpcs.
    @Test
    public void testPBService() throws Exception {
        // Set RPC engine to protobuf RPC engine
        Configuration conf2 = new Configuration();
        RPC.setProtocolEngine(conf2, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        TestRpcBase.TestRpcService client = RPC.getProxy(TestRpcBase.TestRpcService.class, 0, TestRpcBase.addr, conf2);
        TestProtoBufRpc.testProtoBufRpc(client);
    }
}

