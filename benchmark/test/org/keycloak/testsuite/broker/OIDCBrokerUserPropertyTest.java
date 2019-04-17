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
package org.keycloak.testsuite.broker;


import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.testsuite.KeycloakServer;
import org.keycloak.testsuite.rule.AbstractKeycloakRule;


/**
 * Test that the broker AttributeMapper maps user properties like email, firstName, and lastName
 *
 * @author pedroigor
 */
public class OIDCBrokerUserPropertyTest extends AbstractKeycloakIdentityProviderTest {
    @ClassRule
    public static AbstractKeycloakRule samlServerRule = new AbstractKeycloakRule() {
        @Override
        protected void configureServer(KeycloakServer server) {
            server.getConfig().setPort(8082);
        }

        @Override
        protected void configure(KeycloakSession session, RealmManager manager, RealmModel adminRealm) {
            server.importRealm(getClass().getResourceAsStream("/broker-test/realm-with-oidc-property-mappers.json"));
        }

        @Override
        protected String[] getTestRealms() {
            return new String[]{ "realm-with-oidc-idp-property-mappers" };
        }
    };

    /**
     * Test for KEYCLOAK-3505 - Verify the claims from the claim set returned by the OIDC UserInfo are correctly mapped
     *  by the user attribute mapper
     */
    @Test
    public void testSuccessfulAuthentication_verifyAttributeMapperHandlesUserInfoClaims() {
        verifyAttributeMapperHandlesUserInfoClaims();
    }

    @Override
    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile() {
        super.testSuccessfulAuthenticationWithoutUpdateProfile();
    }
}
