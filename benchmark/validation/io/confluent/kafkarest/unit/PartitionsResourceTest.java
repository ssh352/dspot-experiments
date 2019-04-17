/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.kafkarest.unit;


import Errors.PARTITION_NOT_FOUND_ERROR_CODE;
import Errors.PARTITION_NOT_FOUND_MESSAGE;
import Errors.TOPIC_NOT_FOUND_ERROR_CODE;
import Errors.TOPIC_NOT_FOUND_MESSAGE;
import Response.Status.NOT_FOUND;
import io.confluent.kafkarest.AdminClientWrapper;
import io.confluent.kafkarest.DefaultKafkaRestContext;
import io.confluent.kafkarest.KafkaRestApplication;
import io.confluent.kafkarest.KafkaRestConfig;
import io.confluent.kafkarest.ProducerPool;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.Partition;
import io.confluent.kafkarest.entities.PartitionReplica;
import io.confluent.rest.EmbeddedServerTestHarness;
import io.confluent.rest.RestConfigException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class PartitionsResourceTest extends EmbeddedServerTestHarness<KafkaRestConfig, KafkaRestApplication> {
    private AdminClientWrapper adminClientWrapper;

    private ProducerPool producerPool;

    private DefaultKafkaRestContext ctx;

    private final String topicName = "topic1";

    private final List<Partition> partitions = Arrays.asList(new Partition(0, 0, Arrays.asList(new PartitionReplica(0, true, true), new PartitionReplica(1, false, false))), new Partition(1, 1, Arrays.asList(new PartitionReplica(0, false, true), new PartitionReplica(1, true, true))));

    public PartitionsResourceTest() throws RestConfigException {
        adminClientWrapper = EasyMock.createMock(AdminClientWrapper.class);
        producerPool = EasyMock.createMock(ProducerPool.class);
        ctx = new DefaultKafkaRestContext(config, null, producerPool, null, null, null, adminClientWrapper);
        addResource(new io.confluent.kafkarest.resources.TopicsResource(ctx));
        addResource(new io.confluent.kafkarest.resources.PartitionsResource(ctx));
    }

    @Test
    public void testGetPartitions() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            EasyMock.expect(adminClientWrapper.topicExists(topicName)).andReturn(true);
            EasyMock.expect(adminClientWrapper.getTopicPartitions(topicName)).andReturn(partitions);
            EasyMock.replay(adminClientWrapper);
            Response response = request("/topics/topic1/partitions", mediatype.header).get();
            TestUtils.assertOKResponse(response, mediatype.expected);
            List<Partition> partitionsResponse = TestUtils.tryReadEntityOrLog(response, new javax.ws.rs.core.GenericType<List<Partition>>() {});
            Assert.assertEquals(partitions, partitionsResponse);
            EasyMock.verify(adminClientWrapper);
            EasyMock.reset(adminClientWrapper);
        }
    }

    @Test
    public void testGetPartition() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            EasyMock.expect(adminClientWrapper.topicExists(topicName)).andReturn(true);
            EasyMock.expect(adminClientWrapper.getTopicPartition(topicName, 0)).andReturn(partitions.get(0));
            EasyMock.expect(adminClientWrapper.topicExists(topicName)).andReturn(true);
            EasyMock.expect(adminClientWrapper.getTopicPartition(topicName, 1)).andReturn(partitions.get(1));
            EasyMock.replay(adminClientWrapper);
            Response response = request("/topics/topic1/partitions/0", mediatype.header).get();
            TestUtils.assertOKResponse(response, mediatype.expected);
            Partition partition = TestUtils.tryReadEntityOrLog(response, new javax.ws.rs.core.GenericType<Partition>() {});
            Assert.assertEquals(partitions.get(0), partition);
            response = request("/topics/topic1/partitions/1", mediatype.header).get();
            TestUtils.assertOKResponse(response, mediatype.expected);
            partition = TestUtils.tryReadEntityOrLog(response, new javax.ws.rs.core.GenericType<Partition>() {});
            Assert.assertEquals(partitions.get(1), partition);
            EasyMock.verify(adminClientWrapper);
            EasyMock.reset(adminClientWrapper);
        }
    }

    @Test
    public void testListPartitionsInvalidTopic() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            EasyMock.expect(adminClientWrapper.topicExists("nonexistanttopic")).andReturn(false);
            EasyMock.replay(adminClientWrapper);
            Response response = request("/topics/nonexistanttopic/partitions", mediatype.header).get();
            TestUtils.assertErrorResponse(NOT_FOUND, response, TOPIC_NOT_FOUND_ERROR_CODE, TOPIC_NOT_FOUND_MESSAGE, mediatype.expected);
            EasyMock.verify(adminClientWrapper);
            EasyMock.reset(adminClientWrapper);
        }
    }

    @Test
    public void testGetInvalidPartition() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            EasyMock.expect(adminClientWrapper.topicExists(topicName)).andReturn(true);
            EasyMock.expect(adminClientWrapper.getTopicPartition(topicName, 1000)).andReturn(null);
            EasyMock.replay(adminClientWrapper);
            Response response = request("/topics/topic1/partitions/1000", mediatype.header).get();
            TestUtils.assertErrorResponse(NOT_FOUND, response, PARTITION_NOT_FOUND_ERROR_CODE, PARTITION_NOT_FOUND_MESSAGE, mediatype.expected);
            EasyMock.verify(adminClientWrapper);
            EasyMock.reset(adminClientWrapper);
        }
    }
}
