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


import ErrorMsg.REPL_BOOTSTRAP_LOAD_PATH_NOT_VALID;
import HiveConf.ConfVars.EXECPARALLEL;
import HiveConf.ConfVars.METASTOREURIS;
import HiveConf.ConfVars.METASTOREWAREHOUSE;
import HiveConf.ConfVars.REPL_APPROX_MAX_LOAD_TASKS;
import HiveConf.ConfVars.REPL_FUNCTIONS_ROOT_DIR;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.InjectableBehaviourObjectStore;
import org.apache.hadoop.hive.metastore.InjectableBehaviourObjectStore.BehaviourInjection;
import org.apache.hadoop.hive.metastore.InjectableBehaviourObjectStore.CallerArguments;
import org.apache.hadoop.hive.metastore.ReplChangeManager;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.exec.repl.incremental.IncrementalLoadTasksBuilder;
import org.apache.hadoop.hive.ql.exec.repl.util.ReplUtils;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestReplicationScenariosAcrossInstances extends BaseReplicationAcrossInstances {
    @Test
    public void testCreateFunctionIncrementalReplication() throws Throwable {
        WarehouseInstance.Tuple bootStrapDump = BaseReplicationAcrossInstances.primary.dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, bootStrapDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(bootStrapDump.lastReplicationId);
        BaseReplicationAcrossInstances.primary.run(((("CREATE FUNCTION " + (primaryDbName)) + ".testFunctionOne as 'hivemall.tools.string.StopwordUDF' ") + "using jar  'ivy://io.github.myui:hivemall:0.4.0-2'"));
        WarehouseInstance.Tuple incrementalDump = BaseReplicationAcrossInstances.primary.dump(primaryDbName, bootStrapDump.lastReplicationId);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, incrementalDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(incrementalDump.lastReplicationId).run((("SHOW FUNCTIONS LIKE '" + (replicatedDbName)) + "*'")).verifyResult(((replicatedDbName) + ".testFunctionOne"));
        // Test the idempotent behavior of CREATE FUNCTION
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, incrementalDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(incrementalDump.lastReplicationId).run((("SHOW FUNCTIONS LIKE '" + (replicatedDbName)) + "*'")).verifyResult(((replicatedDbName) + ".testFunctionOne"));
    }

    @Test
    public void testBootstrapReplLoadRetryAfterFailureForFunctions() throws Throwable {
        String funcName1 = "f1";
        String funcName2 = "f2";
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run(((((("CREATE FUNCTION " + (primaryDbName)) + ".") + funcName1) + " as 'hivemall.tools.string.StopwordUDF' ") + "using jar  'ivy://io.github.myui:hivemall:0.4.0-2'")).run(((((("CREATE FUNCTION " + (primaryDbName)) + ".") + funcName2) + " as 'hivemall.tools.string.SplitWordsUDF' ") + "using jar  'ivy://io.github.myui:hivemall:0.4.0-1'")).dump(primaryDbName, null);
        // Allow create function only on f1. Create should fail for the second function.
        BehaviourInjection<CallerArguments, Boolean> callerVerifier = new BehaviourInjection<CallerArguments, Boolean>() {
            @Override
            public Boolean apply(CallerArguments args) {
                injectionPathCalled = true;
                if (!(args.dbName.equalsIgnoreCase(replicatedDbName))) {
                    BaseReplicationAcrossInstances.LOG.warn(("Verifier - DB: " + (String.valueOf(args.dbName))));
                    return false;
                }
                if ((args.funcName) != null) {
                    BaseReplicationAcrossInstances.LOG.debug(("Verifier - Function: " + (String.valueOf(args.funcName))));
                    return args.funcName.equals(funcName1);
                }
                return true;
            }
        };
        InjectableBehaviourObjectStore.setCallerVerifier(callerVerifier);
        // Trigger bootstrap dump which just creates function f1 but not f2
        List<String> withConfigs = Arrays.asList("'hive.repl.approx.max.load.tasks'='1'", "'hive.in.repl.test.files.sorted'='true'");
        try {
            BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple.dumpLocation, withConfigs);
            callerVerifier.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetCallerVerifier();// reset the behaviour

        }
        // Verify that only f1 got loaded
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult("null").run((("show functions like '" + (replicatedDbName)) + "*'")).verifyResult((((replicatedDbName) + ".") + funcName1));
        // Verify no calls to load f1 only f2.
        callerVerifier = new BehaviourInjection<CallerArguments, Boolean>() {
            @Override
            public Boolean apply(CallerArguments args) {
                injectionPathCalled = true;
                if (!(args.dbName.equalsIgnoreCase(replicatedDbName))) {
                    BaseReplicationAcrossInstances.LOG.warn(("Verifier - DB: " + (String.valueOf(args.dbName))));
                    return false;
                }
                if ((args.funcName) != null) {
                    BaseReplicationAcrossInstances.LOG.debug(("Verifier - Function: " + (String.valueOf(args.funcName))));
                    return args.funcName.equals(funcName2);
                }
                return true;
            }
        };
        InjectableBehaviourObjectStore.setCallerVerifier(callerVerifier);
        try {
            // Retry with same dump with which it was already loaded should resume the bootstrap load.
            // This time, it completes by adding just the function f2
            BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation);
            callerVerifier.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetCallerVerifier();// reset the behaviour

        }
        // Verify that both the functions are available.
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run((("show functions like '" + (replicatedDbName)) + "*'")).verifyResults(new String[]{ ((replicatedDbName) + ".") + funcName1, ((replicatedDbName) + ".") + funcName2 });
    }

    @Test
    public void testDropFunctionIncrementalReplication() throws Throwable {
        BaseReplicationAcrossInstances.primary.run(((("CREATE FUNCTION " + (primaryDbName)) + ".testFunctionAnother as 'hivemall.tools.string.StopwordUDF' ") + "using jar  'ivy://io.github.myui:hivemall:0.4.0-2'"));
        WarehouseInstance.Tuple bootStrapDump = BaseReplicationAcrossInstances.primary.dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, bootStrapDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(bootStrapDump.lastReplicationId);
        BaseReplicationAcrossInstances.primary.run((("Drop FUNCTION " + (primaryDbName)) + ".testFunctionAnother "));
        WarehouseInstance.Tuple incrementalDump = BaseReplicationAcrossInstances.primary.dump(primaryDbName, bootStrapDump.lastReplicationId);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, incrementalDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(incrementalDump.lastReplicationId).run("SHOW FUNCTIONS LIKE '*testfunctionanother*'").verifyResult(null);
        // Test the idempotent behavior of DROP FUNCTION
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, incrementalDump.dumpLocation).run(("REPL STATUS " + (replicatedDbName))).verifyResult(incrementalDump.lastReplicationId).run("SHOW FUNCTIONS LIKE '*testfunctionanother*'").verifyResult(null);
    }

    @Test
    public void testBootstrapFunctionReplication() throws Throwable {
        BaseReplicationAcrossInstances.primary.run(((("CREATE FUNCTION " + (primaryDbName)) + ".testFunction as 'hivemall.tools.string.StopwordUDF' ") + "using jar  'ivy://io.github.myui:hivemall:0.4.0-2'"));
        WarehouseInstance.Tuple bootStrapDump = BaseReplicationAcrossInstances.primary.dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, bootStrapDump.dumpLocation).run((("SHOW FUNCTIONS LIKE '" + (replicatedDbName)) + "*'")).verifyResult(((replicatedDbName) + ".testFunction"));
    }

    @Test
    public void testCreateFunctionWithFunctionBinaryJarsOnHDFS() throws Throwable {
        TestReplicationScenariosAcrossInstances.Dependencies dependencies = dependencies("ivy://io.github.myui:hivemall:0.4.0-2", BaseReplicationAcrossInstances.primary);
        String jarSubString = dependencies.toJarSubSql();
        BaseReplicationAcrossInstances.primary.run((((("CREATE FUNCTION " + (primaryDbName)) + ".anotherFunction as 'hivemall.tools.string.StopwordUDF' ") + "using ") + jarSubString));
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).run((("SHOW FUNCTIONS LIKE '" + (replicatedDbName)) + "*'")).verifyResult(((replicatedDbName) + ".anotherFunction"));
        FileStatus[] fileStatuses = TestReplicationScenariosAcrossInstances.replica.miniDFSCluster.getFileSystem().globStatus(new Path(((((TestReplicationScenariosAcrossInstances.replica.functionsRoot) + "/") + (replicatedDbName.toLowerCase())) + "/anotherfunction/*/*")), ( path) -> path.toString().endsWith("jar"));
        List<String> expectedDependenciesNames = dependencies.jarNames();
        MatcherAssert.assertThat(fileStatuses.length, CoreMatchers.is(CoreMatchers.equalTo(expectedDependenciesNames.size())));
        List<String> jars = Arrays.stream(fileStatuses).map(( f) -> {
            String[] splits = f.getPath().toString().split("/");
            return splits[(splits.length - 1)];
        }).collect(Collectors.toList());
        MatcherAssert.assertThat(jars, Matchers.containsInAnyOrder(expectedDependenciesNames.toArray()));
    }

    static class Dependencies {
        private final List<Path> fullQualifiedJarPaths;

        Dependencies(List<Path> fullQualifiedJarPaths) {
            this.fullQualifiedJarPaths = fullQualifiedJarPaths;
        }

        private String toJarSubSql() {
            return StringUtils.join(fullQualifiedJarPaths.stream().map(( p) -> ("jar '" + p) + "'").collect(Collectors.toList()), ",");
        }

        private List<String> jarNames() {
            return fullQualifiedJarPaths.stream().map(( p) -> {
                String[] splits = p.toString().split("/");
                return splits[(splits.length - 1)];
            }).collect(Collectors.toList());
        }
    }

    /* From the hive logs(hive.log) we can also check for the info statement
    fgrep "Total Tasks" [location of hive.log]
    each line indicates one run of loadTask.
     */
    @Test
    public void testMultipleStagesOfReplicationLoadTask() throws Throwable {
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t1 (id int)").run("insert into t1 values (1), (2)").run("create table t2 (place string) partitioned by (country string)").run("insert into table t2 partition(country='india') values ('bangalore')").run("insert into table t2 partition(country='us') values ('austin')").run("insert into table t2 partition(country='france') values ('paris')").run("create table t3 (rank int)").dump(primaryDbName, null);
        // each table creation itself takes more than one task, give we are giving a max of 1, we should hit multiple runs.
        List<String> withClause = Collections.singletonList((("'" + (REPL_APPROX_MAX_LOAD_TASKS.varname)) + "'='1'"));
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation, withClause).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "t1", "t2", "t3" }).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run("select country from t2 order by country").verifyResults(new String[]{ "france", "india", "us" });
    }

    @Test
    public void testParallelExecutionOfReplicationBootStrapLoad() throws Throwable {
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t1 (id int)").run("create table t2 (place string) partitioned by (country string)").run("insert into table t2 partition(country='india') values ('bangalore')").run("insert into table t2 partition(country='australia') values ('sydney')").run("insert into table t2 partition(country='russia') values ('moscow')").run("insert into table t2 partition(country='uk') values ('london')").run("insert into table t2 partition(country='us') values ('sfo')").run("insert into table t2 partition(country='france') values ('paris')").run("insert into table t2 partition(country='japan') values ('tokyo')").run("insert into table t2 partition(country='china') values ('hkg')").run("create table t3 (rank int)").dump(primaryDbName, null);
        TestReplicationScenariosAcrossInstances.replica.hiveConf.setBoolVar(EXECPARALLEL, true);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run("show tables").verifyResults(new String[]{ "t1", "t2", "t3" }).run("select country from t2").verifyResults(Arrays.asList("india", "australia", "russia", "uk", "us", "france", "japan", "china"));
        TestReplicationScenariosAcrossInstances.replica.hiveConf.setBoolVar(EXECPARALLEL, false);
    }

    @Test
    public void testMetadataBootstrapDump() throws Throwable {
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run(("create table  acid_table (key int, value int) partitioned by (load_date date) " + "clustered by(key) into 2 buckets stored as orc tblproperties ('transactional'='true')")).run("create table table1 (i int, j int)").run("insert into table1 values (1,2)").dump(primaryDbName, null, Collections.singletonList("'hive.repl.dump.metadata.only'='true'"));
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "acid_table", "table1" }).run("select * from table1").verifyResults(Collections.emptyList());
    }

    @Test
    public void testIncrementalMetadataReplication() throws Throwable {
        // //////////  Bootstrap   ////////////
        WarehouseInstance.Tuple bootstrapTuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table table1 (i int, j int)").run("create table table2 (a int, city string) partitioned by (country string)").run("create table table3 (i int, j int)").run("insert into table1 values (1,2)").dump(primaryDbName, null, Collections.singletonList("'hive.repl.dump.metadata.only'='true'"));
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, bootstrapTuple.dumpLocation).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "table1", "table2", "table3" }).run("select * from table1").verifyResults(Collections.emptyList());
        // //////////  First Incremental ////////////
        WarehouseInstance.Tuple incrementalOneTuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("alter table table1 rename to renamed_table1").run("insert into table2 partition(country='india') values (1,'mumbai') ").run("create table table4 (i int, j int)").dump((((((((("repl dump " + (primaryDbName)) + " from ") + (bootstrapTuple.lastReplicationId)) + " to ") + (Long.parseLong(bootstrapTuple.lastReplicationId))) + 100L) + " limit 100 ") + "with ('hive.repl.dump.metadata.only'='true')"));
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, incrementalOneTuple.dumpLocation).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "renamed_table1", "table2", "table3", "table4" }).run("select * from renamed_table1").verifyResults(Collections.emptyList()).run("select * from table2").verifyResults(Collections.emptyList());
        // //////////  Second Incremental ////////////
        WarehouseInstance.Tuple secondIncremental = BaseReplicationAcrossInstances.primary.run("alter table table2 add columns (zipcode int)").run("alter table table3 change i a string").run("alter table table3 set tblproperties('custom.property'='custom.value')").run("drop table renamed_table1").dump((((("repl dump " + (primaryDbName)) + " from ") + (incrementalOneTuple.lastReplicationId)) + " with ('hive.repl.dump.metadata.only'='true')"));
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, secondIncremental.dumpLocation).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "table2", "table3", "table4" }).run("desc table3").verifyResults(new String[]{ "a                   \tstring              \t                    ", "j                   \tint                 \t                    " }).run("desc table2").verifyResults(new String[]{ "a                   \tint                 \t                    ", "city                \tstring              \t                    ", "country             \tstring              \t                    ", "zipcode             \tint                 \t                    ", "\t \t ", "# Partition Information\t \t ", "# col_name            \tdata_type           \tcomment             ", "country             \tstring              \t                    " }).run("show tblproperties table3('custom.property')").verifyResults(new String[]{ "custom.property\tcustom.value" });
    }

    @Test
    public void testNonReplDBMetadataReplication() throws Throwable {
        String dbName = (primaryDbName) + "_metadata";
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("create database " + dbName)).run(("use " + dbName)).run("create table table1 (i int, j int)").run("create table table2 (a int, city string) partitioned by (country string)").run("create table table3 (i int, j int)").run("insert into table1 values (1,2)").dump(dbName, null, Collections.singletonList("'hive.repl.dump.metadata.only'='true'"));
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "table1", "table2", "table3" }).run("select * from table1").verifyResults(Collections.emptyList());
        tuple = BaseReplicationAcrossInstances.primary.run(("use " + dbName)).run("alter table table1 rename to renamed_table1").run("insert into table2 partition(country='india') values (1,'mumbai') ").run("create table table4 (i int, j int)").dump(dbName, tuple.lastReplicationId, Collections.singletonList("'hive.repl.dump.metadata.only'='true'"));
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "renamed_table1", "table2", "table3", "table4" }).run("select * from renamed_table1").verifyResults(Collections.emptyList()).run("select * from table2").verifyResults(Collections.emptyList());
    }

    @Test
    public void testBootStrapDumpOfWarehouse() throws Throwable {
        String randomOne = RandomStringUtils.random(10, true, false);
        String randomTwo = RandomStringUtils.random(10, true, false);
        String dbOne = (primaryDbName) + randomOne;
        String dbTwo = (primaryDbName) + randomTwo;
        BaseReplicationAcrossInstances.primary.run("alter database default set dbproperties ('repl.source.for' = '1, 2, 3')");
        WarehouseInstance.Tuple tuple = // TODO: this is wrong; this test sets up dummy txn manager and so it cannot create ACID tables.
        // This used to work by accident, now this works due a test flag. The test needs to be fixed.
        // Also applies for a couple more tests.
        BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t1 (i int, j int)").run((((("create database " + dbOne) + " WITH DBPROPERTIES ( '") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1,2,3')")).run(("use " + dbOne)).run(("create table t1 (i int, j int) partitioned by (load_date date) " + "clustered by(i) into 2 buckets stored as orc tblproperties ('transactional'='true') ")).run((((("create database " + dbTwo) + " WITH DBPROPERTIES ( '") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1,2,3')")).run(("use " + dbTwo)).run("create table t1 (i int, j int)").dump("`*`", null, Collections.singletonList("'hive.repl.dump.metadata.only'='true'"));
        /* Due to the limitation that we can only have one instance of Persistence Manager Factory in a JVM
        we are not able to create multiple embedded derby instances for two different MetaStore instances.
         */
        BaseReplicationAcrossInstances.primary.run((("drop database " + (primaryDbName)) + " cascade"));
        BaseReplicationAcrossInstances.primary.run((("drop database " + dbOne) + " cascade"));
        BaseReplicationAcrossInstances.primary.run((("drop database " + dbTwo) + " cascade"));
        /* End of additional steps */
        // Reset ckpt and last repl ID keys to empty set for allowing bootstrap load
        BaseReplicationAcrossInstances.replica.run("show databases").verifyFailure(new String[]{ primaryDbName, dbOne, dbTwo }).run("alter database default set dbproperties ('hive.repl.ckpt.key'='', 'repl.last.id'='')").load("", tuple.dumpLocation).run("show databases").verifyResults(new String[]{ "default", primaryDbName, dbOne, dbTwo }).run(("use " + (primaryDbName))).run("show tables").verifyResults(new String[]{ "t1" }).run(("use " + dbOne)).run("show tables").verifyResults(new String[]{ "t1" }).run(("use " + dbTwo)).run("show tables").verifyResults(new String[]{ "t1" });
        /* Start of cleanup */
        BaseReplicationAcrossInstances.replica.run((("drop database " + (primaryDbName)) + " cascade"));
        BaseReplicationAcrossInstances.replica.run((("drop database " + dbOne) + " cascade"));
        BaseReplicationAcrossInstances.replica.run((("drop database " + dbTwo) + " cascade"));
        /* End of cleanup */
    }

    @Test
    public void testIncrementalDumpOfWarehouse() throws Throwable {
        String randomOne = RandomStringUtils.random(10, true, false);
        String randomTwo = RandomStringUtils.random(10, true, false);
        String dbOne = (primaryDbName) + randomOne;
        BaseReplicationAcrossInstances.primary.run((("alter database default set dbproperties ('" + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1, 2, 3')"));
        WarehouseInstance.Tuple bootstrapTuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t1 (i int, j int)").run((((("create database " + dbOne) + " WITH DBPROPERTIES ( '") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1,2,3')")).run(("use " + dbOne)).run(("create table t1 (i int, j int) partitioned by (load_date date) " + "clustered by(i) into 2 buckets stored as orc tblproperties ('transactional'='true') ")).dump("`*`", null, Collections.singletonList("'hive.repl.dump.metadata.only'='true'"));
        String dbTwo = (primaryDbName) + randomTwo;
        WarehouseInstance.Tuple incrementalTuple = BaseReplicationAcrossInstances.primary.run((((("create database " + dbTwo) + " WITH DBPROPERTIES ( '") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1,2,3')")).run(("use " + dbTwo)).run("create table t1 (i int, j int)").run(("use " + dbOne)).run("create table t2 (a int, b int)").dump("`*`", bootstrapTuple.lastReplicationId, Arrays.asList("'hive.repl.dump.metadata.only'='true'"));
        /* Due to the limitation that we can only have one instance of Persistence Manager Factory in a JVM
        we are not able to create multiple embedded derby instances for two different MetaStore instances.
         */
        BaseReplicationAcrossInstances.primary.run((("drop database " + (primaryDbName)) + " cascade"));
        BaseReplicationAcrossInstances.primary.run((("drop database " + dbOne) + " cascade"));
        BaseReplicationAcrossInstances.primary.run((("drop database " + dbTwo) + " cascade"));
        /* End of additional steps */
        // Reset ckpt and last repl ID keys to empty set for allowing bootstrap load
        BaseReplicationAcrossInstances.replica.run("show databases").verifyFailure(new String[]{ primaryDbName, dbOne, dbTwo }).run("alter database default set dbproperties ('hive.repl.ckpt.key'='', 'repl.last.id'='')").load("", bootstrapTuple.dumpLocation).run("show databases").verifyResults(new String[]{ "default", primaryDbName, dbOne }).run(("use " + (primaryDbName))).run("show tables").verifyResults(new String[]{ "t1" }).run(("use " + dbOne)).run("show tables").verifyResults(new String[]{ "t1" });
        Assert.assertTrue(ReplUtils.isFirstIncPending(BaseReplicationAcrossInstances.replica.getDatabase("default").getParameters()));
        Assert.assertTrue(ReplUtils.isFirstIncPending(BaseReplicationAcrossInstances.replica.getDatabase(primaryDbName).getParameters()));
        Assert.assertTrue(ReplUtils.isFirstIncPending(BaseReplicationAcrossInstances.replica.getDatabase(dbOne).getParameters()));
        BaseReplicationAcrossInstances.replica.load("", incrementalTuple.dumpLocation).run("show databases").verifyResults(new String[]{ "default", primaryDbName, dbOne, dbTwo }).run(("use " + dbTwo)).run("show tables").verifyResults(new String[]{ "t1" }).run(("use " + dbOne)).run("show tables").verifyResults(new String[]{ "t1", "t2" });
        Assert.assertFalse(ReplUtils.isFirstIncPending(BaseReplicationAcrossInstances.replica.getDatabase("default").getParameters()));
        Assert.assertFalse(ReplUtils.isFirstIncPending(BaseReplicationAcrossInstances.replica.getDatabase(primaryDbName).getParameters()));
        Assert.assertFalse(ReplUtils.isFirstIncPending(BaseReplicationAcrossInstances.replica.getDatabase(dbOne).getParameters()));
        Assert.assertFalse(ReplUtils.isFirstIncPending(BaseReplicationAcrossInstances.replica.getDatabase(dbTwo).getParameters()));
        /* Start of cleanup */
        BaseReplicationAcrossInstances.replica.run((("drop database " + (primaryDbName)) + " cascade"));
        BaseReplicationAcrossInstances.replica.run((("drop database " + dbOne) + " cascade"));
        BaseReplicationAcrossInstances.replica.run((("drop database " + dbTwo) + " cascade"));
        /* End of cleanup */
    }

    @Test
    public void testReplLoadFromSourceUsingWithClause() throws Throwable {
        HiveConf replicaConf = BaseReplicationAcrossInstances.replica.getConf();
        List<String> withConfigs = Arrays.asList((("'hive.metastore.warehouse.dir'='" + (replicaConf.getVar(METASTOREWAREHOUSE))) + "'"), (("'hive.metastore.uris'='" + (replicaConf.getVar(METASTOREURIS))) + "'"), (("'hive.repl.replica.functions.root.dir'='" + (replicaConf.getVar(REPL_FUNCTIONS_ROOT_DIR))) + "'"));
        // //////////  Bootstrap   ////////////
        WarehouseInstance.Tuple bootstrapTuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table table1 (i int)").run("create table table2 (id int) partitioned by (country string)").run("insert into table1 values (1)").dump(primaryDbName, null);
        // Run load on primary itself
        BaseReplicationAcrossInstances.primary.load(replicatedDbName, bootstrapTuple.dumpLocation, withConfigs).status(replicatedDbName, withConfigs).verifyResult(bootstrapTuple.lastReplicationId);
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "table1", "table2" }).run("select * from table1").verifyResults(new String[]{ "1" });
        // //////////  First Incremental ////////////
        WarehouseInstance.Tuple incrementalOneTuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("alter table table1 rename to renamed_table1").run("insert into table2 partition(country='india') values (1) ").run("insert into table2 partition(country='usa') values (2) ").run("create table table3 (i int)").run("insert into table3 values(10)").run(((("create function " + (primaryDbName)) + ".testFunctionOne as 'hivemall.tools.string.StopwordUDF' ") + "using jar  'ivy://io.github.myui:hivemall:0.4.0-2'")).dump(primaryDbName, bootstrapTuple.lastReplicationId);
        // Run load on primary itself
        BaseReplicationAcrossInstances.primary.load(replicatedDbName, incrementalOneTuple.dumpLocation, withConfigs).status(replicatedDbName, withConfigs).verifyResult(incrementalOneTuple.lastReplicationId);
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "renamed_table1", "table2", "table3" }).run("select * from renamed_table1").verifyResults(new String[]{ "1" }).run("select id from table2 order by id").verifyResults(new String[]{ "1", "2" }).run("select * from table3").verifyResults(new String[]{ "10" }).run((("show functions like '" + (replicatedDbName)) + "*'")).verifyResult(((replicatedDbName) + ".testFunctionOne"));
        // //////////  Second Incremental ////////////
        WarehouseInstance.Tuple secondIncremental = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("alter table table2 add columns (zipcode int)").run("alter table table3 set tblproperties('custom.property'='custom.value')").run("drop table renamed_table1").run("alter table table2 drop partition(country='usa')").run("truncate table table3").run((("drop function " + (primaryDbName)) + ".testFunctionOne ")).dump(primaryDbName, incrementalOneTuple.lastReplicationId);
        // Run load on primary itself
        BaseReplicationAcrossInstances.primary.load(replicatedDbName, secondIncremental.dumpLocation, withConfigs).status(replicatedDbName, withConfigs).verifyResult(secondIncremental.lastReplicationId);
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "table2", "table3" }).run("desc table2").verifyResults(new String[]{ "id                  \tint                 \t                    ", "country             \tstring              \t                    ", "zipcode             \tint                 \t                    ", "\t \t ", "# Partition Information\t \t ", "# col_name            \tdata_type           \tcomment             ", "country             \tstring              \t                    " }).run("show tblproperties table3('custom.property')").verifyResults(new String[]{ "custom.property\tcustom.value" }).run("select id from table2 order by id").verifyResults(new String[]{ "1" }).run("select * from table3").verifyResults(Collections.emptyList()).run((("show functions like '" + (replicatedDbName)) + "*'")).verifyResult(null);
    }

    @Test
    public void testIncrementalReplWithEventsBatchHavingDropCreateTable() throws Throwable {
        // Bootstrap dump with empty db
        WarehouseInstance.Tuple bootstrapTuple = BaseReplicationAcrossInstances.primary.dump(primaryDbName, null);
        // Bootstrap load in replica
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, bootstrapTuple.dumpLocation).status(replicatedDbName).verifyResult(bootstrapTuple.lastReplicationId);
        // First incremental dump
        WarehouseInstance.Tuple firstIncremental = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table table1 (i int)").run("create table table2 (id int) partitioned by (country string)").run("insert into table1 values (1)").run("insert into table2 partition(country='india') values(1)").dump(primaryDbName, bootstrapTuple.lastReplicationId);
        // Second incremental dump
        WarehouseInstance.Tuple secondIncremental = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("drop table table1").run("drop table table2").run("create table table2 (id int) partitioned by (country string)").run("alter table table2 add partition(country='india')").run("alter table table2 drop partition(country='india')").run("insert into table2 partition(country='us') values(2)").run("create table table1 (i int)").run("insert into table1 values (2)").dump(primaryDbName, firstIncremental.lastReplicationId);
        // First incremental load
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, firstIncremental.dumpLocation).status(replicatedDbName).verifyResult(firstIncremental.lastReplicationId).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "table1", "table2" }).run("select * from table1").verifyResults(new String[]{ "1" }).run("select id from table2 order by id").verifyResults(new String[]{ "1" });
        // Second incremental load
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, secondIncremental.dumpLocation).status(replicatedDbName).verifyResult(secondIncremental.lastReplicationId).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "table1", "table2" }).run("select * from table1").verifyResults(new String[]{ "2" }).run("select id from table2 order by id").verifyResults(new String[]{ "2" });
    }

    @Test
    public void testIncrementalReplWithDropAndCreateTableDifferentPartitionTypeAndInsert() throws Throwable {
        // Bootstrap dump with empty db
        WarehouseInstance.Tuple bootstrapTuple = BaseReplicationAcrossInstances.primary.dump(primaryDbName, null);
        // Bootstrap load in replica
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, bootstrapTuple.dumpLocation).status(replicatedDbName).verifyResult(bootstrapTuple.lastReplicationId);
        // First incremental dump
        WarehouseInstance.Tuple firstIncremental = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table table1 (id int) partitioned by (country string)").run("create table table2 (id int)").run("create table table3 (id int) partitioned by (country string)").run("insert into table1 partition(country='india') values(1)").run("insert into table2 values(2)").run("insert into table3 partition(country='india') values(3)").dump(primaryDbName, bootstrapTuple.lastReplicationId);
        // Second incremental dump
        WarehouseInstance.Tuple secondIncremental = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("drop table table1").run("drop table table2").run("drop table table3").run("create table table1 (id int)").run("insert into table1 values (10)").run("create table table2 (id int) partitioned by (country string)").run("insert into table2 partition(country='india') values(20)").run("create table table3 (id int) partitioned by (name string, rank int)").run("insert into table3 partition(name='adam', rank=100) values(30)").dump(primaryDbName, firstIncremental.lastReplicationId);
        // First incremental load
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, firstIncremental.dumpLocation).status(replicatedDbName).verifyResult(firstIncremental.lastReplicationId).run(("use " + (replicatedDbName))).run("select id from table1").verifyResults(new String[]{ "1" }).run("select * from table2").verifyResults(new String[]{ "2" }).run("select id from table3").verifyResults(new String[]{ "3" });
        // Second incremental load
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, secondIncremental.dumpLocation).status(replicatedDbName).verifyResult(secondIncremental.lastReplicationId).run(("use " + (replicatedDbName))).run("select * from table1").verifyResults(new String[]{ "10" }).run("select id from table2").verifyResults(new String[]{ "20" }).run("select id from table3").verifyResults(new String[]{ "30" });
    }

    @Test
    public void testShouldNotCreateDirectoryForNonNativeTableInDumpDirectory() throws Throwable {
        String createTableQuery = "CREATE TABLE custom_serdes( serde_id bigint COMMENT 'from deserializer', name string " + (((((("COMMENT 'from deserializer', slib string COMMENT 'from deserializer') " + "ROW FORMAT SERDE 'org.apache.hive.storage.jdbc.JdbcSerDe' ") + "STORED BY 'org.apache.hive.storage.jdbc.JdbcStorageHandler' ") + "WITH SERDEPROPERTIES ('serialization.format'='1') ") + "TBLPROPERTIES ( ") + "'hive.sql.database.type'='METASTORE', ") + "\'hive.sql.query\'=\'SELECT \"SERDE_ID\", \"NAME\", \"SLIB\" FROM \"SERDES\"\')");
        WarehouseInstance.Tuple bootstrapTuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run(createTableQuery).dump(primaryDbName, null);
        Path cSerdesTableDumpLocation = new Path(new Path(bootstrapTuple.dumpLocation, primaryDbName), "custom_serdes");
        FileSystem fs = cSerdesTableDumpLocation.getFileSystem(TestReplicationScenariosAcrossInstances.primary.hiveConf);
        Assert.assertFalse(fs.exists(cSerdesTableDumpLocation));
    }

    @Test
    public void testShouldDumpMetaDataForNonNativeTableIfSetMeataDataOnly() throws Throwable {
        String tableName = (testName.getMethodName()) + "_table";
        String createTableQuery = (((((((("CREATE TABLE " + tableName) + " ( serde_id bigint COMMENT 'from deserializer', name string ") + "COMMENT 'from deserializer', slib string COMMENT 'from deserializer') ") + "ROW FORMAT SERDE 'org.apache.hive.storage.jdbc.JdbcSerDe' ") + "STORED BY 'org.apache.hive.storage.jdbc.JdbcStorageHandler' ") + "WITH SERDEPROPERTIES ('serialization.format'='1') ") + "TBLPROPERTIES ( ") + "'hive.sql.database.type'='METASTORE', ") + "\'hive.sql.query\'=\'SELECT \"SERDE_ID\", \"NAME\", \"SLIB\" FROM \"SERDES\"\')";
        WarehouseInstance.Tuple bootstrapTuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run(createTableQuery).dump(primaryDbName, null, Collections.singletonList("'hive.repl.dump.metadata.only'='true'"));
        // Bootstrap load in replica
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, bootstrapTuple.dumpLocation).status(replicatedDbName).verifyResult(bootstrapTuple.lastReplicationId).run(("use " + (replicatedDbName))).run("show tables").verifyResult(tableName);
    }

    @Test
    public void testIncrementalDumpEmptyDumpDirectory() throws Throwable {
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).status(replicatedDbName).verifyResult(tuple.lastReplicationId);
        tuple = BaseReplicationAcrossInstances.primary.dump(primaryDbName, tuple.lastReplicationId);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).status(replicatedDbName).verifyResult(tuple.lastReplicationId);
        // create events for some other database and then dump the primaryDbName to dump an empty directory.
        String testDbName = (primaryDbName) + "_test";
        tuple = BaseReplicationAcrossInstances.primary.run((" create database " + testDbName)).run((("create table " + testDbName) + ".tbl (fld int)")).dump(primaryDbName, tuple.lastReplicationId);
        // Incremental load to existing database with empty dump directory should set the repl id to the last event at src.
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).status(replicatedDbName).verifyResult(tuple.lastReplicationId);
        // Incremental load to non existing db should return database not exist error.
        tuple = BaseReplicationAcrossInstances.primary.dump("someJunkDB", tuple.lastReplicationId);
        CommandProcessorResponse response = BaseReplicationAcrossInstances.replica.runCommand((("REPL LOAD someJunkDB from '" + (tuple.dumpLocation)) + "'"));
        Assert.assertTrue(response.getErrorMessage().toLowerCase().contains("org.apache.hadoop.hive.ql.ddl.DDLTask2. Database does not exist: someJunkDB".toLowerCase()));
        // Bootstrap load from an empty dump directory should return empty load directory error.
        tuple = BaseReplicationAcrossInstances.primary.dump("someJunkDB", null);
        response = BaseReplicationAcrossInstances.replica.runCommand((("REPL LOAD someJunkDB from '" + (tuple.dumpLocation)) + "'"));
        Assert.assertTrue(response.getErrorMessage().toLowerCase().contains("semanticException no data to load in path".toLowerCase()));
        BaseReplicationAcrossInstances.primary.run(((" drop database if exists " + testDbName) + " cascade"));
    }

    @Test
    public void testIncrementalDumpMultiIteration() throws Throwable {
        WarehouseInstance.Tuple bootstrapTuple = BaseReplicationAcrossInstances.primary.dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, bootstrapTuple.dumpLocation).status(replicatedDbName).verifyResult(bootstrapTuple.lastReplicationId);
        WarehouseInstance.Tuple incremental = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table table1 (id int) partitioned by (country string)").run("create table table2 (id int)").run("create table table3 (id int) partitioned by (country string)").run("insert into table1 partition(country='india') values(1)").run("insert into table2 values(2)").run("insert into table3 partition(country='india') values(3)").dump(primaryDbName, bootstrapTuple.lastReplicationId);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, incremental.dumpLocation, Collections.singletonList("'hive.repl.approx.max.load.tasks'='10'")).status(replicatedDbName).verifyResult(incremental.lastReplicationId).run(("use " + (replicatedDbName))).run("select id from table1").verifyResults(new String[]{ "1" }).run("select * from table2").verifyResults(new String[]{ "2" }).run("select id from table3").verifyResults(new String[]{ "3" });
        assert (IncrementalLoadTasksBuilder.getNumIteration()) > 1;
        incremental = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run(("create table  table5 (key int, value int) partitioned by (load_date date) " + "clustered by(key) into 2 buckets stored as orc")).run("create table table4 (i int, j int)").run("insert into table4 values (1,2)").dump(primaryDbName, incremental.lastReplicationId);
        Path path = new Path(incremental.dumpLocation);
        FileSystem fs = path.getFileSystem(BaseReplicationAcrossInstances.conf);
        FileStatus[] fileStatus = fs.listStatus(path);
        int numEvents = (fileStatus.length) - 1;// one is metadata file

        BaseReplicationAcrossInstances.replica.load(replicatedDbName, incremental.dumpLocation, Collections.singletonList("'hive.repl.approx.max.load.tasks'='1'")).run(("use " + (replicatedDbName))).run("show tables").verifyResults(new String[]{ "table1", "table2", "table3", "table4", "table5" }).run("select i from table4").verifyResult("1");
        Assert.assertEquals(IncrementalLoadTasksBuilder.getNumIteration(), numEvents);
    }

    @Test
    public void testIfCkptAndSourceOfReplPropsIgnoredByReplDump() throws Throwable {
        WarehouseInstance.Tuple tuplePrimary = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run(("create table t1 (place string) partitioned by (country string) " + " tblproperties('custom.property'='custom.value')")).run("insert into table t1 partition(country='india') values ('bangalore')").dump(primaryDbName, null);
        // Bootstrap Repl A -> B
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuplePrimary.dumpLocation).run(("repl status " + (replicatedDbName))).verifyResult(tuplePrimary.lastReplicationId).run("show tblproperties t1('custom.property')").verifyResults(new String[]{ "custom.property\tcustom.value" }).dumpFailure(replicatedDbName, null).run((((("alter database " + (replicatedDbName)) + " set dbproperties ('") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1, 2, 3')")).dumpFailure(replicatedDbName, null);// can not dump the db before first successful incremental load is done.

        // do a empty incremental load to allow dump of replicatedDbName
        WarehouseInstance.Tuple temp = BaseReplicationAcrossInstances.primary.dump(primaryDbName, tuplePrimary.lastReplicationId);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, temp.dumpLocation);// first successful incremental load.

        // Bootstrap Repl B -> C
        WarehouseInstance.Tuple tupleReplica = BaseReplicationAcrossInstances.replica.dump(replicatedDbName, null);
        String replDbFromReplica = (replicatedDbName) + "_dupe";
        BaseReplicationAcrossInstances.replica.load(replDbFromReplica, tupleReplica.dumpLocation).run(("use " + replDbFromReplica)).run(("repl status " + replDbFromReplica)).verifyResult(tupleReplica.lastReplicationId).run("show tables").verifyResults(new String[]{ "t1" }).run("select country from t1").verifyResults(Arrays.asList("india")).run("show tblproperties t1('custom.property')").verifyResults(new String[]{ "custom.property\tcustom.value" });
        // Check if DB/table/partition in C doesn't have repl.source.for props. Also ensure, ckpt property
        // is set to bootstrap dump location used in C.
        Database db = BaseReplicationAcrossInstances.replica.getDatabase(replDbFromReplica);
        verifyIfSrcOfReplPropMissing(db.getParameters());
        verifyIfCkptSet(db.getParameters(), tupleReplica.dumpLocation);
        Table t1 = BaseReplicationAcrossInstances.replica.getTable(replDbFromReplica, "t1");
        verifyIfCkptSet(t1.getParameters(), tupleReplica.dumpLocation);
        Partition india = BaseReplicationAcrossInstances.replica.getPartition(replDbFromReplica, "t1", Collections.singletonList("india"));
        verifyIfCkptSet(india.getParameters(), tupleReplica.dumpLocation);
        // Perform alters in A for incremental replication
        WarehouseInstance.Tuple tuplePrimaryInc = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run((("alter database " + (primaryDbName)) + " set dbproperties('dummy_key'='dummy_val')")).run("alter table t1 set tblproperties('dummy_key'='dummy_val')").run("alter table t1 partition(country='india') set fileformat orc").dump(primaryDbName, tuplePrimary.lastReplicationId);
        // Incremental Repl A -> B with alters on db/table/partition
        WarehouseInstance.Tuple tupleReplicaInc = BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuplePrimaryInc.dumpLocation).run(("repl status " + (replicatedDbName))).verifyResult(tuplePrimaryInc.lastReplicationId).dump(replicatedDbName, tupleReplica.lastReplicationId);
        // Check if DB in B have ckpt property is set to bootstrap dump location used in B and missing for table/partition.
        db = BaseReplicationAcrossInstances.replica.getDatabase(replicatedDbName);
        verifyIfCkptSet(db.getParameters(), tuplePrimary.dumpLocation);
        t1 = BaseReplicationAcrossInstances.replica.getTable(replicatedDbName, "t1");
        verifyIfCkptPropMissing(t1.getParameters());
        india = BaseReplicationAcrossInstances.replica.getPartition(replicatedDbName, "t1", Collections.singletonList("india"));
        verifyIfCkptPropMissing(india.getParameters());
        // Incremental Repl B -> C with alters on db/table/partition
        BaseReplicationAcrossInstances.replica.load(replDbFromReplica, tupleReplicaInc.dumpLocation).run(("use " + replDbFromReplica)).run(("repl status " + replDbFromReplica)).verifyResult(tupleReplicaInc.lastReplicationId).run("show tblproperties t1('custom.property')").verifyResults(new String[]{ "custom.property\tcustom.value" });
        // Check if DB/table/partition in C doesn't have repl.source.for props. Also ensure, ckpt property
        // in DB is set to bootstrap dump location used in C but for table/partition, it is missing.
        db = BaseReplicationAcrossInstances.replica.getDatabase(replDbFromReplica);
        verifyIfCkptSet(db.getParameters(), tupleReplica.dumpLocation);
        verifyIfSrcOfReplPropMissing(db.getParameters());
        t1 = BaseReplicationAcrossInstances.replica.getTable(replDbFromReplica, "t1");
        verifyIfCkptPropMissing(t1.getParameters());
        india = BaseReplicationAcrossInstances.replica.getPartition(replDbFromReplica, "t1", Collections.singletonList("india"));
        verifyIfCkptPropMissing(india.getParameters());
        BaseReplicationAcrossInstances.replica.run((("drop database if exists " + replDbFromReplica) + " cascade"));
    }

    @Test
    public void testIfCkptPropIgnoredByExport() throws Throwable {
        WarehouseInstance.Tuple tuplePrimary = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t1 (place string) partitioned by (country string)").run("insert into table t1 partition(country='india') values ('bangalore')").dump(primaryDbName, null);
        // Bootstrap Repl A -> B and then export table t1
        String path = ("hdfs:///tmp/" + (replicatedDbName)) + "/";
        String exportPath = ("'" + path) + "1/'";
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuplePrimary.dumpLocation).run(("repl status " + (replicatedDbName))).verifyResult(tuplePrimary.lastReplicationId).run(("use " + (replicatedDbName))).run(("export table t1 to " + exportPath));
        // Check if ckpt property set in table/partition in B after bootstrap load.
        Table t1 = BaseReplicationAcrossInstances.replica.getTable(replicatedDbName, "t1");
        verifyIfCkptSet(t1.getParameters(), tuplePrimary.dumpLocation);
        Partition india = BaseReplicationAcrossInstances.replica.getPartition(replicatedDbName, "t1", Collections.singletonList("india"));
        verifyIfCkptSet(india.getParameters(), tuplePrimary.dumpLocation);
        // Import table t1 to C
        String importDbFromReplica = (replicatedDbName) + "_dupe";
        BaseReplicationAcrossInstances.replica.run(("create database " + importDbFromReplica)).run(("use " + importDbFromReplica)).run(("import table t1 from " + exportPath)).run("select country from t1").verifyResults(Collections.singletonList("india"));
        // Check if table/partition in C doesn't have ckpt property
        t1 = BaseReplicationAcrossInstances.replica.getTable(importDbFromReplica, "t1");
        verifyIfCkptPropMissing(t1.getParameters());
        india = BaseReplicationAcrossInstances.replica.getPartition(importDbFromReplica, "t1", Collections.singletonList("india"));
        verifyIfCkptPropMissing(india.getParameters());
        BaseReplicationAcrossInstances.replica.run((("drop database if exists " + importDbFromReplica) + " cascade"));
    }

    @Test
    public void testIfBootstrapReplLoadFailWhenRetryAfterBootstrapComplete() throws Throwable {
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t1 (id int)").run("insert into table t1 values (10)").run("create table t2 (place string) partitioned by (country string)").run("insert into table t2 partition(country='india') values ('bangalore')").run("insert into table t2 partition(country='uk') values ('london')").run("insert into table t2 partition(country='us') values ('sfo')").dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation).run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run("show tables").verifyResults(new String[]{ "t1", "t2" }).run("select id from t1").verifyResults(Collections.singletonList("10")).run("select country from t2 order by country").verifyResults(Arrays.asList("india", "uk", "us"));
        BaseReplicationAcrossInstances.replica.verifyIfCkptSet(replicatedDbName, tuple.dumpLocation);
        WarehouseInstance.Tuple tuple_2 = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).dump(primaryDbName, null);
        // Retry with different dump should fail.
        BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple_2.dumpLocation);
        // Retry with same dump with which it was already loaded also fails.
        BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple.dumpLocation);
        // Retry from same dump when the database is empty is also not allowed.
        BaseReplicationAcrossInstances.replica.run("drop table t1").run("drop table t2").loadFailure(replicatedDbName, tuple.dumpLocation);
    }

    @Test
    public void testBootstrapReplLoadRetryAfterFailureForTablesAndConstraints() throws Throwable {
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t1(a string, b string, primary key (a, b) disable novalidate rely)").run("create table t2(a string, b string, foreign key (a, b) references t1(a, b) disable novalidate)").run("create table t3(a string, b string not null disable, unique (a) disable)").dump(primaryDbName, null);
        WarehouseInstance.Tuple tuple2 = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).dump(primaryDbName, null);
        // Need to drop the primary DB as metastore is shared by both primary/replica. So, constraints
        // conflict when loaded. Some issue with framework which needs to be relook into later.
        BaseReplicationAcrossInstances.primary.run((("drop database if exists " + (primaryDbName)) + " cascade"));
        // Allow create table only on t1. Create should fail for rest of the tables and hence constraints
        // also not loaded.
        BehaviourInjection<CallerArguments, Boolean> callerVerifier = new BehaviourInjection<CallerArguments, Boolean>() {
            @Override
            public Boolean apply(CallerArguments args) {
                injectionPathCalled = true;
                if ((!(args.dbName.equalsIgnoreCase(replicatedDbName))) || ((args.constraintTblName) != null)) {
                    BaseReplicationAcrossInstances.LOG.warn(((("Verifier - DB: " + (String.valueOf(args.dbName))) + " Constraint Table: ") + (String.valueOf(args.constraintTblName))));
                    return false;
                }
                if ((args.tblName) != null) {
                    BaseReplicationAcrossInstances.LOG.warn(("Verifier - Table: " + (String.valueOf(args.tblName))));
                    return args.tblName.equals("t1");
                }
                return true;
            }
        };
        InjectableBehaviourObjectStore.setCallerVerifier(callerVerifier);
        // Trigger bootstrap dump which just creates table t1 and other tables (t2, t3) and constraints not loaded.
        List<String> withConfigs = Arrays.asList("'hive.repl.approx.max.load.tasks'='1'");
        try {
            BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple.dumpLocation, withConfigs);
            callerVerifier.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetCallerVerifier();// reset the behaviour

        }
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult("null");
        Assert.assertEquals(0, BaseReplicationAcrossInstances.replica.getPrimaryKeyList(replicatedDbName, "t1").size());
        Assert.assertEquals(0, BaseReplicationAcrossInstances.replica.getUniqueConstraintList(replicatedDbName, "t3").size());
        Assert.assertEquals(0, BaseReplicationAcrossInstances.replica.getNotNullConstraintList(replicatedDbName, "t3").size());
        Assert.assertEquals(0, BaseReplicationAcrossInstances.replica.getForeignKeyList(replicatedDbName, "t2").size());
        // Retry with different dump should fail.
        BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple2.dumpLocation, null, REPL_BOOTSTRAP_LOAD_PATH_NOT_VALID.getErrorCode());
        // Verify if create table is not called on table t1 but called for t2 and t3.
        // Also, allow constraint creation only on t1 and t3. Foreign key creation on t2 fails.
        callerVerifier = new BehaviourInjection<CallerArguments, Boolean>() {
            @Override
            public Boolean apply(CallerArguments args) {
                injectionPathCalled = true;
                if ((!(args.dbName.equalsIgnoreCase(replicatedDbName))) || ((args.funcName) != null)) {
                    BaseReplicationAcrossInstances.LOG.warn(((("Verifier - DB: " + (String.valueOf(args.dbName))) + " Func: ") + (String.valueOf(args.funcName))));
                    return false;
                }
                if ((args.constraintTblName) != null) {
                    BaseReplicationAcrossInstances.LOG.warn(("Verifier - Constraint Table: " + (String.valueOf(args.constraintTblName))));
                    return (args.constraintTblName.equals("t1")) || (args.constraintTblName.equals("t3"));
                }
                return true;
            }
        };
        InjectableBehaviourObjectStore.setCallerVerifier(callerVerifier);
        try {
            // Retry with same dump with which it was already loaded should resume the bootstrap load.
            // This time, it fails when try to load the foreign key constraints. All other constraints are loaded.
            BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple.dumpLocation, withConfigs);
            callerVerifier.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetCallerVerifier();// reset the behaviour

        }
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult("null").run("show tables").verifyResults(new String[]{ "t1", "t2", "t3" });
        Assert.assertEquals(2, BaseReplicationAcrossInstances.replica.getPrimaryKeyList(replicatedDbName, "t1").size());
        Assert.assertEquals(1, BaseReplicationAcrossInstances.replica.getUniqueConstraintList(replicatedDbName, "t3").size());
        Assert.assertEquals(1, BaseReplicationAcrossInstances.replica.getNotNullConstraintList(replicatedDbName, "t3").size());
        Assert.assertEquals(0, BaseReplicationAcrossInstances.replica.getForeignKeyList(replicatedDbName, "t2").size());
        // Verify if no create table/function calls. Only add foreign key constraints on table t2.
        callerVerifier = new BehaviourInjection<CallerArguments, Boolean>() {
            @Override
            public Boolean apply(CallerArguments args) {
                injectionPathCalled = true;
                if ((!(args.dbName.equalsIgnoreCase(replicatedDbName))) || ((args.tblName) != null)) {
                    BaseReplicationAcrossInstances.LOG.warn(((("Verifier - DB: " + (String.valueOf(args.dbName))) + " Table: ") + (String.valueOf(args.tblName))));
                    return false;
                }
                if ((args.constraintTblName) != null) {
                    BaseReplicationAcrossInstances.LOG.warn(("Verifier - Constraint Table: " + (String.valueOf(args.constraintTblName))));
                    return args.constraintTblName.equals("t2");
                }
                return true;
            }
        };
        InjectableBehaviourObjectStore.setCallerVerifier(callerVerifier);
        try {
            // Retry with same dump with which it was already loaded should resume the bootstrap load.
            // This time, it completes by adding just foreign key constraints for table t2.
            BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation);
            callerVerifier.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetCallerVerifier();// reset the behaviour

        }
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run("show tables").verifyResults(new String[]{ "t1", "t2", "t3" });
        Assert.assertEquals(2, BaseReplicationAcrossInstances.replica.getPrimaryKeyList(replicatedDbName, "t1").size());
        Assert.assertEquals(1, BaseReplicationAcrossInstances.replica.getUniqueConstraintList(replicatedDbName, "t3").size());
        Assert.assertEquals(1, BaseReplicationAcrossInstances.replica.getNotNullConstraintList(replicatedDbName, "t3").size());
        Assert.assertEquals(2, BaseReplicationAcrossInstances.replica.getForeignKeyList(replicatedDbName, "t2").size());
    }

    @Test
    public void testBootstrapReplLoadRetryAfterFailureForPartitions() throws Throwable {
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t2 (place string) partitioned by (country string)").run("insert into table t2 partition(country='india') values ('bangalore')").run("insert into table t2 partition(country='uk') values ('london')").run("insert into table t2 partition(country='us') values ('sfo')").run(((("CREATE FUNCTION " + (primaryDbName)) + ".testFunctionOne as 'hivemall.tools.string.StopwordUDF' ") + "using jar  'ivy://io.github.myui:hivemall:0.4.0-2'")).dump(primaryDbName, null);
        WarehouseInstance.Tuple tuple2 = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).dump(primaryDbName, null);
        // Inject a behavior where REPL LOAD failed when try to load table "t2" and partition "uk".
        // So, table "t2" will exist and partition "india" will exist, rest failed as operation failed.
        BehaviourInjection<Partition, Partition> getPartitionStub = new BehaviourInjection<Partition, Partition>() {
            @Nullable
            @Override
            public Partition apply(@Nullable
            Partition ptn) {
                if (ptn.getValues().get(0).equals("india")) {
                    injectionPathCalled = true;
                    BaseReplicationAcrossInstances.LOG.warn("####getPartition Stub called");
                    return null;
                }
                return ptn;
            }
        };
        InjectableBehaviourObjectStore.setGetPartitionBehaviour(getPartitionStub);
        // Make sure that there's some order in which the objects are loaded.
        List<String> withConfigs = Arrays.asList("'hive.repl.approx.max.load.tasks'='1'", "'hive.in.repl.test.files.sorted'='true'");
        BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple.dumpLocation, withConfigs);
        InjectableBehaviourObjectStore.resetGetPartitionBehaviour();// reset the behaviour

        getPartitionStub.assertInjectionsPerformed(true, false);
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult("null").run("show tables").verifyResults(new String[]{ "t2" }).run("select country from t2 order by country").verifyResults(Collections.singletonList("india"));
        // Retry with different dump should fail.
        BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple2.dumpLocation);
        // Verify if no create table calls. Add partitions and create function calls expected.
        BehaviourInjection<CallerArguments, Boolean> callerVerifier = new BehaviourInjection<CallerArguments, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable
            CallerArguments args) {
                if ((!(args.dbName.equalsIgnoreCase(replicatedDbName))) || ((args.tblName) != null)) {
                    injectionPathCalled = true;
                    BaseReplicationAcrossInstances.LOG.warn(((("Verifier - DB: " + (String.valueOf(args.dbName))) + " Table: ") + (String.valueOf(args.tblName))));
                    return false;
                }
                return true;
            }
        };
        InjectableBehaviourObjectStore.setCallerVerifier(callerVerifier);
        try {
            // Retry with same dump with which it was already loaded should resume the bootstrap load.
            // This time, it completes by adding remaining partitions and function.
            BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation);
            callerVerifier.assertInjectionsPerformed(false, false);
        } finally {
            InjectableBehaviourObjectStore.resetCallerVerifier();// reset the behaviour

        }
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run("show tables").verifyResults(new String[]{ "t2" }).run("select country from t2 order by country").verifyResults(Arrays.asList("india", "uk", "us")).run((("show functions like '" + (replicatedDbName)) + "*'")).verifyResult(((replicatedDbName) + ".testFunctionOne"));
    }

    @Test
    public void testMoveOptimizationBootstrapReplLoadRetryAfterFailure() throws Throwable {
        String replicatedDbName_CM = (replicatedDbName) + "_CM";
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t2 (place string) partitioned by (country string)").run("insert into table t2 partition(country='india') values ('bangalore')").dump(primaryDbName, null);
        testMoveOptimization(primaryDbName, replicatedDbName, replicatedDbName_CM, "t2", "ADD_PARTITION", tuple);
    }

    @Test
    public void testMoveOptimizationIncrementalFailureAfterCopyReplace() throws Throwable {
        List<String> withConfigs = Collections.singletonList("'hive.repl.enable.move.optimization'='true'");
        String replicatedDbName_CM = (replicatedDbName) + "_CM";
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t2 (place string) partitioned by (country string)").run("insert into table t2 partition(country='india') values ('bangalore')").run("create table t1 (place string) partitioned by (country string)").dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation, withConfigs);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName_CM, tuple.dumpLocation, withConfigs);
        BaseReplicationAcrossInstances.replica.run((((("alter database " + (replicatedDbName)) + " set DBPROPERTIES ('") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1,2,3')")).run((((("alter database " + replicatedDbName_CM) + " set DBPROPERTIES ('") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1,2,3')"));
        tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("insert overwrite table t1 select * from t2").dump(primaryDbName, tuple.lastReplicationId);
        testMoveOptimization(primaryDbName, replicatedDbName, replicatedDbName_CM, "t1", "ADD_PARTITION", tuple);
    }

    @Test
    public void testMoveOptimizationIncrementalFailureAfterCopy() throws Throwable {
        List<String> withConfigs = Collections.singletonList("'hive.repl.enable.move.optimization'='true'");
        String replicatedDbName_CM = (replicatedDbName) + "_CM";
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t2 (place string) partitioned by (country string)").run("ALTER TABLE t2 ADD PARTITION (country='india')").dump(primaryDbName, null);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation, withConfigs);
        BaseReplicationAcrossInstances.replica.load(replicatedDbName_CM, tuple.dumpLocation, withConfigs);
        BaseReplicationAcrossInstances.replica.run((((("alter database " + (replicatedDbName)) + " set DBPROPERTIES ('") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1,2,3')")).run((((("alter database " + replicatedDbName_CM) + " set DBPROPERTIES ('") + (ReplChangeManager.SOURCE_OF_REPLICATION)) + "' = '1,2,3')"));
        tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("insert into table t2 partition(country='india') values ('bangalore')").dump(primaryDbName, tuple.lastReplicationId);
        testMoveOptimization(primaryDbName, replicatedDbName, replicatedDbName_CM, "t2", "INSERT", tuple);
    }

    // This requires the tables are loaded in a fixed sorted order.
    @Test
    public void testBootstrapLoadRetryAfterFailureForAlterTable() throws Throwable {
        WarehouseInstance.Tuple tuple = BaseReplicationAcrossInstances.primary.run(("use " + (primaryDbName))).run("create table t1 (place string)").run("insert into table t1 values ('testCheck')").run("create table t2 (place string) partitioned by (country string)").run("insert into table t2 partition(country='china') values ('shenzhen')").run("insert into table t2 partition(country='india') values ('banaglore')").dump(primaryDbName, null);
        // fail setting ckpt directory property for table t1.
        BehaviourInjection<CallerArguments, Boolean> callerVerifier = new BehaviourInjection<CallerArguments, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable
            CallerArguments args) {
                if ((args.tblName.equalsIgnoreCase("t1")) && (args.dbName.equalsIgnoreCase(replicatedDbName))) {
                    injectionPathCalled = true;
                    BaseReplicationAcrossInstances.LOG.warn(((("Verifier - DB : " + (args.dbName)) + " TABLE : ") + (args.tblName)));
                    return false;
                }
                return true;
            }
        };
        // Fail repl load before the ckpt proeprty is set for t1 and after it is set for t2. So in the next run, for
        // t2 it goes directly to partion load with no task for table tracker and for t1 it loads the table
        // again from start.
        InjectableBehaviourObjectStore.setAlterTableModifier(callerVerifier);
        try {
            BaseReplicationAcrossInstances.replica.loadFailure(replicatedDbName, tuple.dumpLocation);
            callerVerifier.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetAlterTableModifier();
        }
        // Retry with same dump with which it was already loaded should resume the bootstrap load. Make sure that table t1,
        // is loaded before t2. So that scope is set to table in first iteration for table t1. In the next iteration, it
        // loads only remaining partitions of t2, so that the table tracker has no tasks.
        List<String> withConfigs = Arrays.asList("'hive.in.repl.test.files.sorted'='true'");
        BaseReplicationAcrossInstances.replica.load(replicatedDbName, tuple.dumpLocation, withConfigs);
        BaseReplicationAcrossInstances.replica.run(("use " + (replicatedDbName))).run(("repl status " + (replicatedDbName))).verifyResult(tuple.lastReplicationId).run("select country from t2 order by country").verifyResults(Arrays.asList("china", "india"));
    }
}
