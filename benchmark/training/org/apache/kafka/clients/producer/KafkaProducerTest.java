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
package org.apache.kafka.clients.producer;


import CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL;
import CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import Errors.UNKNOWN_TOPIC_OR_PARTITION;
import MetadataResponse.TopicMetadata;
import ProducerConfig.BATCH_SIZE_CONFIG;
import ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import ProducerConfig.CLIENT_ID_CONFIG;
import ProducerConfig.INTERCEPTOR_CLASSES_CONFIG;
import ProducerConfig.MAX_BLOCK_MS_CONFIG;
import ProducerConfig.MAX_REQUEST_SIZE_CONFIG;
import ProducerConfig.METRICS_RECORDING_LEVEL_CONFIG;
import ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG;
import ProducerConfig.PARTITIONER_CLASS_CONFIG;
import ProducerConfig.RECEIVE_BUFFER_CONFIG;
import ProducerConfig.SEND_BUFFER_CONFIG;
import ProducerConfig.TRANSACTIONAL_ID_CONFIG;
import Selectable.USE_DEFAULT_BUFFER_SIZE;
import Sensor.RecordingLevel.DEBUG;
import Sensor.RecordingLevel.INFO;
import Time.SYSTEM;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.MockClient;
import org.apache.kafka.clients.producer.internals.ProducerInterceptors;
import org.apache.kafka.clients.producer.internals.ProducerMetadata;
import org.apache.kafka.clients.producer.internals.Sender;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.internals.ClusterResourceListeners;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.MetadataResponse;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.ExtendedSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.test.MockMetricsReporter;
import org.apache.kafka.test.MockPartitioner;
import org.apache.kafka.test.MockProducerInterceptor;
import org.apache.kafka.test.MockSerializer;
import org.apache.kafka.test.TestCondition;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class KafkaProducerTest {
    private String topic = "topic";

    private Collection<Node> nodes = Collections.singletonList(new Node(0, "host1", 1000));

    private final Cluster emptyCluster = new Cluster(null, nodes, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

    private final Cluster onePartitionCluster = new Cluster("dummy", Collections.singletonList(new Node(0, "host1", 1000)), Collections.singletonList(new PartitionInfo(topic, 0, null, null, null)), Collections.emptySet(), Collections.emptySet());

    private final Cluster threePartitionCluster = new Cluster("dummy", Collections.singletonList(new Node(0, "host1", 1000)), Arrays.asList(new PartitionInfo(topic, 0, null, null, null), new PartitionInfo(topic, 1, null, null, null), new PartitionInfo(topic, 2, null, null, null)), Collections.emptySet(), Collections.emptySet());

    @Test
    public void testMetricsReporterAutoGeneratedClientId() {
        Properties props = new Properties();
        props.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        props.setProperty(METRIC_REPORTER_CLASSES_CONFIG, MockMetricsReporter.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer(props, new StringSerializer(), new StringSerializer());
        MockMetricsReporter mockMetricsReporter = ((MockMetricsReporter) (producer.metrics.reporters().get(0)));
        Assert.assertEquals(producer.getClientId(), mockMetricsReporter.clientId);
        producer.close();
    }

    @Test
    public void testConstructorWithSerializers() {
        Properties producerProps = new Properties();
        producerProps.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        new KafkaProducer(producerProps, new ByteArraySerializer(), new ByteArraySerializer()).close();
    }

    @Test(expected = ConfigException.class)
    public void testNoSerializerProvided() {
        Properties producerProps = new Properties();
        producerProps.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        new KafkaProducer(producerProps);
    }

    @Test
    public void testConstructorFailureCloseResource() {
        Properties props = new Properties();
        props.setProperty(CLIENT_ID_CONFIG, "testConstructorClose");
        props.setProperty(BOOTSTRAP_SERVERS_CONFIG, "some.invalid.hostname.foo.bar.local:9999");
        props.setProperty(METRIC_REPORTER_CLASSES_CONFIG, MockMetricsReporter.class.getName());
        final int oldInitCount = MockMetricsReporter.INIT_COUNT.get();
        final int oldCloseCount = MockMetricsReporter.CLOSE_COUNT.get();
        try (KafkaProducer<byte[], byte[]> ignored = new KafkaProducer(props, new ByteArraySerializer(), new ByteArraySerializer())) {
            Assert.fail("should have caught an exception and returned");
        } catch (KafkaException e) {
            Assert.assertEquals((oldInitCount + 1), MockMetricsReporter.INIT_COUNT.get());
            Assert.assertEquals((oldCloseCount + 1), MockMetricsReporter.CLOSE_COUNT.get());
            Assert.assertEquals("Failed to construct kafka producer", e.getMessage());
        }
    }

    @Test
    public void testConstructorWithNotStringKey() {
        Properties props = new Properties();
        props.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        props.put(1, "not string key");
        try (KafkaProducer<?, ?> ff = new KafkaProducer(props, new StringSerializer(), new StringSerializer())) {
            Assert.fail("Constructor should throw exception");
        } catch (ConfigException e) {
            Assert.assertTrue(("Unexpected exception message: " + (e.getMessage())), e.getMessage().contains("not string key"));
        }
    }

    @Test
    public void testSerializerClose() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(CLIENT_ID_CONFIG, "testConstructorClose");
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        configs.put(METRIC_REPORTER_CLASSES_CONFIG, MockMetricsReporter.class.getName());
        configs.put(SECURITY_PROTOCOL_CONFIG, DEFAULT_SECURITY_PROTOCOL);
        final int oldInitCount = MockSerializer.INIT_COUNT.get();
        final int oldCloseCount = MockSerializer.CLOSE_COUNT.get();
        KafkaProducer<byte[], byte[]> producer = new KafkaProducer(configs, new MockSerializer(), new MockSerializer());
        Assert.assertEquals((oldInitCount + 2), MockSerializer.INIT_COUNT.get());
        Assert.assertEquals(oldCloseCount, MockSerializer.CLOSE_COUNT.get());
        producer.close();
        Assert.assertEquals((oldInitCount + 2), MockSerializer.INIT_COUNT.get());
        Assert.assertEquals((oldCloseCount + 2), MockSerializer.CLOSE_COUNT.get());
    }

    @Test
    public void testInterceptorConstructClose() {
        try {
            Properties props = new Properties();
            // test with client ID assigned by KafkaProducer
            props.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
            props.setProperty(INTERCEPTOR_CLASSES_CONFIG, MockProducerInterceptor.class.getName());
            props.setProperty(MockProducerInterceptor.APPEND_STRING_PROP, "something");
            KafkaProducer<String, String> producer = new KafkaProducer(props, new StringSerializer(), new StringSerializer());
            Assert.assertEquals(1, MockProducerInterceptor.INIT_COUNT.get());
            Assert.assertEquals(0, MockProducerInterceptor.CLOSE_COUNT.get());
            // Cluster metadata will only be updated on calling onSend.
            Assert.assertNull(MockProducerInterceptor.CLUSTER_META.get());
            producer.close();
            Assert.assertEquals(1, MockProducerInterceptor.INIT_COUNT.get());
            Assert.assertEquals(1, MockProducerInterceptor.CLOSE_COUNT.get());
        } finally {
            // cleanup since we are using mutable static variables in MockProducerInterceptor
            MockProducerInterceptor.resetCounters();
        }
    }

    @Test
    public void testPartitionerClose() {
        try {
            Properties props = new Properties();
            props.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
            MockPartitioner.resetCounters();
            props.setProperty(PARTITIONER_CLASS_CONFIG, MockPartitioner.class.getName());
            KafkaProducer<String, String> producer = new KafkaProducer(props, new StringSerializer(), new StringSerializer());
            Assert.assertEquals(1, MockPartitioner.INIT_COUNT.get());
            Assert.assertEquals(0, MockPartitioner.CLOSE_COUNT.get());
            producer.close();
            Assert.assertEquals(1, MockPartitioner.INIT_COUNT.get());
            Assert.assertEquals(1, MockPartitioner.CLOSE_COUNT.get());
        } finally {
            // cleanup since we are using mutable static variables in MockPartitioner
            MockPartitioner.resetCounters();
        }
    }

    @Test
    public void shouldCloseProperlyAndThrowIfInterrupted() throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        configs.put(PARTITIONER_CLASS_CONFIG, MockPartitioner.class.getName());
        configs.put(BATCH_SIZE_CONFIG, "1");
        Time time = new MockTime();
        MetadataResponse initialUpdateResponse = TestUtils.metadataUpdateWith(1, Collections.singletonMap("topic", 1));
        ProducerMetadata metadata = newMetadata(0, Long.MAX_VALUE);
        MockClient client = new MockClient(time, metadata);
        client.updateMetadata(initialUpdateResponse);
        final Producer<String, String> producer = new KafkaProducer(configs, new StringSerializer(), new StringSerializer(), metadata, client, null, time);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final AtomicReference<Exception> closeException = new AtomicReference<>();
        try {
            Future<?> future = executor.submit(() -> {
                producer.send(new ProducerRecord("topic", "key", "value"));
                try {
                    producer.close();
                    Assert.fail("Close should block and throw.");
                } catch (Exception e) {
                    closeException.set(e);
                }
            });
            // Close producer should not complete until send succeeds
            try {
                future.get(100, TimeUnit.MILLISECONDS);
                Assert.fail("Close completed without waiting for send");
            } catch (TimeoutException expected) {
                /* ignore */
            }
            // Ensure send has started
            client.waitForRequests(1, 1000);
            Assert.assertTrue("Close terminated prematurely", future.cancel(true));
            TestUtils.waitForCondition(() -> (closeException.get()) != null, "InterruptException did not occur within timeout.");
            Assert.assertTrue(("Expected exception not thrown " + closeException), ((closeException.get()) instanceof InterruptException));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void testOsDefaultSocketBufferSizes() {
        Map<String, Object> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        config.put(SEND_BUFFER_CONFIG, USE_DEFAULT_BUFFER_SIZE);
        config.put(RECEIVE_BUFFER_CONFIG, USE_DEFAULT_BUFFER_SIZE);
        new KafkaProducer(config, new ByteArraySerializer(), new ByteArraySerializer()).close();
    }

    @Test(expected = KafkaException.class)
    public void testInvalidSocketSendBufferSize() {
        Map<String, Object> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        config.put(SEND_BUFFER_CONFIG, (-2));
        new KafkaProducer(config, new ByteArraySerializer(), new ByteArraySerializer());
    }

    @Test(expected = KafkaException.class)
    public void testInvalidSocketReceiveBufferSize() {
        Map<String, Object> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        config.put(RECEIVE_BUFFER_CONFIG, (-2));
        new KafkaProducer(config, new ByteArraySerializer(), new ByteArraySerializer());
    }

    @Test
    public void testMetadataFetch() throws InterruptedException {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        ProducerMetadata metadata = Mockito.mock(ProducerMetadata.class);
        // Return empty cluster 4 times and cluster from then on
        Mockito.when(metadata.fetch()).thenReturn(emptyCluster, emptyCluster, emptyCluster, emptyCluster, onePartitionCluster);
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs, new StringSerializer(), new StringSerializer(), metadata, new MockClient(Time.SYSTEM, metadata), null, Time.SYSTEM) {
            @Override
            Sender newSender(LogContext logContext, KafkaClient kafkaClient, ProducerMetadata metadata) {
                // give Sender its own Metadata instance so that we can isolate Metadata calls from KafkaProducer
                return super.newSender(logContext, kafkaClient, newMetadata(0, 100000));
            }
        };
        ProducerRecord<String, String> record = new ProducerRecord(topic, "value");
        producer.send(record);
        // One request update for each empty cluster returned
        Mockito.verify(metadata, Mockito.times(4)).requestUpdate();
        Mockito.verify(metadata, Mockito.times(4)).awaitUpdate(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(metadata, Mockito.times(5)).fetch();
        // Should not request update for subsequent `send`
        producer.send(record, null);
        Mockito.verify(metadata, Mockito.times(4)).requestUpdate();
        Mockito.verify(metadata, Mockito.times(4)).awaitUpdate(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(metadata, Mockito.times(6)).fetch();
        // Should not request update for subsequent `partitionsFor`
        producer.partitionsFor(topic);
        Mockito.verify(metadata, Mockito.times(4)).requestUpdate();
        Mockito.verify(metadata, Mockito.times(4)).awaitUpdate(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(metadata, Mockito.times(7)).fetch();
        producer.close(Duration.ofMillis(0));
    }

    @Test
    public void testMetadataTimeoutWithMissingTopic() throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        configs.put(MAX_BLOCK_MS_CONFIG, 60000);
        // Create a record with a partition higher than the initial (outdated) partition range
        ProducerRecord<String, String> record = new ProducerRecord(topic, 2, null, "value");
        ProducerMetadata metadata = Mockito.mock(ProducerMetadata.class);
        MockTime mockTime = new MockTime();
        AtomicInteger invocationCount = new AtomicInteger(0);
        Mockito.when(metadata.fetch()).then(( invocation) -> {
            invocationCount.incrementAndGet();
            if ((invocationCount.get()) == 5) {
                mockTime.setCurrentTimeMs(((mockTime.milliseconds()) + 70000));
            }
            return emptyCluster;
        });
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs, new StringSerializer(), new StringSerializer(), metadata, new MockClient(Time.SYSTEM, metadata), null, mockTime) {
            @Override
            Sender newSender(LogContext logContext, KafkaClient kafkaClient, ProducerMetadata metadata) {
                // give Sender its own Metadata instance so that we can isolate Metadata calls from KafkaProducer
                return super.newSender(logContext, kafkaClient, newMetadata(0, 100000));
            }
        };
        // Four request updates where the topic isn't present, at which point the timeout expires and a
        // TimeoutException is thrown
        Future future = producer.send(record);
        Mockito.verify(metadata, Mockito.times(4)).requestUpdate();
        Mockito.verify(metadata, Mockito.times(4)).awaitUpdate(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(metadata, Mockito.times(5)).fetch();
        try {
            future.get();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof org.apache.kafka.common.errors.TimeoutException));
        } finally {
            producer.close(Duration.ofMillis(0));
        }
    }

    @Test
    public void testMetadataWithPartitionOutOfRange() throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        configs.put(MAX_BLOCK_MS_CONFIG, 60000);
        // Create a record with a partition higher than the initial (outdated) partition range
        ProducerRecord<String, String> record = new ProducerRecord(topic, 2, null, "value");
        ProducerMetadata metadata = Mockito.mock(ProducerMetadata.class);
        MockTime mockTime = new MockTime();
        Mockito.when(metadata.fetch()).thenReturn(onePartitionCluster, onePartitionCluster, threePartitionCluster);
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs, new StringSerializer(), new StringSerializer(), metadata, new MockClient(Time.SYSTEM, metadata), null, mockTime) {
            @Override
            Sender newSender(LogContext logContext, KafkaClient kafkaClient, ProducerMetadata metadata) {
                // give Sender its own Metadata instance so that we can isolate Metadata calls from KafkaProducer
                return super.newSender(logContext, kafkaClient, newMetadata(0, 100000));
            }
        };
        // One request update if metadata is available but outdated for the given record
        producer.send(record);
        Mockito.verify(metadata, Mockito.times(2)).requestUpdate();
        Mockito.verify(metadata, Mockito.times(2)).awaitUpdate(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(metadata, Mockito.times(3)).fetch();
        producer.close(Duration.ofMillis(0));
    }

    @Test
    public void testMetadataTimeoutWithPartitionOutOfRange() throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        configs.put(MAX_BLOCK_MS_CONFIG, 60000);
        // Create a record with a partition higher than the initial (outdated) partition range
        ProducerRecord<String, String> record = new ProducerRecord(topic, 2, null, "value");
        ProducerMetadata metadata = Mockito.mock(ProducerMetadata.class);
        MockTime mockTime = new MockTime();
        AtomicInteger invocationCount = new AtomicInteger(0);
        Mockito.when(metadata.fetch()).then(( invocation) -> {
            invocationCount.incrementAndGet();
            if ((invocationCount.get()) == 5) {
                mockTime.setCurrentTimeMs(((mockTime.milliseconds()) + 70000));
            }
            return onePartitionCluster;
        });
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs, new StringSerializer(), new StringSerializer(), metadata, new MockClient(Time.SYSTEM, metadata), null, mockTime) {
            @Override
            Sender newSender(LogContext logContext, KafkaClient kafkaClient, ProducerMetadata metadata) {
                // give Sender its own Metadata instance so that we can isolate Metadata calls from KafkaProducer
                return super.newSender(logContext, kafkaClient, newMetadata(0, 100000));
            }
        };
        // Four request updates where the requested partition is out of range, at which point the timeout expires
        // and a TimeoutException is thrown
        Future future = producer.send(record);
        Mockito.verify(metadata, Mockito.times(4)).requestUpdate();
        Mockito.verify(metadata, Mockito.times(4)).awaitUpdate(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(metadata, Mockito.times(5)).fetch();
        try {
            future.get();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof org.apache.kafka.common.errors.TimeoutException));
        } finally {
            producer.close(Duration.ofMillis(0));
        }
    }

    @Test
    public void testTopicRefreshInMetadata() throws InterruptedException {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        configs.put(MAX_BLOCK_MS_CONFIG, "600000");
        long refreshBackoffMs = 500L;
        long metadataExpireMs = 60000L;
        final Time time = new MockTime();
        final ProducerMetadata metadata = new ProducerMetadata(refreshBackoffMs, metadataExpireMs, new LogContext(), new ClusterResourceListeners(), time);
        final String topic = "topic";
        try (KafkaProducer<String, String> producer = new KafkaProducer(configs, new StringSerializer(), new StringSerializer(), metadata, new MockClient(time, metadata), null, time)) {
            AtomicBoolean running = new AtomicBoolean(true);
            Thread t = new Thread(() -> {
                long startTimeMs = System.currentTimeMillis();
                while (running.get()) {
                    while ((!(metadata.updateRequested())) && (((System.currentTimeMillis()) - startTimeMs) < 100))
                        Thread.yield();

                    MetadataResponse updateResponse = TestUtils.metadataUpdateWith("kafka-cluster", 1, Collections.singletonMap(topic, UNKNOWN_TOPIC_OR_PARTITION), Collections.emptyMap());
                    metadata.update(updateResponse, time.milliseconds());
                    time.sleep((60 * 1000L));
                } 
            });
            t.start();
            try {
                producer.partitionsFor(topic);
                Assert.fail("Expect TimeoutException");
            } catch (org.apache.kafka.common.errors.TimeoutException e) {
                // skip
            }
            running.set(false);
            t.join();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @Deprecated
    public void testHeadersWithExtendedClasses() {
        doTestHeaders(ExtendedSerializer.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHeaders() {
        doTestHeaders(Serializer.class);
    }

    @Test
    public void closeShouldBeIdempotent() {
        Properties producerProps = new Properties();
        producerProps.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        Producer producer = new KafkaProducer(producerProps, new ByteArraySerializer(), new ByteArraySerializer());
        producer.close();
        producer.close();
    }

    @Test
    public void testMetricConfigRecordingLevel() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        try (KafkaProducer producer = new KafkaProducer(props, new ByteArraySerializer(), new ByteArraySerializer())) {
            Assert.assertEquals(INFO, producer.metrics.config().recordLevel());
        }
        props.put(METRICS_RECORDING_LEVEL_CONFIG, "DEBUG");
        try (KafkaProducer producer = new KafkaProducer(props, new ByteArraySerializer(), new ByteArraySerializer())) {
            Assert.assertEquals(DEBUG, producer.metrics.config().recordLevel());
        }
    }

    @Test
    public void testInterceptorPartitionSetOnTooLargeRecord() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999");
        configs.put(MAX_REQUEST_SIZE_CONFIG, "1");
        String topic = "topic";
        ProducerRecord<String, String> record = new ProducerRecord(topic, "value");
        ProducerMetadata metadata = newMetadata(0, 90000);
        metadata.add(topic);
        MetadataResponse initialUpdateResponse = TestUtils.metadataUpdateWith(1, Collections.singletonMap(topic, 1));
        metadata.update(initialUpdateResponse, SYSTEM.milliseconds());
        // it is safe to suppress, since this is a mock class
        @SuppressWarnings("unchecked")
        ProducerInterceptors<String, String> interceptors = Mockito.mock(ProducerInterceptors.class);
        KafkaProducer<String, String> producer = new KafkaProducer(configs, new StringSerializer(), new StringSerializer(), metadata, null, interceptors, Time.SYSTEM);
        Mockito.when(interceptors.onSend(ArgumentMatchers.any())).then(( invocation) -> invocation.getArgument(0));
        producer.send(record);
        Mockito.verify(interceptors).onSend(record);
        Mockito.verify(interceptors).onSendError(ArgumentMatchers.eq(record), ArgumentMatchers.notNull(), ArgumentMatchers.notNull());
        producer.close(Duration.ofMillis(0));
    }

    @Test
    public void testPartitionsForWithNullTopic() {
        Properties props = new Properties();
        props.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        try (KafkaProducer<byte[], byte[]> producer = new KafkaProducer(props, new ByteArraySerializer(), new ByteArraySerializer())) {
            producer.partitionsFor(null);
            Assert.fail("Expected NullPointerException to be raised");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test(expected = org.apache.kafka.common.errors.TimeoutException.class)
    public void testInitTransactionTimeout() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(TRANSACTIONAL_ID_CONFIG, "bad-transaction");
        configs.put(MAX_BLOCK_MS_CONFIG, 5);
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        Time time = new MockTime(1);
        MetadataResponse initialUpdateResponse = TestUtils.metadataUpdateWith(1, Collections.singletonMap("topic", 1));
        ProducerMetadata metadata = newMetadata(0, Long.MAX_VALUE);
        metadata.update(initialUpdateResponse, time.milliseconds());
        MockClient client = new MockClient(time, metadata);
        try (Producer<String, String> producer = new KafkaProducer(configs, new StringSerializer(), new StringSerializer(), metadata, client, null, time)) {
            producer.initTransactions();
            Assert.fail("initTransactions() should have raised TimeoutException");
        }
    }

    @Test(expected = KafkaException.class)
    public void testOnlyCanExecuteCloseAfterInitTransactionsTimeout() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(TRANSACTIONAL_ID_CONFIG, "bad-transaction");
        configs.put(MAX_BLOCK_MS_CONFIG, 5);
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        Time time = new MockTime();
        MetadataResponse initialUpdateResponse = TestUtils.metadataUpdateWith(1, Collections.singletonMap("topic", 1));
        ProducerMetadata metadata = newMetadata(0, Long.MAX_VALUE);
        metadata.update(initialUpdateResponse, time.milliseconds());
        MockClient client = new MockClient(time, metadata);
        Producer<String, String> producer = new KafkaProducer(configs, new StringSerializer(), new StringSerializer(), metadata, client, null, time);
        try {
            producer.initTransactions();
        } catch (org.apache.kafka.common.errors.TimeoutException e) {
            // expected
        }
        // other transactional operations should not be allowed if we catch the error after initTransactions failed
        try {
            producer.beginTransaction();
        } finally {
            producer.close(Duration.ofMillis(0));
        }
    }

    @Test
    public void testSendToInvalidTopic() throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        configs.put(MAX_BLOCK_MS_CONFIG, "15000");
        Time time = new MockTime();
        MetadataResponse initialUpdateResponse = TestUtils.metadataUpdateWith(1, Collections.emptyMap());
        ProducerMetadata metadata = newMetadata(0, Long.MAX_VALUE);
        metadata.update(initialUpdateResponse, time.milliseconds());
        MockClient client = new MockClient(time, metadata);
        Producer<String, String> producer = new KafkaProducer(configs, new StringSerializer(), new StringSerializer(), metadata, client, null, time);
        String invalidTopicName = "topic abc";// Invalid topic name due to space

        ProducerRecord<String, String> record = new ProducerRecord(invalidTopicName, "HelloKafka");
        List<MetadataResponse.TopicMetadata> topicMetadata = new ArrayList<>();
        topicMetadata.add(new MetadataResponse.TopicMetadata(Errors.INVALID_TOPIC_EXCEPTION, invalidTopicName, false, Collections.emptyList()));
        MetadataResponse updateResponse = MetadataResponse.prepareResponse(new ArrayList(initialUpdateResponse.brokers()), initialUpdateResponse.clusterId(), initialUpdateResponse.controller().id(), topicMetadata);
        client.prepareMetadataUpdate(updateResponse);
        Future<RecordMetadata> future = producer.send(record);
        Assert.assertEquals("Cluster has incorrect invalid topic list.", Collections.singleton(invalidTopicName), metadata.fetch().invalidTopics());
        TestUtils.assertFutureError(future, InvalidTopicException.class);
        producer.close(Duration.ofMillis(0));
    }

    @Test
    public void testCloseWhenWaitingForMetadataUpdate() throws InterruptedException {
        Map<String, Object> configs = new HashMap<>();
        configs.put(MAX_BLOCK_MS_CONFIG, Long.MAX_VALUE);
        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9000");
        // Simulate a case where metadata for a particular topic is not available. This will cause KafkaProducer#send to
        // block in Metadata#awaitUpdate for the configured max.block.ms. When close() is invoked, KafkaProducer#send should
        // return with a KafkaException.
        String topicName = "test";
        Time time = Time.SYSTEM;
        MetadataResponse initialUpdateResponse = TestUtils.metadataUpdateWith(1, Collections.emptyMap());
        ProducerMetadata metadata = new ProducerMetadata(0, Long.MAX_VALUE, new LogContext(), new ClusterResourceListeners(), time);
        metadata.update(initialUpdateResponse, time.milliseconds());
        MockClient client = new MockClient(time, metadata);
        Producer<String, String> producer = new KafkaProducer(configs, new StringSerializer(), new StringSerializer(), metadata, client, null, time);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final AtomicReference<Exception> sendException = new AtomicReference<>();
        try {
            executor.submit(() -> {
                try {
                    // Metadata for topic "test" will not be available which will cause us to block indefinitely until
                    // KafkaProducer#close is invoked.
                    producer.send(new ProducerRecord(topicName, "key", "value"));
                    Assert.fail();
                } catch (Exception e) {
                    sendException.set(e);
                }
            });
            // Wait until metadata update for the topic has been requested
            TestUtils.waitForCondition(() -> metadata.containsTopic(topicName), "Timeout when waiting for topic to be added to metadata");
            producer.close(Duration.ofMillis(0));
            TestUtils.waitForCondition(() -> (sendException.get()) != null, "No producer exception within timeout");
            Assert.assertEquals(KafkaException.class, sendException.get().getClass());
        } finally {
            executor.shutdownNow();
        }
    }
}
