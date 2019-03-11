/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link SessionInfo}.
 */
public final class SessionInfoTest {
    private static final int MIN_LEN = 1;

    private static final int MAX_LEN = 1000;

    private static final int DELTA = 50;

    private static final int SESSION_TIMEOUT_MS = 1000;

    /**
     * Tests the {@link SessionInfo#SessionInfo(long, int)} constructor.
     */
    @Test
    public void constructor() {
        for (int k = SessionInfoTest.MIN_LEN; k <= (SessionInfoTest.MAX_LEN); k += SessionInfoTest.DELTA) {
            SessionInfo tSessionInfo = new SessionInfo(k, SessionInfoTest.SESSION_TIMEOUT_MS);
            Assert.assertEquals(k, tSessionInfo.getSessionId());
        }
    }

    /**
     * Tests that an exception is thrown in the {@link SessionInfo#SessionInfo(long, int)} constructor
     * when using an invalid id for the session.
     */
    @Test(expected = RuntimeException.class)
    public void constructorWithException() {
        for (int k = 0; k >= (-1000); k -= SessionInfoTest.DELTA) {
            SessionInfo tSessionInfo = new SessionInfo(k, SessionInfoTest.SESSION_TIMEOUT_MS);
            Assert.assertEquals(k, tSessionInfo.getSessionId());
            Assert.fail((("SessionId " + k) + " should be invalid."));
        }
    }

    /**
     * Tests the {@link SessionInfo#getSessionId()} method.
     */
    @Test
    public void getSessionId() {
        for (int k = SessionInfoTest.MIN_LEN; k < (SessionInfoTest.MAX_LEN); k += 66) {
            SessionInfo tSessionInfo = new SessionInfo(k, SessionInfoTest.SESSION_TIMEOUT_MS);
            Assert.assertEquals(k, tSessionInfo.getSessionId());
        }
    }

    /**
     * Tests the {@link SessionInfo#timeout()} method.
     */
    @Test
    public void timeout() {
        SessionInfo tSessionInfo = new SessionInfo(1, SessionInfoTest.SESSION_TIMEOUT_MS);
        Assert.assertFalse(tSessionInfo.timeout());
    }
}

