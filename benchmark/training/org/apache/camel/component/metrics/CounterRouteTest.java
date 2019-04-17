/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.metrics;


import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { CounterRouteTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
public class CounterRouteTest {
    @EndpointInject(uri = "mock:out")
    private MockEndpoint endpoint;

    @Produce(uri = "direct:in-1")
    private ProducerTemplate producer1;

    @Produce(uri = "direct:in-2")
    private ProducerTemplate producer2;

    @Produce(uri = "direct:in-3")
    private ProducerTemplate producer3;

    @Produce(uri = "direct:in-4")
    private ProducerTemplate producer4;

    private MetricRegistry mockRegistry;

    private Counter mockCounter;

    private InOrder inOrder;

    @Configuration
    public static class TestConfig extends SingleRouteCamelConfiguration {
        @Bean
        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:in-1").to("metrics:counter:A?increment=5").to("mock:out");
                    from("direct:in-2").to("metrics:counter:A?decrement=9").to("mock:out");
                    from("direct:in-3").setHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, constant(417L)).to("metrics:counter:A").to("mock:out");
                    from("direct:in-4").setHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, simple("${body.length}")).to("metrics:counter:A").to("mock:out");
                }
            };
        }

        @Bean(name = MetricsComponent.METRIC_REGISTRY_NAME)
        public MetricRegistry getMetricRegistry() {
            return Mockito.mock(MetricRegistry.class);
        }
    }

    @Test
    public void testOverrideMetricsName() throws Exception {
        Mockito.when(mockRegistry.counter("B")).thenReturn(mockCounter);
        endpoint.expectedMessageCount(1);
        producer1.sendBodyAndHeader(new Object(), MetricsConstants.HEADER_METRIC_NAME, "B");
        endpoint.assertIsSatisfied();
        inOrder.verify(mockRegistry, Mockito.times(1)).counter("B");
        inOrder.verify(mockCounter, Mockito.times(1)).inc(5L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOverrideIncrement() throws Exception {
        Mockito.when(mockRegistry.counter("A")).thenReturn(mockCounter);
        endpoint.expectedMessageCount(1);
        producer1.sendBodyAndHeader(new Object(), MetricsConstants.HEADER_COUNTER_INCREMENT, 14L);
        endpoint.assertIsSatisfied();
        inOrder.verify(mockRegistry, Mockito.times(1)).counter("A");
        inOrder.verify(mockCounter, Mockito.times(1)).inc(14L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOverrideIncrementAndDecrement() throws Exception {
        Mockito.when(mockRegistry.counter("A")).thenReturn(mockCounter);
        endpoint.expectedMessageCount(1);
        Map<String, Object> headers = new HashMap<>();
        headers.put(MetricsConstants.HEADER_COUNTER_INCREMENT, 912L);
        headers.put(MetricsConstants.HEADER_COUNTER_DECREMENT, 43219L);
        producer1.sendBodyAndHeaders(new Object(), headers);
        endpoint.assertIsSatisfied();
        inOrder.verify(mockRegistry, Mockito.times(1)).counter("A");
        inOrder.verify(mockCounter, Mockito.times(1)).inc(912L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOverrideDecrement() throws Exception {
        Mockito.when(mockRegistry.counter("A")).thenReturn(mockCounter);
        endpoint.expectedMessageCount(1);
        producer2.sendBodyAndHeader(new Object(), MetricsConstants.HEADER_COUNTER_DECREMENT, 7L);
        endpoint.assertIsSatisfied();
        inOrder.verify(mockRegistry, Mockito.times(1)).counter("A");
        inOrder.verify(mockCounter, Mockito.times(1)).dec(7L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOverrideUsingConstantValue() throws Exception {
        Mockito.when(mockRegistry.counter("A")).thenReturn(mockCounter);
        endpoint.expectedMessageCount(1);
        producer3.sendBody(new Object());
        endpoint.assertIsSatisfied();
        inOrder.verify(mockRegistry, Mockito.times(1)).counter("A");
        inOrder.verify(mockCounter, Mockito.times(1)).inc(417L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOverrideUsingScriptEvaluation() throws Exception {
        Mockito.when(mockRegistry.counter("A")).thenReturn(mockCounter);
        endpoint.expectedMessageCount(1);
        String message = "Hello from Camel Metrics!";
        producer4.sendBody(message);
        endpoint.assertIsSatisfied();
        inOrder.verify(mockRegistry, Mockito.times(1)).counter("A");
        inOrder.verify(mockCounter, Mockito.times(1)).inc(message.length());
        inOrder.verifyNoMoreInteractions();
    }
}
