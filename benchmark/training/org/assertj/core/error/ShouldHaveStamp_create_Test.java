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
package org.assertj.core.error;


import java.util.concurrent.atomic.AtomicStampedReference;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.TestDescription;
import org.junit.jupiter.api.Test;


public class ShouldHaveStamp_create_Test {
    @Test
    public void should_create_error_message() {
        // GIVEN
        AtomicStampedReference<String> actual = new AtomicStampedReference<>("foo", 1234);
        // WHEN
        String message = ShouldHaveStamp.shouldHaveStamp(actual, 5678).create(new TestDescription("TEST"), CONFIGURATION_PROVIDER.representation());
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format(("[TEST] %n" + ((((("Expecting%n" + "  <AtomicStampedReference[stamp=1234, reference=\"foo\"]>%n") + "to have stamp:%n") + "  <5678>%n") + "but had:%n") + "  <1234>"))));
    }
}

