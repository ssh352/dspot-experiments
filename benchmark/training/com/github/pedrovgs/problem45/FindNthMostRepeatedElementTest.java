/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs.problem45;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class FindNthMostRepeatedElementTest {
    private FindNthMostRepeatedElement findNthMostRepeatedElement;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArrays() {
        findNthMostRepeatedElement.find(null, 9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptInputValuesMinorThanZero() {
        findNthMostRepeatedElement.find(new int[0], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfThereAreNoElementsRepeatedNTimesInTheArrayPassedAsArgument() {
        int[] numbers = new int[]{ 1, 2, 3 };
        findNthMostRepeatedElement.find(numbers, 2);
    }

    @Test
    public void shouldFindNthMostRepeatedElement() {
        int[] numbers = new int[]{ 1, 1, 2, 3, 4, 5, 2, 2, 2, 4, 4, 6, 7, 4, 9, 214, 4, 5 };
        int result = findNthMostRepeatedElement.find(numbers, 2);
        Assert.assertEquals(1, result);
    }
}
