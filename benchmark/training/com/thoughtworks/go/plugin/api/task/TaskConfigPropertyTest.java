/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.api.task;


import Property.REQUIRED;
import Property.SECURE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TaskConfigPropertyTest {
    @Test
    public void validateTaskPropertyDefaults() throws Exception {
        TaskConfigProperty taskConfigProperty = new TaskConfigProperty("Test-Property");
        Assert.assertThat(taskConfigProperty.getOptions().size(), Matchers.is(4));
        Assert.assertThat(taskConfigProperty.getOption(REQUIRED), Matchers.is(false));
        Assert.assertThat(taskConfigProperty.getOption(SECURE), Matchers.is(false));
        taskConfigProperty = new TaskConfigProperty("Test-Property", "Dummy Value");
        taskConfigProperty.with(REQUIRED, true);
        Assert.assertThat(taskConfigProperty.getOptions().size(), Matchers.is(4));
        Assert.assertThat(taskConfigProperty.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(taskConfigProperty.getOption(SECURE), Matchers.is(false));
    }

    @Test
    public void shouldAssignDefaults() {
        final TaskConfigProperty property = new TaskConfigProperty("key");
        Assert.assertThat(property.getOption(property.REQUIRED), Matchers.is(false));
        Assert.assertThat(property.getOption(property.SECURE), Matchers.is(false));
        Assert.assertThat(property.getOption(property.DISPLAY_NAME), Matchers.is("key"));
        Assert.assertThat(property.getOption(property.DISPLAY_ORDER), Matchers.is(0));
    }

    @Test
    public void shouldCompareTwoPropertiesBasedOnOrder() {
        TaskConfigProperty p1 = getTaskConfigProperty("Test-Property", 1);
        TaskConfigProperty p2 = getTaskConfigProperty("Test-Property", 0);
        Assert.assertThat(p1.compareTo(p2), Matchers.is(1));
    }
}
