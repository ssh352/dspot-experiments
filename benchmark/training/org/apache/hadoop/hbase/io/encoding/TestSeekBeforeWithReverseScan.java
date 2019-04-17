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
package org.apache.hadoop.hbase.io.encoding;


import HConstants.EMPTY_BYTE_ARRAY;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.RegionScanner;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IOTests.class, SmallTests.class })
public class TestSeekBeforeWithReverseScan {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSeekBeforeWithReverseScan.class);

    private final HBaseTestingUtility testUtil = new HBaseTestingUtility();

    private HRegion region;

    private byte[] cfName = Bytes.toBytes("a");

    private byte[] cqName = Bytes.toBytes("b");

    @Test
    public void testReverseScanWithoutPadding() throws Exception {
        byte[] row1 = Bytes.toBytes("a");
        byte[] row2 = Bytes.toBytes("ab");
        byte[] row3 = Bytes.toBytes("b");
        Put put1 = new Put(row1);
        put1.addColumn(cfName, cqName, EMPTY_BYTE_ARRAY);
        Put put2 = new Put(row2);
        put2.addColumn(cfName, cqName, EMPTY_BYTE_ARRAY);
        Put put3 = new Put(row3);
        put3.addColumn(cfName, cqName, EMPTY_BYTE_ARRAY);
        region.put(put1);
        region.put(put2);
        region.put(put3);
        region.flush(true);
        Scan scan = new Scan();
        scan.setCacheBlocks(false);
        scan.setReversed(true);
        scan.setFilter(new FirstKeyOnlyFilter());
        scan.addFamily(cfName);
        RegionScanner scanner = region.getScanner(scan);
        List<Cell> res = new ArrayList<>();
        int count = 1;
        while (scanner.next(res)) {
            count++;
        } 
        Assert.assertEquals("b", Bytes.toString(res.get(0).getRowArray(), res.get(0).getRowOffset(), res.get(0).getRowLength()));
        Assert.assertEquals("ab", Bytes.toString(res.get(1).getRowArray(), res.get(1).getRowOffset(), res.get(1).getRowLength()));
        Assert.assertEquals("a", Bytes.toString(res.get(2).getRowArray(), res.get(2).getRowOffset(), res.get(2).getRowLength()));
        Assert.assertEquals(3, count);
    }

    @Test
    public void testReverseScanWithPadding() throws Exception {
        byte[] terminator = new byte[]{ -1 };
        byte[] row1 = Bytes.add(invert(Bytes.toBytes("a")), terminator);
        byte[] row2 = Bytes.add(invert(Bytes.toBytes("ab")), terminator);
        byte[] row3 = Bytes.add(invert(Bytes.toBytes("b")), terminator);
        Put put1 = new Put(row1);
        put1.addColumn(cfName, cqName, EMPTY_BYTE_ARRAY);
        Put put2 = new Put(row2);
        put2.addColumn(cfName, cqName, EMPTY_BYTE_ARRAY);
        Put put3 = new Put(row3);
        put3.addColumn(cfName, cqName, EMPTY_BYTE_ARRAY);
        region.put(put1);
        region.put(put2);
        region.put(put3);
        region.flush(true);
        Scan scan = new Scan();
        scan.setCacheBlocks(false);
        scan.setReversed(true);
        scan.setFilter(new FirstKeyOnlyFilter());
        scan.addFamily(cfName);
        RegionScanner scanner = region.getScanner(scan);
        List<Cell> res = new ArrayList<>();
        int count = 1;
        while (scanner.next(res)) {
            count++;
        } 
        Assert.assertEquals(3, count);
    }
}
