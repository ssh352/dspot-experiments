/**
 * Copyright (C) 2011 The Android Open Source Project
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


public class BooleanTest extends TestCase {
    public void test_TRUE() {
        TestCase.assertSame(Boolean.TRUE, Boolean.valueOf(true));
        TestCase.assertTrue(Boolean.TRUE.booleanValue());
    }

    public void test_FALSE() {
        TestCase.assertSame(Boolean.FALSE, Boolean.valueOf(false));
        TestCase.assertFalse(Boolean.FALSE.booleanValue());
    }

    public void test_compare() throws Exception {
        TestCase.assertEquals(0, Boolean.compare(true, true));
        TestCase.assertEquals(0, Boolean.compare(false, false));
        TestCase.assertTrue(((Boolean.compare(false, true)) < 0));
        TestCase.assertTrue(((Boolean.compare(true, false)) > 0));
    }
}

