/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl;


import Charsets.UTF_8;
import java.util.List;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.client.DrillClient;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.apache.drill.exec.proto.UserBitShared.QueryType.PHYSICAL;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.server.Drillbit;
import org.apache.drill.exec.server.RemoteServiceSet;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(OperatorTest.class)
public class TestHashToRandomExchange extends PopUnitTestBase {
    static final Logger logger = LoggerFactory.getLogger(TestHashToRandomExchange.class);

    @Test
    public void twoBitTwoExchangeTwoEntryRun() throws Exception {
        RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();
        try (Drillbit bit1 = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);Drillbit bit2 = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            bit1.run();
            bit2.run();
            client.connect();
            List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/sender/hash_exchange.json"), UTF_8).read());
            int count = 0;
            for (QueryDataBatch b : results) {
                if ((b.getHeader().getRowCount()) != 0) {
                    count += b.getHeader().getRowCount();
                }
                b.release();
            }
            Assert.assertEquals(200, count);
        }
    }
}

