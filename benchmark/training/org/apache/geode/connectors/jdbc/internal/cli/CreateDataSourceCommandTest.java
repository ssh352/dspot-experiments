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
package org.apache.geode.connectors.jdbc.internal.cli;


import CreateDataSourceCommand.PoolProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.JndiBindingsType;
import org.apache.geode.cache.configuration.JndiBindingsType.JndiBinding;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.functions.CreateJndiBindingFunction;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class CreateDataSourceCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private CreateDataSourceCommand command;

    private InternalCache cache;

    JndiBinding binding;

    List<JndiBindingsType.JndiBinding> bindings;

    private static String COMMAND = "create data-source ";

    @Test
    public void missingMandatory() {
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, CreateDataSourceCommandTest.COMMAND).statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void nonPooledWorks() {
        InternalConfigurationPersistenceService clusterConfigService = Mockito.mock(InternalConfigurationPersistenceService.class);
        Mockito.doReturn(clusterConfigService).when(command).getConfigurationPersistenceService();
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --pooled=false --url=url --name=name")).statusIsSuccess();
    }

    @Test
    public void pooledWorks() {
        InternalConfigurationPersistenceService clusterConfigService = Mockito.mock(InternalConfigurationPersistenceService.class);
        Mockito.doReturn(clusterConfigService).when(command).getConfigurationPersistenceService();
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --pooled=true --url=url --name=name")).statusIsSuccess();
    }

    @Test
    public void poolPropertiesIsProperlyParsed() {
        GfshParseResult result = CreateDataSourceCommandTest.gfsh.parse((((CreateDataSourceCommandTest.COMMAND) + " --pooled --name=name --url=url ") + "--pool-properties={'name':'name1','value':'value1'},{'name':'name2','value':'value2'}"));
        CreateDataSourceCommand[] poolProperties = ((CreateDataSourceCommand[]) (result.getParamValue("pool-properties")));
        assertThat(poolProperties).hasSize(2);
        assertThat(poolProperties[0].getName()).isEqualTo("name1");
        assertThat(poolProperties[1].getName()).isEqualTo("name2");
        assertThat(poolProperties[0].getValue()).isEqualTo("value1");
        assertThat(poolProperties[1].getValue()).isEqualTo("value2");
    }

    @Test
    public void poolPropertiesRequiresPooled() {
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, (((CreateDataSourceCommandTest.COMMAND) + " --pooled=false --name=name --url=url ") + "--pool-properties={'name':'name1','value':'value1'}")).statusIsError().containsOutput("pool-properties option is only valid on --pooled");
    }

    @Test
    public void poolFactoryRequiresPooled() {
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, (((CreateDataSourceCommandTest.COMMAND) + " --pooled=false --name=name --url=url ") + "--pooled-data-source-factory-class=factoryClassValue")).statusIsError().containsOutput("pooled-data-source-factory-class option is only valid on --pooled");
    }

    @Test
    public void returnsErrorIfDataSourceAlreadyExistsAndIfUnspecified() {
        InternalConfigurationPersistenceService clusterConfigService = Mockito.mock(InternalConfigurationPersistenceService.class);
        CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
        Mockito.when(cacheConfig.getJndiBindings()).thenReturn(bindings);
        bindings.add(binding);
        Mockito.doReturn(clusterConfigService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(cacheConfig).when(clusterConfigService).getCacheConfig(ArgumentMatchers.any());
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --name=name  --url=url")).statusIsError().containsOutput("already exists.");
    }

    @Test
    public void skipsIfDataSourceAlreadyExistsAndIfSpecified() {
        InternalConfigurationPersistenceService clusterConfigService = Mockito.mock(InternalConfigurationPersistenceService.class);
        CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
        Mockito.doReturn(clusterConfigService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(cacheConfig).when(clusterConfigService).getCacheConfig(ArgumentMatchers.any());
        Mockito.when(cacheConfig.getJndiBindings()).thenReturn(bindings);
        bindings.add(binding);
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --name=name  --url=url --if-not-exists")).statusIsSuccess().containsOutput("Skipping");
    }

    @Test
    public void skipsIfDataSourceAlreadyExistsAndIfSpecifiedTrue() {
        InternalConfigurationPersistenceService clusterConfigService = Mockito.mock(InternalConfigurationPersistenceService.class);
        CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
        Mockito.when(cacheConfig.getJndiBindings()).thenReturn(bindings);
        bindings.add(binding);
        Mockito.doReturn(clusterConfigService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(cacheConfig).when(clusterConfigService).getCacheConfig(ArgumentMatchers.any());
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --name=name  --url=url --if-not-exists=true")).statusIsSuccess().containsOutput("Skipping");
    }

    @Test
    public void returnsErrorIfDataSourceAlreadyExistsAndIfSpecifiedFalse() {
        InternalConfigurationPersistenceService clusterConfigService = Mockito.mock(InternalConfigurationPersistenceService.class);
        CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
        Mockito.when(cacheConfig.getJndiBindings()).thenReturn(bindings);
        bindings.add(binding);
        Mockito.doReturn(clusterConfigService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(cacheConfig).when(clusterConfigService).getCacheConfig(ArgumentMatchers.any());
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --name=name  --url=url --if-not-exists=false")).statusIsError().containsOutput("already exists.");
    }

    @Test
    public void whenNoMembersFoundAndNoClusterConfigServiceRunningThenError() {
        Mockito.doReturn(Collections.emptySet()).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --name=name  --url=url")).statusIsError().containsOutput("No members found and cluster configuration unavailable.");
    }

    @Test
    public void whenNoMembersFoundAndClusterConfigRunningThenUpdateClusterConfig() {
        InternalConfigurationPersistenceService clusterConfigService = Mockito.mock(InternalConfigurationPersistenceService.class);
        CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
        Mockito.doReturn(Collections.emptySet()).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(clusterConfigService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(cacheConfig).when(clusterConfigService).getCacheConfig(ArgumentMatchers.any());
        Mockito.doAnswer(( invocation) -> {
            UnaryOperator<CacheConfig> mutator = invocation.getArgument(1);
            mutator.apply(cacheConfig);
            return null;
        }).when(clusterConfigService).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --name=name  --url=url")).statusIsSuccess().containsOutput("No members found, data source saved to cluster configuration.").containsOutput("Changes to configuration for group 'cluster' are persisted.");
        Mockito.verify(clusterConfigService).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command).updateConfigForGroup(ArgumentMatchers.eq("cluster"), ArgumentMatchers.eq(cacheConfig), ArgumentMatchers.isNotNull());
    }

    @Test
    public void whenMembersFoundAndNoClusterConfigRunningThenOnlyInvokeFunction() {
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Tried creating jndi binding \"name\" on \"server1\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --pooled --name=name  --url=url --pool-properties={'name':'name1','value':'value1'}")).statusIsSuccess().tableHasColumnOnlyWithValues("Member", "server1").tableHasColumnOnlyWithValues("Status", "OK").tableHasColumnOnlyWithValues("Message", "Tried creating jndi binding \"name\" on \"server1\"");
        ArgumentCaptor<CreateJndiBindingFunction> function = ArgumentCaptor.forClass(CreateJndiBindingFunction.class);
        ArgumentCaptor<Object[]> arguments = ArgumentCaptor.forClass(Object[].class);
        ArgumentCaptor<Set<DistributedMember>> targetMembers = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(command, Mockito.times(1)).executeAndGetFunctionResult(function.capture(), arguments.capture(), targetMembers.capture());
        assertThat(function.getValue()).isInstanceOf(CreateJndiBindingFunction.class);
        assertThat(arguments.getValue()).isNotNull();
        Object[] actualArguments = arguments.getValue();
        JndiBinding jndiConfig = ((JndiBinding) (actualArguments[0]));
        boolean creatingDataSource = ((Boolean) (actualArguments[1]));
        assertThat(creatingDataSource).isTrue();
        assertThat(jndiConfig.getJndiName()).isEqualTo("name");
        assertThat(jndiConfig.getConfigProperties().get(0).getName()).isEqualTo("name1");
        assertThat(targetMembers.getValue()).isEqualTo(members);
    }

    @Test
    public void whenMembersFoundAndClusterConfigRunningThenUpdateClusterConfigAndInvokeFunction() {
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Tried creating jndi binding \"name\" on \"server1\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        InternalConfigurationPersistenceService clusterConfigService = Mockito.mock(InternalConfigurationPersistenceService.class);
        CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(clusterConfigService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(cacheConfig).when(clusterConfigService).getCacheConfig(ArgumentMatchers.any());
        Mockito.doAnswer(( invocation) -> {
            UnaryOperator<CacheConfig> mutator = invocation.getArgument(1);
            mutator.apply(cacheConfig);
            return null;
        }).when(clusterConfigService).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        CreateDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((CreateDataSourceCommandTest.COMMAND) + " --pooled --name=name  --url=url --pool-properties={'name':'name1','value':'value1'}")).statusIsSuccess().tableHasColumnOnlyWithValues("Member", "server1").tableHasColumnOnlyWithValues("Status", "OK").tableHasColumnOnlyWithValues("Message", "Tried creating jndi binding \"name\" on \"server1\"");
        Mockito.verify(clusterConfigService).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command).updateConfigForGroup(ArgumentMatchers.eq("cluster"), ArgumentMatchers.eq(cacheConfig), ArgumentMatchers.any());
        ArgumentCaptor<CreateJndiBindingFunction> function = ArgumentCaptor.forClass(CreateJndiBindingFunction.class);
        ArgumentCaptor<Object[]> arguments = ArgumentCaptor.forClass(Object[].class);
        ArgumentCaptor<Set<DistributedMember>> targetMembers = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(command, Mockito.times(1)).executeAndGetFunctionResult(function.capture(), arguments.capture(), targetMembers.capture());
        assertThat(function.getValue()).isInstanceOf(CreateJndiBindingFunction.class);
        assertThat(arguments.getValue()).isNotNull();
        Object[] actualArguments = arguments.getValue();
        JndiBinding jndiConfig = ((JndiBinding) (actualArguments[0]));
        boolean creatingDataSource = ((Boolean) (actualArguments[1]));
        assertThat(function.getValue()).isInstanceOf(CreateJndiBindingFunction.class);
        assertThat(creatingDataSource).isTrue();
        assertThat(jndiConfig.getJndiName()).isEqualTo("name");
        assertThat(jndiConfig.getConfigProperties().get(0).getName()).isEqualTo("name1");
        assertThat(targetMembers.getValue()).isEqualTo(members);
    }
}

