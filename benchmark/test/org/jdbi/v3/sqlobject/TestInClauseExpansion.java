/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.sqlobject;


import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestInClauseExpansion {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugins();// Guava


    private Handle handle;

    @Test
    public void testInClauseExpansion() {
        handle.execute("insert into something (name, id) values ('Brian', 1), ('Jeff', 2), ('Tom', 3)");
        TestInClauseExpansion.DAO dao = handle.attach(TestInClauseExpansion.DAO.class);
        assertThat(dao.findIdsForNames(Arrays.asList(1, 2))).containsExactly("Brian", "Jeff");
    }

    public interface DAO {
        @SqlQuery("select name from something where id in (<names>)")
        ImmutableSet<String> findIdsForNames(@BindList
        List<Integer> names);
    }
}

