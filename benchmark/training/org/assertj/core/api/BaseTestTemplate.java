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
package org.assertj.core.api;


import org.assertj.core.error.AssertionErrorCreator;
import org.assertj.core.internal.Conditions;
import org.assertj.core.internal.Objects;
import org.junit.jupiter.api.Test;


/**
 * Template to write tests for {@link AbstractAssert} implementations.
 *
 * <p>
 * These classes are simple wrapper types, that delegate the real work to internal objects. For each method, we only need to test
 * that:
 * <ul>
 * <li>invoking the method properly delegates to the relevant internal objects;</li>
 * <li>the method returns {@code this} (for assertion chaining).</li>
 * </ul>
 * This template factors most of the code to make the actual tests quick to write.
 * </p>
 * <p>
 * For each assertion class (e.g {@link BigDecimalAssert}), the template is specialized by a "base" class in the same package (
 * {@link BigDecimalAssertBaseTest}). To avoid cluttering the main package with hundreds of classes, the concrete tests reside in
 * a subpackage ({@link org.assertj.core.api.bigdecimal}). The base class also serves as a proxy to the package-private fields
 * of the assertion that need to be verified in the tests.
 * </p>
 *
 * @param <S>
 * 		the "self" type of the assertion under test.
 * @param <A>
 * 		the type of the "actual" value.
 * @author Olivier Michallat
 */
public abstract class BaseTestTemplate<S extends AbstractAssert<S, A>, A> extends BaseTest {
    protected S assertions;

    protected Objects objects;

    protected Conditions conditions;

    protected AssertionErrorCreator assertionErrorCreator;

    @Test
    public void should_have_internal_effects() {
        invoke_api_method();
        verify_internal_effects();
    }

    /**
     * For the few API methods that don't return {@code this}, override this method to do nothing (see
     * {@link AbstractAssert_isNull_Test#should_return_this()} for an example).
     */
    @Test
    public void should_return_this() {
        S returned = invoke_api_method();
        BaseTestTemplate.assertThat(returned).isSameAs(assertions);
    }
}
