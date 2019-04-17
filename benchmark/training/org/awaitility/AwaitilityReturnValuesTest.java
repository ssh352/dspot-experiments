/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.awaitility;


import java.util.concurrent.Callable;
import org.awaitility.classes.Asynch;
import org.awaitility.classes.FakeRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AwaitilityReturnValuesTest {
    private FakeRepository fakeRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 2000)
    public void returnsResultAfterSupplier() throws Exception {
        new Asynch(fakeRepository).perform();
        int value = Awaitility.await().until(new Callable<Integer>() {
            public Integer call() throws Exception {
                return fakeRepository.getValue();
            }
        }, Matchers.greaterThan(0));
        Assert.assertEquals(1, value);
    }

    @Test(timeout = 2000)
    public void returnsResultAfterFieldInSupplier() throws Exception {
        new Asynch(fakeRepository).perform();
        int value = Awaitility.await().until(Awaitility.fieldIn(fakeRepository).ofType(int.class).andWithName("value"), Matchers.equalTo(1));
        Assert.assertEquals(1, value);
    }
}
