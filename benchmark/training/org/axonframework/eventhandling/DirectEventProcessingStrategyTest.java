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
package org.axonframework.eventhandling;


import DirectEventProcessingStrategy.INSTANCE;
import java.util.List;
import java.util.function.Consumer;
import org.axonframework.utils.EventTestUtils;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Rene de Waele
 */
public class DirectEventProcessingStrategyTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testEventsPassedToProcessor() {
        List<? extends EventMessage<?>> events = EventTestUtils.createEvents(10);
        Consumer<List<? extends EventMessage<?>>> mockProcessor = Mockito.mock(Consumer.class);
        INSTANCE.handle(events, mockProcessor);
        Mockito.verify(mockProcessor).accept(events);
    }
}

