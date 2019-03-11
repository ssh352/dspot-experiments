/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.agent;


import org.apache.ambari.server.HostNotRegisteredException;
import org.apache.ambari.server.state.Host;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class AgentSessionManagerTest {
    private AgentSessionManager underTest;

    @Test
    public void hostIsRegistered() throws HostNotRegisteredException {
        String sessionId = "session ID";
        Long hostId = 1L;
        Host host = EasyMock.createNiceMock(Host.class);
        expect(host.getHostId()).andReturn(hostId).anyTimes();
        replay(host);
        underTest.register(sessionId, host);
        Assert.assertTrue(underTest.isRegistered(sessionId));
        Assert.assertEquals(sessionId, underTest.getSessionId(hostId));
        Assert.assertSame(host, underTest.getHost(sessionId));
    }

    @Test(expected = HostNotRegisteredException.class)
    public void exceptionThrownForUnknownHost() throws HostNotRegisteredException {
        Long notRegisteredHostId = 2L;
        underTest.getSessionId(notRegisteredHostId);
    }

    @Test(expected = HostNotRegisteredException.class)
    public void exceptionThrownForUnknownSessionId() throws HostNotRegisteredException {
        underTest.getHost("unknown session ID");
    }

    @Test
    public void registerRemovesOldSessionId() throws HostNotRegisteredException {
        String oldSessionId = "old session ID";
        String newSessionId = "new session ID";
        Long hostId = 1L;
        Host host = EasyMock.createNiceMock(Host.class);
        expect(host.getHostId()).andReturn(hostId).anyTimes();
        replay(host);
        underTest.register(oldSessionId, host);
        underTest.register(newSessionId, host);
        Assert.assertFalse(underTest.isRegistered(oldSessionId));
        Assert.assertEquals(newSessionId, underTest.getSessionId(hostId));
        Assert.assertSame(host, underTest.getHost(newSessionId));
    }

    @Test(expected = HostNotRegisteredException.class)
    public void unregisterRemovesSessionId() throws HostNotRegisteredException {
        String sessionId = "session ID";
        Long hostId = 1L;
        Host host = EasyMock.createNiceMock(Host.class);
        expect(host.getHostId()).andReturn(hostId).anyTimes();
        replay(host);
        underTest.register(sessionId, host);
        underTest.unregisterByHost(hostId);
        Assert.assertFalse(underTest.isRegistered(sessionId));
        underTest.getSessionId(hostId);
    }
}

