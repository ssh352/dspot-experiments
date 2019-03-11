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
package io.confluent.ksql.rest.client.properties;


import ConsumerConfig.FETCH_MIN_BYTES_CONFIG;
import KsqlConfig.KSQL_FUNCTIONS_SUBSTRING_LEGACY_ARGS_CONFIG;
import ProducerConfig.BUFFER_MEMORY_CONFIG;
import StreamsConfig.NUM_STREAM_THREADS_CONFIG;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.config.PropertyParser;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LocalPropertiesTest {
    private static final Map<String, Object> INITIAL = ImmutableMap.of("prop-1", "initial-val-1", "prop-2", "initial-val-2");

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private PropertyParser parser;

    private LocalProperties propsWithMockParser;

    private LocalProperties realProps;

    @Test
    public void shouldValidateInitialPropsByParsing() {
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-1"), Matchers.is("parsed-initial-val-1"));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-2"), Matchers.is("parsed-initial-val-2"));
    }

    @Test
    public void shouldThrowInInitialPropsInvalid() {
        // Given:
        final Map<String, Object> invalid = ImmutableMap.of("this.is.not.valid", "value");
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("invalid property found");
        expectedException.expectMessage("'this.is.not.valid'");
        // When:
        new LocalProperties(invalid);
    }

    @Test
    public void shouldUnsetInitialValue() {
        // When:
        final Object oldValue = propsWithMockParser.unset("prop-1");
        // Then:
        MatcherAssert.assertThat(oldValue, Matchers.is("parsed-initial-val-1"));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-1"), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-2"), Matchers.is("parsed-initial-val-2"));
    }

    @Test
    public void shouldOverrideInitialValue() {
        // When:
        final Object oldValue = propsWithMockParser.set("prop-1", "new-val");
        // Then:
        MatcherAssert.assertThat(oldValue, Matchers.is("parsed-initial-val-1"));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-1"), Matchers.is("parsed-new-val"));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-2"), Matchers.is("parsed-initial-val-2"));
    }

    @Test
    public void shouldUnsetOverriddenValue() {
        // Given:
        propsWithMockParser.set("prop-1", "new-val");
        // When:
        final Object oldValue = propsWithMockParser.unset("prop-1");
        // Then:
        MatcherAssert.assertThat(oldValue, Matchers.is("parsed-new-val"));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-1"), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-2"), Matchers.is("parsed-initial-val-2"));
    }

    @Test
    public void shouldReturnOnlyKnownKeys() {
        MatcherAssert.assertThat(propsWithMockParser.toMap().keySet(), Matchers.containsInAnyOrder("prop-1", "prop-2"));
    }

    @Test
    public void shouldSetNewValue() {
        // When:
        final Object oldValue = propsWithMockParser.set("new-prop", "new-val");
        // Then:
        MatcherAssert.assertThat(oldValue, Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("new-prop"), Matchers.is("parsed-new-val"));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-2"), Matchers.is("parsed-initial-val-2"));
    }

    @Test
    public void shouldUnsetNewValue() {
        // Given:
        propsWithMockParser.set("new-prop", "new-val");
        // When:
        final Object oldValue = propsWithMockParser.unset("new-prop");
        // Then:
        MatcherAssert.assertThat(oldValue, Matchers.is("parsed-new-val"));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("new-prop"), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-2"), Matchers.is("parsed-initial-val-2"));
    }

    @Test
    public void shouldInvokeParserCorrectly() {
        // Given:
        Mockito.when(parser.parse("prop-1", "new-val")).thenReturn("parsed-new-val");
        // When:
        propsWithMockParser.set("prop-1", "new-val");
        // Then:
        MatcherAssert.assertThat(propsWithMockParser.toMap().get("prop-1"), Matchers.is("parsed-new-val"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfParserThrows() {
        // Given:
        Mockito.when(parser.parse("prop-1", "new-val")).thenThrow(new IllegalArgumentException("Boom"));
        // When:
        propsWithMockParser.set("prop-1", "new-val");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowUnknownPropertyToBeSet() {
        realProps.set("some.unknown.prop", "some.value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowUnknownConsumerPropertyToBeSet() {
        realProps.set(((StreamsConfig.CONSUMER_PREFIX) + "some.unknown.prop"), "some.value");
    }

    @Test
    public void shouldAllowKnownConsumerPropertyToBeSet() {
        realProps.set(FETCH_MIN_BYTES_CONFIG, "100");
    }

    @Test
    public void shouldAllowKnownPrefixedConsumerPropertyToBeSet() {
        realProps.set(((StreamsConfig.CONSUMER_PREFIX) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), "100");
    }

    @Test
    public void shouldAllowKnownKsqlPrefixedConsumerPropertyToBeSet() {
        realProps.set((((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), "100");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowUnknownProducerPropertyToBeSet() {
        realProps.set(((StreamsConfig.PRODUCER_PREFIX) + "some.unknown.prop"), "some.value");
    }

    @Test
    public void shouldAllowKnownProducerPropertyToBeSet() {
        realProps.set(BUFFER_MEMORY_CONFIG, "100");
    }

    @Test
    public void shouldAllowKnownKsqlPrefixedProducerPropertyToBeSet() {
        realProps.set((((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.PRODUCER_PREFIX)) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), "100");
    }

    @Test
    public void shouldAllowKnownPrefixedProducerPropertyToBeSet() {
        realProps.set(((StreamsConfig.PRODUCER_PREFIX) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), "100");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowUnknownStreamsConfigToBeSet() {
        realProps.set(((KsqlConfig.KSQL_STREAMS_PREFIX) + "some.unknown.prop"), "some.value");
    }

    @Test
    public void shouldAllowKnownStreamsConfigToBeSet() {
        realProps.set(NUM_STREAM_THREADS_CONFIG, "2");
    }

    @Test
    public void shouldAllowKnownPrefixedStreamsConfigToBeSet() {
        realProps.set(((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.NUM_STREAM_THREADS_CONFIG)), "2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowUnknownKsqlConfigToBeSet() {
        realProps.set(((KsqlConfig.KSQL_CONFIG_PROPERTY_PREFIX) + "some.unknown.prop"), "some.value");
    }

    @Test
    public void shouldAllowKnownUdfConfigToBeSet() {
        realProps.set(KSQL_FUNCTIONS_SUBSTRING_LEGACY_ARGS_CONFIG, "true");
    }

    @Test
    public void shouldAllowUnknownUdfConfigToBeSet() {
        realProps.set(((KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX) + "some_udf.some.prop"), "some thing");
    }
}

