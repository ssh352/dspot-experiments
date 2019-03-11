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
package org.apache.hadoop.hive.metastore.client;


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for exchanging partitions.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestExchangePartitions extends MetaStoreClientTest {
    private AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private static final String DB_NAME = "test_partition_db";

    private static final String STRING_COL_TYPE = "string";

    private static final String INT_COL_TYPE = "int";

    private static final String YEAR_COL_NAME = "year";

    private static final String MONTH_COL_NAME = "month";

    private static final String DAY_COL_NAME = "day";

    private static final short MAX = -1;

    private static Table sourceTable;

    private static Table destTable;

    private static Partition[] partitions;

    public TestExchangePartitions(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    // Tests for the List<Partition> exchange_partitions(Map<String, String> partitionSpecs, String
    // sourceDb, String sourceTable, String destdb, String destTableName) method
    @Test
    public void testExchangePartitions() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        List<Partition> exchangedPartitions = client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        Assert.assertEquals(1, exchangedPartitions.size());
        String partitionName = Warehouse.makePartName(TestExchangePartitions.sourceTable.getPartitionKeys(), TestExchangePartitions.partitions[1].getValues());
        String exchangedPartitionName = Warehouse.makePartName(TestExchangePartitions.sourceTable.getPartitionKeys(), exchangedPartitions.get(0).getValues());
        Assert.assertEquals(partitionName, exchangedPartitionName);
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionsDestTableHasPartitions() throws Exception {
        // Create dest table partitions with custom locations
        createPartition(TestExchangePartitions.destTable, Lists.newArrayList("2019", "march", "15"), ((TestExchangePartitions.destTable.getSd().getLocation()) + "/destPart1"));
        createPartition(TestExchangePartitions.destTable, Lists.newArrayList("2019", "march", "22"), ((TestExchangePartitions.destTable.getSd().getLocation()) + "/destPart2"));
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.DB_NAME, TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        // Check the original partitions of the dest table
        List<String> partitionNames = client.listPartitionNames(TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName(), TestExchangePartitions.MAX);
        Assert.assertEquals(3, partitionNames.size());
        Assert.assertTrue(partitionNames.containsAll(Lists.newArrayList("year=2019/month=march/day=15", "year=2019/month=march/day=22")));
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(((TestExchangePartitions.destTable.getSd().getLocation()) + "/destPart1"))));
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(((TestExchangePartitions.destTable.getSd().getLocation()) + "/destPart2"))));
    }

    @Test
    public void testExchangePartitionsYearSet() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "", ""));
        List<Partition> exchangedPartitions = client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        Assert.assertEquals(4, exchangedPartitions.size());
        List<String> exchangedPartNames = new ArrayList<>();
        for (Partition exchangedPartition : exchangedPartitions) {
            String partName = Warehouse.makePartName(TestExchangePartitions.sourceTable.getPartitionKeys(), exchangedPartition.getValues());
            exchangedPartNames.add(partName);
        }
        Assert.assertTrue(exchangedPartNames.contains("year=2017/month=march/day=15"));
        Assert.assertTrue(exchangedPartNames.contains("year=2017/month=march/day=22"));
        Assert.assertTrue(exchangedPartNames.contains("year=2017/month=april/day=23"));
        Assert.assertTrue(exchangedPartNames.contains("year=2017/month=may/day=23"));
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionsYearAndMonthSet() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "march", ""));
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionsBetweenDBs() throws Exception {
        String dbName = "newDatabase";
        createDB(dbName);
        Table dest = createTable(dbName, "test_dest_table_diff_db", TestExchangePartitions.getYearMonthAndDayPartCols(), null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "march", ""));
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, dest, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, dest, Lists.newArrayList(TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        client.dropDatabase(dbName, true, true, true);
    }

    @Test
    public void testExchangePartitionsCustomTableLocations() throws Exception {
        Table source = createTable(TestExchangePartitions.DB_NAME, "test_source_table_cust_loc", TestExchangePartitions.getYearMonthAndDayPartCols(), ((metaStore.getWarehouseRoot()) + "/sourceTable"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table_cust_loc", TestExchangePartitions.getYearMonthAndDayPartCols(), ((metaStore.getWarehouseRoot()) + "/destTable"));
        Partition[] parts = new Partition[2];
        parts[0] = createPartition(source, Lists.newArrayList("2019", "may", "15"), null);
        parts[1] = createPartition(source, Lists.newArrayList("2019", "june", "14"), null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(parts[1]);
        client.exchange_partitions(partitionSpecs, source.getDbName(), source.getTableName(), dest.getDbName(), dest.getTableName());
        checkExchangedPartitions(source, dest, Lists.newArrayList(parts[1]));
        checkRemainingPartitions(source, dest, Lists.newArrayList(parts[0]));
    }

    @Test
    public void testExchangePartitionsCustomTableAndPartLocation() throws Exception {
        Table source = createTable(TestExchangePartitions.DB_NAME, "test_source_table_cust_loc", TestExchangePartitions.getYearMonthAndDayPartCols(), ((metaStore.getWarehouseRoot()) + "/sourceTable"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table_cust_loc", TestExchangePartitions.getYearMonthAndDayPartCols(), ((metaStore.getWarehouseRoot()) + "/destTable"));
        Partition[] parts = new Partition[2];
        parts[0] = createPartition(source, Lists.newArrayList("2019", "may", "11"), ((source.getSd().getLocation()) + "/2019m11"));
        parts[1] = createPartition(source, Lists.newArrayList("2019", "july", "23"), ((source.getSd().getLocation()) + "/2019j23"));
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(parts[1]);
        try {
            client.exchange_partitions(partitionSpecs, source.getDbName(), source.getTableName(), dest.getDbName(), dest.getTableName());
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception as FileNotFoundException will occur if the partitions have custom
            // location
        }
        checkRemainingPartitions(source, dest, Lists.newArrayList(parts[0], parts[1]));
        List<Partition> destTablePartitions = client.listPartitions(dest.getDbName(), dest.getTableName(), ((short) (-1)));
        Assert.assertTrue(destTablePartitions.isEmpty());
    }

    @Test
    public void testExchangePartitionsCustomPartLocation() throws Exception {
        Table source = createTable(TestExchangePartitions.DB_NAME, "test_source_table", TestExchangePartitions.getYearMonthAndDayPartCols(), null);
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", TestExchangePartitions.getYearMonthAndDayPartCols(), null);
        Partition[] parts = new Partition[2];
        parts[0] = createPartition(source, Lists.newArrayList("2019", "march", "15"), ((source.getSd().getLocation()) + "/2019m15"));
        parts[1] = createPartition(source, Lists.newArrayList("2019", "march", "22"), ((source.getSd().getLocation()) + "/2019m22"));
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(parts[1]);
        try {
            client.exchange_partitions(partitionSpecs, source.getDbName(), source.getTableName(), dest.getDbName(), dest.getTableName());
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception as FileNotFoundException will occur if the partitions have custom
            // location
        }
        checkRemainingPartitions(source, dest, Lists.newArrayList(parts[0], parts[1]));
        List<Partition> destTablePartitions = client.listPartitions(dest.getDbName(), dest.getTableName(), ((short) (-1)));
        Assert.assertTrue(destTablePartitions.isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNonExistingPartLocation() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        metaStore.cleanWarehouseDirs();
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNonExistingSourceTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.DB_NAME, "nonexistingtable", TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNonExistingSourceDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, "nonexistingdb", TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNonExistingDestTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, "nonexistingtable");
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNonExistingDestDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), "nonexistingdb", TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsEmptySourceTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.DB_NAME, "", TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsEmptySourceDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, "", TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsEmptyDestTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, "");
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsEmptyDestDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), "", TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNullSourceTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.DB_NAME, null, TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNullSourceDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, null, TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNullDestTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, null);
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNullDestDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), null, TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsEmptyPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNullPartSpec() throws Exception {
        client.exchange_partitions(null, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), null, TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsPartAlreadyExists() throws Exception {
        Partition partition = buildPartition(TestExchangePartitions.destTable, Lists.newArrayList("2017", "march", "22"), null);
        client.add_partition(partition);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.DB_NAME, TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, TestExchangePartitions.destTable.getTableName());
    }

    @Test
    public void testExchangePartitionsOneFail() throws Exception {
        Partition partition = buildPartition(TestExchangePartitions.destTable, Lists.newArrayList("2017", "march", "22"), null);
        client.add_partition(partition);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "", ""));
        try {
            client.exchange_partitions(partitionSpecs, TestExchangePartitions.DB_NAME, TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, TestExchangePartitions.destTable.getTableName());
            Assert.fail("Exception should have been thrown as one of the partitions already exists in the dest table.");
        } catch (MetaException e) {
            // Expected exception
        }
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        List<Partition> partitionsInDestTable = client.listPartitions(TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName(), TestExchangePartitions.MAX);
        Assert.assertEquals(1, partitionsInDestTable.size());
        Assert.assertEquals(TestExchangePartitions.partitions[1].getValues(), partitionsInDestTable.get(0).getValues());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(partitionsInDestTable.get(0).getSd().getLocation())));
        Partition resultPart = client.getPartition(TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.partitions[1].getValues());
        Assert.assertNotNull(resultPart);
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(TestExchangePartitions.partitions[1].getSd().getLocation())));
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsDifferentColsInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema("test_id", TestExchangePartitions.INT_COL_TYPE, "test col id"));
        cols.add(new FieldSchema("test_value", TestExchangePartitions.STRING_COL_TYPE, "test col value"));
        cols.add(new FieldSchema("test_name", TestExchangePartitions.STRING_COL_TYPE, "test col name"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", TestExchangePartitions.getYearMonthAndDayPartCols(), cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsDifferentColNameInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema("id", TestExchangePartitions.INT_COL_TYPE, "test col id"));
        cols.add(new FieldSchema("test_value", TestExchangePartitions.STRING_COL_TYPE, "test col value"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", TestExchangePartitions.getYearMonthAndDayPartCols(), cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsDifferentColTypesInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema("test_id", TestExchangePartitions.STRING_COL_TYPE, "test col id"));
        cols.add(new FieldSchema("test_value", TestExchangePartitions.STRING_COL_TYPE, "test col value"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", TestExchangePartitions.getYearMonthAndDayPartCols(), cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsDifferentPartColsInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(TestExchangePartitions.YEAR_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "year part col"));
        cols.add(new FieldSchema(TestExchangePartitions.MONTH_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "month part col"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsDifferentPartColNameInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(TestExchangePartitions.YEAR_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "year part col"));
        cols.add(new FieldSchema(TestExchangePartitions.MONTH_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "month part col"));
        cols.add(new FieldSchema("nap", TestExchangePartitions.STRING_COL_TYPE, "day part col"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsDifferentPartColTypesInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(TestExchangePartitions.YEAR_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "year part col"));
        cols.add(new FieldSchema(TestExchangePartitions.MONTH_COL_NAME, TestExchangePartitions.INT_COL_TYPE, "month part col"));
        cols.add(new FieldSchema(TestExchangePartitions.DAY_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "day part col"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test
    public void testExchangePartitionsLessValueInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "2017");
        partitionSpecs.put(TestExchangePartitions.MONTH_COL_NAME, "march");
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionsMoreValueInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "2017");
        partitionSpecs.put(TestExchangePartitions.MONTH_COL_NAME, "march");
        partitionSpecs.put(TestExchangePartitions.DAY_COL_NAME, "22");
        partitionSpecs.put("hour", "18");
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionsDifferentValuesInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "2017");
        partitionSpecs.put("honap", "march");
        partitionSpecs.put("nap", "22");
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[4]));
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNonExistingValuesInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put("ev", "2017");
        partitionSpecs.put("honap", "march");
        partitionSpecs.put("nap", "22");
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test
    public void testExchangePartitionsOnlyMonthSetInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "");
        partitionSpecs.put(TestExchangePartitions.MONTH_COL_NAME, "march");
        partitionSpecs.put(TestExchangePartitions.DAY_COL_NAME, "");
        try {
            client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        List<Partition> partsInDestTable = client.listPartitions(TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName(), TestExchangePartitions.MAX);
        Assert.assertTrue(partsInDestTable.isEmpty());
    }

    @Test
    public void testExchangePartitionsYearAndDaySetInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "2017");
        partitionSpecs.put(TestExchangePartitions.MONTH_COL_NAME, "");
        partitionSpecs.put(TestExchangePartitions.DAY_COL_NAME, "22");
        try {
            client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        List<Partition> partsInDestTable = client.listPartitions(TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName(), TestExchangePartitions.MAX);
        Assert.assertTrue(partsInDestTable.isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNoPartExists() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "march", "25"));
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionsNoPartExistsYearAndMonthSet() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "august", ""));
        client.exchange_partitions(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    // Tests for the Partition exchange_partition(Map<String, String> partitionSpecs, String
    // sourceDb, String sourceTable, String destdb, String destTableName) method
    @Test
    public void testExchangePartition() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        Partition exchangedPartition = client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        Assert.assertEquals(new Partition(), exchangedPartition);
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionDestTableHasPartitions() throws Exception {
        // Create dest table partitions with custom locations
        createPartition(TestExchangePartitions.destTable, Lists.newArrayList("2019", "march", "15"), ((TestExchangePartitions.destTable.getSd().getLocation()) + "/destPart1"));
        createPartition(TestExchangePartitions.destTable, Lists.newArrayList("2019", "march", "22"), ((TestExchangePartitions.destTable.getSd().getLocation()) + "/destPart2"));
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.DB_NAME, TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        // Check the original partitions of the dest table
        List<String> partitionNames = client.listPartitionNames(TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName(), TestExchangePartitions.MAX);
        Assert.assertEquals(3, partitionNames.size());
        Assert.assertTrue(partitionNames.containsAll(Lists.newArrayList("year=2019/month=march/day=15", "year=2019/month=march/day=22")));
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(((TestExchangePartitions.destTable.getSd().getLocation()) + "/destPart1"))));
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(((TestExchangePartitions.destTable.getSd().getLocation()) + "/destPart2"))));
    }

    @Test
    public void testExchangePartitionYearSet() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "", ""));
        Partition exchangedPartition = client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        Assert.assertEquals(new Partition(), exchangedPartition);
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionYearAndMonthSet() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "march", ""));
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionBetweenDBs() throws Exception {
        String dbName = "newDatabase";
        createDB(dbName);
        Table dest = createTable(dbName, "test_dest_table_diff_db", TestExchangePartitions.getYearMonthAndDayPartCols(), null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "march", ""));
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, dest, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, dest, Lists.newArrayList(TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        client.dropDatabase(dbName, true, true, true);
    }

    @Test
    public void testExchangePartitionCustomTableLocations() throws Exception {
        Table source = createTable(TestExchangePartitions.DB_NAME, "test_source_table_cust_loc", TestExchangePartitions.getYearMonthAndDayPartCols(), ((metaStore.getWarehouseRoot()) + "/sourceTable"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table_cust_loc", TestExchangePartitions.getYearMonthAndDayPartCols(), ((metaStore.getWarehouseRoot()) + "/destTable"));
        Partition[] parts = new Partition[2];
        parts[0] = createPartition(source, Lists.newArrayList("2019", "may", "15"), null);
        parts[1] = createPartition(source, Lists.newArrayList("2019", "june", "14"), null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(parts[1]);
        client.exchange_partition(partitionSpecs, source.getDbName(), source.getTableName(), dest.getDbName(), dest.getTableName());
        checkExchangedPartitions(source, dest, Lists.newArrayList(parts[1]));
        checkRemainingPartitions(source, dest, Lists.newArrayList(parts[0]));
    }

    @Test
    public void testExchangePartitionCustomTableAndPartLocation() throws Exception {
        Table source = createTable(TestExchangePartitions.DB_NAME, "test_source_table_cust_loc", TestExchangePartitions.getYearMonthAndDayPartCols(), ((metaStore.getWarehouseRoot()) + "/sourceTable"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table_cust_loc", TestExchangePartitions.getYearMonthAndDayPartCols(), ((metaStore.getWarehouseRoot()) + "/destTable"));
        Partition[] parts = new Partition[2];
        parts[0] = createPartition(source, Lists.newArrayList("2019", "may", "11"), ((source.getSd().getLocation()) + "/2019m11"));
        parts[1] = createPartition(source, Lists.newArrayList("2019", "july", "23"), ((source.getSd().getLocation()) + "/2019j23"));
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(parts[1]);
        try {
            client.exchange_partition(partitionSpecs, source.getDbName(), source.getTableName(), dest.getDbName(), dest.getTableName());
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception as FileNotFoundException will occur if the partitions have custom
            // location
        }
        checkRemainingPartitions(source, dest, Lists.newArrayList(parts[0], parts[1]));
        List<Partition> destTablePartitions = client.listPartitions(dest.getDbName(), dest.getTableName(), ((short) (-1)));
        Assert.assertTrue(destTablePartitions.isEmpty());
    }

    @Test
    public void testExchangePartitionCustomPartLocation() throws Exception {
        Table source = createTable(TestExchangePartitions.DB_NAME, "test_source_table", TestExchangePartitions.getYearMonthAndDayPartCols(), null);
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", TestExchangePartitions.getYearMonthAndDayPartCols(), null);
        Partition[] parts = new Partition[2];
        parts[0] = createPartition(source, Lists.newArrayList("2019", "march", "15"), ((source.getSd().getLocation()) + "/2019m15"));
        parts[1] = createPartition(source, Lists.newArrayList("2019", "march", "22"), ((source.getSd().getLocation()) + "/2019m22"));
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(parts[1]);
        try {
            client.exchange_partition(partitionSpecs, source.getDbName(), source.getTableName(), dest.getDbName(), dest.getTableName());
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception as FileNotFoundException will occur if the partitions have custom
            // location
        }
        checkRemainingPartitions(source, dest, Lists.newArrayList(parts[0], parts[1]));
        List<Partition> destTablePartitions = client.listPartitions(dest.getDbName(), dest.getTableName(), ((short) (-1)));
        Assert.assertTrue(destTablePartitions.isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNonExistingPartLocation() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        metaStore.cleanWarehouseDirs();
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNonExistingSourceTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.DB_NAME, "nonexistingtable", TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNonExistingSourceDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, "nonexistingdb", TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNonExistingDestTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, "nonexistingtable");
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNonExistingDestDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), "nonexistingdb", TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionEmptySourceTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.DB_NAME, "", TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionEmptySourceDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, "", TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionEmptyDestTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, "");
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionEmptyDestDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), "", TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNullSourceTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.DB_NAME, null, TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNullSourceDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, null, TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNullDestTable() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, null);
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNullDestDB() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), null, TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionEmptyPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNullPartSpec() throws Exception {
        client.exchange_partition(null, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), null, TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionPartAlreadyExists() throws Exception {
        Partition partition = buildPartition(TestExchangePartitions.destTable, Lists.newArrayList("2017", "march", "22"), null);
        client.add_partition(partition);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.DB_NAME, TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, TestExchangePartitions.destTable.getTableName());
    }

    @Test
    public void testExchangePartitionOneFail() throws Exception {
        Partition partition = buildPartition(TestExchangePartitions.destTable, Lists.newArrayList("2017", "march", "22"), null);
        client.add_partition(partition);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "", ""));
        try {
            client.exchange_partition(partitionSpecs, TestExchangePartitions.DB_NAME, TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.DB_NAME, TestExchangePartitions.destTable.getTableName());
            Assert.fail("Exception should have been thrown as one of the partitions already exists in the dest table.");
        } catch (MetaException e) {
            // Expected exception
        }
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        List<Partition> partitionsInDestTable = client.listPartitions(TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName(), TestExchangePartitions.MAX);
        Assert.assertEquals(1, partitionsInDestTable.size());
        Assert.assertEquals(TestExchangePartitions.partitions[1].getValues(), partitionsInDestTable.get(0).getValues());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(partitionsInDestTable.get(0).getSd().getLocation())));
        Partition resultPart = client.getPartition(TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.partitions[1].getValues());
        Assert.assertNotNull(resultPart);
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(TestExchangePartitions.partitions[1].getSd().getLocation())));
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionDifferentColsInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema("test_id", TestExchangePartitions.INT_COL_TYPE, "test col id"));
        cols.add(new FieldSchema("test_value", TestExchangePartitions.STRING_COL_TYPE, "test col value"));
        cols.add(new FieldSchema("test_name", TestExchangePartitions.STRING_COL_TYPE, "test col name"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", TestExchangePartitions.getYearMonthAndDayPartCols(), cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionDifferentColNameInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema("id", TestExchangePartitions.INT_COL_TYPE, "test col id"));
        cols.add(new FieldSchema("test_value", TestExchangePartitions.STRING_COL_TYPE, "test col value"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", TestExchangePartitions.getYearMonthAndDayPartCols(), cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionDifferentColTypesInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema("test_id", TestExchangePartitions.STRING_COL_TYPE, "test col id"));
        cols.add(new FieldSchema("test_value", TestExchangePartitions.STRING_COL_TYPE, "test col value"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", TestExchangePartitions.getYearMonthAndDayPartCols(), cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionDifferentPartColsInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(TestExchangePartitions.YEAR_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "year part col"));
        cols.add(new FieldSchema(TestExchangePartitions.MONTH_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "month part col"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionDifferentPartColNameInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(TestExchangePartitions.YEAR_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "year part col"));
        cols.add(new FieldSchema(TestExchangePartitions.MONTH_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "month part col"));
        cols.add(new FieldSchema("nap", TestExchangePartitions.STRING_COL_TYPE, "day part col"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionDifferentPartColTypesInTables() throws Exception {
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(TestExchangePartitions.YEAR_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "year part col"));
        cols.add(new FieldSchema(TestExchangePartitions.MONTH_COL_NAME, TestExchangePartitions.INT_COL_TYPE, "month part col"));
        cols.add(new FieldSchema(TestExchangePartitions.DAY_COL_NAME, TestExchangePartitions.STRING_COL_TYPE, "day part col"));
        Table dest = createTable(TestExchangePartitions.DB_NAME, "test_dest_table", cols, null);
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(TestExchangePartitions.partitions[1]);
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), dest.getDbName(), dest.getTableName());
    }

    @Test
    public void testExchangePartitionLessValueInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "2017");
        partitionSpecs.put(TestExchangePartitions.MONTH_COL_NAME, "march");
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionMoreValueInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "2017");
        partitionSpecs.put(TestExchangePartitions.MONTH_COL_NAME, "march");
        partitionSpecs.put(TestExchangePartitions.DAY_COL_NAME, "22");
        partitionSpecs.put("hour", "18");
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[1]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
    }

    @Test
    public void testExchangePartitionDifferentValuesInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "2017");
        partitionSpecs.put("honap", "march");
        partitionSpecs.put("nap", "22");
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
        checkExchangedPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3]));
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[4]));
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNonExistingValuesInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put("ev", "2017");
        partitionSpecs.put("honap", "march");
        partitionSpecs.put("nap", "22");
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test
    public void testExchangePartitionOnlyMonthSetInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "");
        partitionSpecs.put(TestExchangePartitions.MONTH_COL_NAME, "march");
        partitionSpecs.put(TestExchangePartitions.DAY_COL_NAME, "");
        try {
            client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        List<Partition> partsInDestTable = client.listPartitions(TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName(), TestExchangePartitions.MAX);
        Assert.assertTrue(partsInDestTable.isEmpty());
    }

    @Test
    public void testExchangePartitionYearAndDaySetInPartSpec() throws Exception {
        Map<String, String> partitionSpecs = new HashMap<>();
        partitionSpecs.put(TestExchangePartitions.YEAR_COL_NAME, "2017");
        partitionSpecs.put(TestExchangePartitions.MONTH_COL_NAME, "");
        partitionSpecs.put(TestExchangePartitions.DAY_COL_NAME, "22");
        try {
            client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        checkRemainingPartitions(TestExchangePartitions.sourceTable, TestExchangePartitions.destTable, Lists.newArrayList(TestExchangePartitions.partitions[0], TestExchangePartitions.partitions[1], TestExchangePartitions.partitions[2], TestExchangePartitions.partitions[3], TestExchangePartitions.partitions[4]));
        List<Partition> partsInDestTable = client.listPartitions(TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName(), TestExchangePartitions.MAX);
        Assert.assertTrue(partsInDestTable.isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNoPartExists() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "march", "25"));
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }

    @Test(expected = MetaException.class)
    public void testExchangePartitionNoPartExistsYearAndMonthSet() throws Exception {
        Map<String, String> partitionSpecs = TestExchangePartitions.getPartitionSpec(Lists.newArrayList("2017", "august", ""));
        client.exchange_partition(partitionSpecs, TestExchangePartitions.sourceTable.getDbName(), TestExchangePartitions.sourceTable.getTableName(), TestExchangePartitions.destTable.getDbName(), TestExchangePartitions.destTable.getTableName());
    }
}

