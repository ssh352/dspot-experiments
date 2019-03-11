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
package org.janusgraph.diskstorage.cassandra.astyanax;


import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.diskstorage.cassandra.AbstractCassandraStoreTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AstyanaxColumnPaginationTest extends AbstractCassandraStoreTest {
    private static final int DEFAULT_READ_PAGE_SIZE = 4096;

    @Test
    public void ensureReadPageSizePropertySetCorrectly() {
        Assertions.assertEquals(((AstyanaxStoreManager) (manager)).readPageSize, AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE);
    }

    @Test
    public void retrieveLessThanBoundaryColumnPaginationProperties() {
        final Graph graph = JanusGraphFactory.open(getBaseStorageConfiguration());
        final Vertex v = graph.addVertex();
        for (int i = 0; i < ((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) - 1); i++) {
            v.property(String.valueOf(i), i);
        }
        graph.tx().commit();
        Assertions.assertEquals(((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) - 1), graph.traversal().V(v).valueMap().next().keySet().size());
    }

    @Test
    public void retrieveBoundaryColumnPaginationProperties() {
        final Graph graph = JanusGraphFactory.open(getBaseStorageConfiguration());
        final Vertex v = graph.addVertex();
        for (int i = 0; i < (AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE); i++) {
            v.property(String.valueOf(i), i);
        }
        graph.tx().commit();
        Assertions.assertEquals(AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE, graph.traversal().V(v).valueMap().next().keySet().size());
    }

    @Test
    public void retrieveBeyondBoundaryColumnPaginationProperties() {
        final Graph graph = JanusGraphFactory.open(getBaseStorageConfiguration());
        final Vertex v = graph.addVertex();
        for (int i = 0; i < ((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) + 1); i++) {
            v.property(String.valueOf(i), i);
        }
        graph.tx().commit();
        Assertions.assertEquals(((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) + 1), graph.traversal().V(v).valueMap().next().keySet().size());
    }

    @Test
    public void retrieveWayBeyondBoundaryColumnPaginationProperties() {
        final Graph graph = JanusGraphFactory.open(getBaseStorageConfiguration());
        final Vertex v = graph.addVertex();
        for (int i = 0; i < ((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) * 5); i++) {
            v.property(String.valueOf(i), i);
        }
        graph.tx().commit();
        Assertions.assertEquals(((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) * 5), graph.traversal().V(v).valueMap().next().keySet().size());
    }

    @Test
    public void retrieveLessThanBoundaryColumnPaginationEdges() {
        final Graph graph = JanusGraphFactory.open(getBaseStorageConfiguration());
        final Vertex v = graph.addVertex();
        final Vertex v2 = graph.addVertex();
        for (int i = 0; i < ((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) - 1); i++) {
            v.addEdge("edgeLabel", v2);
        }
        graph.tx().commit();
        Assertions.assertEquals(((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) - 1), graph.traversal().V(v).outE().toList().size());
    }

    @Test
    public void retrieveBoundaryColumnPaginationEdges() {
        final Graph graph = JanusGraphFactory.open(getBaseStorageConfiguration());
        final Vertex v = graph.addVertex();
        final Vertex v2 = graph.addVertex();
        for (int i = 0; i < (AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE); i++) {
            v.addEdge("edgeLabel", v2);
        }
        graph.tx().commit();
        Assertions.assertEquals(AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE, graph.traversal().V(v).outE().toList().size());
    }

    @Test
    public void retrieveBeyondBoundaryColumnPaginationEdges() {
        final Graph graph = JanusGraphFactory.open(getBaseStorageConfiguration());
        final Vertex v = graph.addVertex();
        final Vertex v2 = graph.addVertex();
        for (int i = 0; i < ((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) + 1); i++) {
            v.addEdge("edgeLabel", v2);
        }
        graph.tx().commit();
        Assertions.assertEquals(((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) + 1), graph.traversal().V(v).outE().toList().size());
    }

    @Test
    public void retrieveWayBeyondBoundaryColumnPaginationEdges() {
        final Graph graph = JanusGraphFactory.open(getBaseStorageConfiguration());
        final Vertex v = graph.addVertex();
        final Vertex v2 = graph.addVertex();
        for (int i = 0; i < ((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) * 5); i++) {
            v.addEdge("edgeLabel", v2);
        }
        graph.tx().commit();
        Assertions.assertEquals(((AstyanaxColumnPaginationTest.DEFAULT_READ_PAGE_SIZE) * 5), graph.traversal().V(v).outE().toList().size());
    }
}

