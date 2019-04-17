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
package org.apache.hadoop.hbase.client.replication;


import HConstants.REPLICATION_SCOPE_GLOBAL;
import HConstants.REPLICATION_SCOPE_LOCAL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.replication.BaseReplicationEndpoint;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.replication.TestReplicationBase;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Unit testing of ReplicationAdmin with clusters
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestReplicationAdminWithClusters extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationAdminWithClusters.class);

    static Connection connection1;

    static Connection connection2;

    static Admin admin1;

    static Admin admin2;

    static ReplicationAdmin adminExt;

    @Rule
    public TestName name = new TestName();

    @Test
    public void disableNotFullReplication() throws Exception {
        HTableDescriptor table = new HTableDescriptor(TestReplicationAdminWithClusters.admin2.getTableDescriptor(TestReplicationBase.tableName));
        HColumnDescriptor f = new HColumnDescriptor("notReplicatedFamily");
        table.addFamily(f);
        TestReplicationAdminWithClusters.admin1.disableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin1.modifyTable(TestReplicationBase.tableName, table);
        TestReplicationAdminWithClusters.admin1.enableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin1.disableTableReplication(TestReplicationBase.tableName);
        table = TestReplicationAdminWithClusters.admin1.getTableDescriptor(TestReplicationBase.tableName);
        for (HColumnDescriptor fam : table.getColumnFamilies()) {
            Assert.assertEquals(REPLICATION_SCOPE_LOCAL, fam.getScope());
        }
        TestReplicationAdminWithClusters.admin1.deleteColumnFamily(table.getTableName(), f.getName());
    }

    @Test
    public void testEnableReplicationWhenSlaveClusterDoesntHaveTable() throws Exception {
        TestReplicationAdminWithClusters.admin1.disableTableReplication(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin2.disableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin2.deleteTable(TestReplicationBase.tableName);
        Assert.assertFalse(TestReplicationAdminWithClusters.admin2.tableExists(TestReplicationBase.tableName));
        TestReplicationAdminWithClusters.admin1.enableTableReplication(TestReplicationBase.tableName);
        Assert.assertTrue(TestReplicationAdminWithClusters.admin2.tableExists(TestReplicationBase.tableName));
    }

    @Test
    public void testEnableReplicationWhenReplicationNotEnabled() throws Exception {
        HTableDescriptor table = new HTableDescriptor(TestReplicationAdminWithClusters.admin1.getTableDescriptor(TestReplicationBase.tableName));
        for (HColumnDescriptor fam : table.getColumnFamilies()) {
            fam.setScope(REPLICATION_SCOPE_LOCAL);
        }
        TestReplicationAdminWithClusters.admin1.disableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin1.modifyTable(TestReplicationBase.tableName, table);
        TestReplicationAdminWithClusters.admin1.enableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin2.disableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin2.modifyTable(TestReplicationBase.tableName, table);
        TestReplicationAdminWithClusters.admin2.enableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin1.enableTableReplication(TestReplicationBase.tableName);
        table = TestReplicationAdminWithClusters.admin1.getTableDescriptor(TestReplicationBase.tableName);
        for (HColumnDescriptor fam : table.getColumnFamilies()) {
            Assert.assertEquals(REPLICATION_SCOPE_GLOBAL, fam.getScope());
        }
    }

    @Test
    public void testEnableReplicationWhenTableDescriptorIsNotSameInClusters() throws Exception {
        HTableDescriptor table = new HTableDescriptor(TestReplicationAdminWithClusters.admin2.getTableDescriptor(TestReplicationBase.tableName));
        HColumnDescriptor f = new HColumnDescriptor("newFamily");
        table.addFamily(f);
        TestReplicationAdminWithClusters.admin2.disableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin2.modifyTable(TestReplicationBase.tableName, table);
        TestReplicationAdminWithClusters.admin2.enableTable(TestReplicationBase.tableName);
        try {
            TestReplicationAdminWithClusters.admin1.enableTableReplication(TestReplicationBase.tableName);
            Assert.fail("Exception should be thrown if table descriptors in the clusters are not same.");
        } catch (RuntimeException ignored) {
        }
        TestReplicationAdminWithClusters.admin1.disableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin1.modifyTable(TestReplicationBase.tableName, table);
        TestReplicationAdminWithClusters.admin1.enableTable(TestReplicationBase.tableName);
        TestReplicationAdminWithClusters.admin1.enableTableReplication(TestReplicationBase.tableName);
        table = TestReplicationAdminWithClusters.admin1.getTableDescriptor(TestReplicationBase.tableName);
        for (HColumnDescriptor fam : table.getColumnFamilies()) {
            Assert.assertEquals(REPLICATION_SCOPE_GLOBAL, fam.getScope());
        }
        TestReplicationAdminWithClusters.admin1.deleteColumnFamily(TestReplicationBase.tableName, f.getName());
        TestReplicationAdminWithClusters.admin2.deleteColumnFamily(TestReplicationBase.tableName, f.getName());
    }

    @Test
    public void testDisableAndEnableReplication() throws Exception {
        TestReplicationAdminWithClusters.admin1.disableTableReplication(TestReplicationBase.tableName);
        HTableDescriptor table = TestReplicationAdminWithClusters.admin1.getTableDescriptor(TestReplicationBase.tableName);
        for (HColumnDescriptor fam : table.getColumnFamilies()) {
            Assert.assertEquals(REPLICATION_SCOPE_LOCAL, fam.getScope());
        }
        TestReplicationAdminWithClusters.admin1.enableTableReplication(TestReplicationBase.tableName);
        table = TestReplicationAdminWithClusters.admin1.getTableDescriptor(TestReplicationBase.tableName);
        for (HColumnDescriptor fam : table.getColumnFamilies()) {
            Assert.assertEquals(REPLICATION_SCOPE_GLOBAL, fam.getScope());
        }
    }

    @Test
    public void testEnableReplicationForTableWithRegionReplica() throws Exception {
        TableName tn = TableName.valueOf(name.getMethodName());
        TableDescriptor td = TableDescriptorBuilder.newBuilder(tn).setRegionReplication(5).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestReplicationBase.noRepfamName).build()).build();
        TestReplicationAdminWithClusters.admin1.createTable(td);
        try {
            TestReplicationAdminWithClusters.admin1.enableTableReplication(tn);
            td = TestReplicationAdminWithClusters.admin1.getDescriptor(tn);
            for (ColumnFamilyDescriptor fam : td.getColumnFamilies()) {
                Assert.assertEquals(REPLICATION_SCOPE_GLOBAL, fam.getScope());
            }
        } finally {
            TestReplicationBase.utility1.deleteTable(tn);
            TestReplicationBase.utility2.deleteTable(tn);
        }
    }

    @Test(expected = TableNotFoundException.class)
    public void testDisableReplicationForNonExistingTable() throws Exception {
        TestReplicationAdminWithClusters.admin1.disableTableReplication(TableName.valueOf(name.getMethodName()));
    }

    @Test(expected = TableNotFoundException.class)
    public void testEnableReplicationForNonExistingTable() throws Exception {
        TestReplicationAdminWithClusters.admin1.enableTableReplication(TableName.valueOf(name.getMethodName()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisableReplicationWhenTableNameAsNull() throws Exception {
        TestReplicationAdminWithClusters.admin1.disableTableReplication(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnableReplicationWhenTableNameAsNull() throws Exception {
        TestReplicationAdminWithClusters.admin1.enableTableReplication(null);
    }

    /* Test enable table replication should create table only in user explicit specified table-cfs.
    HBASE-14717
     */
    @Test
    public void testEnableReplicationForExplicitSetTableCfs() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        String peerId = "2";
        if (TestReplicationAdminWithClusters.admin2.isTableAvailable(TestReplicationBase.tableName)) {
            TestReplicationAdminWithClusters.admin2.disableTable(TestReplicationBase.tableName);
            TestReplicationAdminWithClusters.admin2.deleteTable(TestReplicationBase.tableName);
        }
        Assert.assertFalse("Table should not exists in the peer cluster", TestReplicationAdminWithClusters.admin2.isTableAvailable(TestReplicationBase.tableName));
        // update peer config
        ReplicationPeerConfig rpc = TestReplicationAdminWithClusters.admin1.getReplicationPeerConfig(peerId);
        rpc.setReplicateAllUserTables(false);
        TestReplicationAdminWithClusters.admin1.updateReplicationPeerConfig(peerId, rpc);
        Map<TableName, ? extends Collection<String>> tableCfs = new HashMap<>();
        tableCfs.put(tableName, null);
        try {
            TestReplicationAdminWithClusters.adminExt.setPeerTableCFs(peerId, tableCfs);
            TestReplicationAdminWithClusters.admin1.enableTableReplication(TestReplicationBase.tableName);
            Assert.assertFalse(("Table should not be created if user has set table cfs explicitly for the " + "peer and this is not part of that collection"), TestReplicationAdminWithClusters.admin2.isTableAvailable(TestReplicationBase.tableName));
            tableCfs.put(TestReplicationBase.tableName, null);
            TestReplicationAdminWithClusters.adminExt.setPeerTableCFs(peerId, tableCfs);
            TestReplicationAdminWithClusters.admin1.enableTableReplication(TestReplicationBase.tableName);
            Assert.assertTrue("Table should be created if user has explicitly added table into table cfs collection", TestReplicationAdminWithClusters.admin2.isTableAvailable(TestReplicationBase.tableName));
        } finally {
            TestReplicationAdminWithClusters.adminExt.removePeerTableCFs(peerId, TestReplicationAdminWithClusters.adminExt.getPeerTableCFs(peerId));
            TestReplicationAdminWithClusters.admin1.disableTableReplication(TestReplicationBase.tableName);
            rpc = TestReplicationAdminWithClusters.admin1.getReplicationPeerConfig(peerId);
            rpc.setReplicateAllUserTables(true);
            TestReplicationAdminWithClusters.admin1.updateReplicationPeerConfig(peerId, rpc);
        }
    }

    @Test
    public void testReplicationPeerConfigUpdateCallback() throws Exception {
        String peerId = "1";
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(TestReplicationBase.utility2.getClusterKey());
        rpc.setReplicationEndpointImpl(TestReplicationAdminWithClusters.TestUpdatableReplicationEndpoint.class.getName());
        rpc.getConfiguration().put("key1", "value1");
        TestReplicationAdminWithClusters.admin1.addReplicationPeer(peerId, rpc);
        rpc.getConfiguration().put("key1", "value2");
        TestReplicationBase.admin.updatePeerConfig(peerId, rpc);
        if (!(TestReplicationAdminWithClusters.TestUpdatableReplicationEndpoint.hasCalledBack())) {
            synchronized(TestReplicationAdminWithClusters.TestUpdatableReplicationEndpoint.class) {
                TestReplicationAdminWithClusters.TestUpdatableReplicationEndpoint.class.wait(2000L);
            }
        }
        Assert.assertEquals(true, TestReplicationAdminWithClusters.TestUpdatableReplicationEndpoint.hasCalledBack());
        TestReplicationBase.admin.removePeer(peerId);
    }

    public static class TestUpdatableReplicationEndpoint extends BaseReplicationEndpoint {
        private static boolean calledBack = false;

        public static boolean hasCalledBack() {
            return TestReplicationAdminWithClusters.TestUpdatableReplicationEndpoint.calledBack;
        }

        @Override
        public synchronized void peerConfigUpdated(ReplicationPeerConfig rpc) {
            TestReplicationAdminWithClusters.TestUpdatableReplicationEndpoint.calledBack = true;
            notifyAll();
        }

        @Override
        public void start() {
            startAsync();
        }

        @Override
        public void stop() {
            stopAsync();
        }

        @Override
        protected void doStart() {
            notifyStarted();
        }

        @Override
        protected void doStop() {
            notifyStopped();
        }

        @Override
        public UUID getPeerUUID() {
            return getRandomUUID();
        }

        @Override
        public boolean replicate(ReplicateContext replicateContext) {
            return false;
        }
    }
}
