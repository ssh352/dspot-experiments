/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.merge;


import AbstractContainerCollector.EmptyIterator;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractContainerCollectorTest extends HazelcastTestSupport {
    private NodeEngineImpl nodeEngine;

    @Test
    public void testAbstractContainerCollector() {
        TestContainerCollector collector = new TestContainerCollector(nodeEngine, true, true);
        HazelcastTestSupport.assertEqualsStringFormat("Expected the to have %d containers, but found %d", 1, collector.containers.size());
        run();
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d merging values, but found %d", 1, collector.getMergingValueCount());
        Assert.assertEquals("Expected the collected containers to be removed from the container map", 0, collector.containers.size());
    }

    @Test
    public void testAbstractContainerCollector_withoutContainers() {
        TestContainerCollector collector = new TestContainerCollector(nodeEngine, false, true);
        HazelcastTestSupport.assertEqualsStringFormat("Expected the to have %d containers, but found %d", 0, collector.containers.size());
        run();
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d merging values, but found %d", 0, collector.getMergingValueCount());
        Assert.assertEquals("Expected the collected containers to be removed from the container map", 0, collector.containers.size());
    }

    @Test
    public void testAbstractContainerCollector_withoutMergeableContainers() {
        TestContainerCollector collector = new TestContainerCollector(nodeEngine, true, false);
        HazelcastTestSupport.assertEqualsStringFormat("Expected the to have %d containers, but found %d", 1, collector.containers.size());
        run();
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d merging values, but found %d", 0, collector.getMergingValueCount());
        Assert.assertEquals("Expected the collected containers to be removed from the container map", 0, collector.containers.size());
    }

    @Test
    public void testEmptyIterator() {
        TestContainerCollector collector = new TestContainerCollector(nodeEngine, false, false);
        Iterator<Object> iterator = collector.containerIterator(0);
        HazelcastTestSupport.assertInstanceOf(EmptyIterator.class, iterator);
        Assert.assertFalse("Expected no next elements in iterator", iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Expected EmptyIterator.next() to throw NoSuchElementException");
        } catch (NoSuchElementException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        try {
            iterator.remove();
            Assert.fail("Expected EmptyIterator.remove() to throw UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            HazelcastTestSupport.ignore(expected);
        }
    }
}
