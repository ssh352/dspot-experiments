/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred;


import java.io.IOException;
import org.apache.hadoop.util.ExitUtil.ExitException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TestTask {
    @Mock
    private TaskUmbilicalProtocol umbilical;

    @Mock
    private AMFeedback feedback;

    private Task task;

    @Test
    public void testStatusUpdateDoesNotExitInUberMode() throws Exception {
        setupTest(true);
        task.statusUpdate(umbilical);
    }

    @Test(expected = ExitException.class)
    public void testStatusUpdateExitsInNonUberMode() throws Exception {
        setupTest(false);
        task.statusUpdate(umbilical);
    }

    public class StubTask extends Task {
        @Override
        public void run(JobConf job, TaskUmbilicalProtocol umbilical) throws IOException, ClassNotFoundException, InterruptedException {
            // nop
        }

        @Override
        public boolean isMapTask() {
            return false;
        }
    }
}

