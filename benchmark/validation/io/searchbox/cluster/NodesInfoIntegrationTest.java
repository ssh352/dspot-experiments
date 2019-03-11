package io.searchbox.cluster;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class NodesInfoIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void nodesInfoWithoutNodeAndInfo() throws IOException {
        JestResult result = client.execute(new NodesInfo.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void nodesInfoWithNodeWithoutInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").build();
        JestResult result = client.execute(nodesInfo);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void nodesInfoWithoutNodeWithInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().withOs().build();
        JestResult result = client.execute(nodesInfo);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void nodesInfoWithNodeAndWithInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").withOs().build();
        JestResult result = client.execute(nodesInfo);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void nodesInfoWithMultipleNodeAndWithoutInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").addNode("node2").build();
        JestResult result = client.execute(nodesInfo);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void nodesInfoWithMultipleNodeAndMultipleInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").addNode("node2").withProcess().withOs().build();
        JestResult result = client.execute(nodesInfo);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
}

