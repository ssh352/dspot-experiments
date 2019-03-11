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


import java.util.Collections;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.ql.exec.repl.util.ReplUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestReplicationWithTableMigrationEx - test replication for Hive2 to Hive3 (Strict managed tables)
 */
public class TestReplicationWithTableMigrationEx {
    @Rule
    public final TestName testName = new TestName();

    protected static final Logger LOG = LoggerFactory.getLogger(TestReplicationWithTableMigrationEx.class);

    private static WarehouseInstance primary;

    private static WarehouseInstance replica;

    private String primaryDbName;

    private String replicatedDbName;

    @Test
    public void testConcurrentOpDuringBootStrapDumpCreateTableReplay() throws Throwable {
        prepareData(primaryDbName);
        // dump with operation after last repl id is fetched.
        WarehouseInstance.Tuple tuple = dumpWithLastEventIdHacked(2);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
        Assert.assertTrue(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
        // next incremental dump
        tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
    }

    @Test
    public void testConcurrentOpDuringBootStrapDumpInsertReplay() throws Throwable {
        prepareData(primaryDbName);
        // dump with operation after last repl id is fetched.
        WarehouseInstance.Tuple tuple = dumpWithLastEventIdHacked(4);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
        Assert.assertTrue(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
        // next incremental dump
        tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
    }

    @Test
    public void testTableLevelDumpMigration() throws Throwable {
        WarehouseInstance.Tuple tuple = TestReplicationWithTableMigrationEx.primary.run(("use " + (primaryDbName))).run("create table t1 (i int, j int)").dump(((primaryDbName) + ".t1"), null);
        TestReplicationWithTableMigrationEx.replica.run(("create database " + (replicatedDbName)));
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(((replicatedDbName) + ".t1"), tuple.dumpLocation);
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
        Assert.assertTrue(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getTable(replicatedDbName, "t1").getParameters()));
        tuple = TestReplicationWithTableMigrationEx.primary.run(("use " + (primaryDbName))).run("insert into t1 values (1, 2)").dump(((primaryDbName) + ".t1"), tuple.lastReplicationId);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(((replicatedDbName) + ".t1"), tuple.dumpLocation);
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getTable(replicatedDbName, "t1").getParameters()));
    }

    @Test
    public void testConcurrentOpDuringBootStrapDumpInsertOverwrite() throws Throwable {
        TestReplicationWithTableMigrationEx.primary.run(("use " + (primaryDbName))).run("create table tacid (id int) clustered by(id) into 3 buckets stored as orc ").run("insert into tacid values(1)").run("insert into tacid values(2)").run("insert into tacid values(3)").run("insert overwrite table tacid values(4)").run("insert into tacid values(5)");
        // dump with operation after last repl id is fetched.
        WarehouseInstance.Tuple tuple = dumpWithLastEventIdHacked(2);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        TestReplicationWithTableMigrationEx.replica.run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "tacid" }).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run("select count(*) from tacid").verifyResult("2").run("select id from tacid order by id").verifyResults(new String[]{ "4", "5" });
        Assert.assertTrue(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
        // next incremental dump
        tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        TestReplicationWithTableMigrationEx.replica.run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "tacid" }).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run("select count(*) from tacid").verifyResult("2").run("select id from tacid order by id").verifyResults(new String[]{ "4", "5" });
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
    }

    @Test
    public void testIncLoadPenFlagPropAlterDB() throws Throwable {
        prepareData(primaryDbName);
        // dump with operation after last repl id is fetched.
        WarehouseInstance.Tuple tuple = dumpWithLastEventIdHacked(4);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
        Assert.assertTrue(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.primary.getDatabase(primaryDbName).getParameters()));
        tuple = TestReplicationWithTableMigrationEx.primary.run(("use " + (primaryDbName))).run((("alter database " + (primaryDbName)) + " set dbproperties('dummy_key'='dummy_val')")).run("create table tbl_temp (fld int)").dump(primaryDbName, tuple.lastReplicationId);
        loadWithFailureInAddNotification("tbl_temp", tuple.dumpLocation);
        Database replDb = TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName);
        Assert.assertTrue(ReplUtils.isFirstIncPending(replDb.getParameters()));
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.primary.getDatabase(primaryDbName).getParameters()));
        Assert.assertTrue(replDb.getParameters().get("dummy_key").equalsIgnoreCase("dummy_val"));
        // next incremental dump
        tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
    }

    @Test
    public void testIncLoadPenFlagWithMoveOptimization() throws Throwable {
        List<String> withClause = Collections.singletonList("'hive.repl.enable.move.optimization'='true'");
        prepareData(primaryDbName);
        // dump with operation after last repl id is fetched.
        WarehouseInstance.Tuple tuple = dumpWithLastEventIdHacked(4);
        TestReplicationWithTableMigrationEx.replica.load(replicatedDbName, tuple.dumpLocation, withClause);
        verifyLoadExecution(replicatedDbName, tuple.lastReplicationId);
        Assert.assertTrue(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
        // next incremental dump
        tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigrationEx.replica.load(replicatedDbName, tuple.dumpLocation, withClause);
        Assert.assertFalse(ReplUtils.isFirstIncPending(TestReplicationWithTableMigrationEx.replica.getDatabase(replicatedDbName).getParameters()));
    }

    @Test
    public void testOnwerPropagation() throws Throwable {
        TestReplicationWithTableMigrationEx.primary.run(("use " + (primaryDbName))).run("create table tbl_own (fld int)").run("create table tacid (id int) clustered by(id) into 3 buckets stored as orc ").run(("create table tacidpart (place string) partitioned by (country string) clustered by(place) " + "into 3 buckets stored as orc ")).run("create table tbl_part (fld int) partitioned by (country string)").run("insert into tbl_own values (1)").run("create view view_own as select * from tbl_own");
        // test bootstrap
        alterUserName("hive");
        WarehouseInstance.Tuple tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, null);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        verifyUserName("hive");
        // test incremental
        alterUserName("hive1");
        tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        verifyUserName("hive1");
    }

    @Test
    public void testOnwerPropagationInc() throws Throwable {
        WarehouseInstance.Tuple tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, null);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        TestReplicationWithTableMigrationEx.primary.run(("use " + (primaryDbName))).run("create table tbl_own (fld int)").run("create table tacid (id int) clustered by(id) into 3 buckets stored as orc ").run(("create table tacidpart (place string) partitioned by (country string) clustered by(place) " + "into 3 buckets stored as orc ")).run("create table tbl_part (fld int) partitioned by (country string)").run("insert into tbl_own values (1)").run("create view view_own as select * from tbl_own");
        // test incremental when table is getting created in the same load
        alterUserName("hive");
        tuple = TestReplicationWithTableMigrationEx.primary.dump(primaryDbName, tuple.lastReplicationId);
        TestReplicationWithTableMigrationEx.replica.loadWithoutExplain(replicatedDbName, tuple.dumpLocation);
        verifyUserName("hive");
    }
}

