/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.net.ssl;


import ch.qos.logback.core.net.ssl.mock.MockContextAware;
import ch.qos.logback.core.net.ssl.mock.MockKeyManagerFactoryFactoryBean;
import ch.qos.logback.core.net.ssl.mock.MockKeyStoreFactoryBean;
import ch.qos.logback.core.net.ssl.mock.MockSecureRandomFactoryBean;
import ch.qos.logback.core.net.ssl.mock.MockTrustManagerFactoryFactoryBean;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link SSLContextFactoryBean}.
 *
 * @author Carl Harris
 */
public class SSLContextFactoryBeanTest {
    private static final String SSL_CONFIGURATION_MESSAGE_PATTERN = "SSL protocol '.*?' provider '.*?'";

    private static final String KEY_MANAGER_FACTORY_MESSAGE_PATTERN = "key manager algorithm '.*?' provider '.*?'";

    private static final String TRUST_MANAGER_FACTORY_MESSAGE_PATTERN = "trust manager algorithm '.*?' provider '.*?'";

    private static final String KEY_STORE_MESSAGE_PATTERN = "key store of type '.*?' provider '.*?': .*";

    private static final String TRUST_STORE_MESSAGE_PATTERN = "trust store of type '.*?' provider '.*?': .*";

    private static final String SECURE_RANDOM_MESSAGE_PATTERN = "secure random algorithm '.*?' provider '.*?'";

    private MockKeyManagerFactoryFactoryBean keyManagerFactory = new MockKeyManagerFactoryFactoryBean();

    private MockTrustManagerFactoryFactoryBean trustManagerFactory = new MockTrustManagerFactoryFactoryBean();

    private MockKeyStoreFactoryBean keyStore = new MockKeyStoreFactoryBean();

    private MockKeyStoreFactoryBean trustStore = new MockKeyStoreFactoryBean();

    private MockSecureRandomFactoryBean secureRandom = new MockSecureRandomFactoryBean();

    private MockContextAware context = new MockContextAware();

    private SSLContextFactoryBean factoryBean = new SSLContextFactoryBean();

    @Test
    public void testCreateDefaultContext() throws Exception {
        // should be able to create a context with no configuration at all
        Assert.assertNotNull(factoryBean.createContext(context));
        Assert.assertTrue(context.hasInfoMatching(SSLContextFactoryBeanTest.SSL_CONFIGURATION_MESSAGE_PATTERN));
    }

    @Test
    public void testCreateContext() throws Exception {
        factoryBean.setKeyManagerFactory(keyManagerFactory);
        factoryBean.setKeyStore(keyStore);
        factoryBean.setTrustManagerFactory(trustManagerFactory);
        factoryBean.setTrustStore(trustStore);
        factoryBean.setSecureRandom(secureRandom);
        Assert.assertNotNull(factoryBean.createContext(context));
        Assert.assertTrue(keyManagerFactory.isFactoryCreated());
        Assert.assertTrue(trustManagerFactory.isFactoryCreated());
        Assert.assertTrue(keyStore.isKeyStoreCreated());
        Assert.assertTrue(trustStore.isKeyStoreCreated());
        Assert.assertTrue(secureRandom.isSecureRandomCreated());
        // it's important that each configured component output an appropriate
        // informational message to the context; i.e. this logging is not just
        // for programmers, it's there for systems administrators to use in
        // verifying that SSL is configured properly
        Assert.assertTrue(context.hasInfoMatching(SSLContextFactoryBeanTest.SSL_CONFIGURATION_MESSAGE_PATTERN));
        Assert.assertTrue(context.hasInfoMatching(SSLContextFactoryBeanTest.KEY_MANAGER_FACTORY_MESSAGE_PATTERN));
        Assert.assertTrue(context.hasInfoMatching(SSLContextFactoryBeanTest.TRUST_MANAGER_FACTORY_MESSAGE_PATTERN));
        Assert.assertTrue(context.hasInfoMatching(SSLContextFactoryBeanTest.KEY_STORE_MESSAGE_PATTERN));
        Assert.assertTrue(context.hasInfoMatching(SSLContextFactoryBeanTest.TRUST_STORE_MESSAGE_PATTERN));
        Assert.assertTrue(context.hasInfoMatching(SSLContextFactoryBeanTest.SECURE_RANDOM_MESSAGE_PATTERN));
    }
}

