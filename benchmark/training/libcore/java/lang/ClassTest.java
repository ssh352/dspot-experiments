/**
 * Copyright (C) 2013 The Android Open Source Project
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
package libcore.java.lang;


import junit.framework.TestCase;


public class ClassTest extends TestCase {
    interface Foo {
        public void foo();
    }

    interface ParameterizedFoo<T> {
        public void foo(T param);
    }

    interface ParameterizedBar<T> extends ClassTest.ParameterizedFoo<T> {
        public void bar(T param);
    }

    interface ParameterizedBaz extends ClassTest.ParameterizedFoo<String> {}

    public void test_getGenericSuperclass_nullReturnCases() {
        // Should always return null for interfaces.
        TestCase.assertNull(ClassTest.Foo.class.getGenericSuperclass());
        TestCase.assertNull(ClassTest.ParameterizedFoo.class.getGenericSuperclass());
        TestCase.assertNull(ClassTest.ParameterizedBar.class.getGenericSuperclass());
        TestCase.assertNull(ClassTest.ParameterizedBaz.class.getGenericSuperclass());
        TestCase.assertNull(Object.class.getGenericSuperclass());
        TestCase.assertNull(void.class.getGenericSuperclass());
        TestCase.assertNull(int.class.getGenericSuperclass());
    }

    public void test_getGenericSuperclass_returnsObjectForArrays() {
        TestCase.assertSame(Object.class, new Integer[0].getClass().getGenericSuperclass());
    }
}

