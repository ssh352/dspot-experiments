/**
 * Copyright (C) 2007 The Android Open Source Project
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
package tests.api.javax.net.ssl;


import javax.net.ssl.SSLKeyException;
import junit.framework.TestCase;


public class SSLKeyExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    /**
     * Test for <code>SSLKeyException(String)</code> constructor Assertion:
     * constructs SSLKeyException with detail message msg. Parameter
     * <code>msg</code> is not null.
     */
    public void test_Constructor01() {
        SSLKeyException skE;
        for (int i = 0; i < (SSLKeyExceptionTest.msgs.length); i++) {
            skE = new SSLKeyException(SSLKeyExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(SSLKeyExceptionTest.msgs[i]), skE.getMessage(), SSLKeyExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", skE.getCause());
        }
    }

    /**
     * Test for <code>SSLPeerUnverifiedException(String)</code> constructor Assertion:
     * constructs SSLPeerUnverifiedException with detail message msg. Parameter
     * <code>msg</code> is null.
     */
    public void test_Constructor02() {
        String msg = null;
        SSLKeyException skE = new SSLKeyException(msg);
        TestCase.assertNull("getMessage() must return null.", skE.getMessage());
        TestCase.assertNull("getCause() must return null", skE.getCause());
    }
}

