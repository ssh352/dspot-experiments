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
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.MetaStoreTestUtils;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.api.Catalog;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.CatalogBuilder;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for appending partitions.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestAppendPartitions extends MetaStoreClientTest {
    private AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private static final String DB_NAME = "test_append_part_db";

    private static Table tableWithPartitions;

    private static Table externalTable;

    private static Table tableNoPartColumns;

    private static Table tableView;

    public TestAppendPartitions(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    // Tests for Partition appendPartition(String tableName, String dbName, List<String> partVals) method
    @Test
    public void testAppendPartition() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        Table table = TestAppendPartitions.tableWithPartitions;
        Partition appendedPart = client.appendPartition(table.getDbName(), table.getTableName(), partitionValues);
        Assert.assertNotNull(appendedPart);
        Partition partition = client.getPartition(table.getDbName(), table.getTableName(), partitionValues);
        appendedPart.setWriteId(partition.getWriteId());
        Assert.assertEquals(partition, appendedPart);
        verifyPartition(partition, table, partitionValues, "year=2017/month=may");
        verifyPartitionNames(table, Lists.newArrayList("year=2017/month=march", "year=2017/month=april", "year=2018/month=march", "year=2017/month=may"));
    }

    @Test
    public void testAppendPartitionToExternalTable() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        Table table = TestAppendPartitions.externalTable;
        Partition appendedPart = client.appendPartition(table.getDbName(), table.getTableName(), partitionValues);
        Assert.assertNotNull(appendedPart);
        Partition partition = client.getPartition(table.getDbName(), table.getTableName(), partitionValues);
        appendedPart.setWriteId(partition.getWriteId());
        Assert.assertEquals(partition, appendedPart);
        verifyPartition(partition, table, partitionValues, "year=2017/month=may");
        verifyPartitionNames(table, Lists.newArrayList("year=2017/month=may"));
    }

    @Test
    public void testAppendPartitionMultiplePartitions() throws Exception {
        List<String> partitionValues1 = Lists.newArrayList("2017", "may");
        List<String> partitionValues2 = Lists.newArrayList("2018", "may");
        List<String> partitionValues3 = Lists.newArrayList("2017", "june");
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionValues1);
        client.appendPartition(table.getDbName(), table.getTableName(), partitionValues2);
        client.appendPartition(table.getDbName(), table.getTableName(), partitionValues3);
        verifyPartitionNames(table, Lists.newArrayList("year=2017/month=may", "year=2018/month=may", "year=2017/month=june", "year=2017/month=march", "year=2017/month=april", "year=2018/month=march"));
    }

    @Test(expected = MetaException.class)
    public void testAppendPartitionToTableWithoutPartCols() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        Table table = TestAppendPartitions.tableNoPartColumns;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionValues);
    }

    @Test(expected = MetaException.class)
    public void testAppendPartitionToView() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        Table table = TestAppendPartitions.tableView;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionValues);
    }

    @Test(expected = AlreadyExistsException.class)
    public void testAppendPartitionAlreadyExists() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "april");
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionValues);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartitionNonExistingDB() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        client.appendPartition("nonexistingdb", TestAppendPartitions.tableWithPartitions.getTableName(), partitionValues);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartitionNonExistingTable() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        client.appendPartition(TestAppendPartitions.tableWithPartitions.getDbName(), "nonexistingtable", partitionValues);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartitionEmptyDB() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        client.appendPartition("", TestAppendPartitions.tableWithPartitions.getTableName(), partitionValues);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartitionEmptyTable() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        client.appendPartition(TestAppendPartitions.tableWithPartitions.getDbName(), "", partitionValues);
    }

    @Test(expected = MetaException.class)
    public void testAppendPartitionNullDB() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        client.appendPartition(null, TestAppendPartitions.tableWithPartitions.getTableName(), partitionValues);
    }

    @Test(expected = MetaException.class)
    public void testAppendPartitionNullTable() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2017", "may");
        client.appendPartition(TestAppendPartitions.tableWithPartitions.getDbName(), null, partitionValues);
    }

    @Test(expected = MetaException.class)
    public void testAppendPartitionEmptyPartValues() throws Exception {
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), new ArrayList());
    }

    @Test(expected = MetaException.class)
    public void testAppendPartitionNullPartValues() throws Exception {
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), ((List<String>) (null)));
    }

    @Test
    public void testAppendPartitionLessPartValues() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2019");
        Table table = TestAppendPartitions.tableWithPartitions;
        try {
            client.appendPartition(table.getDbName(), table.getTableName(), partitionValues);
            Assert.fail("Exception should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        verifyPartitionNames(table, Lists.newArrayList("year=2017/month=march", "year=2017/month=april", "year=2018/month=march"));
        String partitionLocation = (table.getSd().getLocation()) + "/year=2019";
        Assert.assertFalse(metaStore.isPathExists(new Path(partitionLocation)));
    }

    @Test
    public void testAppendPartitionMorePartValues() throws Exception {
        List<String> partitionValues = Lists.newArrayList("2019", "march", "12");
        Table table = TestAppendPartitions.tableWithPartitions;
        try {
            client.appendPartition(table.getDbName(), table.getTableName(), partitionValues);
            Assert.fail("Exception should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        verifyPartitionNames(table, Lists.newArrayList("year=2017/month=march", "year=2017/month=april", "year=2018/month=march"));
        String partitionLocation = (TestAppendPartitions.tableWithPartitions.getSd().getLocation()) + "/year=2019";
        Assert.assertFalse(metaStore.isPathExists(new Path(partitionLocation)));
    }

    // Tests for Partition appendPartition(String tableName, String dbName, String name) method
    @Test
    public void testAppendPart() throws Exception {
        Table table = TestAppendPartitions.tableWithPartitions;
        String partitionName = "year=2017/month=may";
        Partition appendedPart = client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
        Assert.assertNotNull(appendedPart);
        Partition partition = client.getPartition(table.getDbName(), table.getTableName(), TestAppendPartitions.getPartitionValues(partitionName));
        appendedPart.setWriteId(partition.getWriteId());
        Assert.assertEquals(partition, appendedPart);
        verifyPartition(partition, table, TestAppendPartitions.getPartitionValues(partitionName), partitionName);
        verifyPartitionNames(table, Lists.newArrayList("year=2017/month=march", "year=2017/month=april", "year=2018/month=march", partitionName));
    }

    @Test
    public void testAppendPartToExternalTable() throws Exception {
        Table table = TestAppendPartitions.externalTable;
        String partitionName = "year=2017/month=may";
        Partition appendedPart = client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
        Assert.assertNotNull(appendedPart);
        Partition partition = client.getPartition(table.getDbName(), table.getTableName(), TestAppendPartitions.getPartitionValues(partitionName));
        appendedPart.setWriteId(partition.getWriteId());
        Assert.assertEquals(partition, appendedPart);
        verifyPartition(partition, table, TestAppendPartitions.getPartitionValues(partitionName), partitionName);
        verifyPartitionNames(table, Lists.newArrayList(partitionName));
    }

    @Test
    public void testAppendPartMultiplePartitions() throws Exception {
        String partitionName1 = "year=2017/month=may";
        String partitionName2 = "year=2018/month=may";
        String partitionName3 = "year=2017/month=june";
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName1);
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName2);
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName3);
        verifyPartitionNames(table, Lists.newArrayList(partitionName1, partitionName2, partitionName3, "year=2017/month=march", "year=2017/month=april", "year=2018/month=march"));
    }

    @Test(expected = MetaException.class)
    public void testAppendPartToTableWithoutPartCols() throws Exception {
        String partitionName = "year=2017/month=may";
        Table table = TestAppendPartitions.tableNoPartColumns;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
    }

    @Test(expected = MetaException.class)
    public void testAppendPartToView() throws Exception {
        String partitionName = "year=2017/month=may";
        Table table = TestAppendPartitions.tableView;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
    }

    @Test(expected = AlreadyExistsException.class)
    public void testAppendPartAlreadyExists() throws Exception {
        String partitionName = "year=2017/month=april";
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartNonExistingDB() throws Exception {
        String partitionName = "year=2017/month=april";
        client.appendPartition("nonexistingdb", TestAppendPartitions.tableWithPartitions.getTableName(), partitionName);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartNonExistingTable() throws Exception {
        String partitionName = "year=2017/month=april";
        client.appendPartition(TestAppendPartitions.tableWithPartitions.getDbName(), "nonexistingtable", partitionName);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartEmptyDB() throws Exception {
        String partitionName = "year=2017/month=april";
        client.appendPartition("", TestAppendPartitions.tableWithPartitions.getTableName(), partitionName);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartEmptyTable() throws Exception {
        String partitionName = "year=2017/month=april";
        client.appendPartition(TestAppendPartitions.tableWithPartitions.getDbName(), "", partitionName);
    }

    @Test(expected = MetaException.class)
    public void testAppendPartNullDB() throws Exception {
        String partitionName = "year=2017/month=april";
        client.appendPartition(null, TestAppendPartitions.tableWithPartitions.getTableName(), partitionName);
    }

    @Test(expected = MetaException.class)
    public void testAppendPartNullTable() throws Exception {
        String partitionName = "year=2017/month=april";
        client.appendPartition(TestAppendPartitions.tableWithPartitions.getDbName(), null, partitionName);
    }

    @Test(expected = MetaException.class)
    public void testAppendPartEmptyPartName() throws Exception {
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), "");
    }

    @Test(expected = MetaException.class)
    public void testAppendPartNullPartName() throws Exception {
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), ((String) (null)));
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartLessPartValues() throws Exception {
        String partitionName = "year=2019";
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
    }

    @Test
    public void testAppendPartMorePartValues() throws Exception {
        String partitionName = "year=2019/month=march/day=12";
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartInvalidPartName() throws Exception {
        String partitionName = "invalidpartname";
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartWrongColumnInPartName() throws Exception {
        String partitionName = "year=2019/honap=march";
        Table table = TestAppendPartitions.tableWithPartitions;
        client.appendPartition(table.getDbName(), table.getTableName(), partitionName);
    }

    @Test
    public void otherCatalog() throws TException {
        String catName = "append_partition_catalog";
        Catalog cat = new CatalogBuilder().setName(catName).setLocation(MetaStoreTestUtils.getTestWarehouseDir(catName)).build();
        client.createCatalog(cat);
        String dbName = "append_partition_database_in_other_catalog";
        Database db = new DatabaseBuilder().setName(dbName).setCatalogName(catName).create(client, metaStore.getConf());
        String tableName = "table_in_other_catalog";
        new TableBuilder().inDb(db).setTableName(tableName).addCol("id", "int").addCol("name", "string").addPartCol("partcol", "string").create(client, metaStore.getConf());
        Partition created = client.appendPartition(catName, dbName, tableName, Collections.singletonList("a1"));
        Assert.assertEquals(1, created.getValuesSize());
        Assert.assertEquals("a1", created.getValues().get(0));
        Partition fetched = client.getPartition(catName, dbName, tableName, Collections.singletonList("a1"));
        created.setWriteId(fetched.getWriteId());
        Assert.assertEquals(created, fetched);
        created = client.appendPartition(catName, dbName, tableName, "partcol=a2");
        Assert.assertEquals(1, created.getValuesSize());
        Assert.assertEquals("a2", created.getValues().get(0));
        fetched = client.getPartition(catName, dbName, tableName, Collections.singletonList("a2"));
        created.setWriteId(fetched.getWriteId());
        Assert.assertEquals(created, fetched);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartitionBogusCatalog() throws Exception {
        client.appendPartition("nosuch", TestAppendPartitions.DB_NAME, TestAppendPartitions.tableWithPartitions.getTableName(), Lists.newArrayList("2017", "may"));
    }

    @Test(expected = InvalidObjectException.class)
    public void testAppendPartitionByNameBogusCatalog() throws Exception {
        client.appendPartition("nosuch", TestAppendPartitions.DB_NAME, TestAppendPartitions.tableWithPartitions.getTableName(), "year=2017/month=april");
    }
}
