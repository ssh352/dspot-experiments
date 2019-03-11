/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.internal.crypto;


import BouncyCastleProvider.PROVIDER_NAME;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.function.Predicate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assume;
import org.junit.Test;


public class BouncyCastleKeyFactoryProviderTest {
    /**
     * Tests if a SSLeay PKCS#5 private key is accepted.
     */
    @Test
    public void pkcs5() {
        BouncyCastleKeyFactoryProvider.call(this::loadPkcs5);
    }

    /**
     * Tests if a PKCS#8 private key is accepted.
     */
    @Test
    public void pkcs8() {
        BouncyCastleKeyFactoryProvider.call(this::loadPkcs8);
    }

    /**
     * Tests if everything works even if Bouncy Castle is loaded already.
     */
    @Test
    public void bouncyCastlePreInstalled() {
        Assume.assumeTrue(Arrays.stream(Security.getProviders()).noneMatch(( p) -> PROVIDER_NAME.equals(p.getName())));
        Security.addProvider(new BouncyCastleProvider());
        try {
            BouncyCastleKeyFactoryProvider.call(this::loadPkcs5);
            BouncyCastleKeyFactoryProvider.call(this::loadPkcs8);
        } finally {
            Security.removeProvider(PROVIDER_NAME);
        }
    }

    @Test
    public void nestedInvocation() {
        BouncyCastleKeyFactoryProvider.call(() -> BouncyCastleKeyFactoryProvider.call(this::loadPkcs5));
    }
}

