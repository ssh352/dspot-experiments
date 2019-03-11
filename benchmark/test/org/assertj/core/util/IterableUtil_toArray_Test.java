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
package org.assertj.core.util;


import java.util.ArrayList;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link IterableUtil#toArray(Iterable)}</code>.
 *
 * @author Jean-Christophe Gay
 */
public class IterableUtil_toArray_Test {
    private final ArrayList<String> values = Lists.newArrayList("one", "two");

    @Test
    public void should_return_null_when_given_iterable_is_null() {
        Assertions.assertThat(IterableUtil.toArray(null)).isNull();
        Assertions.assertThat(IterableUtil.toArray(null, Object.class)).isNull();
    }

    @Test
    public void should_return_an_object_array_with_given_iterable_elements() {
        Object[] objects = IterableUtil.toArray(values);
        Assertions.assertThat(objects).containsExactly("one", "two");
        String[] strings = IterableUtil.toArray(values, String.class);
        Assertions.assertThat(strings).containsExactly("one", "two");
    }

    @Test
    public void should_return_empty_array_when_given_iterable_is_empty() {
        Assertions.assertThat(IterableUtil.toArray(Collections.emptyList())).isEmpty();
        Assertions.assertThat(IterableUtil.toArray(Collections.emptyList(), Object.class)).isEmpty();
    }

    @Test
    public void should_return_an_array_of_given_iterable_type_with_given_iterable_elements() {
        CharSequence[] result = IterableUtil.toArray(values, CharSequence.class);
        Assertions.assertThat(result).containsExactly("one", "two");
    }
}

