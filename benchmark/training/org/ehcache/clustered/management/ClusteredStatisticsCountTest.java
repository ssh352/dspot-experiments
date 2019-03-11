/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.management;


import java.util.List;
import org.ehcache.Cache;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.management.model.stats.ContextualStatistics;


public class ClusteredStatisticsCountTest extends AbstractClusteringManagementTest {
    private static final long CACHE_HIT_COUNT = 2L;

    private static final long CLUSTERED_HIT_COUNT = 2L;

    private static final long CACHE_MISS_COUNT = 2L;

    private static final long CLUSTERED_MISS_COUNT = 2L;

    @Test
    public void countTest() throws Exception {
        AbstractClusteringManagementTest.sendManagementCallOnClientToCollectStats();
        Cache<String, String> cache = AbstractClusteringManagementTest.cacheManager.getCache("dedicated-cache-1", String.class, String.class);
        cache.put("one", "val1");
        cache.put("two", "val2");
        cache.get("one");// hit

        cache.get("two");// hit

        cache.get("three");// miss

        cache.get("four");// miss

        long cacheHitCount = 0;
        long clusteredHitCount = 0;
        long cacheMissCount = 0;
        long clusteredMissCount = 0;
        // it could be several seconds before the sampled stats could become available
        // let's try until we find the correct values
        do {
            // get the stats (we are getting the primitive counter, not the sample history)
            List<ContextualStatistics> stats = AbstractClusteringManagementTest.waitForNextStats();
            for (ContextualStatistics stat : stats) {
                if ((stat.getContext().contains("cacheName")) && (stat.getContext().get("cacheName").equals("dedicated-cache-1"))) {
                    // please leave it there - it's really useful to see what's coming
                    /* System.out.println("stats:");
                    for (Map.Entry<String, Statistic<?, ?>> entry : stat.getStatistics().entrySet()) {
                    System.out.println(" - " + entry.getKey() + " : " + entry.getValue());
                    }
                     */
                    cacheHitCount = stat.<Long>getLatestSampleValue("Cache:HitCount").get();
                    clusteredHitCount = stat.<Long>getLatestSampleValue("Clustered:HitCount").get();
                    clusteredMissCount = stat.<Long>getLatestSampleValue("Clustered:MissCount").get();
                    cacheMissCount = stat.<Long>getLatestSampleValue("Cache:MissCount").get();
                }
            }
        } while (((((!(Thread.currentThread().isInterrupted())) && (cacheHitCount != (ClusteredStatisticsCountTest.CACHE_HIT_COUNT))) && (clusteredHitCount != (ClusteredStatisticsCountTest.CLUSTERED_HIT_COUNT))) && (cacheMissCount != (ClusteredStatisticsCountTest.CACHE_MISS_COUNT))) && (clusteredMissCount != (ClusteredStatisticsCountTest.CLUSTERED_MISS_COUNT)) );
        Assert.assertThat(cacheHitCount, CoreMatchers.is(ClusteredStatisticsCountTest.CACHE_HIT_COUNT));
        Assert.assertThat(clusteredHitCount, CoreMatchers.is(ClusteredStatisticsCountTest.CLUSTERED_HIT_COUNT));
        Assert.assertThat(cacheMissCount, CoreMatchers.is(ClusteredStatisticsCountTest.CACHE_MISS_COUNT));
        Assert.assertThat(clusteredMissCount, CoreMatchers.is(ClusteredStatisticsCountTest.CLUSTERED_MISS_COUNT));
    }
}

