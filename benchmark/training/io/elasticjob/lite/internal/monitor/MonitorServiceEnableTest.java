/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.internal.monitor;


import MonitorService.DUMP_COMMAND;
import io.elasticjob.lite.fixture.TestSimpleJob;
import io.elasticjob.lite.integrate.AbstractBaseStdJobTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public final class MonitorServiceEnableTest extends AbstractBaseStdJobTest {
    private static final int MONITOR_PORT = 9000;

    public MonitorServiceEnableTest() {
        super(TestSimpleJob.class, MonitorServiceEnableTest.MONITOR_PORT);
    }

    @Test
    public void assertMonitorWithCommand() throws IOException {
        initJob();
        Assert.assertNotNull(SocketUtils.sendCommand(DUMP_COMMAND, MonitorServiceEnableTest.MONITOR_PORT));
        Assert.assertNull(SocketUtils.sendCommand("unknown_command", MonitorServiceEnableTest.MONITOR_PORT));
    }
}

