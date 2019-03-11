/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rest.handler.async;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.flink.runtime.rest.messages.TriggerId;
import org.apache.flink.runtime.util.ManualTicker;
import org.apache.flink.types.Either;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CompletedOperationCache}.
 */
public class CompletedOperationCacheTest extends TestLogger {
    private static final OperationKey TEST_OPERATION_KEY = new OperationKey(new TriggerId());

    private static final CompletableFuture<String> TEST_OPERATION_RESULT = CompletableFuture.completedFuture("foo");

    private ManualTicker manualTicker;

    private CompletedOperationCache<OperationKey, String> completedOperationCache;

    @Test
    public void testShouldFinishClosingCacheIfAllResultsAreEvicted() {
        completedOperationCache.registerOngoingOperation(CompletedOperationCacheTest.TEST_OPERATION_KEY, CompletedOperationCacheTest.TEST_OPERATION_RESULT);
        final CompletableFuture<Void> closeCacheFuture = completedOperationCache.closeAsync();
        Assert.assertThat(closeCacheFuture.isDone(), Matchers.is(false));
        manualTicker.advanceTime(300, TimeUnit.SECONDS);
        completedOperationCache.cleanUp();
        Assert.assertThat(closeCacheFuture.isDone(), Matchers.is(true));
    }

    @Test
    public void testShouldFinishClosingCacheIfAllResultsAccessed() throws Exception {
        completedOperationCache.registerOngoingOperation(CompletedOperationCacheTest.TEST_OPERATION_KEY, CompletedOperationCacheTest.TEST_OPERATION_RESULT);
        final CompletableFuture<Void> closeCacheFuture = completedOperationCache.closeAsync();
        Assert.assertThat(closeCacheFuture.isDone(), Matchers.is(false));
        final Either<Throwable, String> operationResultOrError = completedOperationCache.get(CompletedOperationCacheTest.TEST_OPERATION_KEY);
        Assert.assertThat(operationResultOrError, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(operationResultOrError.right(), Matchers.is(Matchers.equalTo(CompletedOperationCacheTest.TEST_OPERATION_RESULT.get())));
        Assert.assertThat(closeCacheFuture.isDone(), Matchers.is(true));
    }
}

