/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.binding;


public class AmplWrongMapperTest {
    @org.junit.Test(expected = java.lang.RuntimeException.class)
    public void shouldFailForBothOneAndMany() throws java.lang.Exception {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.addMapper(org.apache.ibatis.binding.MapperWithOneAndMany.class);
    }

    /* amplification of org.apache.ibatis.binding.WrongMapperTest#shouldFailForBothOneAndMany */
    @org.junit.Test(timeout = 10000)
    public void shouldFailForBothOneAndMany_add1_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            // MethodCallAdder
            configuration.addMapper(org.apache.ibatis.binding.MapperWithOneAndMany.class);
            configuration.addMapper(org.apache.ibatis.binding.MapperWithOneAndMany.class);
            org.junit.Assert.fail("shouldFailForBothOneAndMany_add1 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }
}

