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
package org.assertj.core.api.floatarray;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.FloatArrayAssertBaseTest;
import org.assertj.core.data.Index;
import org.assertj.core.test.FloatArrays;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link FloatArrayAssert#doesNotContain(float, Index)}</code>.
 *
 * @author Alex Ruiz
 */
public class FloatArrayAssert_doesNotContain_at_Index_Test extends FloatArrayAssertBaseTest {
    private final Index index = TestData.someIndex();

    @Test
    public void should_pass_with_precision_specified_as_last_argument() {
        // GIVEN
        float[] actual = FloatArrays.arrayOf(1.0F, 2.0F);
        // THEN
        Assertions.assertThat(actual).doesNotContain(1.01F, Assertions.atIndex(0), Assertions.withPrecision(1.0E-4F));
    }

    @Test
    public void should_pass_with_precision_specified_in_comparator() {
        // GIVEN
        float[] actual = FloatArrays.arrayOf(1.0F, 2.0F);
        // THEN
        Assertions.assertThat(actual).usingComparatorWithPrecision(0.1F).doesNotContain(2.2F, Assertions.atIndex(1));
    }
}

