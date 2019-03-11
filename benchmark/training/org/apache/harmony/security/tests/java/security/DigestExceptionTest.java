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


import java.security.DigestException;
import junit.framework.TestCase;


/**
 * Tests for <code>DigestException</code> class constructors and methods.
 */
public class DigestExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>DigestException()</code> constructor Assertion:
     * constructs DigestException with no detail message
     */
    public void testDigestException01() {
        DigestException tE = new DigestException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>DigestException(String)</code> constructor Assertion:
     * constructs DigestException with detail message msg. Parameter
     * <code>msg</code> is not null.
     */
    public void testDigestException02() {
        DigestException tE;
        for (int i = 0; i < (DigestExceptionTest.msgs.length); i++) {
            tE = new DigestException(DigestExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(DigestExceptionTest.msgs[i]), tE.getMessage(), DigestExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>DigestException(String)</code> constructor Assertion:
     * constructs DigestException when <code>msg</code> is null
     */
    public void testDigestException03() {
        String msg = null;
        DigestException tE = new DigestException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>DigestException(Throwable)</code> constructor Assertion:
     * constructs DigestException when <code>cause</code> is null
     */
    public void testDigestException04() {
        Throwable cause = null;
        DigestException tE = new DigestException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>DigestException(Throwable)</code> constructor Assertion:
     * constructs DigestException when <code>cause</code> is not null
     */
    public void testDigestException05() {
        DigestException tE = new DigestException(DigestExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = DigestExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(DigestExceptionTest.tCause.toString()), tE.getCause(), DigestExceptionTest.tCause);
    }

    /**
     * Test for <code>DigestException(String, Throwable)</code> constructor
     * Assertion: constructs DigestException when <code>cause</code> is null
     * <code>msg</code> is null
     */
    public void testDigestException06() {
        DigestException tE = new DigestException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>DigestException(String, Throwable)</code> constructor
     * Assertion: constructs DigestException when <code>cause</code> is null
     * <code>msg</code> is not null
     */
    public void testDigestException07() {
        DigestException tE;
        for (int i = 0; i < (DigestExceptionTest.msgs.length); i++) {
            tE = new DigestException(DigestExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(DigestExceptionTest.msgs[i]), tE.getMessage(), DigestExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>DigestException(String, Throwable)</code> constructor
     * Assertion: constructs DigestException when <code>cause</code> is not
     * null <code>msg</code> is null
     */
    public void testDigestException08() {
        DigestException tE = new DigestException(null, DigestExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = DigestExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(DigestExceptionTest.tCause.toString()), tE.getCause(), DigestExceptionTest.tCause);
    }

    /**
     * Test for <code>DigestException(String, Throwable)</code> constructor
     * Assertion: constructs DigestException when <code>cause</code> is not
     * null <code>msg</code> is not null
     */
    public void testDigestException09() {
        DigestException tE;
        for (int i = 0; i < (DigestExceptionTest.msgs.length); i++) {
            tE = new DigestException(DigestExceptionTest.msgs[i], DigestExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = DigestExceptionTest.tCause.toString();
            if ((DigestExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(DigestExceptionTest.msgs[i]), ((getM.indexOf(DigestExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(DigestExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(DigestExceptionTest.tCause.toString()), tE.getCause(), DigestExceptionTest.tCause);
        }
    }
}

