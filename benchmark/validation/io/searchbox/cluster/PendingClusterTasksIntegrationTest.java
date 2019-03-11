package io.searchbox.cluster;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.google.common.collect.Iterables;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.Test;


@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class PendingClusterTasksIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void pendingClusterTasks() throws IOException {
        PendingClusterTasks pendingClusterTasks = new PendingClusterTasks.Builder().build();
        JestResult result = client.execute(pendingClusterTasks);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertTrue(result.getJsonObject().get("tasks").isJsonArray());
        assertTrue(Iterables.isEmpty(result.getJsonObject().getAsJsonArray("tasks")));
    }
}

