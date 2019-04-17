/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.cli.fsadmin.command;


import alluxio.client.cli.fs.AbstractShellIntegrationTest;
import alluxio.client.cli.fsadmin.AbstractFsAdminShellTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for report metrics command.
 */
public final class MetricsCommandIntegrationTest extends AbstractFsAdminShellTest {
    @Test
    public void metrics() {
        int ret = mFsAdminShell.run("report", "metrics");
        Assert.assertEquals(0, ret);
        String output = mOutput.toString();
        checkMetricsResults(output);
    }
}
