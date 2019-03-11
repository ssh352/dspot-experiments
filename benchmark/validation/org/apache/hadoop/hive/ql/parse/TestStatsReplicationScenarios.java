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
package org.apache.hadoop.hive.ql.parse;


import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for statistics replication.
 */
public class TestStatsReplicationScenarios {
    @Rule
    public final TestName testName = new TestName();

    protected static final Logger LOG = LoggerFactory.getLogger(TestReplicationScenarios.class);

    static WarehouseInstance primary;

    private static WarehouseInstance replica;

    private String primaryDbName;

    private String replicatedDbName;

    private static HiveConf conf;

    private static boolean hasAutogather;

    @Test
    public void testForNonAcidTables() throws Throwable {
        testStatsReplicationCommon(false, false);
    }

    @Test
    public void testForNonAcidTablesParallelBootstrapLoad() throws Throwable {
        testStatsReplicationCommon(true, false);
    }

    @Test
    public void testNonAcidMetadataOnlyDump() throws Throwable {
        testStatsReplicationCommon(false, true);
    }
}

