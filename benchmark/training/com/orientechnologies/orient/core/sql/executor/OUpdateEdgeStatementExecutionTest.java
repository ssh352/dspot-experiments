package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OUpdateEdgeStatementExecutionTest {
    static ODatabaseDocument database;

    @Test
    public void testUpdateEdge() {
        OUpdateEdgeStatementExecutionTest.database.command("create class V1 extends V");
        OUpdateEdgeStatementExecutionTest.database.command("create class E1 extends E");
        OUpdateEdgeStatementExecutionTest.database.getMetadata().getSchema().reload();
        // VERTEXES
        ODocument v1 = OUpdateEdgeStatementExecutionTest.database.command(new OCommandSQL("create vertex")).execute();
        Assert.assertEquals(v1.getClassName(), "V");
        ODocument v2 = OUpdateEdgeStatementExecutionTest.database.command(new OCommandSQL("create vertex V1")).execute();
        Assert.assertEquals(v2.getClassName(), "V1");
        ODocument v3 = OUpdateEdgeStatementExecutionTest.database.command(new OCommandSQL("create vertex set vid = 'v3', brand = 'fiat'")).execute();
        Assert.assertEquals(v3.getClassName(), "V");
        Assert.assertEquals(v3.field("brand"), "fiat");
        ODocument v4 = OUpdateEdgeStatementExecutionTest.database.command(new OCommandSQL("create vertex V1 set vid = 'v4',  brand = 'fiat',name = 'wow'")).execute();
        Assert.assertEquals(v4.getClassName(), "V1");
        Assert.assertEquals(v4.field("brand"), "fiat");
        Assert.assertEquals(v4.field("name"), "wow");
        OResultSet edges = OUpdateEdgeStatementExecutionTest.database.command(((("create edge E1 from " + (v1.getIdentity())) + " to ") + (v2.getIdentity())));
        Assert.assertTrue(edges.hasNext());
        OResult edge = edges.next();
        Assert.assertFalse(edges.hasNext());
        Assert.assertEquals(getClassName(), "E1");
        edges.close();
        OUpdateEdgeStatementExecutionTest.database.command(((((("update edge E1 set out = " + (v3.getIdentity())) + ", in = ") + (v4.getIdentity())) + " where @rid = ") + (edge.getElement().get().getIdentity())));
        OResultSet result = OUpdateEdgeStatementExecutionTest.database.query(("select expand(out('E1')) from " + (v3.getIdentity())));
        Assert.assertTrue(result.hasNext());
        OResult vertex4 = result.next();
        Assert.assertEquals(vertex4.getProperty("vid"), "v4");
        Assert.assertFalse(result.hasNext());
        result.close();
        result = OUpdateEdgeStatementExecutionTest.database.query(("select expand(in('E1')) from " + (v4.getIdentity())));
        Assert.assertTrue(result.hasNext());
        OResult vertex3 = result.next();
        Assert.assertEquals(vertex3.getProperty("vid"), "v3");
        Assert.assertFalse(result.hasNext());
        result.close();
        result = OUpdateEdgeStatementExecutionTest.database.query(("select expand(out('E1')) from " + (v1.getIdentity())));
        Assert.assertFalse(result.hasNext());
        result.close();
        result = OUpdateEdgeStatementExecutionTest.database.query(("select expand(in('E1')) from " + (v2.getIdentity())));
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testUpdateEdgeOfTypeE() {
        // issue #6378
        ODocument v1 = OUpdateEdgeStatementExecutionTest.database.command(new OCommandSQL("create vertex")).execute();
        ODocument v2 = OUpdateEdgeStatementExecutionTest.database.command(new OCommandSQL("create vertex")).execute();
        ODocument v3 = OUpdateEdgeStatementExecutionTest.database.command(new OCommandSQL("create vertex")).execute();
        OResultSet edges = OUpdateEdgeStatementExecutionTest.database.command(((("create edge E from " + (v1.getIdentity())) + " to ") + (v2.getIdentity())));
        OResult edge = edges.next();
        OUpdateEdgeStatementExecutionTest.database.command(((("UPDATE EDGE " + (edge.getElement().get().getIdentity())) + " SET in = ") + (v3.getIdentity())));
        Iterable<ODocument> result = OUpdateEdgeStatementExecutionTest.database.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(("select expand(out()) from " + (v1.getIdentity())))).execute();
        Assert.assertEquals(result.iterator().next().getIdentity(), v3.getIdentity());
        result = OUpdateEdgeStatementExecutionTest.database.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(("select expand(in()) from " + (v3.getIdentity())))).execute();
        Assert.assertEquals(result.iterator().next().getIdentity(), v1.getIdentity());
        result = OUpdateEdgeStatementExecutionTest.database.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(("select expand(in()) from " + (v2.getIdentity())))).execute();
        Assert.assertFalse(result.iterator().hasNext());
    }
}

