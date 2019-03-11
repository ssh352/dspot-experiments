/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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
package org.springframework.security.core.userdetails.cache;


import org.junit.Test;
import org.springframework.cache.CacheManager;


/**
 * Tests
 * {@link org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache}.
 *
 * @author Marten Deinum
 * @since 3.2
 */
public class SpringCacheBasedUserCacheTests {
    private static CacheManager cacheManager;

    @Test
    public void cacheOperationsAreSuccessful() throws Exception {
        SpringCacheBasedUserCache cache = new SpringCacheBasedUserCache(getCache());
        // Check it gets stored in the cache
        cache.putUserInCache(getUser());
        assertThat(getUser().getPassword()).isEqualTo(cache.getUserFromCache(getUser().getUsername()).getPassword());
        // Check it gets removed from the cache
        cache.removeUserFromCache(getUser());
        assertThat(cache.getUserFromCache(getUser().getUsername())).isNull();
        // Check it doesn't return values for null or unknown users
        assertThat(cache.getUserFromCache(null)).isNull();
        assertThat(cache.getUserFromCache("UNKNOWN_USER")).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void startupDetectsMissingCache() throws Exception {
        new SpringCacheBasedUserCache(null);
    }
}

