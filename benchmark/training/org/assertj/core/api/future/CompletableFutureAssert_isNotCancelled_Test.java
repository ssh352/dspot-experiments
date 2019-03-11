/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.future;


import java.util.concurrent.CompletableFuture;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.BaseTest;
import org.assertj.core.error.future.ShouldNotBeCancelled;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class CompletableFutureAssert_isNotCancelled_Test extends BaseTest {
    @Test
    public void should_pass_if_completable_future_is_cancelled() {
        CompletableFuture<String> future = new CompletableFuture<>();
        Assertions.assertThat(future).isNotCancelled();
    }

    @Test
    public void should_fail_when_completable_future_is_null() {
        AssertionsForClassTypes.assertThatThrownBy(() -> assertThat(((CompletableFuture<String>) (null))).isNotCancelled()).isInstanceOf(AssertionError.class).hasMessage(String.format(FailureMessages.actualIsNull()));
    }

    @Test
    public void should_fail_if_completable_future_is_not_cancelled() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.cancel(true);
        AssertionsForClassTypes.assertThatThrownBy(() -> assertThat(future).isNotCancelled()).isInstanceOf(AssertionError.class).hasMessage(ShouldNotBeCancelled.shouldNotBeCancelled(future).create());
    }
}

