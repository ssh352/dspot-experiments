/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.fetch;


import com.google.common.collect.Iterables;
import io.crate.execution.engine.collect.stats.JobsLogs;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.common.breaker.NoopCircuitBreaker;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.core.Is;
import org.junit.Test;


public class NodeFetchOperationTest extends CrateDummyClusterServiceUnitTest {
    @Test
    public void testSysOperationsIsClearedIfNothingToFetch() throws Exception {
        ThreadPoolExecutor threadPoolExecutor = ((ThreadPoolExecutor) (Executors.newFixedThreadPool(1)));
        try {
            JobsLogs jobsLogs = new JobsLogs(() -> true);
            NodeFetchOperation fetchOperation = new NodeFetchOperation(threadPoolExecutor, 2, jobsLogs, new io.crate.execution.jobs.TasksService(Settings.EMPTY, clusterService, jobsLogs), new NoopCircuitBreaker("dummy"));
            fetchOperation.fetch(UUID.randomUUID(), 1, null, true).get(5, TimeUnit.SECONDS);
            assertThat(Iterables.size(jobsLogs.activeOperations()), Is.is(0));
        } finally {
            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(2, TimeUnit.SECONDS);
        }
    }
}

