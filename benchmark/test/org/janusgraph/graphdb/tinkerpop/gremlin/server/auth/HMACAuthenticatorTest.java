/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.graphdb.tinkerpop.gremlin.server.auth;


import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.tinkerpop.gremlin.server.auth.AuthenticatedUser;
import org.apache.tinkerpop.gremlin.server.auth.AuthenticationException;
import org.janusgraph.graphdb.tinkerpop.gremlin.server.handler.HttpHMACAuthenticationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class HMACAuthenticatorTest extends JanusGraphAbstractAuthenticatorTest {
    @Test
    public void testSetupNoHmacSecret() {
        final HMACAuthenticator authenticator = new HMACAuthenticator();
        final Map<String, Object> configMap = new HashMap<>();
        configMap.put(CONFIG_CREDENTIALS_DB, "configCredDb");
        Assertions.assertThrows(IllegalStateException.class, () -> authenticator.setup(configMap));
    }

    @Test
    public void testAuthenticateBasicAuthValid() throws AuthenticationException {
        final HMACAuthenticator authenticator = createMockedAuthenticator();
        final String defaultUser = "user";
        final String defaultPassword = "pass";
        final Map<String, Object> config = HmacConfigBuilder.build().defaultUser(defaultUser).defaultPassword(defaultPassword).create();
        authenticator.setup(config);
        final Map<String, String> credentials = CredentialsBuilder.build().user(defaultUser).password(defaultPassword).create();
        authenticator.authenticate(credentials);
    }

    @Test
    public void testAuthenticateBasicAuthInvalid() {
        final HMACAuthenticator authenticator = createMockedAuthenticator();
        final String defaultUser = "user";
        final String defaultPassword = "pass";
        final Map<String, Object> config = HmacConfigBuilder.build().defaultUser(defaultUser).defaultPassword(defaultPassword).create();
        authenticator.setup(config);
        final Map<String, String> credentials = CredentialsBuilder.build().user(defaultUser).password("invalid").create();
        Assertions.assertThrows(AuthenticationException.class, () -> authenticator.authenticate(credentials));
    }

    @Test
    public void testAuthenticateGenerateToken() throws AuthenticationException {
        final HMACAuthenticator authenticator = createMockedAuthenticator();
        final String defaultUser = "user";
        final String defaultPassword = "pass";
        authenticator.setup(HmacConfigBuilder.build().defaultUser(defaultUser).defaultPassword(defaultPassword).create());
        final Map<String, String> credentials = CredentialsBuilder.build().user(defaultUser).password(defaultPassword).enableTokenGeneration().create();
        authenticator.authenticate(credentials);
        Assertions.assertNotNull(credentials.get(HttpHMACAuthenticationHandler.PROPERTY_TOKEN));
    }

    @Test
    public void testAuthenticateWithToken() throws AuthenticationException {
        final HMACAuthenticator authenticator = createMockedAuthenticator();
        final String defaultUser = "user";
        String token = generateTokenForAuthenticatedUser(authenticator, defaultUser);
        AuthenticatedUser authenticatedUser = authenticator.authenticate(CredentialsBuilder.build().token(token).create());
        Assertions.assertEquals(defaultUser, authenticatedUser.getName());
    }

    @Test
    public void testAuthenticateWithShortenedToken() throws AuthenticationException {
        final HMACAuthenticator authenticator = createMockedAuthenticator();
        String token = generateTokenForAuthenticatedUser(authenticator);
        final String brokenToken = token.substring(0, ((token.length()) - 4));
        Assertions.assertThrows(AuthenticationException.class, () -> authenticator.authenticate(CredentialsBuilder.build().token(brokenToken).create()));
    }

    @Test
    public void testAuthenticateWithBrokenToken() throws AuthenticationException {
        final HMACAuthenticator authenticator = createMockedAuthenticator();
        String token = generateTokenForAuthenticatedUser(authenticator);
        final String brokenToken = (token.substring(0, ((token.length()) - ("abcdefgh".length())))) + "abcdefgh";
        Assertions.assertThrows(AuthenticationException.class, () -> authenticator.authenticate(CredentialsBuilder.build().token(brokenToken).create()));
    }

    @Test
    public void testAuthenticateWithTimedOutToken() throws InterruptedException, AuthenticationException {
        final HMACAuthenticator authenticator = createMockedAuthenticator();
        final String defaultPassword = "pass";
        final String defaultUser = "user";
        ConfigBuilder.build().defaultUser(defaultUser).create();
        final int tokenTimeoutInMs = 1;
        final Map<String, Object> config = HmacConfigBuilder.build().tokenTimeout(tokenTimeoutInMs).defaultUser(defaultUser).defaultPassword(defaultPassword).create();
        authenticator.setup(config);
        final Map<String, String> credentials = CredentialsBuilder.build().user(defaultUser).password(defaultPassword).enableTokenGeneration().create();
        authenticator.authenticate(credentials);
        String token = credentials.get(HttpHMACAuthenticationHandler.PROPERTY_TOKEN);
        Thread.sleep(tokenTimeoutInMs);
        Assertions.assertThrows(AuthenticationException.class, () -> authenticator.authenticate(CredentialsBuilder.build().token(token).create()));
    }

    @Test
    public void testNewSaslNegotiatorOfInetAddr() {
        final HMACAuthenticator authenticator = new HMACAuthenticator();
        Assertions.assertThrows(RuntimeException.class, () -> authenticator.newSaslNegotiator(createMock(InetAddress.class)));
    }

    @Test
    public void testNewSaslNegotiator() {
        final HMACAuthenticator authenticator = new HMACAuthenticator();
        Assertions.assertThrows(RuntimeException.class, () -> authenticator.newSaslNegotiator());
    }
}

