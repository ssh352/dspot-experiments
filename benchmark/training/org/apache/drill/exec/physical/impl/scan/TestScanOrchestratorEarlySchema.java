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
package org.apache.drill.exec.physical.impl.scan;


import DataMode.OPTIONAL;
import MinorType.BIGINT;
import MinorType.INT;
import MinorType.VARCHAR;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.exec.physical.impl.protocol.SchemaTracker;
import org.apache.drill.exec.physical.impl.scan.project.ReaderSchemaOrchestrator;
import org.apache.drill.exec.physical.impl.scan.project.ScanSchemaOrchestrator;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.BatchSchema.SelectionVectorMode;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the early-schema support of the scan orchestrator. "Early schema"
 * refers to the case in which the reader can provide a schema when the
 * reader is opened. Examples: CSV, HBase, MapR-DB binary, JDBC.
 * <p>
 * The tests here focus on the scan orchestrator itself; the tests assume
 * that tests for lower-level components have already passed.
 */
// TODO: Start with early schema, but add columns
@Category(RowSetTests.class)
public class TestScanOrchestratorEarlySchema extends SubOperatorTest {
    /**
     * Test SELECT * from an early-schema table of (a, b)
     */
    @Test
    public void testEarlySchemaWildcard() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT * ...
        scanner.build(RowSetTestUtils.projectAll());
        // ... FROM table
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        // Simulate a first reader in a scan that can provide an
        // empty batch to define schema.
        {
            reader.defineSchema();
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(tableSchema).build();
            Assert.assertNotNull(scanner.output());
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(tableSchema).addRow(1, "fred").addRow(2, "wilma").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        // Second batch.
        reader.startBatch();
        loader.writer().addRow(3, "barney").addRow(4, "betty");
        reader.endBatch();
        // Verify
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(tableSchema).addRow(3, "barney").addRow(4, "betty").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        // Explicit reader close. (All other tests are lazy, they
        // use an implicit close.)
        scanner.closeReader();
        scanner.close();
    }

