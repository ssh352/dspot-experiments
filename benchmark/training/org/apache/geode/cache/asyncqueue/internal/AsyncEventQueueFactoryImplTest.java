/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.cache.asyncqueue.internal;


import org.apache.geode.cache.asyncqueue.AsyncEventQueueFactory;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.test.junit.categories.AEQTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Extracted from AsyncEventListenerDistributedTest.
 */
@Category(AEQTest.class)
public class AsyncEventQueueFactoryImplTest {
    private InternalCache cache;

    private AsyncEventQueueFactory asyncEventQueueFactory;

    /**
     * Test to verify that AsyncEventQueue can not be created when null listener is passed.
     */
    @Test
    public void testCreateAsyncEventQueueWithNullListener() {
        asyncEventQueueFactory = new AsyncEventQueueFactoryImpl(cache);
        assertThatThrownBy(() -> asyncEventQueueFactory.create("id", null)).isInstanceOf(IllegalArgumentException.class);
    }
}

