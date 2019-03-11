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
package org.assertj.core.internal;


import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class RecursiveFieldByFieldComparator_toString_Test {
    @Test
    @SuppressWarnings("unchecked")
    public void should_return_description_of_RecursiveFieldByFieldComparator() {
        // GIVEN
        RecursiveFieldByFieldComparator actual = new RecursiveFieldByFieldComparator(Collections.EMPTY_MAP, new TypeComparators());
        // THEN
        Assertions.assertThat(actual).hasToString(String.format(("recursive field/property by field/property comparator on all fields/properties%n" + ("Comparators used:%n" + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}"))));
    }
}

