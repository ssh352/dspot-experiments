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
package org.assertj.core.api.atomic.long_;


import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;


public class AtomicLongAssert_isNull_Test {
    @Test
    public void should_be_able_to_use_isNull_assertion() {
        AtomicLong actual = null;
        Assertions.assertThat(actual).isNull();
        BDDAssertions.then(actual).isNull();
    }
}

