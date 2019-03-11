/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang.reflect;


import java.lang.reflect.Array;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ArrayTest {
    @Test
    public void createsNewInstance() {
        Object instance = Array.newInstance(Object.class, 10);
        Assert.assertEquals(Object[].class, instance.getClass());
        Assert.assertEquals(10, Array.getLength(instance));
    }

    @Test
    public void createsNewPrimitiveInstance() {
        Object instance = Array.newInstance(int.class, 15);
        Assert.assertEquals(int[].class, instance.getClass());
        Assert.assertEquals(15, Array.getLength(instance));
    }

    @Test
    public void setWorks() {
        Object array = Array.newInstance(String.class, 2);
        Array.set(array, 0, "foo");
        Array.set(array, 1, "bar");
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, ((String[]) (array)));
    }

    @Test
    public void setPrimitiveWorks() {
        Object array = Array.newInstance(int.class, 2);
        Array.set(array, 0, 23);
        Array.set(array, 1, 42);
        Assert.assertArrayEquals(new int[]{ 23, 42 }, ((int[]) (array)));
    }
}

