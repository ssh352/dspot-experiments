/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.spring.messaging;


import java.util.Collections;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.spring.utils.StubDomainEvent;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.messaging.MessageChannel;


/**
 *
 *
 * @author Allard Buijze
 * @author Nakul Mishra
 */
public class OutboundEventMessageChannelAdapterTest {
    private OutboundEventMessageChannelAdapter testSubject;

    private EventBus mockEventBus;

    private MessageChannel mockChannel;

    @Test
    public void testMessageForwardedToChannel() {
        StubDomainEvent event = new StubDomainEvent();
        testSubject.handle(Collections.singletonList(new org.axonframework.eventhandling.GenericEventMessage(event)));
        Mockito.verify(mockChannel).send(messageWithPayload(event));
    }

    @Test
    public void testEventListenerRegisteredOnInit() {
        Mockito.verify(mockEventBus, Mockito.never()).subscribe(ArgumentMatchers.any());
        testSubject.afterPropertiesSet();
        Mockito.verify(mockEventBus).subscribe(ArgumentMatchers.any());
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testFilterBlocksEvents() {
        testSubject = new OutboundEventMessageChannelAdapter(mockEventBus, mockChannel, ( m) -> !(m.getPayloadType().isAssignableFrom(.class)));
        testSubject.handle(Collections.singletonList(newDomainEvent()));
        Mockito.verify(mockEventBus, Mockito.never()).publish(ArgumentMatchers.isA(EventMessage.class));
    }
}

