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
package tests.api.javax.net.ssl;


import javax.net.ssl.SSLEngineResult;
import junit.framework.TestCase;

import static javax.net.ssl.SSLEngineResult.Status.valueOf;
import static javax.net.ssl.SSLEngineResult.Status.values;


/**
 * Tests for SSLEngineResult.Status class
 */
public class SSLEngineResultStatusTest extends TestCase {
    /**
     * Test for <code> SSLEngineResult.Status.values() </code>
     */
    public void test_SSLEngineResultStatus_values() {
        boolean flag = false;
        String[] str = new String[]{ "BUFFER_OVERFLOW", "BUFFER_UNDERFLOW", "CLOSED", "OK" };
        SSLEngineResult.Status[] enS = values();
        if ((enS.length) == (str.length)) {
            for (int i = 0; i < (enS.length); i++) {
                flag = false;
                for (int j = 0; j < (str.length); j++) {
                    // RoboVM note: Changed == to equals() here. The test sometimes failed with ==.
                    // There's nothing that guarantees that the same String instance will be used.
                    // RoboVM uses a cache of interned String instances. If too many other Strings
                    // have been interned between the load time of the SSLEngineResult.Status enum
                    // and this test is run the Strings in str will not be the same instances as
                    // those returned by toString() and == will fail.
                    if (enS[i].toString().equals(str[j])) {
                        flag = true;
                        break;
                    }
                }
                // RoboVM note: Moved this assert inside the for-loop. Otherwise the test will succeed
                // as long as the last value in enS can be found in str.
                TestCase.assertTrue("Incorrect Status", flag);
            }
        } else {
            TestCase.fail("Incorrect number of enum constant was returned");
        }
    }

    /**
     * Test for <code> SSLEngineResult.Status.valueOf(String name) </code>
     */
    public void test_SSLEngineResultStatus_valueOf() {
        String[] str = new String[]{ "BUFFER_OVERFLOW", "BUFFER_UNDERFLOW", "CLOSED", "OK" };
        String[] str_invalid = new String[]{ "", "OK1", "BUFFER_overflow", "BUFFER_UND", "CLOSED_CLOSED", "Bad string for verification valueOf method" };
        SSLEngineResult.Status enS;
        // Correct parameter
        for (int i = 0; i < (str.length); i++) {
            try {
                enS = valueOf(str[i]);
                TestCase.assertEquals("Incorrect Status", enS.toString(), str[i]);
            } catch (Exception e) {
                TestCase.fail(((("Unexpected exception " + e) + " was thrown for ") + (str[i])));
            }
        }
        // Incorrect parameter
        for (int i = 0; i < (str_invalid.length); i++) {
            try {
                enS = valueOf(str_invalid[i]);
                TestCase.fail(("IllegalArgumentException should be thrown for " + (str_invalid[i])));
            } catch (IllegalArgumentException iae) {
                // expected
            }
        }
        // Null parameter
        try {
            enS = valueOf(null);
            TestCase.fail("NullPointerException/IllegalArgumentException should be thrown for NULL parameter");
        } catch (NullPointerException npe) {
            // expected
        } catch (IllegalArgumentException iae) {
        }
    }
}

