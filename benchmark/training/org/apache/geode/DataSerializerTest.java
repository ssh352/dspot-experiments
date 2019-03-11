/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode;


import org.apache.geode.internal.cache.EventID;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.apache.geode.test.junit.categories.SerializationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ SerializationTest.class })
public class DataSerializerTest {
    @Test
    public void shouldBeMockable() throws Exception {
        DataSerializer mockDataSerializer = Mockito.mock(DataSerializer.class);
        EventID mockEventID = Mockito.mock(EventID.class);
        ClientProxyMembershipID mockClientProxyMembershipID = Mockito.mock(ClientProxyMembershipID.class);
        Mockito.when(mockDataSerializer.getEventId()).thenReturn(mockEventID);
        Mockito.when(mockDataSerializer.getContext()).thenReturn(mockClientProxyMembershipID);
        mockDataSerializer.setEventId(mockEventID);
        mockDataSerializer.setContext(mockClientProxyMembershipID);
        Mockito.verify(mockDataSerializer, Mockito.times(1)).setEventId(mockEventID);
        Mockito.verify(mockDataSerializer, Mockito.times(1)).setContext(mockClientProxyMembershipID);
        assertThat(mockDataSerializer.getEventId()).isSameAs(mockEventID);
        assertThat(mockDataSerializer.getContext()).isSameAs(mockClientProxyMembershipID);
    }
}

