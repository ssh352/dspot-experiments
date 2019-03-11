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
package org.assertj.core.navigation;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.StringAssert;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class ClassBasedNavigableList_withString_Test {
    @Test
    public void should_navigate_to_list_elements_and_perform_specific_string_assertions() {
        List<String> list = Lists.newArrayList("one", "two", "three");
        Assertions.assertThat(list, StringAssert.class).first().startsWith("o");
        Assertions.assertThat(list, StringAssert.class).last().endsWith("ee");
        Assertions.assertThat(list, StringAssert.class).element(1).contains("w");
    }

    @Test
    public void should_honor_list_assertions() {
        List<String> list = Lists.newArrayList("one", "two", "three");
        Assertions.assertThat(list, StringAssert.class).contains("one", Assertions.atIndex(0)).first().startsWith("o");
    }
}

