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
package org.apache.hadoop.hbase.master.procedure;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test to ensure that the priority for procedures and stuck checker can partially solve the problem
 * describe in HBASE-19976, that is, RecoverMetaProcedure can finally be executed within a certain
 * period of time.
 */
@Category({ MasterTests.class, LargeTests.class })
public class TestProcedurePriority {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedurePriority.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static String TABLE_NAME_PREFIX = "TestProcedurePriority-";

    private static byte[] CF = Bytes.toBytes("cf");

    private static byte[] CQ = Bytes.toBytes("cq");

    private static int CORE_POOL_SIZE;

    private static int TABLE_COUNT;

    private static volatile boolean FAIL = false;

    public static final class MyCP implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> c, Get get, List<Cell> result) throws IOException {
            if ((TestProcedurePriority.FAIL) && (c.getEnvironment().getRegionInfo().isMetaRegion())) {
                throw new IOException("Inject error");
            }
        }

        @Override
        public void prePut(ObserverContext<RegionCoprocessorEnvironment> c, Put put, WALEdit edit, Durability durability) throws IOException {
            if ((TestProcedurePriority.FAIL) && (c.getEnvironment().getRegionInfo().isMetaRegion())) {
                throw new IOException("Inject error");
            }
        }
    }

    @Test
    public void test() throws Exception {
        RegionServerThread rsWithMetaThread = TestProcedurePriority.UTIL.getMiniHBaseCluster().getRegionServerThreads().stream().filter(( t) -> !(t.getRegionServer().getRegions(TableName.META_TABLE_NAME).isEmpty())).findAny().get();
        HRegionServer rsNoMeta = TestProcedurePriority.UTIL.getOtherRegionServer(rsWithMetaThread.getRegionServer());
        TestProcedurePriority.FAIL = true;
        TestProcedurePriority.UTIL.getMiniHBaseCluster().killRegionServer(rsNoMeta.getServerName());
        // wait until all the worker thread are stuck, which means that the stuck checker will start to
        // add new worker thread.
        ProcedureExecutor<?> executor = TestProcedurePriority.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor();
        TestProcedurePriority.UTIL.waitFor(60000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (executor.getWorkerThreadCount()) > (TestProcedurePriority.CORE_POOL_SIZE);
            }

            @Override
            public String explainFailure() throws Exception {
                return "Stuck checker does not add new worker thread";
            }
        });
        TestProcedurePriority.UTIL.getMiniHBaseCluster().killRegionServer(rsWithMetaThread.getRegionServer().getServerName());
        rsWithMetaThread.join();
        TestProcedurePriority.FAIL = false;
        // verify that the cluster is back
        TestProcedurePriority.UTIL.waitUntilNoRegionsInTransition(480000);
        for (int i = 0; i < (TestProcedurePriority.TABLE_COUNT); i++) {
            try (Table table = TestProcedurePriority.UTIL.getConnection().getTable(TableName.valueOf(((TestProcedurePriority.TABLE_NAME_PREFIX) + i)))) {
                table.put(new Put(Bytes.toBytes(i)).addColumn(TestProcedurePriority.CF, TestProcedurePriority.CQ, Bytes.toBytes(i)));
            }
        }
        waitFor(60000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (executor.getWorkerThreadCount()) == (TestProcedurePriority.CORE_POOL_SIZE);
            }

            @Override
            public String explainFailure() throws Exception {
                return "The new workers do not timeout";
            }
        });
    }
}
