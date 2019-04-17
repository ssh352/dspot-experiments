/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cache.interceptor;


import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests corner case of using {@link Cacheable} and  {@link CachePut} on the
 * same operation.
 *
 * @author Stephane Nicoll
 */
public class CachePutEvaluationTests {
    private ConfigurableApplicationContext context;

    private Cache cache;

    private CachePutEvaluationTests.SimpleService service;

    @Test
    public void mutualGetPutExclusion() {
        String key = "1";
        Long first = this.service.getOrPut(key, true);
        Long second = this.service.getOrPut(key, true);
        Assert.assertSame(first, second);
        // This forces the method to be executed again
        Long expected = first + 1;
        Long third = this.service.getOrPut(key, false);
        Assert.assertEquals(expected, third);
        Long fourth = this.service.getOrPut(key, true);
        Assert.assertSame(third, fourth);
    }

    @Test
    public void getAndPut() {
        this.cache.clear();
        long key = 1;
        Long value = this.service.getAndPut(key);
        Assert.assertEquals("Wrong value for @Cacheable key", value, this.cache.get(key).get());
        Assert.assertEquals("Wrong value for @CachePut key", value, this.cache.get((value + 100)).get());// See @CachePut

        // CachePut forced a method call
        Long anotherValue = this.service.getAndPut(key);
        Assert.assertNotSame(value, anotherValue);
        // NOTE: while you might expect the main key to have been updated, it hasn't. @Cacheable operations
        // are only processed in case of a cache miss. This is why combining @Cacheable with @CachePut
        // is a very bad idea. We could refine the condition now that we can figure out if we are going
        // to invoke the method anyway but that brings a whole new set of potential regressions.
        // assertEquals("Wrong value for @Cacheable key", anotherValue, cache.get(key).get());
        Assert.assertEquals("Wrong value for @CachePut key", anotherValue, this.cache.get((anotherValue + 100)).get());
    }

    @Configuration
    @EnableCaching
    static class Config extends CachingConfigurerSupport {
        @Bean
        @Override
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }

        @Bean
        public CachePutEvaluationTests.SimpleService simpleService() {
            return new CachePutEvaluationTests.SimpleService();
        }
    }

    @CacheConfig(cacheNames = "test")
    public static class SimpleService {
        private AtomicLong counter = new AtomicLong();

        /**
         * Represent a mutual exclusion use case. The boolean flag exclude one of the two operation.
         */
        @Cacheable(condition = "#p1", key = "#p0")
        @CachePut(condition = "!#p1", key = "#p0")
        public Long getOrPut(Object id, boolean flag) {
            return this.counter.getAndIncrement();
        }

        /**
         * Represent an invalid use case. If the result of the operation is non null, then we put
         * the value with a different key. This forces the method to be executed every time.
         */
        @Cacheable
        @CachePut(key = "#result + 100", condition = "#result != null")
        public Long getAndPut(long id) {
            return this.counter.getAndIncrement();
        }
    }
}
