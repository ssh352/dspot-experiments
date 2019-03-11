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
package org.apache.drill.exec.record;


import BatchSchema.SelectionVectorMode;
import MinorType.INT;
import MinorType.SMALLINT;
import MinorType.VARCHAR;
import TypeProtos.MinorType.FLOAT4;
import java.util.List;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.memory.RootAllocator;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.DrillTest;
import org.apache.drill.test.OperatorFixture;
import org.apache.drill.test.rowSet.DirectRowSet;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(VectorTest.class)
public class TestVectorContainer extends DrillTest {
    // TODO: Replace the following with an extension of SubOperatorTest class
    // once that is available.
    protected static OperatorFixture fixture;

    @ClassRule
    public static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    /**
     * Test of the ability to merge two schemas and to merge
     * two vector containers. The merge is "horizontal", like
     * a row-by-row join. Since each container is a list of
     * vectors, we just combine the two lists to create the
     * merged result.
     */
    @Test
    public void testContainerMerge() {
        // Simulated data from a reader
        BatchSchema leftSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR).build();
        RowSet.SingleRowSet left = TestVectorContainer.fixture.rowSetBuilder(leftSchema).addRow(10, "fred").addRow(20, "barney").addRow(30, "wilma").build();
        // Simulated "implicit" columns: row number and file name
        BatchSchema rightSchema = new SchemaBuilder().add("x", SMALLINT).add("y", VARCHAR).build();
        RowSet.SingleRowSet right = TestVectorContainer.fixture.rowSetBuilder(rightSchema).addRow(1, "foo.txt").addRow(2, "bar.txt").addRow(3, "dino.txt").build();
        // The merge batch we expect to see
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR).add("x", SMALLINT).add("y", VARCHAR).build();
        RowSet.SingleRowSet expected = TestVectorContainer.fixture.rowSetBuilder(expectedSchema).addRow(10, "fred", 1, "foo.txt").addRow(20, "barney", 2, "bar.txt").addRow(30, "wilma", 3, "dino.txt").build();
        // Merge containers without selection vector
        RowSet merged = TestVectorContainer.fixture.wrap(left.container().merge(right.container()));
        RowSetComparison comparison = new RowSetComparison(expected);
        comparison.verify(merged);
        // Merge containers via row set facade
        RowSet mergedRs = DirectRowSet.fromContainer(left.container().merge(right.container()));
        comparison.verifyAndClearAll(mergedRs);
        // Add a selection vector. Merging is forbidden, in the present code,
        // for batches that have a selection vector.
        RowSet.SingleRowSet leftIndirect = left.toIndirect();
        try {
            leftIndirect.container().merge(right.container());
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        leftIndirect.clear();
        right.clear();
    }

    @Test
    public void testPrettyPrintRecord() {
        final MaterializedField colA = MaterializedField.create("colA", Types.required(TypeProtos.MinorType.INT));
        final MaterializedField colB = MaterializedField.create("colB", Types.required(TypeProtos.MinorType.VARCHAR));
        final MaterializedField colC = MaterializedField.create("colC", Types.repeated(FLOAT4));
        final MaterializedField colD = MaterializedField.create("colD", Types.repeated(TypeProtos.MinorType.VARCHAR));
        final List<MaterializedField> cols = Lists.newArrayList(colA, colB, colC, colD);
        final BatchSchema batchSchema = new BatchSchema(SelectionVectorMode.NONE, cols);
        try (RootAllocator allocator = new RootAllocator(10000000)) {
            final RowSet rowSet = new RowSetBuilder(allocator, batchSchema).addRow(110, "green", new float[]{ 5.5F, 2.3F }, new String[]{ "1a", "1b" }).addRow(1440, "yellow", new float[]{ 1.0F }, new String[]{ "dog" }).build();
            final String expected = "[\"colA\" = 110, \"colB\" = green, \"colC\" = [5.5,2.3], \"colD\" = [\"1a\",\"1b\"]]";
            final String actual = rowSet.container().prettyPrintRecord(0);
            try {
                Assert.assertEquals(expected, actual);
            } finally {
                rowSet.clear();
            }
        }
    }
}

