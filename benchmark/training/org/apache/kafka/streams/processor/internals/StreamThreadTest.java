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
package org.apache.kafka.streams.processor.internals;


import Serdes.ByteArraySerde;
import StreamThread.State.CREATED;
import StreamThread.State.DEAD;
import StreamThread.State.PARTITIONS_ASSIGNED;
import StreamThread.State.PARTITIONS_REVOKED;
import StreamThread.State.PENDING_SHUTDOWN;
import StreamThread.State.RUNNING;
import StreamThread.State.STARTING;
import StreamThread.StreamsMetricsThreadImpl;
import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG;
import StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG;
import StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import StreamsConfig.STATE_DIR_CONFIG;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.admin.MockAdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.InvalidOffsetException;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.metrics.JmxReporter;
import org.apache.kafka.common.metrics.Measurable;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.errors.TaskMigratedException;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.internals.ConsumedInternal;
import org.apache.kafka.streams.kstream.internals.InternalStreamsBuilder;
import org.apache.kafka.streams.kstream.internals.MaterializedInternal;
import org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.processor.ThreadMetadata;
import org.apache.kafka.streams.processor.internals.testutil.LogCaptureAppender;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.internals.OffsetCheckpoint;
import org.apache.kafka.test.MockClientSupplier;
import org.apache.kafka.test.MockProcessor;
import org.apache.kafka.test.StreamsTestUtils;
import org.apache.kafka.test.TestUtils;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class StreamThreadTest {
    private final String clientId = "clientId";

    private final String applicationId = "stream-thread-test";

    private final MockTime mockTime = new MockTime();

    private final Metrics metrics = new Metrics();

    private final MockClientSupplier clientSupplier = new MockClientSupplier();

    private final InternalStreamsBuilder internalStreamsBuilder = new InternalStreamsBuilder(new InternalTopologyBuilder());

    private final StreamsConfig config = new StreamsConfig(configProps(false));

    private final String stateDir = TestUtils.tempDirectory().getPath();

    private final StateDirectory stateDirectory = new StateDirectory(config, mockTime, true);

    private final ConsumedInternal<Object, Object> consumed = new ConsumedInternal();

    private UUID processId = UUID.randomUUID();

    private InternalTopologyBuilder internalTopologyBuilder;

    private StreamsMetadataState streamsMetadataState;

    private final String topic1 = "topic1";

    private final String topic2 = "topic2";

    private final TopicPartition t1p1 = new TopicPartition(topic1, 1);

    private final TopicPartition t1p2 = new TopicPartition(topic1, 2);

    private final TopicPartition t2p1 = new TopicPartition(topic2, 1);

    // task0 is unused
    private final TaskId task1 = new TaskId(0, 1);

    private final TaskId task2 = new TaskId(0, 2);

    private final TaskId task3 = new TaskId(1, 1);

    @Test
    public void testPartitionAssignmentChangeForSingleGroup() {
        internalTopologyBuilder.addSource(null, "source1", null, null, null, topic1);
        final StreamThread thread = createStreamThread(clientId, config, false);
        final StreamThreadTest.StateListenerStub stateListener = new StreamThreadTest.StateListenerStub();
        thread.setStateListener(stateListener);
        Assert.assertEquals(thread.state(), CREATED);
        final ConsumerRebalanceListener rebalanceListener = thread.rebalanceListener;
        final List<TopicPartition> revokedPartitions;
        final List<TopicPartition> assignedPartitions;
        // revoke nothing
        thread.setState(STARTING);
        revokedPartitions = Collections.emptyList();
        rebalanceListener.onPartitionsRevoked(revokedPartitions);
        Assert.assertEquals(thread.state(), PARTITIONS_REVOKED);
        // assign single partition
        assignedPartitions = Collections.singletonList(t1p1);
        thread.taskManager().setAssignmentMetadata(Collections.emptyMap(), Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(assignedPartitions);
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        Assert.assertEquals(thread.state(), RUNNING);
        Assert.assertEquals(4, stateListener.numChanges);
        Assert.assertEquals(PARTITIONS_ASSIGNED, stateListener.oldState);
        thread.shutdown();
        Assert.assertSame(PENDING_SHUTDOWN, thread.state());
    }

    @Test
    public void testStateChangeStartClose() throws Exception {
        final StreamThread thread = createStreamThread(clientId, config, false);
        final StreamThreadTest.StateListenerStub stateListener = new StreamThreadTest.StateListenerStub();
        thread.setStateListener(stateListener);
        thread.start();
        TestUtils.waitForCondition(() -> (thread.state()) == StreamThread.State.STARTING, (10 * 1000), "Thread never started.");
        thread.shutdown();
        TestUtils.waitForCondition(() -> (thread.state()) == StreamThread.State.DEAD, (10 * 1000), "Thread never shut down.");
        thread.shutdown();
        Assert.assertEquals(thread.state(), DEAD);
    }

    @Test
    public void testMetricsCreatedAtStartup() {
        final StreamThread thread = createStreamThread(clientId, config, false);
        final String defaultGroupName = "stream-metrics";
        final Map<String, String> defaultTags = Collections.singletonMap("client-id", thread.getName());
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("commit-latency-avg", defaultGroupName, "The average commit time in ms", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("commit-latency-max", defaultGroupName, "The maximum commit time in ms", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("commit-rate", defaultGroupName, "The average per-second number of commit calls", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("commit-total", defaultGroupName, "The total number of commit calls", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("poll-latency-avg", defaultGroupName, "The average poll time in ms", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("poll-latency-max", defaultGroupName, "The maximum poll time in ms", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("poll-rate", defaultGroupName, "The average per-second number of record-poll calls", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("poll-total", defaultGroupName, "The total number of record-poll calls", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("process-latency-avg", defaultGroupName, "The average process time in ms", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("process-latency-max", defaultGroupName, "The maximum process time in ms", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("process-rate", defaultGroupName, "The average per-second number of process calls", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("process-total", defaultGroupName, "The total number of process calls", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("punctuate-latency-avg", defaultGroupName, "The average punctuate time in ms", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("punctuate-latency-max", defaultGroupName, "The maximum punctuate time in ms", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("punctuate-rate", defaultGroupName, "The average per-second number of punctuate calls", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("punctuate-total", defaultGroupName, "The total number of punctuate calls", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("task-created-rate", defaultGroupName, "The average per-second number of newly created tasks", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("task-created-total", defaultGroupName, "The total number of newly created tasks", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("task-closed-rate", defaultGroupName, "The average per-second number of closed tasks", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("task-closed-total", defaultGroupName, "The total number of closed tasks", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("skipped-records-rate", defaultGroupName, "The average per-second number of skipped records.", defaultTags)));
        Assert.assertNotNull(metrics.metrics().get(metrics.metricName("skipped-records-total", defaultGroupName, "The total number of skipped records.", defaultTags)));
        final JmxReporter reporter = new JmxReporter("kafka.streams");
        metrics.addReporter(reporter);
        Assert.assertTrue(reporter.containsMbean(String.format("kafka.streams:type=%s,client-id=%s", defaultGroupName, thread.getName())));
    }

    @Test
    public void shouldNotCommitBeforeTheCommitInterval() {
        final long commitInterval = 1000L;
        final Properties props = configProps(false);
        props.setProperty(STATE_DIR_CONFIG, stateDir);
        props.setProperty(COMMIT_INTERVAL_MS_CONFIG, Long.toString(commitInterval));
        final StreamsConfig config = new StreamsConfig(props);
        final Consumer<byte[], byte[]> consumer = EasyMock.createNiceMock(Consumer.class);
        final TaskManager taskManager = mockTaskManagerCommit(consumer, 1, 1);
        final StreamThread.StreamsMetricsThreadImpl streamsMetrics = new StreamThread.StreamsMetricsThreadImpl(metrics, "");
        final StreamThread thread = new StreamThread(mockTime, config, null, consumer, consumer, null, taskManager, streamsMetrics, internalTopologyBuilder, clientId, new LogContext(""), new AtomicInteger());
        thread.setNow(mockTime.milliseconds());
        thread.maybeCommit();
        mockTime.sleep((commitInterval - 10L));
        thread.setNow(mockTime.milliseconds());
        thread.maybeCommit();
        EasyMock.verify(taskManager);
    }

    @Test
    public void shouldRespectNumIterationsInMainLoop() {
        final MockProcessor mockProcessor = new MockProcessor(PunctuationType.WALL_CLOCK_TIME, 10L);
        internalTopologyBuilder.addSource(null, "source1", null, null, null, topic1);
        internalTopologyBuilder.addProcessor("processor1", () -> mockProcessor, "source1");
        internalTopologyBuilder.addProcessor("processor2", () -> new MockProcessor(PunctuationType.STREAM_TIME, 10L), "source1");
        final Properties properties = new Properties();
        properties.put(COMMIT_INTERVAL_MS_CONFIG, 100L);
        final StreamsConfig config = new StreamsConfig(StreamsTestUtils.getStreamsConfig(applicationId, "localhost:2171", ByteArraySerde.class.getName(), ByteArraySerde.class.getName(), properties));
        final StreamThread thread = createStreamThread(clientId, config, false);
        thread.setState(STARTING);
        thread.setState(PARTITIONS_REVOKED);
        final Set<TopicPartition> assignedPartitions = Collections.singleton(t1p1);
        thread.taskManager().setAssignmentMetadata(Collections.singletonMap(new TaskId(0, t1p1.partition()), assignedPartitions), Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(Collections.singleton(t1p1));
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        // processed one record, punctuated after the first record, and hence num.iterations is still 1
        long offset = -1;
        addRecord(mockConsumer, (++offset), 0L);
        thread.runOnce();
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(1));
        // processed one more record without punctuation, and bump num.iterations to 2
        addRecord(mockConsumer, (++offset), 1L);
        thread.runOnce();
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(2));
        // processed zero records, early exit and iterations stays as 2
        thread.runOnce();
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(2));
        // system time based punctutation halves to 1
        mockTime.sleep(11L);
        thread.runOnce();
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(1));
        // processed two records, bumping up iterations to 2
        addRecord(mockConsumer, (++offset), 5L);
        addRecord(mockConsumer, (++offset), 6L);
        thread.runOnce();
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(2));
        // stream time based punctutation halves to 1
        addRecord(mockConsumer, (++offset), 11L);
        thread.runOnce();
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(1));
        // processed three records, bumping up iterations to 3 (1 + 2)
        addRecord(mockConsumer, (++offset), 12L);
        addRecord(mockConsumer, (++offset), 13L);
        addRecord(mockConsumer, (++offset), 14L);
        thread.runOnce();
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(3));
        mockProcessor.requestCommit();
        addRecord(mockConsumer, (++offset), 15L);
        thread.runOnce();
        // user requested commit should not impact on iteration adjustment
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(3));
        // time based commit, halves iterations to 3 / 2 = 1
        mockTime.sleep(90L);
        thread.runOnce();
        MatcherAssert.assertThat(thread.currentNumIterations(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldNotCauseExceptionIfNothingCommitted() {
        final long commitInterval = 1000L;
        final Properties props = configProps(false);
        props.setProperty(STATE_DIR_CONFIG, stateDir);
        props.setProperty(COMMIT_INTERVAL_MS_CONFIG, Long.toString(commitInterval));
        final StreamsConfig config = new StreamsConfig(props);
        final Consumer<byte[], byte[]> consumer = EasyMock.createNiceMock(Consumer.class);
        final TaskManager taskManager = mockTaskManagerCommit(consumer, 1, 0);
        final StreamThread.StreamsMetricsThreadImpl streamsMetrics = new StreamThread.StreamsMetricsThreadImpl(metrics, "");
        final StreamThread thread = new StreamThread(mockTime, config, null, consumer, consumer, null, taskManager, streamsMetrics, internalTopologyBuilder, clientId, new LogContext(""), new AtomicInteger());
        thread.setNow(mockTime.milliseconds());
        thread.maybeCommit();
        mockTime.sleep((commitInterval - 10L));
        thread.setNow(mockTime.milliseconds());
        thread.maybeCommit();
        EasyMock.verify(taskManager);
    }

    @Test
    public void shouldCommitAfterTheCommitInterval() {
        final long commitInterval = 1000L;
        final Properties props = configProps(false);
        props.setProperty(STATE_DIR_CONFIG, stateDir);
        props.setProperty(COMMIT_INTERVAL_MS_CONFIG, Long.toString(commitInterval));
        final StreamsConfig config = new StreamsConfig(props);
        final Consumer<byte[], byte[]> consumer = EasyMock.createNiceMock(Consumer.class);
        final TaskManager taskManager = mockTaskManagerCommit(consumer, 2, 1);
        final StreamThread.StreamsMetricsThreadImpl streamsMetrics = new StreamThread.StreamsMetricsThreadImpl(metrics, "");
        final StreamThread thread = new StreamThread(mockTime, config, null, consumer, consumer, null, taskManager, streamsMetrics, internalTopologyBuilder, clientId, new LogContext(""), new AtomicInteger());
        thread.setNow(mockTime.milliseconds());
        thread.maybeCommit();
        mockTime.sleep((commitInterval + 1));
        thread.setNow(mockTime.milliseconds());
        thread.maybeCommit();
        EasyMock.verify(taskManager);
    }

    @Test
    public void shouldInjectSharedProducerForAllTasksUsingClientSupplierOnCreateIfEosDisabled() {
        internalTopologyBuilder.addSource(null, "source1", null, null, null, topic1);
        internalStreamsBuilder.buildAndOptimizeTopology();
        final StreamThread thread = createStreamThread(clientId, config, false);
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(Collections.emptyList());
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        assignedPartitions.add(t1p2);
        activeTasks.put(task1, Collections.singleton(t1p1));
        activeTasks.put(task2, Collections.singleton(t1p2));
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(assignedPartitions);
        final Map<TopicPartition, Long> beginOffsets = new HashMap<>();
        beginOffsets.put(t1p1, 0L);
        beginOffsets.put(t1p2, 0L);
        mockConsumer.updateBeginningOffsets(beginOffsets);
        thread.rebalanceListener.onPartitionsAssigned(new java.util.HashSet(assignedPartitions));
        Assert.assertEquals(1, clientSupplier.producers.size());
        final Producer globalProducer = clientSupplier.producers.get(0);
        for (final Task task : thread.tasks().values()) {
            Assert.assertSame(globalProducer, producer());
        }
        Assert.assertSame(clientSupplier.consumer, thread.consumer);
        Assert.assertSame(clientSupplier.restoreConsumer, thread.restoreConsumer);
    }

    @Test
    public void shouldInjectProducerPerTaskUsingClientSupplierOnCreateIfEosEnable() {
        internalTopologyBuilder.addSource(null, "source1", null, null, null, topic1);
        final StreamThread thread = createStreamThread(clientId, new StreamsConfig(configProps(true)), true);
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(Collections.emptyList());
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        assignedPartitions.add(t1p2);
        activeTasks.put(task1, Collections.singleton(t1p1));
        activeTasks.put(task2, Collections.singleton(t1p2));
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(assignedPartitions);
        final Map<TopicPartition, Long> beginOffsets = new HashMap<>();
        beginOffsets.put(t1p1, 0L);
        beginOffsets.put(t1p2, 0L);
        mockConsumer.updateBeginningOffsets(beginOffsets);
        thread.rebalanceListener.onPartitionsAssigned(new java.util.HashSet(assignedPartitions));
        thread.runOnce();
        Assert.assertEquals(thread.tasks().size(), clientSupplier.producers.size());
        Assert.assertSame(clientSupplier.consumer, thread.consumer);
        Assert.assertSame(clientSupplier.restoreConsumer, thread.restoreConsumer);
    }

    @Test
    public void shouldCloseAllTaskProducersOnCloseIfEosEnabled() {
        internalTopologyBuilder.addSource(null, "source1", null, null, null, topic1);
        final StreamThread thread = createStreamThread(clientId, new StreamsConfig(configProps(true)), true);
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(Collections.emptyList());
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        assignedPartitions.add(t1p2);
        activeTasks.put(task1, Collections.singleton(t1p1));
        activeTasks.put(task2, Collections.singleton(t1p2));
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(assignedPartitions);
        final Map<TopicPartition, Long> beginOffsets = new HashMap<>();
        beginOffsets.put(t1p1, 0L);
        beginOffsets.put(t1p2, 0L);
        mockConsumer.updateBeginningOffsets(beginOffsets);
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.shutdown();
        thread.run();
        for (final Task task : thread.tasks().values()) {
            Assert.assertTrue(closed());
        }
    }

    @Test
    public void shouldShutdownTaskManagerOnClose() {
        final Consumer<byte[], byte[]> consumer = EasyMock.createNiceMock(Consumer.class);
        final TaskManager taskManager = EasyMock.createNiceMock(TaskManager.class);
        taskManager.shutdown(true);
        EasyMock.expectLastCall();
        EasyMock.replay(taskManager, consumer);
        final StreamThread.StreamsMetricsThreadImpl streamsMetrics = new StreamThread.StreamsMetricsThreadImpl(metrics, "");
        final StreamThread thread = new StreamThread(mockTime, config, null, consumer, consumer, null, taskManager, streamsMetrics, internalTopologyBuilder, clientId, new LogContext(""), new AtomicInteger()).updateThreadMetadata(StreamThread.getSharedAdminClientId(clientId));
        thread.setStateListener(( t, newState, oldState) -> {
            if ((oldState == StreamThread.State.CREATED) && (newState == StreamThread.State.STARTING)) {
                thread.shutdown();
            }
        });
        thread.run();
        EasyMock.verify(taskManager);
    }

    @Test
    public void shouldShutdownTaskManagerOnCloseWithoutStart() {
        final Consumer<byte[], byte[]> consumer = EasyMock.createNiceMock(Consumer.class);
        final TaskManager taskManager = EasyMock.createNiceMock(TaskManager.class);
        taskManager.shutdown(true);
        EasyMock.expectLastCall();
        EasyMock.replay(taskManager, consumer);
        final StreamThread.StreamsMetricsThreadImpl streamsMetrics = new StreamThread.StreamsMetricsThreadImpl(metrics, "");
        final StreamThread thread = new StreamThread(mockTime, config, null, consumer, consumer, null, taskManager, streamsMetrics, internalTopologyBuilder, clientId, new LogContext(""), new AtomicInteger()).updateThreadMetadata(StreamThread.getSharedAdminClientId(clientId));
        thread.shutdown();
        EasyMock.verify(taskManager);
    }

    @Test
    public void shouldOnlyShutdownOnce() {
        final Consumer<byte[], byte[]> consumer = EasyMock.createNiceMock(Consumer.class);
        final TaskManager taskManager = EasyMock.createNiceMock(TaskManager.class);
        taskManager.shutdown(true);
        EasyMock.expectLastCall();
        EasyMock.replay(taskManager, consumer);
        final StreamThread.StreamsMetricsThreadImpl streamsMetrics = new StreamThread.StreamsMetricsThreadImpl(metrics, "");
        final StreamThread thread = new StreamThread(mockTime, config, null, consumer, consumer, null, taskManager, streamsMetrics, internalTopologyBuilder, clientId, new LogContext(""), new AtomicInteger()).updateThreadMetadata(StreamThread.getSharedAdminClientId(clientId));
        thread.shutdown();
        // Execute the run method. Verification of the mock will check that shutdown was only done once
        thread.run();
        EasyMock.verify(taskManager);
    }

    @Test
    public void shouldNotNullPointerWhenStandbyTasksAssignedAndNoStateStoresForTopology() {
        internalTopologyBuilder.addSource(null, "name", null, null, null, "topic");
        internalTopologyBuilder.addSink("out", "output", null, null, null, "name");
        final StreamThread thread = createStreamThread(clientId, config, false);
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(Collections.emptyList());
        final Map<TaskId, Set<TopicPartition>> standbyTasks = new HashMap<>();
        // assign single partition
        standbyTasks.put(task1, Collections.singleton(t1p1));
        thread.taskManager().setAssignmentMetadata(Collections.emptyMap(), standbyTasks);
        thread.taskManager().createTasks(Collections.emptyList());
        thread.rebalanceListener.onPartitionsAssigned(Collections.emptyList());
    }

    @Test
    public void shouldCloseTaskAsZombieAndRemoveFromActiveTasksIfProducerWasFencedWhileProcessing() throws Exception {
        internalTopologyBuilder.addSource(null, "source", null, null, null, topic1);
        internalTopologyBuilder.addSink("sink", "dummyTopic", null, null, null, "source");
        final StreamThread thread = createStreamThread(clientId, new StreamsConfig(configProps(true)), true);
        final MockConsumer<byte[], byte[]> consumer = clientSupplier.consumer;
        consumer.updatePartitions(topic1, Collections.singletonList(new PartitionInfo(topic1, 1, null, null, null)));
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(null);
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        activeTasks.put(task1, Collections.singleton(t1p1));
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(assignedPartitions);
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        MatcherAssert.assertThat(thread.tasks().size(), CoreMatchers.equalTo(1));
        final MockProducer producer = clientSupplier.producers.get(0);
        // change consumer subscription from "pattern" to "manual" to be able to call .addRecords()
        consumer.updateBeginningOffsets(Collections.singletonMap(assignedPartitions.iterator().next(), 0L));
        consumer.unsubscribe();
        consumer.assign(new java.util.HashSet(assignedPartitions));
        consumer.addRecord(new ConsumerRecord(topic1, 1, 0, new byte[0], new byte[0]));
        mockTime.sleep(((config.getLong(COMMIT_INTERVAL_MS_CONFIG)) + 1));
        thread.runOnce();
        MatcherAssert.assertThat(producer.history().size(), CoreMatchers.equalTo(1));
        Assert.assertFalse(producer.transactionCommitted());
        mockTime.sleep(((config.getLong(COMMIT_INTERVAL_MS_CONFIG)) + 1L));
        TestUtils.waitForCondition(() -> (producer.commitCount()) == 1, "StreamsThread did not commit transaction.");
        producer.fenceProducer();
        mockTime.sleep(((config.getLong(COMMIT_INTERVAL_MS_CONFIG)) + 1L));
        consumer.addRecord(new ConsumerRecord(topic1, 1, 1, new byte[0], new byte[0]));
        try {
            thread.runOnce();
            Assert.fail("Should have thrown TaskMigratedException");
        } catch (final TaskMigratedException expected) {
            /* ignore */
        }
        TestUtils.waitForCondition(() -> thread.tasks().isEmpty(), "StreamsThread did not remove fenced zombie task.");
        MatcherAssert.assertThat(producer.commitCount(), CoreMatchers.equalTo(1L));
    }

    @Test
    public void shouldCloseTaskAsZombieAndRemoveFromActiveTasksIfProducerGotFencedInCommitTransactionWhenSuspendingTaks() {
        final StreamThread thread = createStreamThread(clientId, new StreamsConfig(configProps(true)), true);
        internalTopologyBuilder.addSource(null, "name", null, null, null, topic1);
        internalTopologyBuilder.addSink("out", "output", null, null, null, "name");
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(null);
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        activeTasks.put(task1, Collections.singleton(t1p1));
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(assignedPartitions);
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        MatcherAssert.assertThat(thread.tasks().size(), CoreMatchers.equalTo(1));
        clientSupplier.producers.get(0).fenceProducer();
        thread.rebalanceListener.onPartitionsRevoked(null);
        Assert.assertTrue(clientSupplier.producers.get(0).transactionInFlight());
        Assert.assertFalse(clientSupplier.producers.get(0).transactionCommitted());
        Assert.assertTrue(closed());
        Assert.assertTrue(thread.tasks().isEmpty());
    }

    @Test
    public void shouldCloseTaskAsZombieAndRemoveFromActiveTasksIfProducerGotFencedInCloseTransactionWhenSuspendingTasks() {
        final StreamThread thread = createStreamThread(clientId, new StreamsConfig(configProps(true)), true);
        internalTopologyBuilder.addSource(null, "name", null, null, null, topic1);
        internalTopologyBuilder.addSink("out", "output", null, null, null, "name");
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(null);
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        activeTasks.put(task1, Collections.singleton(t1p1));
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(assignedPartitions);
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        MatcherAssert.assertThat(thread.tasks().size(), CoreMatchers.equalTo(1));
        clientSupplier.producers.get(0).fenceProducerOnClose();
        thread.rebalanceListener.onPartitionsRevoked(null);
        Assert.assertFalse(clientSupplier.producers.get(0).transactionInFlight());
        Assert.assertTrue(clientSupplier.producers.get(0).transactionCommitted());
        Assert.assertFalse(closed());
        Assert.assertTrue(thread.tasks().isEmpty());
    }

    private static class StateListenerStub implements StreamThread.StateListener {
        int numChanges = 0;

        ThreadStateTransitionValidator oldState = null;

        ThreadStateTransitionValidator newState = null;

        @Override
        public void onChange(final Thread thread, final ThreadStateTransitionValidator newState, final ThreadStateTransitionValidator oldState) {
            ++(numChanges);
            if ((this.newState) != null) {
                if ((this.newState) != oldState) {
                    throw new RuntimeException(((("State mismatch " + oldState) + " different from ") + (this.newState)));
                }
            }
            this.oldState = oldState;
            this.newState = newState;
        }
    }

    @Test
    public void shouldReturnActiveTaskMetadataWhileRunningState() {
        internalTopologyBuilder.addSource(null, "source", null, null, null, topic1);
        final StreamThread thread = createStreamThread(clientId, config, false);
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(null);
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        activeTasks.put(task1, Collections.singleton(t1p1));
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(assignedPartitions);
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        final ThreadMetadata threadMetadata = thread.threadMetadata();
        Assert.assertEquals(RUNNING.name(), threadMetadata.threadState());
        Assert.assertTrue(threadMetadata.activeTasks().contains(new org.apache.kafka.streams.processor.TaskMetadata(task1.toString(), Utils.mkSet(t1p1))));
        Assert.assertTrue(threadMetadata.standbyTasks().isEmpty());
    }

    @Test
    public void shouldReturnStandbyTaskMetadataWhileRunningState() {
        internalStreamsBuilder.stream(Collections.singleton(topic1), consumed).groupByKey().count(Materialized.as("count-one"));
        internalStreamsBuilder.buildAndOptimizeTopology();
        final StreamThread thread = createStreamThread(clientId, config, false);
        final MockConsumer<byte[], byte[]> restoreConsumer = clientSupplier.restoreConsumer;
        restoreConsumer.updatePartitions("stream-thread-test-count-one-changelog", Collections.singletonList(new PartitionInfo("stream-thread-test-count-one-changelog", 0, null, new Node[0], new Node[0])));
        final HashMap<TopicPartition, Long> offsets = new HashMap<>();
        offsets.put(new TopicPartition("stream-thread-test-count-one-changelog", 1), 0L);
        restoreConsumer.updateEndOffsets(offsets);
        restoreConsumer.updateBeginningOffsets(offsets);
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(null);
        final Map<TaskId, Set<TopicPartition>> standbyTasks = new HashMap<>();
        // assign single partition
        standbyTasks.put(task1, Collections.singleton(t1p1));
        thread.taskManager().setAssignmentMetadata(Collections.emptyMap(), standbyTasks);
        thread.rebalanceListener.onPartitionsAssigned(Collections.emptyList());
        thread.runOnce();
        final ThreadMetadata threadMetadata = thread.threadMetadata();
        Assert.assertEquals(RUNNING.name(), threadMetadata.threadState());
        Assert.assertTrue(threadMetadata.standbyTasks().contains(new org.apache.kafka.streams.processor.TaskMetadata(task1.toString(), Utils.mkSet(t1p1))));
        Assert.assertTrue(threadMetadata.activeTasks().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUpdateStandbyTask() throws Exception {
        final String storeName1 = "count-one";
        final String storeName2 = "table-two";
        final String changelogName1 = (((applicationId) + "-") + storeName1) + "-changelog";
        final String changelogName2 = (((applicationId) + "-") + storeName2) + "-changelog";
        final TopicPartition partition1 = new TopicPartition(changelogName1, 1);
        final TopicPartition partition2 = new TopicPartition(changelogName2, 1);
        internalStreamsBuilder.stream(Collections.singleton(topic1), consumed).groupByKey().count(Materialized.as(storeName1));
        final MaterializedInternal<Object, Object, KeyValueStore<Bytes, byte[]>> materialized = new MaterializedInternal(Materialized.as(storeName2), internalStreamsBuilder, "");
        internalStreamsBuilder.table(topic2, new ConsumedInternal(), materialized);
        internalStreamsBuilder.buildAndOptimizeTopology();
        final StreamThread thread = createStreamThread(clientId, config, false);
        final MockConsumer<byte[], byte[]> restoreConsumer = clientSupplier.restoreConsumer;
        restoreConsumer.updatePartitions(changelogName1, Collections.singletonList(new PartitionInfo(changelogName1, 1, null, new Node[0], new Node[0])));
        restoreConsumer.assign(Utils.mkSet(partition1, partition2));
        restoreConsumer.updateEndOffsets(Collections.singletonMap(partition1, 10L));
        restoreConsumer.updateBeginningOffsets(Collections.singletonMap(partition1, 0L));
        restoreConsumer.updateEndOffsets(Collections.singletonMap(partition2, 10L));
        restoreConsumer.updateBeginningOffsets(Collections.singletonMap(partition2, 0L));
        // let the store1 be restored from 0 to 10; store2 be restored from 5 (checkpointed) to 10
        final OffsetCheckpoint checkpoint = new OffsetCheckpoint(new File(stateDirectory.directoryForTask(task3), AbstractStateManager.CHECKPOINT_FILE_NAME));
        checkpoint.write(Collections.singletonMap(partition2, 5L));
        for (long i = 0L; i < 10L; i++) {
            restoreConsumer.addRecord(new ConsumerRecord(changelogName1, 1, i, ("K" + i).getBytes(), ("V" + i).getBytes()));
            restoreConsumer.addRecord(new ConsumerRecord(changelogName2, 1, i, ("K" + i).getBytes(), ("V" + i).getBytes()));
        }
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(null);
        final Map<TaskId, Set<TopicPartition>> standbyTasks = new HashMap<>();
        // assign single partition
        standbyTasks.put(task1, Collections.singleton(t1p1));
        standbyTasks.put(task3, Collections.singleton(t2p1));
        thread.taskManager().setAssignmentMetadata(Collections.emptyMap(), standbyTasks);
        thread.rebalanceListener.onPartitionsAssigned(Collections.emptyList());
        thread.runOnce();
        final StandbyTask standbyTask1 = thread.taskManager().standbyTask(partition1);
        final StandbyTask standbyTask2 = thread.taskManager().standbyTask(partition2);
        final KeyValueStore<Object, Long> store1 = ((KeyValueStore<Object, Long>) (standbyTask1.getStore(storeName1)));
        final KeyValueStore<Object, Long> store2 = ((KeyValueStore<Object, Long>) (standbyTask2.getStore(storeName2)));
        Assert.assertEquals(10L, store1.approximateNumEntries());
        Assert.assertEquals(5L, store2.approximateNumEntries());
        Assert.assertEquals(0, thread.standbyRecords().size());
    }

    @Test
    public void shouldPunctuateActiveTask() {
        final List<Long> punctuatedStreamTime = new ArrayList<>();
        final List<Long> punctuatedWallClockTime = new ArrayList<>();
        final ProcessorSupplier<Object, Object> punctuateProcessor = () -> new Processor<Object, Object>() {
            @Override
            public void init(final ProcessorContext context) {
                context.schedule(Duration.ofMillis(100L), PunctuationType.STREAM_TIME, punctuatedStreamTime::add);
                context.schedule(Duration.ofMillis(100L), PunctuationType.WALL_CLOCK_TIME, punctuatedWallClockTime::add);
            }

            @Override
            public void process(final Object key, final Object value) {
            }

            @Override
            public void close() {
            }
        };
        internalStreamsBuilder.stream(Collections.singleton(topic1), consumed).process(punctuateProcessor);
        internalStreamsBuilder.buildAndOptimizeTopology();
        final StreamThread thread = createStreamThread(clientId, config, false);
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(null);
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        activeTasks.put(task1, Collections.singleton(t1p1));
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        clientSupplier.consumer.assign(assignedPartitions);
        clientSupplier.consumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        Assert.assertEquals(0, punctuatedStreamTime.size());
        Assert.assertEquals(0, punctuatedWallClockTime.size());
        mockTime.sleep(100L);
        for (long i = 0L; i < 10L; i++) {
            clientSupplier.consumer.addRecord(new ConsumerRecord(topic1, 1, i, (i * 100L), TimestampType.CREATE_TIME, ConsumerRecord.NULL_CHECKSUM, ("K" + i).getBytes().length, ("V" + i).getBytes().length, ("K" + i).getBytes(), ("V" + i).getBytes()));
        }
        thread.runOnce();
        Assert.assertEquals(1, punctuatedStreamTime.size());
        Assert.assertEquals(1, punctuatedWallClockTime.size());
        mockTime.sleep(100L);
        thread.runOnce();
        // we should skip stream time punctuation, only trigger wall-clock time punctuation
        Assert.assertEquals(1, punctuatedStreamTime.size());
        Assert.assertEquals(2, punctuatedWallClockTime.size());
    }

    @Test
    public void shouldAlwaysUpdateTasksMetadataAfterChangingState() {
        final StreamThread thread = createStreamThread(clientId, config, false);
        ThreadMetadata metadata = thread.threadMetadata();
        Assert.assertEquals(CREATED.name(), metadata.threadState());
        thread.setState(STARTING);
        thread.setState(PARTITIONS_REVOKED);
        thread.setState(PARTITIONS_ASSIGNED);
        thread.setState(RUNNING);
        metadata = thread.threadMetadata();
        Assert.assertEquals(RUNNING.name(), metadata.threadState());
    }

    @Test
    public void shouldAlwaysReturnEmptyTasksMetadataWhileRebalancingStateAndTasksNotRunning() {
        internalStreamsBuilder.stream(Collections.singleton(topic1), consumed).groupByKey().count(Materialized.as("count-one"));
        internalStreamsBuilder.buildAndOptimizeTopology();
        final StreamThread thread = createStreamThread(clientId, config, false);
        final MockConsumer<byte[], byte[]> restoreConsumer = clientSupplier.restoreConsumer;
        restoreConsumer.updatePartitions("stream-thread-test-count-one-changelog", Arrays.asList(new PartitionInfo("stream-thread-test-count-one-changelog", 0, null, new Node[0], new Node[0]), new PartitionInfo("stream-thread-test-count-one-changelog", 1, null, new Node[0], new Node[0])));
        final HashMap<TopicPartition, Long> offsets = new HashMap<>();
        offsets.put(new TopicPartition("stream-thread-test-count-one-changelog", 0), 0L);
        offsets.put(new TopicPartition("stream-thread-test-count-one-changelog", 1), 0L);
        restoreConsumer.updateEndOffsets(offsets);
        restoreConsumer.updateBeginningOffsets(offsets);
        clientSupplier.consumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        final List<TopicPartition> assignedPartitions = new ArrayList<>();
        thread.setState(STARTING);
        thread.rebalanceListener.onPartitionsRevoked(assignedPartitions);
        assertThreadMetadataHasEmptyTasksWithState(thread.threadMetadata(), PARTITIONS_REVOKED);
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final Map<TaskId, Set<TopicPartition>> standbyTasks = new HashMap<>();
        // assign single partition
        assignedPartitions.add(t1p1);
        activeTasks.put(task1, Collections.singleton(t1p1));
        standbyTasks.put(task2, Collections.singleton(t1p2));
        thread.taskManager().setAssignmentMetadata(activeTasks, standbyTasks);
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        assertThreadMetadataHasEmptyTasksWithState(thread.threadMetadata(), PARTITIONS_ASSIGNED);
    }

    @Test
    public void shouldRecoverFromInvalidOffsetExceptionOnRestoreAndFinishRestore() throws Exception {
        internalStreamsBuilder.stream(Collections.singleton("topic"), consumed).groupByKey().count(Materialized.as("count"));
        internalStreamsBuilder.buildAndOptimizeTopology();
        final StreamThread thread = createStreamThread("clientId", config, false);
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        final MockConsumer<byte[], byte[]> mockRestoreConsumer = ((MockConsumer<byte[], byte[]>) (thread.restoreConsumer));
        final TopicPartition topicPartition = new TopicPartition("topic", 0);
        final Set<TopicPartition> topicPartitionSet = Collections.singleton(topicPartition);
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        activeTasks.put(new TaskId(0, 0), topicPartitionSet);
        thread.taskManager().setAssignmentMetadata(activeTasks, Collections.emptyMap());
        mockConsumer.updatePartitions("topic", Collections.singletonList(new PartitionInfo("topic", 0, null, new Node[0], new Node[0])));
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(topicPartition, 0L));
        mockRestoreConsumer.updatePartitions("stream-thread-test-count-changelog", Collections.singletonList(new PartitionInfo("stream-thread-test-count-changelog", 0, null, new Node[0], new Node[0])));
        final TopicPartition changelogPartition = new TopicPartition("stream-thread-test-count-changelog", 0);
        final Set<TopicPartition> changelogPartitionSet = Collections.singleton(changelogPartition);
        mockRestoreConsumer.updateBeginningOffsets(Collections.singletonMap(changelogPartition, 0L));
        mockRestoreConsumer.updateEndOffsets(Collections.singletonMap(changelogPartition, 2L));
        mockConsumer.schedulePollTask(() -> {
            thread.setState(StreamThread.State.PARTITIONS_REVOKED);
            thread.rebalanceListener.onPartitionsAssigned(topicPartitionSet);
        });
        try {
            thread.start();
            TestUtils.waitForCondition(() -> (mockRestoreConsumer.assignment().size()) == 1, "Never restore first record");
            mockRestoreConsumer.addRecord(new ConsumerRecord("stream-thread-test-count-changelog", 0, 0L, "K1".getBytes(), "V1".getBytes()));
            TestUtils.waitForCondition(() -> (mockRestoreConsumer.position(changelogPartition)) == 1L, "Never restore first record");
            mockRestoreConsumer.setException(new InvalidOffsetException("Try Again!") {
                @Override
                public Set<TopicPartition> partitions() {
                    return changelogPartitionSet;
                }
            });
            mockRestoreConsumer.addRecord(new ConsumerRecord("stream-thread-test-count-changelog", 0, 0L, "K1".getBytes(), "V1".getBytes()));
            mockRestoreConsumer.addRecord(new ConsumerRecord("stream-thread-test-count-changelog", 0, 1L, "K2".getBytes(), "V2".getBytes()));
            TestUtils.waitForCondition(() -> {
                mockRestoreConsumer.assign(changelogPartitionSet);
                return (mockRestoreConsumer.position(changelogPartition)) == 2L;
            }, "Never finished restore");
        } finally {
            thread.shutdown();
            thread.join(10000);
        }
    }

    @Test
    public void shouldRecordSkippedMetricForDeserializationException() {
        final LogCaptureAppender appender = LogCaptureAppender.createAndRegister();
        internalTopologyBuilder.addSource(null, "source1", null, null, null, topic1);
        final Properties config = configProps(false);
        config.setProperty(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class.getName());
        config.setProperty(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        final StreamThread thread = createStreamThread(clientId, new StreamsConfig(config), false);
        thread.setState(STARTING);
        thread.setState(PARTITIONS_REVOKED);
        final Set<TopicPartition> assignedPartitions = Collections.singleton(t1p1);
        thread.taskManager().setAssignmentMetadata(Collections.singletonMap(new TaskId(0, t1p1.partition()), assignedPartitions), Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(Collections.singleton(t1p1));
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        final MetricName skippedTotalMetric = metrics.metricName("skipped-records-total", "stream-metrics", Collections.singletonMap("client-id", thread.getName()));
        final MetricName skippedRateMetric = metrics.metricName("skipped-records-rate", "stream-metrics", Collections.singletonMap("client-id", thread.getName()));
        Assert.assertEquals(0.0, metrics.metric(skippedTotalMetric).metricValue());
        Assert.assertEquals(0.0, metrics.metric(skippedRateMetric).metricValue());
        long offset = -1;
        mockConsumer.addRecord(new ConsumerRecord(t1p1.topic(), t1p1.partition(), (++offset), (-1), TimestampType.CREATE_TIME, ConsumerRecord.NULL_CHECKSUM, (-1), (-1), new byte[0], "I am not an integer.".getBytes()));
        mockConsumer.addRecord(new ConsumerRecord(t1p1.topic(), t1p1.partition(), (++offset), (-1), TimestampType.CREATE_TIME, ConsumerRecord.NULL_CHECKSUM, (-1), (-1), new byte[0], "I am not an integer.".getBytes()));
        thread.runOnce();
        Assert.assertEquals(2.0, metrics.metric(skippedTotalMetric).metricValue());
        Assert.assertNotEquals(0.0, metrics.metric(skippedRateMetric).metricValue());
        LogCaptureAppender.unregister(appender);
        final List<String> strings = appender.getMessages();
        Assert.assertTrue(strings.contains("task [0_1] Skipping record due to deserialization error. topic=[topic1] partition=[1] offset=[0]"));
        Assert.assertTrue(strings.contains("task [0_1] Skipping record due to deserialization error. topic=[topic1] partition=[1] offset=[1]"));
    }

    @Test
    public void shouldReportSkippedRecordsForInvalidTimestamps() {
        final LogCaptureAppender appender = LogCaptureAppender.createAndRegister();
        internalTopologyBuilder.addSource(null, "source1", null, null, null, topic1);
        final Properties config = configProps(false);
        config.setProperty(DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, LogAndSkipOnInvalidTimestamp.class.getName());
        final StreamThread thread = createStreamThread(clientId, new StreamsConfig(config), false);
        thread.setState(STARTING);
        thread.setState(PARTITIONS_REVOKED);
        final Set<TopicPartition> assignedPartitions = Collections.singleton(t1p1);
        thread.taskManager().setAssignmentMetadata(Collections.singletonMap(new TaskId(0, t1p1.partition()), assignedPartitions), Collections.emptyMap());
        final MockConsumer<byte[], byte[]> mockConsumer = ((MockConsumer<byte[], byte[]>) (thread.consumer));
        mockConsumer.assign(Collections.singleton(t1p1));
        mockConsumer.updateBeginningOffsets(Collections.singletonMap(t1p1, 0L));
        thread.rebalanceListener.onPartitionsAssigned(assignedPartitions);
        thread.runOnce();
        final MetricName skippedTotalMetric = metrics.metricName("skipped-records-total", "stream-metrics", Collections.singletonMap("client-id", thread.getName()));
        final MetricName skippedRateMetric = metrics.metricName("skipped-records-rate", "stream-metrics", Collections.singletonMap("client-id", thread.getName()));
        Assert.assertEquals(0.0, metrics.metric(skippedTotalMetric).metricValue());
        Assert.assertEquals(0.0, metrics.metric(skippedRateMetric).metricValue());
        long offset = -1;
        addRecord(mockConsumer, (++offset));
        addRecord(mockConsumer, (++offset));
        thread.runOnce();
        Assert.assertEquals(2.0, metrics.metric(skippedTotalMetric).metricValue());
        Assert.assertNotEquals(0.0, metrics.metric(skippedRateMetric).metricValue());
        addRecord(mockConsumer, (++offset));
        addRecord(mockConsumer, (++offset));
        addRecord(mockConsumer, (++offset));
        addRecord(mockConsumer, (++offset));
        thread.runOnce();
        Assert.assertEquals(6.0, metrics.metric(skippedTotalMetric).metricValue());
        Assert.assertNotEquals(0.0, metrics.metric(skippedRateMetric).metricValue());
        addRecord(mockConsumer, (++offset), 1L);
        addRecord(mockConsumer, (++offset), 1L);
        thread.runOnce();
        Assert.assertEquals(6.0, metrics.metric(skippedTotalMetric).metricValue());
        Assert.assertNotEquals(0.0, metrics.metric(skippedRateMetric).metricValue());
        LogCaptureAppender.unregister(appender);
        final List<String> strings = appender.getMessages();
        Assert.assertTrue(strings.contains(("task [0_1] Skipping record due to negative extracted timestamp. " + ("topic=[topic1] partition=[1] offset=[0] extractedTimestamp=[-1] " + "extractor=[org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp]"))));
        Assert.assertTrue(strings.contains(("task [0_1] Skipping record due to negative extracted timestamp. " + ("topic=[topic1] partition=[1] offset=[1] extractedTimestamp=[-1] " + "extractor=[org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp]"))));
        Assert.assertTrue(strings.contains(("task [0_1] Skipping record due to negative extracted timestamp. " + ("topic=[topic1] partition=[1] offset=[2] extractedTimestamp=[-1] " + "extractor=[org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp]"))));
        Assert.assertTrue(strings.contains(("task [0_1] Skipping record due to negative extracted timestamp. " + ("topic=[topic1] partition=[1] offset=[3] extractedTimestamp=[-1] " + "extractor=[org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp]"))));
        Assert.assertTrue(strings.contains(("task [0_1] Skipping record due to negative extracted timestamp. " + ("topic=[topic1] partition=[1] offset=[4] extractedTimestamp=[-1] " + "extractor=[org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp]"))));
        Assert.assertTrue(strings.contains(("task [0_1] Skipping record due to negative extracted timestamp. " + ("topic=[topic1] partition=[1] offset=[5] extractedTimestamp=[-1] " + "extractor=[org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp]"))));
    }

    // TODO: Need to add a test case covering EOS when we create a mock taskManager class
    @Test
    public void producerMetricsVerificationWithoutEOS() {
        final MockProducer<byte[], byte[]> producer = new MockProducer();
        final Consumer<byte[], byte[]> consumer = EasyMock.createNiceMock(Consumer.class);
        final TaskManager taskManager = mockTaskManagerCommit(consumer, 1, 0);
        final StreamThread.StreamsMetricsThreadImpl streamsMetrics = new StreamThread.StreamsMetricsThreadImpl(metrics, "");
        final StreamThread thread = new StreamThread(mockTime, config, producer, consumer, consumer, null, taskManager, streamsMetrics, internalTopologyBuilder, clientId, new LogContext(""), new AtomicInteger());
        final MetricName testMetricName = new MetricName("test_metric", "", "", new HashMap());
        final Metric testMetric = new org.apache.kafka.common.metrics.KafkaMetric(new Object(), testMetricName, ((Measurable) (( config, now) -> 0)), null, new MockTime());
        producer.setMockMetrics(testMetricName, testMetric);
        final Map<MetricName, Metric> producerMetrics = thread.producerMetrics();
        Assert.assertEquals(testMetricName, producerMetrics.get(testMetricName).metricName());
    }

    @Test
    public void adminClientMetricsVerification() {
        final Node broker1 = new Node(0, "dummyHost-1", 1234);
        final Node broker2 = new Node(1, "dummyHost-2", 1234);
        final List<Node> cluster = Arrays.asList(broker1, broker2);
        final MockAdminClient adminClient = new MockAdminClient(cluster, broker1, null);
        final MockProducer<byte[], byte[]> producer = new MockProducer();
        final Consumer<byte[], byte[]> consumer = EasyMock.createNiceMock(Consumer.class);
        final TaskManager taskManager = EasyMock.createNiceMock(TaskManager.class);
        final StreamThread.StreamsMetricsThreadImpl streamsMetrics = new StreamThread.StreamsMetricsThreadImpl(metrics, "");
        final StreamThread thread = new StreamThread(mockTime, config, producer, consumer, consumer, null, taskManager, streamsMetrics, internalTopologyBuilder, clientId, new LogContext(""), new AtomicInteger());
        final MetricName testMetricName = new MetricName("test_metric", "", "", new HashMap());
        final Metric testMetric = new org.apache.kafka.common.metrics.KafkaMetric(new Object(), testMetricName, ((Measurable) (( config, now) -> 0)), null, new MockTime());
        EasyMock.expect(taskManager.getAdminClient()).andReturn(adminClient);
        EasyMock.expectLastCall();
        EasyMock.replay(taskManager, consumer);
        adminClient.setMockMetrics(testMetricName, testMetric);
        final Map<MetricName, Metric> adminClientMetrics = thread.adminClientMetrics();
        Assert.assertEquals(testMetricName, adminClientMetrics.get(testMetricName).metricName());
    }
}
