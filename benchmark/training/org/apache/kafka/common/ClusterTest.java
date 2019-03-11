/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common;


import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.kafka.common.utils.Utils;
import org.junit.Assert;
import org.junit.Test;


public class ClusterTest {
    @Test
    public void testBootstrap() {
        String ipAddress = "140.211.11.105";
        String hostName = "www.example.com";
        Cluster cluster = Cluster.bootstrap(Arrays.asList(new InetSocketAddress(ipAddress, 9002), new InetSocketAddress(hostName, 9002)));
        Set<String> expectedHosts = Utils.mkSet(ipAddress, hostName);
        Set<String> actualHosts = new HashSet<>();
        for (Node node : cluster.nodes())
            actualHosts.add(node.host());

        Assert.assertEquals(expectedHosts, actualHosts);
    }
}

