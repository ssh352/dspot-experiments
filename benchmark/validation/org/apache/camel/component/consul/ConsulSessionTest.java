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
package org.apache.camel.component.consul;


import ConsulConstants.CONSUL_ACTION;
import ConsulConstants.CONSUL_SESSION;
import ConsulSessionActions.CREATE;
import ConsulSessionActions.DESTROY;
import ConsulSessionActions.LIST;
import com.orbitz.consul.model.session.SessionCreatedResponse;
import com.orbitz.consul.model.session.SessionInfo;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class ConsulSessionTest extends ConsulTestSupport {
    @Test
    public void testServiceInstance() throws Exception {
        final String name = UUID.randomUUID().toString();
        final int sessions = getConsul().sessionClient().listSessions().size();
        {
            List<SessionInfo> list = fluentTemplate().withHeader(CONSUL_ACTION, LIST).to("direct:consul").request(List.class);
            Assert.assertEquals(sessions, list.size());
            Assert.assertFalse(list.stream().anyMatch(( s) -> (s.getName().isPresent()) && (s.getName().get().equals(name))));
        }
        SessionCreatedResponse res = fluentTemplate().withHeader(CONSUL_ACTION, CREATE).withBody(com.orbitz.consul.model.session.ImmutableSession.builder().name(name).build()).to("direct:consul").request(SessionCreatedResponse.class);
        Assert.assertNotNull(res.getId());
        {
            List<SessionInfo> list = fluentTemplate().withHeader(CONSUL_ACTION, LIST).to("direct:consul").request(List.class);
            Assert.assertEquals((sessions + 1), list.size());
            Assert.assertTrue(list.stream().anyMatch(( s) -> (s.getName().isPresent()) && (s.getName().get().equals(name))));
        }
        {
            fluentTemplate().withHeader(CONSUL_ACTION, DESTROY).withHeader(CONSUL_SESSION, res.getId()).to("direct:consul").send();
            List<SessionInfo> list = fluentTemplate().withHeader(CONSUL_ACTION, LIST).to("direct:consul").request(List.class);
            Assert.assertEquals(sessions, list.size());
            Assert.assertFalse(list.stream().anyMatch(( s) -> (s.getName().isPresent()) && (s.getName().get().equals(name))));
        }
    }
}
