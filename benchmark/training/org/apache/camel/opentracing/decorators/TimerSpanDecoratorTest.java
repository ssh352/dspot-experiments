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
package org.apache.camel.opentracing.decorators;


import Exchange.TIMER_NAME;
import org.apache.camel.Exchange;
import org.apache.camel.opentracing.SpanDecorator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TimerSpanDecoratorTest {
    private static final String TEST_NAME = "TestName";

    @Test
    public void testGetOperationName() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getProperty(TIMER_NAME)).thenReturn(TimerSpanDecoratorTest.TEST_NAME);
        SpanDecorator decorator = new TimerSpanDecorator() {
            @Override
            public String getComponent() {
                return null;
            }
        };
        Assert.assertEquals(TimerSpanDecoratorTest.TEST_NAME, decorator.getOperationName(exchange, null));
    }
}
