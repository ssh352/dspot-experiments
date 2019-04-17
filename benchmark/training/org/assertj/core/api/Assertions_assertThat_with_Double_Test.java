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


import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Assertions#assertThat(Double)}</code>.
 *
 * @author Alex Ruiz
 */
public class Assertions_assertThat_with_Double_Test {
    @Test
    public void should_create_Assert() {
        Double zero = 0.0;
        AbstractDoubleAssert<?> assertions = Assertions.assertThat(zero);
        Assertions.assertThat(assertions).isNotNull();
    }

    @Test
    public void should_pass_actual() {
        Double eight = 8.0;
        AbstractDoubleAssert<?> assertions = Assertions.assertThat(eight);
        Assertions.assertThat(assertions.actual).isSameAs(eight);
    }
}
