/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.test;


import com.gs.collections.impl.utility.Iterate;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public interface MutableUnorderedIterableTestCase extends UnorderedIterableTestCase {
    @Test
    @Override
    default void Iterable_remove() {
        Iterable<Integer> iterable = newWith(3, 3, 3, 2, 2, 1);
        Iterator<Integer> iterator = iterable.iterator();
        iterator.next();
        iterator.remove();
        IterableTestCase.assertEquals((this.allowsDuplicates() ? 5 : 2), Iterate.sizeOf(iterable));
        Assert.assertThat(iterable, Matchers.isOneOf(this.newWith(3, 3, 3, 2, 2), this.newWith(3, 3, 3, 2, 1), this.newWith(3, 3, 2, 2, 1)));
    }
}
