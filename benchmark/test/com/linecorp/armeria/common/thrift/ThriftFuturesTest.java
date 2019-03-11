/**
 * Copyright 2016 LINE Corporation
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
package com.linecorp.armeria.common.thrift;


import org.junit.Test;


public class ThriftFuturesTest {
    @Test
    public void testSuccessfulCompletedFuture() throws Exception {
        final ThriftCompletableFuture<String> future = ThriftFutures.successfulCompletedFuture("success");
        assertThat(future.get()).isEqualTo("success");
    }

    @Test
    public void testFailedCompletedFuture() throws Exception {
        final ThriftCompletableFuture<String> future = ThriftFutures.failedCompletedFuture(new IllegalStateException());
        assertThat(catchThrowable(future::get)).hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testSuccessfulListenableFuture() throws Exception {
        ThriftFuturesTest.assumeUnshadedGuava();
        final ThriftListenableFuture<String> future = ThriftFutures.successfulListenableFuture("success");
        assertThat(future.get()).isEqualTo("success");
    }

    @Test
    public void testFailedListenableFuture() throws Exception {
        ThriftFuturesTest.assumeUnshadedGuava();
        final ThriftListenableFuture<String> future = ThriftFutures.failedListenableFuture(new IllegalStateException());
        assertThat(catchThrowable(future::get)).hasCauseInstanceOf(IllegalStateException.class);
    }
}

