/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.commands;


import java.util.HashSet;
import java.util.Set;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;


public class AlterAsyncEventQueueCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private AlterAsyncEventQueueCommand command;

    private InternalConfigurationPersistenceService service;

    private org.apache.geode.cache.Region configRegion;

    private Set<String> groupSet = new HashSet<>();

    @Test
    public void mandatoryOption() throws Exception {
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue").statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void noOptionToModify() throws Exception {
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --id=test").statusIsError().containsOutput("need to specify at least one option to modify.");
    }

    @Test
    public void emptyConfiguration() throws Exception {
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --id=test --batch-size=100").statusIsError().containsOutput("Can not find an async event queue");
    }

    @Test
    public void emptyConfiguration_ifExists() throws Exception {
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --id=test --batch-size=100 --if-exists").statusIsSuccess().containsOutput("Skipping: Can not find an async event queue with id");
    }

    @Test
    public void cluster_config_service_not_available() throws Exception {
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --id=test --batch-size=100").statusIsError().containsOutput("Cluster Configuration Service is not available");
    }

    @Test
    public void queueIdNotFoundInTheMap() throws Exception {
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --batch-size=100 --id=queue").statusIsError().containsOutput("Can not find an async event queue");
    }

    @Test
    public void queueIdFoundInTheMap_updateBatchSize() throws Exception {
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --batch-size=100 --id=queue1").statusIsSuccess().tableHasRowCount("Group", 2).tableHasRowWithValues("Group", "Status", "group1", "Cluster Configuration Updated").containsOutput("Please restart the servers to apply any changed configuration");
    }

    @Test
    public void queueIdFoundInTheMap_updateBatchTimeInterval() throws Exception {
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --batch-time-interval=100 --id=queue1").statusIsSuccess().tableHasRowCount("Group", 2).tableHasRowWithValues("Group", "Status", "group1", "Cluster Configuration Updated").containsOutput("Please restart the servers to apply any changed configuration");
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --batch-time-interval=100 --id=queue1").statusIsSuccess().containsOutput("Please restart the servers");
    }

    @Test
    public void queueIdFoundInTheMap_updateMaxMemory() throws Exception {
        AlterAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "alter async-event-queue --max-queue-memory=100 --id=queue1").statusIsSuccess().tableHasRowCount("Group", 2).tableHasRowWithValues("Group", "Status", "group1", "Cluster Configuration Updated").containsOutput("Please restart the servers to apply any changed configuration");
    }
}
