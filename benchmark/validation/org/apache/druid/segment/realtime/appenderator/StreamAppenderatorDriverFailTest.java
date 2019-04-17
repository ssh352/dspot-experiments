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
package org.apache.druid.segment.realtime.appenderator;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.apache.druid.data.input.Committer;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.SegmentDescriptor;
import org.apache.druid.segment.loading.DataSegmentKiller;
import org.apache.druid.segment.realtime.FireDepartmentMetrics;
import org.apache.druid.timeline.DataSegment;
import org.easymock.EasyMockSupport;
import org.hamcrest.CoreMatchers;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class StreamAppenderatorDriverFailTest extends EasyMockSupport {
    private static final String DATA_SOURCE = "foo";

    private static final ObjectMapper OBJECT_MAPPER = new DefaultObjectMapper();

    private static final long PUBLISH_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(5);

    private static final List<InputRow> ROWS = ImmutableList.of(new org.apache.druid.data.input.MapBasedInputRow(DateTimes.of("2000"), ImmutableList.of("dim1"), ImmutableMap.of("dim1", "foo", "met1", "1")), new org.apache.druid.data.input.MapBasedInputRow(DateTimes.of("2000T01"), ImmutableList.of("dim1"), ImmutableMap.of("dim1", "foo", "met1", 2.0)), new org.apache.druid.data.input.MapBasedInputRow(DateTimes.of("2000T01"), ImmutableList.of("dim2"), ImmutableMap.of("dim2", "bar", "met1", 2.0)));

    SegmentAllocator allocator;

    StreamAppenderatorDriverTest.TestSegmentHandoffNotifierFactory segmentHandoffNotifierFactory;

    StreamAppenderatorDriver driver;

    DataSegmentKiller dataSegmentKiller;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFailDuringPersist() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(ISE.class));
        expectedException.expectMessage(("Fail test while persisting segments" + ("[[foo_2000-01-01T00:00:00.000Z_2000-01-01T01:00:00.000Z_abc123, " + "foo_2000-01-01T01:00:00.000Z_2000-01-01T02:00:00.000Z_abc123]]")));
        driver = new StreamAppenderatorDriver(StreamAppenderatorDriverFailTest.createPersistFailAppenderator(), allocator, segmentHandoffNotifierFactory, new StreamAppenderatorDriverFailTest.NoopUsedSegmentChecker(), dataSegmentKiller, StreamAppenderatorDriverFailTest.OBJECT_MAPPER, new FireDepartmentMetrics());
        driver.startJob();
        final StreamAppenderatorDriverTest.TestCommitterSupplier<Integer> committerSupplier = new StreamAppenderatorDriverTest.TestCommitterSupplier<>();
        segmentHandoffNotifierFactory.setHandoffDelay(100);
        Assert.assertNull(driver.startJob());
        for (int i = 0; i < (StreamAppenderatorDriverFailTest.ROWS.size()); i++) {
            committerSupplier.setMetadata((i + 1));
            Assert.assertTrue(driver.add(StreamAppenderatorDriverFailTest.ROWS.get(i), "dummy", committerSupplier, false, true).isOk());
        }
        driver.publish(StreamAppenderatorDriverTest.makeOkPublisher(), committerSupplier.get(), ImmutableList.of("dummy")).get(StreamAppenderatorDriverFailTest.PUBLISH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testFailDuringPush() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(ISE.class));
        expectedException.expectMessage(("Fail test while pushing segments" + ("[[foo_2000-01-01T00:00:00.000Z_2000-01-01T01:00:00.000Z_abc123, " + "foo_2000-01-01T01:00:00.000Z_2000-01-01T02:00:00.000Z_abc123]]")));
        driver = new StreamAppenderatorDriver(StreamAppenderatorDriverFailTest.createPushFailAppenderator(), allocator, segmentHandoffNotifierFactory, new StreamAppenderatorDriverFailTest.NoopUsedSegmentChecker(), dataSegmentKiller, StreamAppenderatorDriverFailTest.OBJECT_MAPPER, new FireDepartmentMetrics());
        driver.startJob();
        final StreamAppenderatorDriverTest.TestCommitterSupplier<Integer> committerSupplier = new StreamAppenderatorDriverTest.TestCommitterSupplier<>();
        segmentHandoffNotifierFactory.setHandoffDelay(100);
        Assert.assertNull(driver.startJob());
        for (int i = 0; i < (StreamAppenderatorDriverFailTest.ROWS.size()); i++) {
            committerSupplier.setMetadata((i + 1));
            Assert.assertTrue(driver.add(StreamAppenderatorDriverFailTest.ROWS.get(i), "dummy", committerSupplier, false, true).isOk());
        }
        driver.publish(StreamAppenderatorDriverTest.makeOkPublisher(), committerSupplier.get(), ImmutableList.of("dummy")).get(StreamAppenderatorDriverFailTest.PUBLISH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testFailDuringDrop() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(ISE.class));
        expectedException.expectMessage("Fail test while dropping segment[foo_2000-01-01T00:00:00.000Z_2000-01-01T01:00:00.000Z_abc123]");
        driver = new StreamAppenderatorDriver(StreamAppenderatorDriverFailTest.createDropFailAppenderator(), allocator, segmentHandoffNotifierFactory, new StreamAppenderatorDriverFailTest.NoopUsedSegmentChecker(), dataSegmentKiller, StreamAppenderatorDriverFailTest.OBJECT_MAPPER, new FireDepartmentMetrics());
        driver.startJob();
        final StreamAppenderatorDriverTest.TestCommitterSupplier<Integer> committerSupplier = new StreamAppenderatorDriverTest.TestCommitterSupplier<>();
        segmentHandoffNotifierFactory.setHandoffDelay(100);
        Assert.assertNull(driver.startJob());
        for (int i = 0; i < (StreamAppenderatorDriverFailTest.ROWS.size()); i++) {
            committerSupplier.setMetadata((i + 1));
            Assert.assertTrue(driver.add(StreamAppenderatorDriverFailTest.ROWS.get(i), "dummy", committerSupplier, false, true).isOk());
        }
        final SegmentsAndMetadata published = driver.publish(StreamAppenderatorDriverTest.makeOkPublisher(), committerSupplier.get(), ImmutableList.of("dummy")).get(StreamAppenderatorDriverFailTest.PUBLISH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        driver.registerHandoff(published).get();
    }

    @Test
    public void testFailDuringPublish() throws Exception {
        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(ISE.class));
        expectedException.expectMessage("Failed to publish segments because of [test].");
        testFailDuringPublishInternal(false);
    }

    @Test
    public void testFailWithExceptionDuringPublish() throws Exception {
        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(RuntimeException.class));
        expectedException.expectMessage("test");
        testFailDuringPublishInternal(true);
    }

    private static class NoopUsedSegmentChecker implements UsedSegmentChecker {
        @Override
        public Set<DataSegment> findUsedSegments(Set<SegmentIdWithShardSpec> identifiers) {
            return ImmutableSet.of();
        }
    }

    private static class FailableAppenderator implements Appenderator {
        private final Map<SegmentIdWithShardSpec, List<InputRow>> rows = new HashMap<>();

        private boolean dropEnabled = true;

        private boolean persistEnabled = true;

        private boolean pushEnabled = true;

        private boolean interruptPush = false;

        private int numRows;

        public StreamAppenderatorDriverFailTest.FailableAppenderator disableDrop() {
            dropEnabled = false;
            return this;
        }

        public StreamAppenderatorDriverFailTest.FailableAppenderator disablePersist() {
            persistEnabled = false;
            return this;
        }

        public StreamAppenderatorDriverFailTest.FailableAppenderator disablePush() {
            pushEnabled = false;
            interruptPush = false;
            return this;
        }

        public StreamAppenderatorDriverFailTest.FailableAppenderator interruptPush() {
            pushEnabled = false;
            interruptPush = true;
            return this;
        }

        @Override
        public String getDataSource() {
            return null;
        }

        @Override
        public Object startJob() {
            return null;
        }

        @Override
        public AppenderatorAddResult add(SegmentIdWithShardSpec identifier, InputRow row, Supplier<Committer> committerSupplier, boolean allowIncrementalPersists) {
            rows.computeIfAbsent(identifier, ( k) -> new ArrayList<>()).add(row);
            (numRows)++;
            return new AppenderatorAddResult(identifier, numRows, false, null);
        }

        @Override
        public List<SegmentIdWithShardSpec> getSegments() {
            return ImmutableList.copyOf(rows.keySet());
        }

        @Override
        public int getRowCount(SegmentIdWithShardSpec identifier) {
            final List<InputRow> rows = this.rows.get(identifier);
            if (rows != null) {
                return rows.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getTotalRowCount() {
            return numRows;
        }

        @Override
        public void clear() {
            rows.clear();
        }

        @Override
        public ListenableFuture<?> drop(SegmentIdWithShardSpec identifier) {
            if (dropEnabled) {
                rows.remove(identifier);
                return Futures.immediateFuture(null);
            } else {
                return Futures.immediateFailedFuture(new ISE("Fail test while dropping segment[%s]", identifier));
            }
        }

        @Override
        public ListenableFuture<Object> persistAll(Committer committer) {
            if (persistEnabled) {
                // do nothing
                return Futures.immediateFuture(committer.getMetadata());
            } else {
                return Futures.immediateFailedFuture(new ISE("Fail test while persisting segments[%s]", rows.keySet()));
            }
        }

        @Override
        public ListenableFuture<SegmentsAndMetadata> push(Collection<SegmentIdWithShardSpec> identifiers, Committer committer, boolean useUniquePath) {
            if (pushEnabled) {
                final List<DataSegment> segments = identifiers.stream().map(( id) -> new DataSegment(id.getDataSource(), id.getInterval(), id.getVersion(), ImmutableMap.of(), ImmutableList.of(), ImmutableList.of(), id.getShardSpec(), 0, 0)).collect(Collectors.toList());
                return Futures.transform(persistAll(committer), ((Function<Object, SegmentsAndMetadata>) (( commitMetadata) -> new SegmentsAndMetadata(segments, commitMetadata))));
            } else {
                if (interruptPush) {
                    return new AbstractFuture<SegmentsAndMetadata>() {
                        @Override
                        public SegmentsAndMetadata get(long timeout, TimeUnit unit) throws InterruptedException {
                            throw new InterruptedException("Interrupt test while pushing segments");
                        }

                        @Override
                        public SegmentsAndMetadata get() throws InterruptedException {
                            throw new InterruptedException("Interrupt test while pushing segments");
                        }
                    };
                } else {
                    return Futures.immediateFailedFuture(new ISE("Fail test while pushing segments[%s]", identifiers));
                }
            }
        }

        @Override
        public void close() {
        }

        @Override
        public void closeNow() {
        }

        @Override
        public <T> QueryRunner<T> getQueryRunnerForIntervals(Query<T> query, Iterable<Interval> intervals) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> QueryRunner<T> getQueryRunnerForSegments(Query<T> query, Iterable<SegmentDescriptor> specs) {
            throw new UnsupportedOperationException();
        }
    }
}
