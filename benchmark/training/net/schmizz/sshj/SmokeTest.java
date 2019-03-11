/**
 * Copyright (C)2009 - SSHJ Contributors
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
package net.schmizz.sshj;


import com.hierynomus.sshj.test.SshFixture;
import java.io.IOException;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.UserAuthException;
import org.junit.Assert;
import org.junit.Test;


/* Kinda basic right now */
public class SmokeTest {
    private final SshFixture fixture = new SshFixture();

    @Test
    public void connected() throws IOException {
        Assert.assertTrue(fixture.getClient().isConnected());
    }

    @Test
    public void authenticated() throws TransportException, UserAuthException {
        fixture.getClient().authPassword("dummy", "dummy");
        Assert.assertTrue(fixture.getClient().isAuthenticated());
    }
}

