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
package libcore.java.lang;


import junit.framework.TestCase;


public class OldThrowableTest extends TestCase {
    public void test_ConstructorLStringLThrowable() {
        String message = "Test message";
        NullPointerException npe = new NullPointerException();
        Throwable thr = new Throwable(message, npe);
        TestCase.assertEquals("message is incorrect.", message, thr.getMessage());
        TestCase.assertEquals("cause is incorrect.", npe, thr.getCause());
        thr = new Throwable(null, npe);
        TestCase.assertNull("message is not null.", thr.getMessage());
        TestCase.assertEquals("cause is incorrect.", npe, thr.getCause());
        thr = new Throwable(message, null);
        TestCase.assertEquals("message is incorrect.", message, thr.getMessage());
        TestCase.assertNull("cause is not null.", thr.getCause());
    }

    public void test_ConstructorLThrowable() {
        NullPointerException npe = new NullPointerException();
        Throwable thr = new Throwable(npe);
        TestCase.assertEquals("Returned cause is incorrect.", npe, thr.getCause());
        thr = new Throwable(((Throwable) (null)));
        TestCase.assertNull("The cause is not null.", thr.getCause());
    }

    public void test_getLocalizedMessage() {
        String testMessage = "Test message";
        Throwable e = new Throwable(testMessage);
        TestCase.assertEquals("Returned incorrect localized message.", testMessage, e.getLocalizedMessage());
        OldThrowableTest.TestThrowable tt = new OldThrowableTest.TestThrowable(testMessage);
        TestCase.assertEquals("localized message", tt.getLocalizedMessage());
    }

    class TestThrowable extends Throwable {
        public TestThrowable(String message) {
            super(message);
        }

        public String getLocalizedMessage() {
            return "localized message";
        }
    }

    public void test_getStackTrace() {
        String message = "Test message";
        NullPointerException npe = new NullPointerException();
        Throwable thr = new Throwable(message, npe);
        StackTraceElement[] ste = thr.getStackTrace();
        TestCase.assertNotNull("Returned stack trace is empty", ((ste.length) != 0));
    }

    public void test_initCause() {
        String message = "Test message";
        NullPointerException npe = new NullPointerException();
        IllegalArgumentException iae = new IllegalArgumentException();
        Throwable thr = new Throwable();
        thr.initCause(iae);
        TestCase.assertEquals("getCause returns incorrect cause.", iae, thr.getCause());
        thr = new Throwable("message");
        thr.initCause(npe);
        TestCase.assertEquals("getCause returns incorrect cause.", npe, thr.getCause());
        thr = new Throwable(message, npe);
        try {
            thr.initCause(iae);
            TestCase.fail("IllegalStateException was not thrown.");
        } catch (IllegalStateException ise) {
            // expected
        }
        thr = new Throwable(npe);
        try {
            thr.initCause(iae);
            TestCase.fail("IllegalStateException was not thrown.");
        } catch (IllegalStateException ise) {
            // expected
        }
        thr = new Throwable();
        try {
            thr.initCause(thr);
            TestCase.fail("IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException ise) {
            // expected
        }
    }

    public void test_setStackTrace() {
        NullPointerException npe = new NullPointerException();
        Throwable thr = new Throwable(npe);
        StackTraceElement[] ste = thr.getStackTrace();
        Throwable thr1 = new Throwable(npe);
        thr1.setStackTrace(ste);
        TestCase.assertEquals(ste.length, thr1.getStackTrace().length);
        try {
            thr.setStackTrace(null);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException np) {
            // expected
        }
    }
}

