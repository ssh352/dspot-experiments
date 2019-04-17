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


import java.util.LinkedHashSet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Sets#newLinkedHashSet()}.
 *
 * @author Christian R?sch
 */
public class Sets_newLinkedHashSet_Test {
    @Test
    public void should_return_empty_mutable_Set() {
        LinkedHashSet<Object> set = Sets.newLinkedHashSet();
        Assertions.assertThat(set).isEmpty();
        set.add("element");
        Assertions.assertThat(set).containsExactly("element");
    }

    @Test
    public void should_return_new_HashSet() {
        LinkedHashSet<Object> set1 = Sets.newLinkedHashSet();
        LinkedHashSet<Object> set2 = Sets.newLinkedHashSet();
        Assertions.assertThat(set2).isNotSameAs(set1);
        // be sure they have nothing in common
        set1.add("element");
        Assertions.assertThat(set2).isEmpty();
    }
}
