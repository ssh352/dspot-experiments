/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache;


import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class ClientReconnectAfterClusterRestartTest extends GridCommonAbstractTest {
    /**
     * Server id.
     */
    private static final int SERVER_ID = 0;

    /**
     * Client id.
     */
    private static final int CLIENT_ID = 1;

    /**
     * Cache params.
     */
    private static final String CACHE_PARAMS = "PPRB_PARAMS";

    /**
     *
     */
    private int joinTimeout;

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testReconnectClient() throws Exception {
        checkReconnectClient();
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testReconnectClient10sTimeout() throws Exception {
        joinTimeout = 10000;
        checkReconnectClient();
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testReconnectClient2sTimeout() throws Exception {
        joinTimeout = 2000;
        checkReconnectClient();
    }
}
