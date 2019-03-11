/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.graphdb.query;


import Contain.IN;
import Order.ASC;
import Order.DESC;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.graphdb.internal.OrderList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class QueryTest {
    private JanusGraph graph;

    private JanusGraphTransaction tx;

    @Test
    public void testMultipleKeysQuery() {
        tx.makePropertyKey("name").dataType(String.class).make();
        tx.addVertex("name", "vertex1");
        tx.addVertex("name", "vertex2");
        tx.addVertex("name", "vertex3");
        int found = Iterators.size(tx.query().has("name", IN, Collections.singletonList("vertex1")).vertices().iterator());
        Assertions.assertEquals(1, found);
        found = Iterators.size(tx.query().has("name", IN, Arrays.asList("vertex1", "vertex2")).vertices().iterator());
        Assertions.assertEquals(2, found);
        found = Iterators.size(tx.query().has("name", IN, Arrays.asList("vertex1", "vertex2", "vertex3")).vertices().iterator());
        Assertions.assertEquals(3, found);
        found = Iterators.size(tx.query().has("name", IN, Arrays.asList("vertex1", "vertex2", "vertex3", "vertex4")).vertices().iterator());
        Assertions.assertEquals(3, found);
        int limit = 2;
        found = Iterators.size(tx.query().has("name", IN, Arrays.asList("vertex1", "vertex2", "vertex3", "vertex4")).limit(limit).vertices().iterator());
        Assertions.assertEquals(limit, found);
    }

    @Test
    public void testOrderList() {
        PropertyKey name = tx.makePropertyKey("name").dataType(String.class).make();
        PropertyKey weight = tx.makePropertyKey("weight").dataType(Double.class).make();
        PropertyKey time = tx.makePropertyKey("time").dataType(Long.class).make();
        OrderList ol1 = new OrderList();
        ol1.add(name, DESC);
        ol1.add(weight, ASC);
        ol1.makeImmutable();
        try {
            ol1.add(time, DESC);
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
        Assertions.assertEquals(2, ol1.size());
        Assertions.assertEquals(name, ol1.getKey(0));
        Assertions.assertEquals(weight, ol1.getKey(1));
        Assertions.assertEquals(DESC, ol1.getOrder(0));
        Assertions.assertEquals(ASC, ol1.getOrder(1));
        Assertions.assertFalse(ol1.hasCommonOrder());
        OrderList ol2 = new OrderList();
        ol2.add(time, ASC);
        ol2.add(weight, ASC);
        ol2.add(name, ASC);
        ol2.makeImmutable();
        Assertions.assertTrue(ol2.hasCommonOrder());
        Assertions.assertEquals(ASC, ol2.getCommonOrder());
        OrderList ol3 = new OrderList();
        ol3.add(weight, DESC);
        JanusGraphVertex v1 = tx.addVertex("name", "abc", "time", 20, "weight", 2.5);
        JanusGraphVertex v2 = tx.addVertex("name", "bcd", "time", 10, "weight", 2.5);
        JanusGraphVertex v3 = tx.addVertex("name", "abc", "time", 10, "weight", 4.5);
        Assertions.assertTrue(((ol1.compare(v1, v2)) > 0));
        Assertions.assertTrue(((ol1.compare(v2, v3)) < 0));
        Assertions.assertTrue(((ol1.compare(v1, v3)) < 0));
        Assertions.assertTrue(((ol2.compare(v1, v2)) > 0));
        Assertions.assertTrue(((ol2.compare(v2, v3)) < 0));
        Assertions.assertTrue(((ol2.compare(v1, v3)) > 0));
        Assertions.assertTrue(((ol3.compare(v1, v2)) == 0));
        Assertions.assertTrue(((ol3.compare(v2, v3)) > 0));
        Assertions.assertTrue(((ol3.compare(v1, v3)) > 0));
    }

    @Test
    public void testMultipleIndexQueryWithLimits() {
        JanusGraphManagement mgmt = graph.openManagement();
        PropertyKey prop1Key = mgmt.makePropertyKey("prop1").dataType(String.class).make();
        PropertyKey prop2Key = mgmt.makePropertyKey("prop2").dataType(String.class).make();
        mgmt.buildIndex("prop1_idx", Vertex.class).addKey(prop1Key).buildCompositeIndex();
        mgmt.buildIndex("prop2_idx", Vertex.class).addKey(prop2Key).buildCompositeIndex();
        mgmt.commit();
        // Creates 20 vertices with prop1=prop1val1, prop2=prop2val1
        for (int i = 0; i < 20; i++) {
            tx.addVertex().property("prop1", "prop1val1").element().property("prop2", "prop2val1");
        }
        // Creates an additional vertex with prop1=prop1val1, prop2=prop2val2
        tx.addVertex().property("prop1", "prop1val1").element().property("prop2", "prop2val2");
        tx.commit();
        List<Vertex> res;
        // Tests that queries for the single vertex containing prop1=prop1val1, prop2=prop2val2, are returned when limit(1) is applied
        // Tests that single vertex containing prop1=prop1val1, prop2=prop2val2 is returned when indices are not used
        res = graph.traversal().V().map(( x) -> x.get()).has("prop2", "prop2val2").has("prop1", "prop1val1").limit(1).toList();
        Assertions.assertEquals(1, res.size());
        // Tests that single vertex containing prop1=prop1val1, prop2=prop2val2 is returned when only prop1 index is used
        res = graph.traversal().V().has("prop1", "prop1val1").map(( x) -> x.get()).has("prop2", "prop2val2").limit(1).toList();
        Assertions.assertEquals(1, res.size());
        // Tests that single vertex containing prop1=prop1val1, prop2=prop2val2 is returned when only prop2 index is used
        res = graph.traversal().V().has("prop2", "prop2val2").map(( x) -> x.get()).has("prop1", "prop1val1").limit(1).toList();
        Assertions.assertEquals(1, res.size());
        // Tests that JanusGraphStep strategy properly combines has() steps to use both indices
        // Tests without limits
        res = graph.traversal().V().has("prop1", "prop1val1").has("prop2", "prop2val2").toList();
        Assertions.assertEquals(1, res.size());
        // Tests with limit
        res = graph.traversal().V().has("prop1", "prop1val1").has("prop2", "prop2val2").limit(1).toList();
        Assertions.assertEquals(1, res.size());
        res = graph.traversal().V().has("prop2", "prop2val2").has("prop1", "prop1val1").limit(1).toList();
        Assertions.assertEquals(1, res.size());
    }
}

