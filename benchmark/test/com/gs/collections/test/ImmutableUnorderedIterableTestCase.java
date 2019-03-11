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


import com.gs.collections.impl.test.Verify;
import java.util.Iterator;
import org.junit.Test;


public interface ImmutableUnorderedIterableTestCase extends UnorderedIterableTestCase {
    @Override
    @Test
    default void Iterable_remove() {
        Iterator<Integer> iterator = this.newWith(3, 2, 1).iterator();
        iterator.next();
        Verify.assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}

