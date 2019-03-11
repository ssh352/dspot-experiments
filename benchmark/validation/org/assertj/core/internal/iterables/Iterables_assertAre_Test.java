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
package org.assertj.core.internal.iterables;


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ElementsShouldBe;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.internal.IterablesWithConditionsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertAre(AssertionInfo, Iterable, org.assertj.core.api.Condition)}</code> .
 *
 * @author Nicolas Fran?ois
 * @author Mikhail Mazursky
 * @author Joel Costigliola
 */
public class Iterables_assertAre_Test extends IterablesWithConditionsBaseTest {
    @Test
    public void should_pass_if_each_element_satisfies_condition() {
        actual = Lists.newArrayList("Yoda", "Luke");
        iterables.assertAre(TestData.someInfo(), actual, jedi);
        Mockito.verify(conditions).assertIsNotNull(jedi);
    }

    @Test
    public void should_throw_error_if_condition_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            actual = newArrayList("Yoda", "Luke");
            iterables.assertAre(someInfo(), actual, null);
        }).withMessage("The condition to evaluate should not be null");
        Mockito.verify(conditions).assertIsNotNull(null);
    }

    @Test
    public void should_fail_if_condition_is_not_met() {
        testCondition.shouldMatch(false);
        AssertionInfo info = TestData.someInfo();
        try {
            actual = Lists.newArrayList("Yoda", "Luke", "Leia");
            iterables.assertAre(TestData.someInfo(), actual, jedi);
        } catch (AssertionError e) {
            Mockito.verify(conditions).assertIsNotNull(jedi);
            Mockito.verify(failures).failure(info, ElementsShouldBe.elementsShouldBe(actual, Lists.newArrayList("Leia"), jedi));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

