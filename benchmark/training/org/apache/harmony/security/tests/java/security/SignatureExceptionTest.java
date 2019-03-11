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
/**
 *
 *
 * @author Vera Y. Petrashkova
 * @version $Revision$
 */
package org.apache.harmony.security.tests.java.security;


import java.security.SignatureException;
import junit.framework.TestCase;


/**
 * Tests for <code>SignatureException</code> class constructors and methods.
 */
public class SignatureExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>SignatureException()</code> constructor Assertion:
     * constructs SignatureException with no detail message
     */
    public void testSignatureException01() {
        SignatureException tE = new SignatureException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>SignatureException(String)</code> constructor Assertion:
     * constructs SignatureException with detail message msg. Parameter
     * <code>msg</code> is not null.
     */
    public void testSignatureException02() {
        SignatureException tE;
        for (int i = 0; i < (SignatureExceptionTest.msgs.length); i++) {
            tE = new SignatureException(SignatureExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(SignatureExceptionTest.msgs[i]), tE.getMessage(), SignatureExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>SignatureException(String)</code> constructor Assertion:
     * constructs SignatureException when <code>msg</code> is null
     */
    public void testSignatureException03() {
        String msg = null;
        SignatureException tE = new SignatureException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>SignatureException(Throwable)</code> constructor
     * Assertion: constructs SignatureException when <code>cause</code> is
     * null
     */
    public void testSignatureException04() {
        Throwable cause = null;
        SignatureException tE = new SignatureException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>SignatureException(Throwable)</code> constructor
     * Assertion: constructs SignatureException when <code>cause</code> is not
     * null
     */
    public void testSignatureException05() {
        SignatureException tE = new SignatureException(SignatureExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = SignatureExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(SignatureExceptionTest.tCause.toString()), tE.getCause(), SignatureExceptionTest.tCause);
    }

    /**
     * Test for <code>SignatureException(String, Throwable)</code> constructor
     * Assertion: constructs SignatureException when <code>cause</code> is
     * null <code>msg</code> is null
     */
    public void testSignatureException06() {
        SignatureException tE = new SignatureException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>SignatureException(String, Throwable)</code> constructor
     * Assertion: constructs SignatureException when <code>cause</code> is
     * null <code>msg</code> is not null
     */
    public void testSignatureException07() {
        SignatureException tE;
        for (int i = 0; i < (SignatureExceptionTest.msgs.length); i++) {
            tE = new SignatureException(SignatureExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(SignatureExceptionTest.msgs[i]), tE.getMessage(), SignatureExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>SignatureException(String, Throwable)</code> constructor
     * Assertion: constructs SignatureException when <code>cause</code> is not
     * null <code>msg</code> is null
     */
    public void testSignatureException08() {
        SignatureException tE = new SignatureException(null, SignatureExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = SignatureExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(SignatureExceptionTest.tCause.toString()), tE.getCause(), SignatureExceptionTest.tCause);
    }

    /**
     * Test for <code>SignatureException(String, Throwable)</code> constructor
     * Assertion: constructs SignatureException when <code>cause</code> is not
     * null <code>msg</code> is not null
     */
    public void testSignatureException09() {
        SignatureException tE;
        for (int i = 0; i < (SignatureExceptionTest.msgs.length); i++) {
            tE = new SignatureException(SignatureExceptionTest.msgs[i], SignatureExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = SignatureExceptionTest.tCause.toString();
            if ((SignatureExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(SignatureExceptionTest.msgs[i]), ((getM.indexOf(SignatureExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(SignatureExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(SignatureExceptionTest.tCause.toString()), tE.getCause(), SignatureExceptionTest.tCause);
        }
    }
}

