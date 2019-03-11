/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.server.internal.messages;


import SyncMessageType.MESSAGE_TRACKER;
import java.util.Map;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityResponse;
import org.ehcache.clustered.common.internal.messages.EhcacheResponseType;
import org.ehcache.clustered.server.TestClientSourceId;
import org.junit.Test;


public class EhcacheMessageTrackerMessageTest {
    public static class NullResponse extends EhcacheEntityResponse {
        @Override
        public EhcacheResponseType getResponseType() {
            return null;
        }
    }

    private EhcacheMessageTrackerMessage message;

    private TestClientSourceId id1 = new TestClientSourceId(1);

    private TestClientSourceId id2 = new TestClientSourceId(2);

    private EhcacheEntityResponse r3 = new EhcacheMessageTrackerMessageTest.NullResponse();

    private EhcacheEntityResponse r4 = new EhcacheMessageTrackerMessageTest.NullResponse();

    private EhcacheEntityResponse r5 = new EhcacheMessageTrackerMessageTest.NullResponse();

    @Test
    public void getMessageType() throws Exception {
        assertThat(message.getMessageType()).isEqualTo(MESSAGE_TRACKER);
    }

    @Test
    public void getTrackedMessages() throws Exception {
        Map<Long, Map<Long, EhcacheEntityResponse>> result = message.getTrackedMessages();
        assertThat(result).containsKeys(id1.toLong(), id2.toLong());
        assertThat(result.get(id1.toLong())).contains(entry(3L, r3), entry(4L, r4));
        assertThat(result.get(id2.toLong())).contains(entry(5L, r5));
    }
}

