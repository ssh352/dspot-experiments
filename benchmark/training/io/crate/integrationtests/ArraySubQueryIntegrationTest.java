/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.integrationtests;


import io.crate.testing.TestingHelpers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ArraySubQueryIntegrationTest extends SQLTransportIntegrationTest {
    private Setup setup = new Setup(sqlExecutor);

    @Test
    public void testSubQueryInSelectList() throws Exception {
        execute(("select mountain, height , " + (((("array(select height from  sys.summits where country = 'AT' order by height desc limit 5) as array_top5_at_mountains " + "from sys.summits ") + "where country = 'AT' ") + "order by height desc ") + "limit 5")));
        assertThat(TestingHelpers.printedTable(response.rows()), Matchers.is(("Gro\u00dfglockner| 3798| [3798, 3770, 3666, 3564, 3550]\n" + ((("Wildspitze| 3770| [3798, 3770, 3666, 3564, 3550]\n" + "Gro\u00dfvenediger| 3666| [3798, 3770, 3666, 3564, 3550]\n") + "Gro\u00dfes Wiesbachhorn| 3564| [3798, 3770, 3666, 3564, 3550]\n") + "Gro\u00dfer Ramolkogel| 3550| [3798, 3770, 3666, 3564, 3550]\n"))));
    }

    @Test
    public void testSubQueryInWhereClause() throws Exception {
        execute(("select mountain, height " + (((("from sys.summits " + "where country = 'AT' ") + "and [3798, 3770] = array(select height from sys.summits where country = 'AT' order by height desc limit 2) ") + "order by height desc ") + "limit 5")));
        assertThat(TestingHelpers.printedTable(response.rows()), Matchers.is(("Gro\u00dfglockner| 3798\n" + ((("Wildspitze| 3770\n" + "Gro\u00dfvenediger| 3666\n") + "Gro\u00dfes Wiesbachhorn| 3564\n") + "Gro\u00dfer Ramolkogel| 3550\n"))));
    }
}
