/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.inheritance;


import QInheritance11Test_Bar.bar;
import QInheritance11Test_BarBase.barBase;
import QInheritance11Test_Foo.foo;
import QInheritance11Test_FooBase.fooBase;
import com.querydsl.core.annotations.QueryEntity;
import org.junit.Assert;
import org.junit.Test;


public class Inheritance11Test {
    @QueryEntity
    public class Foo extends Inheritance11Test.FooBase<Inheritance11Test.Foo> {}

    @QueryEntity
    public class FooBase<T> {}

    @QueryEntity
    public class BarBase<T> {}

    @QueryEntity
    public class Bar extends Inheritance11Test.BarBase<Inheritance11Test.Foo> {}

    @Test
    public void test() {
        Assert.assertNotNull(foo);
        Assert.assertNotNull(fooBase);
        Assert.assertNotNull(bar);
        Assert.assertNotNull(barBase);
    }
}