    /**
     * Test SELECT a, b FROM table(a, b)
     */
    @Test
    public void testEarlySchemaSelectAll() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT a, b ...
        scanner.build(RowSetTestUtils.projectList("a", "b"));
        // ... FROM table
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        // Don't bother with an empty batch here or in other tests.
        // Simulates the second reader in a scan.
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(tableSchema).addRow(1, "fred").addRow(2, "wilma").build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * Test SELECT b, a FROM table(a, b)
     */
    @Test
    public void testEarlySchemaSelectAllReorder() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT b, a ...
        scanner.build(RowSetTestUtils.projectList("b", "a"));
        // ... FROM table
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        BatchSchema expectedSchema = new SchemaBuilder().add("b", VARCHAR).add("a", INT).build();
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("fred", 1).addRow("wilma", 2).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * Test SELECT a, b, c FROM table(a, b)
     * c will be null
     */
    @Test
    public void testEarlySchemaSelectExtra() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT a, b, c ...
        scanner.build(RowSetTestUtils.projectList("a", "b", "c"));
        // ... FROM table
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).addNullable("c", INT).build();
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(1, "fred", null).addRow(2, "wilma", null).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * Test SELECT a, b, c FROM table(a, b)
     * c will be null of type VARCHAR
     */
    @Test
    public void testEarlySchemaSelectExtraCustomType() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // Null columns of type VARCHAR
        MajorType nullType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(OPTIONAL).build();
        scanner.setNullType(nullType);
        // SELECT a, b, c ...
        scanner.build(RowSetTestUtils.projectList("a", "b", "c"));
        // ... FROM table ...
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).addNullable("c", VARCHAR).build();
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(1, "fred", null).addRow(2, "wilma", null).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * Test SELECT a FROM table(a, b)
     */
    @Test
    public void testEarlySchemaSelectSubset() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT a ...
        scanner.build(RowSetTestUtils.projectList("a"));
        // ... FROM table
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        // Verify that unprojected column is unprojected in the
        // table loader.
        Assert.assertFalse(loader.writer().column("b").schema().isProjected());
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).build();
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(1).addRow(2).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * Test SELECT - FROM table(a, b)
     */
    @Test
    public void testEarlySchemaSelectNone() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT ...
        // (Like SELECT COUNT(*) ...
        scanner.build(RowSetTestUtils.projectList());
        // ... FROM table
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        // Verify that unprojected column is unprojected in the
        // table loader.
        Assert.assertTrue(loader.isProjectionEmpty());
        Assert.assertFalse(loader.writer().column("a").schema().isProjected());
        Assert.assertFalse(loader.writer().column("b").schema().isProjected());
        // Verify empty batch.
        BatchSchema expectedSchema = new SchemaBuilder().build();
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        {
            // Two rows, no data.
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow().addRow().build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        // Fast path to fill in empty rows
        reader.startBatch();
        loader.skipRows(10);
        reader.endBatch();
        // Verify
        {
            VectorContainer output = scanner.output();
            Assert.assertEquals(10, output.getRecordCount());
            output.zeroVectors();
        }
        scanner.close();
    }

    /**
     * Test SELECT * from an early-schema table of () (that is,
     * a schema that consists of zero columns.
     */
    @Test
    public void testEmptySchema() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT * ...
        scanner.build(RowSetTestUtils.projectAll());
        // ... FROM table
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema ()
        TupleMetadata tableSchema = new SchemaBuilder().buildSchema();
        // Create the table loader
        reader.makeTableLoader(tableSchema);
        // Create a batch of data. Because there are no columns, it does
        // not make sense to ready any rows.
        reader.startBatch();
        reader.endBatch();
        // Verify
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(tableSchema).build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        scanner.close();
    }

    /**
     * Test SELECT a from an early-schema table of () (that is,
     * a schema that consists of zero columns.
     */
    @Test
    public void testEmptySchemaExtra() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT * ...
        scanner.build(RowSetTestUtils.projectList("a"));
        // ... FROM table
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema ()
        TupleMetadata tableSchema = new SchemaBuilder().buildSchema();
        // Create the table loader
        reader.makeTableLoader(tableSchema);
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("a", INT).build();
        // Create a batch of data. Because there are no columns, it does
        // not make sense to ready any rows.
        reader.startBatch();
        reader.endBatch();
        // Verify
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * The projection mechanism provides "type smoothing": null
     * columns prefer the type of previously-seen non-null columns.
     *
     * <code><pre>
     * SELECT a, b ...
     *
     * Table 1: (a: BIGINT, b: VARCHAR)
     * Table 2: (a: BIGINT)
     * Table 3: (b: VARCHAR)
     * </pre></code>
     * The result in all cases should be
     * <tt>(a : BIGINT, b: VARCHAR)</tt>
     */
    @Test
    public void testTypeSmoothingExplicit() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        TupleMetadata table1Schema = new SchemaBuilder().add("A", BIGINT).addNullable("B", VARCHAR).addArray("C", INT).buildSchema();
        BatchSchema resultSchema = new BatchSchema(SelectionVectorMode.NONE, table1Schema.toFieldList());
        SchemaTracker tracker = new SchemaTracker();
        // SELECT * ...
        scanner.build(RowSetTestUtils.projectList("a", "b", "c"));
        int schemaVersion;
        {
            // ... FROM table1(a, b, c)
            ReaderSchemaOrchestrator reader = scanner.startReader();
            reader.makeTableLoader(table1Schema);
            reader.defineSchema();
            VectorContainer output = scanner.output();
            tracker.trackSchema(output);
            schemaVersion = tracker.schemaVersion();
            Assert.assertTrue(resultSchema.isEquivalent(output.getSchema()));
            scanner.closeReader();
        }
        {
            // ... FROM table1(a, c)
            // 
            // B is dropped. But, it is nullable, so the vector cache
            // can supply the proper type to ensure continuity.
            TupleMetadata table2Schema = new SchemaBuilder().add("A", BIGINT).addArray("C", INT).buildSchema();
            ReaderSchemaOrchestrator reader = scanner.startReader();
            reader.makeTableLoader(table2Schema);
            reader.defineSchema();
            VectorContainer output = scanner.output();
            tracker.trackSchema(output);
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            Assert.assertTrue(resultSchema.isEquivalent(output.getSchema()));
            scanner.closeReader();
        }
        {
            // ... FROM table1(a, b)
            // 
            // C is dropped. But, it is an array, which uses zero-elements
            // to indicate null, so the vector cache can fill in the type.
            TupleMetadata table3Schema = new SchemaBuilder().add("A", BIGINT).addNullable("B", VARCHAR).buildSchema();
            ReaderSchemaOrchestrator reader = scanner.startReader();
            reader.makeTableLoader(table3Schema);
            reader.defineSchema();
            VectorContainer output = scanner.output();
            tracker.trackSchema(output);
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            Assert.assertTrue(resultSchema.isEquivalent(output.getSchema()));
            scanner.closeReader();
        }
        {
            // ... FROM table1(b, c)
            // 
            // This version carries over a non-nullable BIGINT, but that
            // can't become a null column, so nullable BIGINT is substituted,
            // result in a schema change.
            TupleMetadata table2Schema = new SchemaBuilder().addNullable("B", VARCHAR).addArray("C", INT).buildSchema();
            ReaderSchemaOrchestrator reader = scanner.startReader();
            reader.makeTableLoader(table2Schema);
            reader.defineSchema();
            VectorContainer output = scanner.output();
            tracker.trackSchema(output);
            Assert.assertEquals(BIGINT, output.getSchema().getColumn(0).getType().getMinorType());
            Assert.assertEquals(OPTIONAL, output.getSchema().getColumn(0).getType().getMode());
            Assert.assertTrue((schemaVersion < (tracker.schemaVersion())));
            scanner.closeReader();
        }
        scanner.close();
    }

    /**
     * Test the ability of the scan projector to "smooth" out schema changes
     * by reusing the type from a previous reader, if known. That is,
     * given three readers:<br>
     * (a, b)<br>
     * (b)<br>
     * (a, b)<br>
     * Then the type of column a should be preserved for the second reader that
     * does not include a. This works if a is nullable. If so, a's type will
     * be used for the empty column, rather than the usual nullable int.
     * <p>
     * Detailed testing of type matching for "missing" columns is done
     * in {@link #testNullColumnLoader()}.
     * <p>
     * As a side effect, makes sure that two identical tables (in this case,
     * separated by a different table) results in no schema change.
     */
    @Test
    public void testTypeSmoothing() {
        ScanSchemaOrchestrator projector = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        // SELECT a, b ...
        projector.build(RowSetTestUtils.projectList("a", "b"));
        // file schema (a, b)
        TupleMetadata twoColSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).buildSchema();
        SchemaTracker tracker = new SchemaTracker();
        int schemaVersion;
        {
            // ... FROM table 1
            ReaderSchemaOrchestrator reader = projector.startReader();
            ResultSetLoader loader = reader.makeTableLoader(twoColSchema);
            // Projection of (a, b) to (a, b)
            reader.startBatch();
            loader.writer().addRow(10, "fred").addRow(20, "wilma");
            reader.endBatch();
            tracker.trackSchema(projector.output());
            schemaVersion = tracker.schemaVersion();
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(twoColSchema).addRow(10, "fred").addRow(20, "wilma").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(projector.output()));
        }
        {
            // ... FROM table 2
            ReaderSchemaOrchestrator reader = projector.startReader();
            // File schema (a)
            TupleMetadata oneColSchema = new SchemaBuilder().add("a", INT).buildSchema();
            // Projection of (a) to (a, b), reusing b from above.
            ResultSetLoader loader = reader.makeTableLoader(oneColSchema);
            reader.startBatch();
            loader.writer().addRow(30).addRow(40);
            reader.endBatch();
            tracker.trackSchema(projector.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(twoColSchema).addRow(30, null).addRow(40, null).build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(projector.output()));
        }
        {
            // ... FROM table 3
            ReaderSchemaOrchestrator reader = projector.startReader();
            // Projection of (a, b), to (a, b), reusing b yet again
            ResultSetLoader loader = reader.makeTableLoader(twoColSchema);
            reader.startBatch();
            loader.writer().addRow(50, "dino").addRow(60, "barney");
            reader.endBatch();
            tracker.trackSchema(projector.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(twoColSchema).addRow(50, "dino").addRow(60, "barney").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(projector.output()));
        }
        projector.close();
    }

    @Test
    public void testModeSmoothing() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        scanner.enableSchemaSmoothing(true);
        scanner.build(RowSetTestUtils.projectList("a"));
        // Most general schema: nullable, with precision.
        TupleMetadata schema1 = new SchemaBuilder().addNullable("a", VARCHAR, 10).buildSchema();
        SchemaTracker tracker = new SchemaTracker();
        int schemaVersion;
        {
            // Table 1: most permissive type
            ReaderSchemaOrchestrator reader = scanner.startReader();
            ResultSetLoader loader = reader.makeTableLoader(schema1);
            // Create a batch
            reader.startBatch();
            loader.writer().addRow("fred").addRow("wilma");
            reader.endBatch();
            tracker.trackSchema(scanner.output());
            schemaVersion = tracker.schemaVersion();
            // Verify
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema1).addRow("fred").addRow("wilma").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
            scanner.closeReader();
        }
        {
            // Table 2: required, use nullable
            // Required version.
            TupleMetadata schema2 = new SchemaBuilder().add("a", VARCHAR, 10).buildSchema();
            ReaderSchemaOrchestrator reader = scanner.startReader();
            ResultSetLoader loader = reader.makeTableLoader(schema2);
            // Create a batch
            reader.startBatch();
            loader.writer().addRow("barney").addRow("betty");
            reader.endBatch();
            // Verify, using persistent schema
            tracker.trackSchema(scanner.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema1).addRow("barney").addRow("betty").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
            scanner.closeReader();
        }
        {
            // Table 3: narrower precision, use wider
            // Required version with narrower precision.
            TupleMetadata schema3 = new SchemaBuilder().add("a", VARCHAR, 5).buildSchema();
            ReaderSchemaOrchestrator reader = scanner.startReader();
            ResultSetLoader loader = reader.makeTableLoader(schema3);
            // Create a batch
            reader.startBatch();
            loader.writer().addRow("bam-bam").addRow("pebbles");
            reader.endBatch();
            // Verify, using persistent schema
            tracker.trackSchema(scanner.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema1).addRow("bam-bam").addRow("pebbles").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
            scanner.closeReader();
        }
        scanner.close();
    }

    /**
     * Verify that different table column orders are projected into the
     * SELECT order, preserving vectors, so no schema change for column
     * reordering.
     */
    @Test
    public void testColumnReordering() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        scanner.enableSchemaSmoothing(true);
        scanner.build(RowSetTestUtils.projectList("a", "b", "c"));
        TupleMetadata schema1 = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).add("c", BIGINT).buildSchema();
        TupleMetadata schema2 = new SchemaBuilder().add("c", BIGINT).add("a", INT).addNullable("b", VARCHAR, 10).buildSchema();
        TupleMetadata schema3 = new SchemaBuilder().add("a", INT).add("c", BIGINT).addNullable("b", VARCHAR, 10).buildSchema();
        SchemaTracker tracker = new SchemaTracker();
        int schemaVersion;
        {
            // ... FROM table 1
            ReaderSchemaOrchestrator reader = scanner.startReader();
            // Projection of (a, b, c) to (a, b, c)
            ResultSetLoader loader = reader.makeTableLoader(schema1);
            reader.startBatch();
            loader.writer().addRow(10, "fred", 110L).addRow(20, "wilma", 110L);
            reader.endBatch();
            tracker.trackSchema(scanner.output());
            schemaVersion = tracker.schemaVersion();
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema1).addRow(10, "fred", 110L).addRow(20, "wilma", 110L).build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
            scanner.closeReader();
        }
        {
            // ... FROM table 2
            ReaderSchemaOrchestrator reader = scanner.startReader();
            // Projection of (c, a, b) to (a, b, c)
            ResultSetLoader loader = reader.makeTableLoader(schema2);
            reader.startBatch();
            loader.writer().addRow(330L, 30, "bambam").addRow(440L, 40, "betty");
            reader.endBatch();
            tracker.trackSchema(scanner.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema1).addRow(30, "bambam", 330L).addRow(40, "betty", 440L).build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        {
            // ... FROM table 3
            ReaderSchemaOrchestrator reader = scanner.startReader();
            // Projection of (a, c, b) to (a, b, c)
            ResultSetLoader loader = reader.makeTableLoader(schema3);
            reader.startBatch();
            loader.writer().addRow(50, 550L, "dino").addRow(60, 660L, "barney");
            reader.endBatch();
            tracker.trackSchema(scanner.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema1).addRow(50, "dino", 550L).addRow(60, "barney", 660L).build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        scanner.close();
    }
}
