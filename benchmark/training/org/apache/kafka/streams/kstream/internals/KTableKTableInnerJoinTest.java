/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream.internals;


import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.MockProcessorContext;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.internals.testutil.LogCaptureAppender;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.test.MockProcessorSupplier;
import org.apache.kafka.test.MockValueJoiner;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class KTableKTableInnerJoinTest {
    private final String topic1 = "topic1";

    private final String topic2 = "topic2";

    private final String output = "output";

    private final Consumed<Integer, String> consumed = Consumed.with(Serdes.Integer(), Serdes.String());

    private final Materialized<Integer, String, KeyValueStore<Bytes, byte[]>> materialized = Materialized.with(Serdes.Integer(), Serdes.String());

    private final ConsumerRecordFactory<Integer, String> recordFactory = new ConsumerRecordFactory(Serdes.Integer().serializer(), Serdes.String().serializer());

    private final Properties props = StreamsTestUtils.getStreamsConfig(Serdes.Integer(), Serdes.String());

    @Test
    public void testJoin() {
        final StreamsBuilder builder = new StreamsBuilder();
        final int[] expectedKeys = new int[]{ 0, 1, 2, 3 };
        final KTable<Integer, String> table1;
        final KTable<Integer, String> table2;
        final KTable<Integer, String> joined;
        table1 = builder.table(topic1, consumed);
        table2 = builder.table(topic2, consumed);
        joined = table1.join(table2, MockValueJoiner.TOSTRING_JOINER);
        joined.toStream().to(output);
        doTestJoin(builder, expectedKeys);
    }

    @Test
    public void testQueryableJoin() {
        final StreamsBuilder builder = new StreamsBuilder();
        final int[] expectedKeys = new int[]{ 0, 1, 2, 3 };
        final KTable<Integer, String> table1;
        final KTable<Integer, String> table2;
        final KTable<Integer, String> table3;
        table1 = builder.table(topic1, consumed);
        table2 = builder.table(topic2, consumed);
        table3 = table1.join(table2, MockValueJoiner.TOSTRING_JOINER, materialized);
        table3.toStream().to(output);
        doTestJoin(builder, expectedKeys);
    }

    @Test
    public void testQueryableNotSendingOldValues() {
        final StreamsBuilder builder = new StreamsBuilder();
        final int[] expectedKeys = new int[]{ 0, 1, 2, 3 };
        final KTable<Integer, String> table1;
        final KTable<Integer, String> table2;
        final KTable<Integer, String> joined;
        final MockProcessorSupplier<Integer, String> supplier = new MockProcessorSupplier<>();
        table1 = builder.table(topic1, consumed);
        table2 = builder.table(topic2, consumed);
        joined = table1.join(table2, MockValueJoiner.TOSTRING_JOINER, materialized);
        builder.build().addProcessor("proc", supplier, ((KTableImpl<?, ?, ?>) (joined)).name);
        doTestNotSendingOldValues(builder, expectedKeys, table1, table2, supplier, joined);
    }

    @Test
    public void testNotSendingOldValues() {
        final StreamsBuilder builder = new StreamsBuilder();
        final int[] expectedKeys = new int[]{ 0, 1, 2, 3 };
        final KTable<Integer, String> table1;
        final KTable<Integer, String> table2;
        final KTable<Integer, String> joined;
        final MockProcessorSupplier<Integer, String> supplier = new MockProcessorSupplier<>();
        table1 = builder.table(topic1, consumed);
        table2 = builder.table(topic2, consumed);
        joined = table1.join(table2, MockValueJoiner.TOSTRING_JOINER);
        builder.build().addProcessor("proc", supplier, ((KTableImpl<?, ?, ?>) (joined)).name);
        doTestNotSendingOldValues(builder, expectedKeys, table1, table2, supplier, joined);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldLogAndMeterSkippedRecordsDueToNullLeftKey() {
        final StreamsBuilder builder = new StreamsBuilder();
        final Processor<String, Change<String>> join = new KTableKTableInnerJoin(((KTableImpl<String, String, String>) (builder.table("left", Consumed.with(Serdes.String(), Serdes.String())))), ((KTableImpl<String, String, String>) (builder.table("right", Consumed.with(Serdes.String(), Serdes.String())))), null).get();
        final MockProcessorContext context = new MockProcessorContext();
        context.setRecordMetadata("left", (-1), (-2), null, (-3));
        join.init(context);
        final LogCaptureAppender appender = LogCaptureAppender.createAndRegister();
        join.process(null, new Change("new", "old"));
        LogCaptureAppender.unregister(appender);
        Assert.assertEquals(1.0, StreamsTestUtils.getMetricByName(context.metrics().metrics(), "skipped-records-total", "stream-metrics").metricValue());
        MatcherAssert.assertThat(appender.getMessages(), CoreMatchers.hasItem("Skipping record due to null key. change=[(new<-old)] topic=[left] partition=[-1] offset=[-2]"));
    }
}

