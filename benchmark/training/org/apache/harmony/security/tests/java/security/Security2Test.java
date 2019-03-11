/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.security.tests.java.security;


import java.security.InvalidParameterException;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import tests.support.Support_ProviderTrust;
import tests.support.Support_TestProvider;


public class Security2Test extends TestCase {
    /**
     * java.security.Security#getProviders(java.lang.String)
     */
    public void test_getProvidersLjava_lang_String() {
        // Test for method void
        // java.security.Security.getProviders(java.lang.String)
        Map<String, Integer> allSupported = new HashMap<String, Integer>();
        Provider[] allProviders = Security.getProviders();
        // Add all non-alias entries to allSupported
        for (Provider provider : allProviders) {
            for (Object k : provider.keySet()) {
                String key = ((String) (k));
                // No aliases and no provider data
                if ((!(isAlias(key))) && (!(isProviderData(key)))) {
                    addOrIncrementTable(allSupported, key);
                }
            }// end while more entries

        }// end for all providers

        // Now walk through aliases. If an alias has actually been added
        // to the allSupported table then increment the count of the
        // entry that is being aliased.
        for (Provider provider : allProviders) {
            for (Map.Entry entry : provider.entrySet()) {
                String key = ((String) (entry.getKey()));
                if (isAlias(key)) {
                    String aliasName = key.substring("ALG.ALIAS.".length()).toUpperCase();
                    String realName = (aliasName.substring(0, ((aliasName.indexOf(".")) + 1))) + (entry.getValue());
                    // Skip over nonsense alias declarations where alias and
                    // aliased are identical. Such entries can occur.
                    if (!(aliasName.equalsIgnoreCase(realName))) {
                        // Has a real entry been added for aliasName ?
                        if (allSupported.containsKey(aliasName)) {
                            // Add 1 to the provider count of the thing being aliased
                            addOrIncrementTable(allSupported, aliasName);
                        }
                    }
                }
            }// end while more entries

        }// end for all providers

        for (String filterString : allSupported.keySet()) {
            try {
                Provider[] provTest = Security.getProviders(filterString);
                int expected = allSupported.get(filterString);
                TestCase.assertEquals(((("Unexpected number of providers returned for filter " + filterString) + ":\n") + allSupported), expected, provTest.length);
            } catch (InvalidParameterException e) {
                // NO OP
            }
        }// end while

        // exception
        try {
            Security.getProviders("Signature.SHA1withDSA :512");
            TestCase.fail("InvalidParameterException should be thrown <Signature.SHA1withDSA :512>");
        } catch (InvalidParameterException e) {
            // Expected
        }
    }

    /**
     * java.security.Security#getProviders(java.util.Map)
     */
    public void test_getProvidersLjava_util_Map() {
        // Test for method void
        // java.security.Security.getProviders(java.util.Map)
        Map<String, String> filter = new HashMap<String, String>();
        filter.put("KeyStore.BKS", "");
        filter.put("Signature.SHA1withDSA", "");
        Provider[] provTest = Security.getProviders(filter);
        if (provTest == null) {
            TestCase.assertEquals("Filter : <KeyStore.BKS>,<Signature.SHA1withDSA>", 0, getProvidersCount(filter));
        } else {
            TestCase.assertEquals("Filter : <KeyStore.BKS>,<Signature.SHA1withDSA>", getProvidersCount(filter), provTest.length);
        }
        filter = new HashMap<String, String>();
        filter.put("MessageDigest.SHA-384", "");
        filter.put("CertificateFactory.X.509", "");
        filter.put("KeyFactory.RSA", "");
        provTest = Security.getProviders(filter);
        if (provTest == null) {
            TestCase.assertEquals("Filter : <MessageDigest.SHA-384>,<CertificateFactory.X.509>,<KeyFactory.RSA>", 0, getProvidersCount(filter));
        } else {
            TestCase.assertEquals("Filter : <MessageDigest.SHA-384>,<CertificateFactory.X.509>,<KeyFactory.RSA>", getProvidersCount(filter), provTest.length);
        }
        filter = new HashMap<String, String>();
        filter.put("MessageDigest.SHA1", "");
        filter.put("TrustManagerFactory.X509", "");
        provTest = Security.getProviders(filter);
        if (provTest == null) {
            TestCase.assertEquals("Filter : <MessageDigest.SHA1><TrustManagerFactory.X509>", 0, getProvidersCount(filter));
        } else {
            TestCase.assertEquals("Filter : <MessageDigest.SHA1><TrustManagerFactory.X509>", getProvidersCount(filter), provTest.length);
        }
        filter = new HashMap<String, String>();
        filter.put("CertificateFactory.X509", "");
        provTest = Security.getProviders(filter);
        if (provTest == null) {
            TestCase.assertEquals("Filter : <CertificateFactory.X509>", 0, getProvidersCount(filter));
        } else {
            TestCase.assertEquals("Filter : <CertificateFactory.X509>", getProvidersCount(filter), provTest.length);
        }
        filter = new HashMap<String, String>();
        filter.put("Provider.id name", "DRLCertFactory");
        provTest = Security.getProviders(filter);
        TestCase.assertNull("Filter : <Provider.id name, DRLCertFactory >", provTest);
        // exception - no attribute name after the service.algorithm yet we
        // still supply an expected value. This is not valid.
        try {
            filter = new HashMap<String, String>();
            filter.put("Signature.SHA1withDSA", "512");
            provTest = Security.getProviders(filter);
            TestCase.fail("InvalidParameterException should be thrown <Signature.SHA1withDSA><512>");
        } catch (InvalidParameterException e) {
            // Expected
        }
        // exception - space character in the service.algorithm pair. Not valid.
        try {
            filter = new HashMap<String, String>();
            filter.put("Signature. KeySize", "512");
            provTest = Security.getProviders(filter);
            TestCase.fail("InvalidParameterException should be thrown <Signature. KeySize><512>");
        } catch (InvalidParameterException e) {
            // Expected
        }
    }

    /**
     * java.security.Security#removeProvider(java.lang.String)
     */
    public void test_removeProviderLjava_lang_String() {
        // Test for method void
        // java.security.Security.removeProvider(java.lang.String)
        Provider test = new Support_TestProvider();
        Provider entrust = new Support_ProviderTrust();
        try {
            // Make sure provider not already loaded. Should do nothing
            // if not already loaded.
            Security.removeProvider(test.getName());
            // Now add it
            int addResult = Security.addProvider(test);
            TestCase.assertTrue("Failed to add provider", (addResult != (-1)));
            Security.removeProvider(test.getName());
            TestCase.assertNull("the provider TestProvider is found after it was removed", Security.getProvider(test.getName()));
            // Make sure entrust provider not already loaded. Should do nothing
            // if not already loaded.
            Security.removeProvider(entrust.getName());
            // Now add entrust
            addResult = Security.addProvider(entrust);
            TestCase.assertTrue("Failed to add provider", (addResult != (-1)));
            Security.removeProvider(entrust.getName());
            for (Provider provider : Security.getProviders()) {
                TestCase.assertTrue("the provider entrust is found after it was removed", ((provider.getName()) != (entrust.getName())));
            }
        } finally {
            // Tidy up - the following calls do nothing if the providers were
            // already removed above.
            Security.removeProvider(test.getName());
            Security.removeProvider(entrust.getName());
        }
    }
}

