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
package org.assertj.core.api.map;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.MapAssertBaseTest;
import org.assertj.core.data.MapEntry;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.api.MapAssert#containsOnly(org.assertj.core.data.MapEntry...)}</code>.
 *
 * @author Jean-Christophe Gay
 */
public class MapAssert_containsOnly_Test extends MapAssertBaseTest {
    final MapEntry<String, String>[] entries = Arrays.array(MapEntry.entry("key1", "value1"), MapEntry.entry("key2", "value2"));

    @Test
    public void invoke_api_like_user() {
        Assertions.assertThat(map("key1", "value1", "key2", "value2")).containsOnly(MapEntry.entry("key1", "value1"), MapEntry.entry("key2", "value2"));
    }
}
