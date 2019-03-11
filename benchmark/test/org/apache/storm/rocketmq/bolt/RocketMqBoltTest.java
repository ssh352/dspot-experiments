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
package org.apache.storm.rocketmq.bolt;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.message.Message;
import org.apache.storm.rocketmq.TestUtils;
import org.apache.storm.tuple.Tuple;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RocketMqBoltTest {
    private RocketMqBolt rocketMqBolt;

    private DefaultMQProducer producer;

    @Test
    public void execute() throws Exception {
        Tuple tuple = TestUtils.generateTestTuple("f1", "f2", "v1", "v2");
        rocketMqBolt.execute(tuple);
        Mockito.verify(producer).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.any(SendCallback.class));
    }

    @Test
    public void cleanup() throws Exception {
        rocketMqBolt.cleanup();
        Mockito.verify(producer).shutdown();
    }
}

