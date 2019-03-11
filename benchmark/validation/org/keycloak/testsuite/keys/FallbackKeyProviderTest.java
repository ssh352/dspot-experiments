/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.keys;


import AppPage.RequestType.AUTH_RESPONSE;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import java.util.LinkedList;
import java.util.List;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.crypto.Algorithm;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FallbackKeyProviderTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void fallbackAfterDeletingAllKeysInRealm() {
        String realmId = realmsResouce().realm("test").toRepresentation().getId();
        List<ComponentRepresentation> providers = realmsResouce().realm("test").components().query(realmId, "org.keycloak.keys.KeyProvider");
        Assert.assertEquals(3, providers.size());
        for (ComponentRepresentation p : providers) {
            realmsResouce().realm("test").components().component(p.getId()).remove();
        }
        providers = realmsResouce().realm("test").components().query(realmId, "org.keycloak.keys.KeyProvider");
        Assert.assertEquals(0, providers.size());
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertNotNull(response.getAccessToken());
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        providers = realmsResouce().realm("test").components().query(realmId, "org.keycloak.keys.KeyProvider");
        assertProviders(providers, "fallback-RS256", "fallback-HS256");
    }

    @Test
    public void differentAlgorithms() {
        String realmId = realmsResouce().realm("test").toRepresentation().getId();
        String[] algorithmsToTest = new String[]{ Algorithm.RS384, Algorithm.RS512, Algorithm.ES256, Algorithm.ES384, Algorithm.ES512 };
        oauth.doLogin("test-user@localhost", "password");
        for (String algorithm : algorithmsToTest) {
            RealmRepresentation rep = realmsResouce().realm("test").toRepresentation();
            rep.setDefaultSignatureAlgorithm(algorithm);
            realmsResouce().realm("test").update(rep);
            oauth.openLoginForm();
            String code = oauth.getCurrentQuery().get(CODE);
            OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
            Assert.assertNotNull(response.getAccessToken());
        }
        List<ComponentRepresentation> providers = realmsResouce().realm("test").components().query(realmId, "org.keycloak.keys.KeyProvider");
        List<String> expected = new LinkedList<>();
        expected.add("rsa");
        expected.add("hmac-generated");
        expected.add("aes-generated");
        for (String a : algorithmsToTest) {
            expected.add(("fallback-" + a));
        }
        assertProviders(providers, expected.toArray(new String[providers.size()]));
    }
}

