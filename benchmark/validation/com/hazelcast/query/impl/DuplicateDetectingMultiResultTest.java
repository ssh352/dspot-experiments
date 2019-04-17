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
package com.hazelcast.query.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DuplicateDetectingMultiResultTest {
    private DuplicateDetectingMultiResult result = new DuplicateDetectingMultiResult();

    @Test
    public void testAddResultSet_empty() throws Exception {
        MatcherAssert.assertThat(result.size(), Is.is(0));
    }

    @Test
    public void testContains_empty() throws Exception {
        MatcherAssert.assertThat(result.contains(entry(data())), Is.is(false));
    }

    @Test
    public void testIterator_empty() throws Exception {
        MatcherAssert.assertThat(result.iterator().hasNext(), Is.is(false));
    }

    @Test
    public void testSize_empty() throws Exception {
        MatcherAssert.assertThat(result.isEmpty(), Is.is(true));
    }

    @Test
    public void testAddResultSet_notEmpty() throws Exception {
        addEntry(entry(data()));
        MatcherAssert.assertThat(result.size(), Is.is(1));
    }

    @Test
    public void testContains_notEmpty() throws Exception {
        QueryableEntry entry = entry(data());
        addEntry(entry);
        MatcherAssert.assertThat(result.contains(entry), Is.is(true));
    }

    @Test
    public void testIterator_notEmpty() throws Exception {
        QueryableEntry entry = entry(data());
        addEntry(entry);
        MatcherAssert.assertThat(result.iterator().hasNext(), Is.is(true));
        MatcherAssert.assertThat(result.iterator().next(), Is.is(entry));
    }

    @Test
    public void testSize_notEmpty() throws Exception {
        addEntry(entry(data()));
        MatcherAssert.assertThat(result.isEmpty(), Is.is(false));
    }

    @Test
    public void testAddResultSet_duplicate() throws Exception {
        QueryableEntry entry = entry(data());
        addEntry(entry);
        addEntry(entry);
        MatcherAssert.assertThat(result.size(), Is.is(1));
    }

    @Test
    public void testContains_duplicate() throws Exception {
        QueryableEntry entry = entry(data());
        addEntry(entry);
        addEntry(entry);
        MatcherAssert.assertThat(result.contains(entry), Is.is(true));
    }

    @Test
    public void testIterator_duplicate() throws Exception {
        QueryableEntry entry = entry(data());
        addEntry(entry);
        addEntry(entry);
        MatcherAssert.assertThat(result.iterator().hasNext(), Is.is(true));
        MatcherAssert.assertThat(result.iterator().next(), Is.is(entry));
    }

    @Test
    public void testSize_duplicate() throws Exception {
        QueryableEntry entry = entry(data());
        addEntry(entry);
        addEntry(entry);
        MatcherAssert.assertThat(result.isEmpty(), Is.is(false));
    }
}
