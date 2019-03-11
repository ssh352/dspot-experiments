/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * The UnboundedSemaphoreTest.
 */
public class UnboundedSemaphoreTest {
    @Test
    public void release() throws Exception {
        UnboundedSemaphore semaphore;
        semaphore = new UnboundedSemaphore();
        MatcherAssert.assertThat(semaphore.release(Long.MAX_VALUE), Matchers.is(Long.MAX_VALUE));
        semaphore = new UnboundedSemaphore();
        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE / 2

        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE - 1

        MatcherAssert.assertThat(semaphore.availablePermits(), Matchers.is(((Long.MAX_VALUE) - 1)));
        semaphore = new UnboundedSemaphore();
        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE / 2

        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE - 1

        semaphore.release(1);
        MatcherAssert.assertThat(semaphore.availablePermits(), Matchers.is(Long.MAX_VALUE));
        semaphore = new UnboundedSemaphore();
        semaphore.release(5);
        semaphore.release(7);
        MatcherAssert.assertThat(semaphore.availablePermits(), Matchers.is(12L));
    }

    @Test
    public void releaseWithOverflow() throws Exception {
        UnboundedSemaphore semaphore;
        semaphore = new UnboundedSemaphore();
        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE / 2

        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE - 1

        semaphore.release(1);
        MatcherAssert.assertThat(semaphore.availablePermits(), Matchers.is(Long.MAX_VALUE));
        semaphore = new UnboundedSemaphore();
        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE / 2

        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE - 1

        semaphore.release(2);
        MatcherAssert.assertThat(semaphore.availablePermits(), Matchers.is(Long.MAX_VALUE));
        semaphore = new UnboundedSemaphore();
        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE / 2

        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE - 1

        semaphore.release(((Long.MAX_VALUE) / 2));
        MatcherAssert.assertThat(semaphore.availablePermits(), Matchers.is(Long.MAX_VALUE));
        semaphore = new UnboundedSemaphore();
        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE / 2

        semaphore.release(((Long.MAX_VALUE) / 2));// permits = Long.MAX_VALUE - 1

        semaphore.release(Long.MAX_VALUE);
        MatcherAssert.assertThat(semaphore.availablePermits(), Matchers.is(Long.MAX_VALUE));
        semaphore = new UnboundedSemaphore();
        semaphore.release(Long.MAX_VALUE);
        semaphore.release(Long.MAX_VALUE);
        MatcherAssert.assertThat(semaphore.availablePermits(), Matchers.is(Long.MAX_VALUE));
    }
}

