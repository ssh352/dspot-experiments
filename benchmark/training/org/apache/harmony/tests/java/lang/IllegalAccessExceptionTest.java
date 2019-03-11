/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.lang;


import junit.framework.TestCase;


public class IllegalAccessExceptionTest extends TestCase {
    /**
     * java.lang.IllegalAccessException#IllegalAccessException()
     */
    public void test_Constructor() {
        IllegalAccessException e = new IllegalAccessException();
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getLocalizedMessage());
        TestCase.assertNull(e.getCause());
    }

    /**
     * java.lang.IllegalAccessException#IllegalAccessException(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        IllegalAccessException e = new IllegalAccessException("fixture");
        TestCase.assertEquals("fixture", e.getMessage());
        TestCase.assertNull(e.getCause());
    }
}

