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
package org.jdbi.v3.core.statement;


import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.core.result.NoResultsException;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.result.ResultIterator;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestQueries {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Handle h;

    @Test
    public void testCreateQueryObject() {
        h.createUpdate("insert into something (id, name) values (1, 'eric')").execute();
        h.createUpdate("insert into something (id, name) values (2, 'brian')").execute();
        List<Map<String, Object>> results = h.createQuery("select * from something order by id").mapToMap().list();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).get("name")).isEqualTo("eric");
    }

    @Test
    public void testMappedQueryObject() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        ResultIterable<Something> query = h.createQuery("select * from something order by id").mapToBean(Something.class);
        List<Something> r = query.list();
        assertThat(r).startsWith(new Something(1, "eric"));
    }

    @Test
    public void testMappedQueryObjectWithNulls() {
        h.execute("insert into something (id, name, integerValue) values (1, 'eric', null)");
        ResultIterable<Something> query = h.createQuery("select * from something order by id").mapToBean(Something.class);
        List<Something> r = query.list();
        Something eric = r.get(0);
        assertThat(eric).isEqualTo(new Something(1, "eric"));
        assertThat(eric.getIntegerValue()).isNull();
    }

    @Test
    public void testMappedQueryObjectWithNullForPrimitiveIntField() {
        h.execute("insert into something (id, name, intValue) values (1, 'eric', null)");
        ResultIterable<Something> query = h.createQuery("select * from something order by id").mapToBean(Something.class);
        List<Something> r = query.list();
        Something eric = r.get(0);
        assertThat(eric).isEqualTo(new Something(1, "eric"));
        assertThat(eric.getIntValue()).isZero();
    }

    @Test
    public void testMapper() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        ResultIterable<String> query = h.createQuery("select name from something order by id").map(( r, ctx) -> r.getString(1));
        List<String> r = query.list();
        assertThat(r).startsWith("eric");
    }

    @Test
    public void testConvenienceMethod() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        List<Map<String, Object>> r = h.select("select * from something order by id").mapToMap().list();
        assertThat(r).hasSize(2);
        assertThat(r.get(0).get("name")).isEqualTo("eric");
    }

    @Test
    public void testConvenienceMethodWithParam() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        List<Map<String, Object>> r = h.select("select * from something where id = ?", 1).mapToMap().list();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).get("name")).isEqualTo("eric");
    }

    @Test
    public void testPositionalArgWithNamedParam() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        assertThatThrownBy(() -> h.createQuery("select * from something where name = :name").bind(0, "eric").mapToBean(.class).list()).isInstanceOf(UnableToCreateStatementException.class).hasMessageContaining("Missing named parameter 'name'");
    }

    @Test
    public void testMixedSetting() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        assertThatThrownBy(() -> h.createQuery("select * from something where name = :name and id = :id").bind(0, "eric").bind("id", 1).mapToBean(.class).list()).isInstanceOf(UnableToCreateStatementException.class).hasMessageContaining("Missing named parameter 'name'");
    }

    @Test
    public void testHelpfulErrorOnNothingSet() {
        assertThatThrownBy(() -> h.createQuery("select * from something where name = :name").mapToMap().list()).isInstanceOf(UnableToCreateStatementException.class);
    }

    @Test
    public void testFirstResult() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        Something r = h.createQuery("select * from something order by id").mapToBean(Something.class).findFirst().get();
        assertThat(r.getName()).isEqualTo("eric");
    }

    @Test
    public void testIteratedResult() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        try (ResultIterator<Something> i = h.createQuery("select * from something order by id").mapToBean(Something.class).iterator()) {
            assertThat(i.hasNext()).isTrue();
            Something first = i.next();
            assertThat(first.getName()).isEqualTo("eric");
            assertThat(i.hasNext()).isTrue();
            Something second = i.next();
            assertThat(second.getId()).isEqualTo(2);
            assertThat(i.hasNext()).isFalse();
        }
    }

    @Test
    public void testIteratorBehavior() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        try (ResultIterator<Something> i = h.createQuery("select * from something order by id").mapToBean(Something.class).iterator()) {
            assertThat(i.hasNext()).isTrue();
            Something first = i.next();
            assertThat(first.getName()).isEqualTo("eric");
            assertThat(i.hasNext()).isTrue();
            Something second = i.next();
            assertThat(second.getId()).isEqualTo(2);
            assertThat(i.hasNext()).isFalse();
        }
    }

    @Test
    public void testIteratorBehavior2() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'brian')");
        try (ResultIterator<Something> i = h.createQuery("select * from something order by id").mapToBean(Something.class).iterator()) {
            Something first = i.next();
            assertThat(first.getName()).isEqualTo("eric");
            Something second = i.next();
            assertThat(second.getId()).isEqualTo(2);
            assertThat(i.hasNext()).isFalse();
        }
    }

    @Test
    public void testIteratorBehavior3() {
        h.execute("insert into something (id, name) values (1, 'eric')");
        h.execute("insert into something (id, name) values (2, 'eric')");
        assertThat(h.createQuery("select * from something order by id").mapToBean(Something.class)).extracting(Something::getName).containsExactly("eric", "eric");
    }

    @Test
    public void testFetchSize() {
        h.createScript(ClasspathSqlLocator.findSqlOnClasspath("default-data")).execute();
        ResultIterable<Something> ri = h.createQuery("select id, name from something order by id").setFetchSize(1).mapToBean(Something.class);
        ResultIterator<Something> r = ri.iterator();
        assertThat(r.hasNext()).isTrue();
        r.next();
        assertThat(r.hasNext()).isTrue();
        r.next();
        assertThat(r.hasNext()).isFalse();
    }

    @Test
    public void testFirstWithNoResult() {
        Optional<Something> s = h.createQuery("select id, name from something").mapToBean(Something.class).findFirst();
        assertThat(s.isPresent()).isFalse();
    }

    @Test
    public void testNullValueInColumn() {
        h.execute("insert into something (id, name) values (?, ?)", 1, null);
        Optional<String> s = h.createQuery("select name from something where id=1").mapTo(String.class).findFirst();
        assertThat(s.isPresent()).isFalse();
    }

    @Test
    public void testListWithMaxRows() {
        h.prepareBatch("insert into something (id, name) values (?, ?)").add(1, "Brian").add(2, "Keith").add(3, "Eric").execute();
        assertThat(h.createQuery("select id, name from something").mapToBean(Something.class).withStream(( stream) -> stream.limit(1).count()).longValue()).isEqualTo(1);
        assertThat(h.createQuery("select id, name from something").mapToBean(Something.class).withStream(( stream) -> stream.limit(2).count()).longValue()).isEqualTo(2);
    }

    @Test
    public void testFold() {
        h.prepareBatch("insert into something (id, name) values (?, ?)").add(1, "Brian").add(2, "Keith").execute();
        Map<String, Integer> rs = h.createQuery("select id, name from something").<Map.Entry<String, Integer>>map(( r, ctx) -> Maps.immutableEntry(r.getString("name"), r.getInt("id"))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat(rs).containsOnly(entry("Brian", 1), entry("Keith", 2));
    }

    @Test
    public void testCollectList() {
        h.prepareBatch("insert into something (id, name) values (?, ?)").add(1, "Brian").add(2, "Keith").execute();
        List<String> rs = h.createQuery("select name from something order by id").mapTo(String.class).collect(Collectors.toList());
        assertThat(rs).containsExactly("Brian", "Keith");
    }

    @Test
    public void testUsefulArgumentOutputForDebug() {
        expectedException.expect(StatementException.class);
        expectedException.expectMessage("arguments:{positional:{7:8}, named:{name:brian}, finder:[{one=two},{lazy bean property arguments \"java.lang.Object");
        h.createUpdate("insert into something (id, name) values (:id, :name)").bind("name", "brian").bind(7, 8).bindMap(new HandyMapThing<String>().add("one", "two")).bindBean(new Object()).execute();
    }

    @Test
    public void testStatementCustomizersPersistAfterMap() {
        h.execute("insert into something (id, name) values (?, ?)", 1, "hello");
        h.execute("insert into something (id, name) values (?, ?)", 2, "world");
        List<Something> rs = h.createQuery("select id, name from something").setMaxRows(1).mapToBean(Something.class).list();
        assertThat(rs).hasSize(1);
    }

    @Test
    public void testQueriesWithNullResultSets() {
        expectedException.expect(NoResultsException.class);
        h.select("insert into something (id, name) values (?, ?)", 1, "hello").mapToMap().list();
    }

    @Test
    public void testMapMapperOrdering() {
        h.execute("insert into something (id, name) values (?, ?)", 1, "hello");
        h.execute("insert into something (id, name) values (?, ?)", 2, "world");
        List<Map<String, Object>> rs = h.createQuery("select id, name from something").mapToMap().list();
        assertThat(rs).hasSize(2);
        assertThat(rs).hasOnlyElementsOfType(LinkedHashMap.class);
    }
}
