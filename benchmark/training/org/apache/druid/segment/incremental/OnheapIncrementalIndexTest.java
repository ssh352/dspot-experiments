/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.incremental;


import Granularities.MINUTE;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.druid.data.input.MapBasedInputRow;
import org.apache.druid.query.aggregation.Aggregator;
import org.apache.druid.query.aggregation.LongMaxAggregator;
import org.apache.druid.query.aggregation.LongMaxAggregatorFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class OnheapIncrementalIndexTest {
    private static final int MAX_ROWS = 100000;

    @Test
    public void testMultithreadAddFacts() throws Exception {
        final IncrementalIndex index = new IncrementalIndex.Builder().setIndexSchema(new IncrementalIndexSchema.Builder().withQueryGranularity(MINUTE).withMetrics(new LongMaxAggregatorFactory("max", "max")).build()).setMaxRowCount(OnheapIncrementalIndexTest.MAX_ROWS).buildOnheap();
        final int addThreadCount = 2;
        Thread[] addThreads = new Thread[addThreadCount];
        for (int i = 0; i < addThreadCount; ++i) {
            addThreads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    final Random random = ThreadLocalRandom.current();
                    try {
                        for (int j = 0; j < ((OnheapIncrementalIndexTest.MAX_ROWS) / addThreadCount); ++j) {
                            index.add(new MapBasedInputRow(0, Collections.singletonList("billy"), ImmutableMap.of("billy", random.nextLong(), "max", 1)));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            addThreads[i].start();
        }
        final AtomicInteger checkFailedCount = new AtomicInteger(0);
        Thread checkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(Thread.interrupted())) {
                    for (IncrementalIndexRow row : index.getFacts().keySet()) {
                        if ((index.getMetricLongValue(row.getRowIndex(), 0)) != 1) {
                            checkFailedCount.addAndGet(1);
                        }
                    }
                } 
            }
        });
        checkThread.start();
        for (int i = 0; i < addThreadCount; ++i) {
            addThreads[i].join();
        }
        checkThread.interrupt();
        Assert.assertEquals(0, checkFailedCount.get());
    }

    @Test
    public void testOnHeapIncrementalIndexClose() throws Exception {
        // Prepare the mocks & set close() call count expectation to 1
        Aggregator mockedAggregator = EasyMock.createMock(LongMaxAggregator.class);
        mockedAggregator.close();
        EasyMock.expectLastCall().times(1);
        final OnheapIncrementalIndex index = ((OnheapIncrementalIndex) (new IncrementalIndex.Builder().setIndexSchema(new IncrementalIndexSchema.Builder().withQueryGranularity(MINUTE).withMetrics(new LongMaxAggregatorFactory("max", "max")).build()).setMaxRowCount(OnheapIncrementalIndexTest.MAX_ROWS).buildOnheap()));
        index.add(new MapBasedInputRow(0, Collections.singletonList("billy"), ImmutableMap.of("billy", 1, "max", 1)));
        // override the aggregators with the mocks
        index.concurrentGet(0)[0] = mockedAggregator;
        // close the indexer and validate the expectations
        EasyMock.replay(mockedAggregator);
        index.close();
        EasyMock.verify(mockedAggregator);
    }
}

