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
package io.confluent.ksql.metrics;


import TopicSensors.Stat;
import java.util.Collection;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.metrics.Metrics;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class ProducerCollectorTest {
    private static final String TEST_TOPIC = "test-topic".toLowerCase();

    @Test
    public void shouldDisplayRateThroughput() {
        final ProducerCollector collector = new ProducerCollector().configure(new Metrics(), "clientid", MetricCollectors.getTime());
        for (int i = 0; i < 1000; i++) {
            collector.onSend(new ProducerRecord(ProducerCollectorTest.TEST_TOPIC, 1, "key", "value"));
        }
        final Collection<TopicSensors.Stat> stats = collector.stats("test-topic", false);
        Assert.assertThat(stats.toString(), CoreMatchers.containsString("name=messages-per-sec,"));
    }
}

