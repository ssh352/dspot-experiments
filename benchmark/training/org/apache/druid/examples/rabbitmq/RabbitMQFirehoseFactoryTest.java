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
package org.apache.druid.examples.rabbitmq;


import ConnectionFactory.DEFAULT_AMQP_PORT;
import ConnectionFactory.DEFAULT_HOST;
import ConnectionFactory.DEFAULT_USER;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.druid.firehose.rabbitmq.JacksonifiedConnectionFactory;
import org.apache.druid.firehose.rabbitmq.RabbitMQFirehoseConfig;
import org.apache.druid.firehose.rabbitmq.RabbitMQFirehoseFactory;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class RabbitMQFirehoseFactoryTest {
    private static final ObjectMapper mapper = new DefaultObjectMapper();

    @Test
    public void testSerde() throws Exception {
        RabbitMQFirehoseConfig config = new RabbitMQFirehoseConfig("test", "test2", "test3", true, true, true, 5, 10, 20);
        JacksonifiedConnectionFactory connectionFactory = new JacksonifiedConnectionFactory("foo", 9978, "user", "pw", "host", null, 5, 10, 11, 12, ImmutableMap.of("hi", "bye"));
        RabbitMQFirehoseFactory factory = new RabbitMQFirehoseFactory(connectionFactory, config, null);
        byte[] bytes = RabbitMQFirehoseFactoryTest.mapper.writeValueAsBytes(factory);
        RabbitMQFirehoseFactory factory2 = RabbitMQFirehoseFactoryTest.mapper.readValue(bytes, RabbitMQFirehoseFactory.class);
        byte[] bytes2 = RabbitMQFirehoseFactoryTest.mapper.writeValueAsBytes(factory2);
        Assert.assertArrayEquals(bytes, bytes2);
        Assert.assertEquals(factory.getConfig(), factory2.getConfig());
        Assert.assertEquals(factory.getConnectionFactory(), factory2.getConnectionFactory());
    }

    @Test
    public void testDefaultSerde() throws Exception {
        RabbitMQFirehoseConfig config = RabbitMQFirehoseConfig.makeDefaultConfig();
        JacksonifiedConnectionFactory connectionFactory = JacksonifiedConnectionFactory.makeDefaultConnectionFactory();
        RabbitMQFirehoseFactory factory = new RabbitMQFirehoseFactory(connectionFactory, config, null);
        byte[] bytes = RabbitMQFirehoseFactoryTest.mapper.writeValueAsBytes(factory);
        RabbitMQFirehoseFactory factory2 = RabbitMQFirehoseFactoryTest.mapper.readValue(bytes, RabbitMQFirehoseFactory.class);
        byte[] bytes2 = RabbitMQFirehoseFactoryTest.mapper.writeValueAsBytes(factory2);
        Assert.assertArrayEquals(bytes, bytes2);
        Assert.assertEquals(factory.getConfig(), factory2.getConfig());
        Assert.assertEquals(factory.getConnectionFactory(), factory2.getConnectionFactory());
        Assert.assertEquals(300, factory2.getConfig().getMaxDurationSeconds());
        Assert.assertEquals(DEFAULT_HOST, factory2.getConnectionFactory().getHost());
        Assert.assertEquals(DEFAULT_USER, factory2.getConnectionFactory().getUsername());
        Assert.assertEquals(DEFAULT_AMQP_PORT, factory2.getConnectionFactory().getPort());
    }
}

