/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.cache.ehcache.test;


import ConfigSettings.MISSING_CACHE_STRATEGY;
import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cache.spi.SecondLevelCacheLogger;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MissingCacheStrategyTest extends BaseUnitTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(SecondLevelCacheLogger.INSTANCE);

    @Test
    public void testMissingCacheStrategyDefault() {
        // default settings
        doTestMissingCacheStrategyCreateWarn(( ignored) -> {
        });
    }

    @Test
    public void testMissingCacheStrategyFail() {
        /* The cache manager is created per session factory, and we don't use any specific ehcache configuration,
        so we know the caches don't exist before we start the session factory.
         */
        // let's try to build the standard testing SessionFactory, without pre-defining caches
        try (SessionFactoryImplementor ignored = TestHelper.buildStandardSessionFactory(( builder) -> builder.applySetting(MISSING_CACHE_STRATEGY, "fail"))) {
            Assert.fail();
        } catch (ServiceException expected) {
            assertTyping(CacheException.class, expected.getCause());
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.equalTo((("Unable to create requested service [" + (CacheImplementor.class.getName())) + "]")));
            MatcherAssert.assertThat(expected.getCause().getMessage(), CoreMatchers.startsWith("On-the-fly creation of Ehcache Cache objects is not supported"));
        } catch (CacheException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.equalTo("On-the-fly creation of Ehcache Cache objects is not supported"));
        }
    }

    @Test
    public void testMissingCacheStrategyFail_legacyNames() {
        /* The cache manager is created per session factory, and we don't use any specific ehcache configuration,
        so we know the caches don't exist before we start the session factory.
         */
        // let's try to build the standard testing SessionFactory, without pre-defining caches
        try (SessionFactoryImplementor ignored = TestHelper.buildStandardSessionFactory(( builder) -> builder.applySetting(MISSING_CACHE_STRATEGY, "fail"))) {
            Assert.fail();
        } catch (ServiceException expected) {
            assertTyping(CacheException.class, expected.getCause());
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.equalTo((("Unable to create requested service [" + (CacheImplementor.class.getName())) + "]")));
            MatcherAssert.assertThat(expected.getCause().getMessage(), CoreMatchers.startsWith("On-the-fly creation of Ehcache Cache objects is not supported"));
        } catch (CacheException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.equalTo("On-the-fly creation of Ehcache Cache objects is not supported"));
        }
    }

    @Test
    public void testMissingCacheStrategyCreate() {
        /* The cache manager is created per session factory, and we don't use any specific ehcache configuration,
        so we know the caches don't exist before we start the session factory.
         */
        // and now let's try to build the standard testing SessionFactory, without pre-defining caches
        try (SessionFactoryImplementor sessionFactory = TestHelper.buildStandardSessionFactory(( builder) -> builder.applySetting(MISSING_CACHE_STRATEGY, "create"))) {
            // The caches should have been created automatically
            for (String regionName : TestHelper.allDomainRegionNames) {
                MatcherAssert.assertThat((("Cache '" + regionName) + "' should have been created"), TestHelper.getCache(sessionFactory, regionName), CoreMatchers.notNullValue());
            }
        }
    }

    @Test
    public void testMissingCacheStrategyCreateWarn() {
        doTestMissingCacheStrategyCreateWarn(( builder) -> builder.applySetting(MISSING_CACHE_STRATEGY, "create-warn"));
    }

    @Test
    public void testMissingCacheStrategyFailLegacyNames1() {
        doTestMissingCacheStrategyFailLegacyNames("/hibernate-config/ehcache-explicitlegacy1.xml", TestHelper.queryRegionLegacyNames1, TestHelper.queryRegionLegacyNames2);
    }

    @Test
    public void testMissingCacheStrategyFailLegacyNames2() {
        doTestMissingCacheStrategyFailLegacyNames("/hibernate-config/ehcache-explicitlegacy2.xml", TestHelper.queryRegionLegacyNames2, TestHelper.queryRegionLegacyNames1);
    }
}
