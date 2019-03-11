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


import CliFunctionResult.StatusState;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.i18n.CliStrings;
import org.apache.geode.test.junit.assertions.CommandResultAssert;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;


public class DestroyGatewayReceiverCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private DestroyGatewayReceiverCommand command;

    private InternalCache cache;

    private List<CliFunctionResult> functionResults;

    private InternalConfigurationPersistenceService ccService;

    private CliFunctionResult result1;

    @Test
    public void noGroupOrMember_isError() {
        Mockito.doThrow(new org.apache.geode.management.internal.cli.exceptions.UserErrorException(CliStrings.PROVIDE_EITHER_MEMBER_OR_GROUP_MESSAGE)).when(command).findMembers(null, null);
        DestroyGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "destroy gateway-receiver").statusIsError().containsOutput("provide either \\\"member\\\" or \\\"group\\\" option");
    }

    @Test
    public void memberNoGroup_isOK() {
        result1 = new CliFunctionResult("member1", StatusState.OK, "result1");
        functionResults.add(result1);
        Set<DistributedMember> membersSet = new HashSet<>();
        membersSet.add(new InternalDistributedMember("member1", 0));
        Mockito.doReturn(membersSet).when(command).findMembers(null, new String[]{ "member1" });
        CommandResultAssert resultAssert = DestroyGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "destroy gateway-receiver --member=\"member1\"");
        resultAssert.statusIsSuccess().tableHasColumnWithValuesContaining("Message", "result1");
    }

    @Test
    public void groupNoMember_isOK() {
        result1 = new CliFunctionResult("member1", StatusState.OK, "result1");
        functionResults.add(result1);
        Set<DistributedMember> membersSet = new HashSet<>();
        membersSet.add(new InternalDistributedMember("member1", 0));
        Mockito.doReturn(membersSet).when(command).findMembers(new String[]{ "group1" }, null);
        CommandResultAssert resultAssert = DestroyGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "destroy gateway-receiver --group=\"group1\"");
        resultAssert.statusIsSuccess().tableHasColumnWithValuesContaining("Message", "result1");
    }
}

