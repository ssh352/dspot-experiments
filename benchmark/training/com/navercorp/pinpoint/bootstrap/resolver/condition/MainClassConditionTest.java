/**
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.resolver.condition;


import com.navercorp.pinpoint.common.util.SimpleProperty;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class MainClassConditionTest {
    private static final String TEST_MAIN_CLASS = "main.class.for.Test";

    @Test
    public void getValueShouldReturnBootstrapMainClass() {
        // Given
        SimpleProperty property = MainClassConditionTest.createTestProperty(MainClassConditionTest.TEST_MAIN_CLASS);
        MainClassCondition mainClassCondition = new MainClassCondition(property);
        // When
        String expectedMainClass = mainClassCondition.getValue();
        // Then
        Assert.assertEquals(MainClassConditionTest.TEST_MAIN_CLASS, expectedMainClass);
    }

    @Test
    public void getValueShouldReturnEmptyStringWhenMainClassCannotBeResolved() {
        // Given
        SimpleProperty property = MainClassConditionTest.createTestProperty();
        MainClassCondition mainClassCondition = new MainClassCondition(property);
        // When
        String expectedMainClass = mainClassCondition.getValue();
        // Then
        Assert.assertEquals("", expectedMainClass);
    }

    @Test
    public void testMatch() {
        // Given
        SimpleProperty property = MainClassConditionTest.createTestProperty(MainClassConditionTest.TEST_MAIN_CLASS);
        MainClassCondition mainClassCondition = new MainClassCondition(property);
        // When
        boolean matches = mainClassCondition.check(MainClassConditionTest.TEST_MAIN_CLASS);
        // Then
        Assert.assertTrue(matches);
    }

    @Test
    public void testNoMatch() {
        // Given
        String givenBootstrapMainClass = "some.other.main.class";
        SimpleProperty property = MainClassConditionTest.createTestProperty(givenBootstrapMainClass);
        MainClassCondition mainClassCondition = new MainClassCondition(property);
        // When
        boolean matches = mainClassCondition.check(MainClassConditionTest.TEST_MAIN_CLASS);
        // Then
        Assert.assertFalse(matches);
    }

    @Test
    public void nullConditionShouldNotMatch() {
        // Given
        SimpleProperty property = MainClassConditionTest.createTestProperty(MainClassConditionTest.TEST_MAIN_CLASS);
        MainClassCondition mainClassCondition = new MainClassCondition(property);
        // When
        boolean matches = mainClassCondition.check(null);
        // Then
        Assert.assertFalse(matches);
    }

    @Test
    public void shouldNotMatchWhenMainClassCannotBeResolved() {
        // Given
        SimpleProperty property = MainClassConditionTest.createTestProperty();
        MainClassCondition mainClassCondition = new MainClassCondition(property);
        // When
        boolean matches = mainClassCondition.check(null);
        // Then
        Assert.assertFalse(matches);
    }

    @Test
    public void shouldNotMatchWhenWhenJarFileCannotBeFound() {
        // Given
        SimpleProperty property = MainClassConditionTest.createTestProperty("non-existent-test-jar.jar");
        MainClassCondition mainClassCondition = new MainClassCondition(property);
        // When
        boolean matches = mainClassCondition.check(null);
        // Then
        Assert.assertFalse(matches);
    }
}
