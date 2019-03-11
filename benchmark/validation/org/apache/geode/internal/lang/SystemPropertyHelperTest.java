/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.lang;


import org.junit.Test;


public class SystemPropertyHelperTest {
    private String restoreSetOperationTransactionBehavior = "restoreSetOperationTransactionBehavior";

    @Test
    public void testRestoreSetOperationTransactionBehaviorDefaultToFalse() {
        assertThat(SystemPropertyHelper.restoreSetOperationTransactionBehavior()).isFalse();
    }

    @Test
    public void testRestoreSetOperationTransactionBehaviorSystemProperty() {
        String gemfirePrefixProperty = "gemfire." + (restoreSetOperationTransactionBehavior);
        System.setProperty(gemfirePrefixProperty, "true");
        assertThat(SystemPropertyHelper.restoreSetOperationTransactionBehavior()).isTrue();
        System.clearProperty(gemfirePrefixProperty);
        String geodePrefixProperty = "geode." + (restoreSetOperationTransactionBehavior);
        System.setProperty(geodePrefixProperty, "true");
        assertThat(SystemPropertyHelper.restoreSetOperationTransactionBehavior()).isTrue();
        System.clearProperty(geodePrefixProperty);
    }

    @Test
    public void testRestoreSetOperationTransactionBehaviorGeodePreference() {
        String gemfirePrefixProperty = "gemfire." + (restoreSetOperationTransactionBehavior);
        String geodePrefixProperty = "geode." + (restoreSetOperationTransactionBehavior);
        System.setProperty(geodePrefixProperty, "false");
        System.setProperty(gemfirePrefixProperty, "true");
        assertThat(SystemPropertyHelper.restoreSetOperationTransactionBehavior()).isFalse();
        System.clearProperty(geodePrefixProperty);
        System.clearProperty(gemfirePrefixProperty);
    }

    @Test
    public void getIntegerPropertyPrefersGeodePrefix() {
        String testProperty = "testIntegerProperty";
        String gemfirePrefixProperty = "gemfire." + testProperty;
        String geodePrefixProperty = "geode." + testProperty;
        System.setProperty(geodePrefixProperty, "1");
        System.setProperty(gemfirePrefixProperty, "0");
        assertThat(SystemPropertyHelper.getProductIntegerProperty(testProperty).get()).isEqualTo(1);
        System.clearProperty(geodePrefixProperty);
        System.clearProperty(gemfirePrefixProperty);
    }

    @Test
    public void getIntegerPropertyReturnsGemfirePrefixIfGeodeMissing() {
        String testProperty = "testIntegerProperty";
        String gemfirePrefixProperty = "gemfire." + testProperty;
        System.setProperty(gemfirePrefixProperty, "1");
        assertThat(SystemPropertyHelper.getProductIntegerProperty(testProperty).get()).isEqualTo(1);
        System.clearProperty(gemfirePrefixProperty);
    }

    @Test
    public void getIntegerPropertyReturnsEmptyOptionalIfPropertiesMissing() {
        String testProperty = "notSetProperty";
        assertThat(SystemPropertyHelper.getProductIntegerProperty(testProperty).isPresent()).isFalse();
    }

    @Test
    public void getBooleanPropertyReturnsEmptyOptionalIfProperiesMissing() {
        String testProperty = "notSetProperty";
        assertThat(SystemPropertyHelper.getProductBooleanProperty(testProperty).isPresent()).isFalse();
    }

    @Test
    public void getBooleanPropertyPrefersGeodePrefix() {
        String testProperty = "testBooleanProperty";
        String gemfirePrefixProperty = "gemfire." + testProperty;
        String geodePrefixProperty = "geode." + testProperty;
        System.setProperty(geodePrefixProperty, "true");
        System.setProperty(gemfirePrefixProperty, "false");
        assertThat(SystemPropertyHelper.getProductBooleanProperty(testProperty).get()).isTrue();
        System.clearProperty(geodePrefixProperty);
        System.clearProperty(gemfirePrefixProperty);
    }

    @Test
    public void getBooleanPropertyReturnsGemfirePrefixIfGeodeMissing() {
        String testProperty = "testBooleanProperty";
        String gemfirePrefixProperty = "gemfire." + testProperty;
        System.setProperty(gemfirePrefixProperty, "true");
        assertThat(SystemPropertyHelper.getProductBooleanProperty(testProperty).get()).isTrue();
        System.clearProperty(gemfirePrefixProperty);
    }
}

