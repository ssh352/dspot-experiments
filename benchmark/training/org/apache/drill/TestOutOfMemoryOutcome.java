/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill;


import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Run several tpch queries and inject an OutOfMemoryException in ScanBatch that will cause an OUT_OF_MEMORY outcome to
 * be propagated downstream. Make sure the proper "memory error" message is sent to the client.
 */
@Category({ UnlikelyTest.class })
public class TestOutOfMemoryOutcome extends BaseTestQuery {
    private static final String SINGLE_MODE = "ALTER SESSION SET `planner.disable_exchanges` = true";

    @Test
    public void tpch01() throws Exception {
        testSingleMode("queries/tpch/01.sql");
    }

    @Test
    public void tpch03() throws Exception {
        testSingleMode("queries/tpch/03.sql");
    }

    @Test
    public void tpch04() throws Exception {
        testSingleMode("queries/tpch/04.sql");
    }

    @Test
    public void tpch05() throws Exception {
        testSingleMode("queries/tpch/05.sql");
    }

    @Test
    public void tpch06() throws Exception {
        testSingleMode("queries/tpch/06.sql");
    }

    @Test
    public void tpch07() throws Exception {
        testSingleMode("queries/tpch/07.sql");
    }

    @Test
    public void tpch08() throws Exception {
        testSingleMode("queries/tpch/08.sql");
    }

    @Test
    public void tpch09() throws Exception {
        testSingleMode("queries/tpch/09.sql");
    }

    @Test
    public void tpch10() throws Exception {
        testSingleMode("queries/tpch/10.sql");
    }

    @Test
    public void tpch12() throws Exception {
        testSingleMode("queries/tpch/12.sql");
    }

    @Test
    public void tpch13() throws Exception {
        testSingleMode("queries/tpch/13.sql");
    }

    @Test
    public void tpch14() throws Exception {
        testSingleMode("queries/tpch/14.sql");
    }

    @Test
    public void tpch18() throws Exception {
        testSingleMode("queries/tpch/18.sql");
    }

    @Test
    public void tpch20() throws Exception {
        testSingleMode("queries/tpch/20.sql");
    }
}

