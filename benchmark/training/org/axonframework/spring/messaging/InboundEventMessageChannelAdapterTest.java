/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.spring.messaging;


import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.spring.utils.StubDomainEvent;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class InboundEventMessageChannelAdapterTest {
    private EventBus mockEventBus;

    private InboundEventMessageChannelAdapter testSubject;

    @Test
    public void testMessagePayloadIsPublished() {
        testSubject = new InboundEventMessageChannelAdapter();
        StubDomainEvent event = new StubDomainEvent();
        testSubject.handleMessage(new org.springframework.messaging.support.GenericMessage<Object>(event));
        Mockito.verify(mockEventBus, Mockito.never()).publish(ArgumentMatchers.isA(EventMessage.class));
        testSubject.subscribe(mockEventBus::publish);
        testSubject.handleMessage(new org.springframework.messaging.support.GenericMessage<Object>(event));
        Mockito.verify(mockEventBus).publish(ArgumentMatchers.anyList());
    }
}

