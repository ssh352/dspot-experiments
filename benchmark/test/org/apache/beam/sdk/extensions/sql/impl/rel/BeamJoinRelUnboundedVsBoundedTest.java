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
package org.apache.beam.sdk.extensions.sql.impl.rel;


import PCollection.IsBounded;
import Schema.FieldType.INT32;
import Schema.FieldType.STRING;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.extensions.sql.BeamSqlSeekableTable;
import org.apache.beam.sdk.extensions.sql.TestUtils;
import org.apache.beam.sdk.extensions.sql.impl.schema.BaseBeamTable;
import org.apache.beam.sdk.extensions.sql.impl.transform.BeamSqlOutputToConsoleFn;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.POutput;
import org.apache.beam.sdk.values.Row;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;

import static org.apache.beam.sdk.extensions.sql.TestUtils.RowsBuilder.of;


/**
 * Unbounded + Unbounded Test for {@code BeamJoinRel}.
 */
public class BeamJoinRelUnboundedVsBoundedTest extends BaseRelTest {
    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    public static final DateTime FIRST_DATE = new DateTime(1);

    public static final DateTime SECOND_DATE = new DateTime((1 + (3600 * 1000)));

    public static final DateTime THIRD_DATE = new DateTime((((1 + (3600 * 1000)) + (3600 * 1000)) + 1));

    private static final Duration WINDOW_SIZE = Duration.standardHours(1);

    /**
     * Test table for JOIN-AS-LOOKUP.
     */
    public static class SiteLookupTable extends BaseBeamTable implements BeamSqlSeekableTable {
        public SiteLookupTable(Schema schema) {
            super(schema);
        }

        @Override
        public IsBounded isBounded() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PCollection<Row> buildIOReader(PBegin begin) {
            throw new UnsupportedOperationException();
        }

        @Override
        public POutput buildIOWriter(PCollection<Row> input) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Row> seekRow(Row lookupSubRow) {
            return Arrays.asList(Row.withSchema(getSchema()).addValues(1, "SITE1").build());
        }
    }

    @Test
    public void testInnerJoin_unboundedTableOnTheLeftSide() throws Exception {
        String sql = "SELECT o1.order_id, o1.sum_site_id, o2.buyer FROM " + ((((("(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS " + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " JOIN ") + " ORDER_DETAILS1 o2 ") + " on ") + " o1.order_id=o2.order_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(INT32, "order_id", INT32, "sum_site_id", STRING, "buyer").addRows(1, 3, "james", 2, 5, "bond").getStringRows());
        pipeline.run();
    }

    @Test
    public void testInnerJoin_boundedTableOnTheLeftSide() throws Exception {
        String sql = "SELECT o1.order_id, o1.sum_site_id, o2.buyer FROM " + (((((" ORDER_DETAILS1 o2 " + " JOIN ") + "(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " on ") + " o1.order_id=o2.order_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(INT32, "order_id", INT32, "sum_site_id", STRING, "buyer").addRows(1, 3, "james", 2, 5, "bond").getStringRows());
        pipeline.run();
    }

    @Test
    public void testLeftOuterJoin() throws Exception {
        String sql = "SELECT o1.order_id, o1.sum_site_id, o2.buyer FROM " + ((((("(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS " + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " LEFT OUTER JOIN ") + " ORDER_DETAILS1 o2 ") + " on ") + " o1.order_id=o2.order_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        rows.apply(ParDo.of(new BeamSqlOutputToConsoleFn("helloworld")));
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(Schema.builder().addField("order_id", INT32).addField("sum_site_id", INT32).addNullableField("buyer", STRING).build()).addRows(1, 3, "james", 2, 5, "bond", 3, 3, null).getStringRows());
        pipeline.run();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLeftOuterJoinError() throws Exception {
        String sql = "SELECT o1.order_id, o1.sum_site_id, o2.buyer FROM " + (((((" ORDER_DETAILS1 o2 " + " LEFT OUTER JOIN ") + "(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " on ") + " o1.order_id=o2.order_id");
        pipeline.enableAbandonedNodeEnforcement(false);
        BaseRelTest.compilePipeline(sql, pipeline);
        pipeline.run();
    }

    @Test
    public void testRightOuterJoin() throws Exception {
        String sql = "SELECT o1.order_id, o1.sum_site_id, o2.buyer FROM " + (((((" ORDER_DETAILS1 o2 " + " RIGHT OUTER JOIN ") + "(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " on ") + " o1.order_id=o2.order_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(Schema.builder().addField("order_id", INT32).addField("sum_site_id", INT32).addNullableField("buyer", STRING).build()).addRows(1, 3, "james", 2, 5, "bond", 3, 3, null).getStringRows());
        pipeline.run();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRightOuterJoinError() throws Exception {
        String sql = "SELECT o1.order_id, o1.sum_site_id, o2.buyer FROM " + ((((("(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS " + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " RIGHT OUTER JOIN ") + " ORDER_DETAILS1 o2 ") + " on ") + " o1.order_id=o2.order_id");
        pipeline.enableAbandonedNodeEnforcement(false);
        BaseRelTest.compilePipeline(sql, pipeline);
        pipeline.run();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFullOuterJoinError() throws Exception {
        String sql = "SELECT o1.order_id, o1.sum_site_id, o2.buyer FROM " + (((((" ORDER_DETAILS1 o2 " + " FULL OUTER JOIN ") + "(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " on ") + " o1.order_id=o2.order_id");
        pipeline.enableAbandonedNodeEnforcement(false);
        BaseRelTest.compilePipeline(sql, pipeline);
        pipeline.run();
    }

    @Test
    public void testJoinAsLookup() throws Exception {
        String sql = "SELECT o1.order_id, o2.site_name FROM " + ((((" ORDER_DETAILS o1 " + " JOIN SITE_LKP o2 ") + " on ") + " o1.site_id=o2.site_id ") + " WHERE o1.site_id=1");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(INT32, "order_id", STRING, "site_name").addRows(1, "SITE1").getStringRows());
        pipeline.run();
    }

    @Test
    public void testJoinAsLookupSwapped() throws Exception {
        String sql = "SELECT o1.order_id, o2.site_name FROM " + ((((" SITE_LKP o2 " + " JOIN ORDER_DETAILS o1 ") + " on ") + " o1.site_id=o2.site_id ") + " WHERE o1.site_id=1");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(INT32, "order_id", STRING, "site_name").addRows(1, "SITE1").getStringRows());
        pipeline.run();
    }
}

