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


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.customizer.BindBeanList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.stringtemplate4.UseStringTemplateEngine;
import org.junit.Rule;
import org.junit.Test;


public class BindBeanListTest {
    private Handle handle;

    private List<Something> expectedSomethings;

    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething();

    // 
    @Test
    public void testSomethingWithExplicitAttributeName() {
        final BindBeanListTest.SomethingWithExplicitAttributeName s = handle.attach(BindBeanListTest.SomethingWithExplicitAttributeName.class);
        final List<Something> out = s.get(new BindBeanListTest.SomethingKey(1, "1"), new BindBeanListTest.SomethingKey(2, "2"));
        assertThat(out).hasSameElementsAs(expectedSomethings);
    }

    @UseStringTemplateEngine
    public interface SomethingWithExplicitAttributeName {
        @SqlQuery("select id, name from something where (id, name) in (<keys>)")
        List<Something> get(@BindBeanList(value = "keys", propertyNames = { "id", "name" })
        BindBeanListTest.SomethingKey... blarg);
    }

    // 
    @Test
    public void testSomethingByVarargsWithVarargs() {
        final BindBeanListTest.SomethingByVarargs s = handle.attach(BindBeanListTest.SomethingByVarargs.class);
        final List<Something> out = s.get(new BindBeanListTest.SomethingKey(1, "1"), new BindBeanListTest.SomethingKey(2, "2"));
        assertThat(out).hasSameElementsAs(expectedSomethings);
    }

    @Test
    public void testSomethingByVarargsWithEmptyVarargs() {
        final BindBeanListTest.SomethingByVarargs s = handle.attach(BindBeanListTest.SomethingByVarargs.class);
        assertThatThrownBy(s::get).isInstanceOf(IllegalArgumentException.class);
    }

    @UseStringTemplateEngine
    public interface SomethingByVarargs {
        @SqlQuery("select id, name from something where (id, name) in (<keys>)")
        List<Something> get(@BindBeanList(propertyNames = { "id", "name" })
        BindBeanListTest.SomethingKey... keys);
    }

    // 
    @Test
    public void testSomethingByArrayWithNull() {
        final BindBeanListTest.SomethingByArray s = handle.attach(BindBeanListTest.SomethingByArray.class);
        assertThatThrownBy(() -> s.get(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testSomethingByArrayWithEmptyArray() {
        final BindBeanListTest.SomethingByArray s = handle.attach(BindBeanListTest.SomethingByArray.class);
        assertThatThrownBy(() -> s.get(new org.jdbi.v3.sqlobject.SomethingKey[]{  })).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testSomethingByArrayWithNonEmptyArray() {
        final BindBeanListTest.SomethingByVarargs s = handle.attach(BindBeanListTest.SomethingByVarargs.class);
        final List<Something> out = s.get(new BindBeanListTest.SomethingKey(1, "1"), new BindBeanListTest.SomethingKey(2, "2"));
        assertThat(out).hasSameElementsAs(expectedSomethings);
    }

    @UseStringTemplateEngine
    private interface SomethingByArray {
        @SqlQuery("select id, name from something where (id, name) in (<keys>)")
        List<Something> get(@BindBeanList(propertyNames = { "id", "name" })
        BindBeanListTest.SomethingKey[] keys);
    }

    // 
    @Test
    public void testSomethingByIterableWithIterable() {
        final BindBeanListTest.SomethingByIterable s = handle.attach(BindBeanListTest.SomethingByIterable.class);
        final List<Something> out = s.get(() -> Arrays.asList(new BindBeanListTest.SomethingKey(1, "1"), new BindBeanListTest.SomethingKey(2, "2")).iterator());
        assertThat(out).hasSameElementsAs(expectedSomethings);
    }

    @Test
    public void testSomethingByIterableWithEmptyIterable() {
        final BindBeanListTest.SomethingByIterable s = handle.attach(BindBeanListTest.SomethingByIterable.class);
        assertThatThrownBy(() -> s.get(new ArrayList<>())).isInstanceOf(IllegalArgumentException.class);
    }

    @UseStringTemplateEngine
    public interface SomethingByIterable {
        @SqlQuery("select id, name from something where (id, name) in (<keys>)")
        List<Something> get(@BindBeanList(propertyNames = { "id", "name" })
        Iterable<BindBeanListTest.SomethingKey> keys);
    }

    @UseStringTemplateEngine
    public interface SomethingByIterator {
        @SqlQuery("select id, name from something where (id, name) in (<keys>)")
        List<Something> get(@BindBeanList(propertyNames = { "id", "name" })
        Iterator<BindBeanListTest.SomethingKey> keys);
    }

    public static class SomethingKey {
        private final int id;

        private final String name;

        SomethingKey(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
