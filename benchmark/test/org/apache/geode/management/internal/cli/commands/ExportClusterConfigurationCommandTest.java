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


import java.util.Properties;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.management.internal.configuration.domain.Configuration;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ExportClusterConfigurationCommandTest {
    private static String CLUSTER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + (((("<cache xmlns=\"http://geode.apache.org/schema/cache\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" copy-on-read=\"false\" is-server=\"false\" lock-lease=\"120\" lock-timeout=\"60\" search-timeout=\"300\" version=\"1.0\" xsi:schemaLocation=\"http://geode.apache.org/schema/cache http://geode.apache.org/schema/cache/cache-1.0.xsd\">\n" + "<region name=\"regionForCluster\">\n") + "    <region-attributes data-policy=\"replicate\" scope=\"distributed-ack\"/>\n") + "  </region>\n") + "</cache>\n");

    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private ExportClusterConfigurationCommand command;

    private InternalConfigurationPersistenceService ccService;

    private Configuration configuration;

    @Test
    public void checkDefaultValue() {
        GfshParseResult parseResult = ExportClusterConfigurationCommandTest.gfsh.parse(((EXPORT_SHARED_CONFIG) + " --xml-file=my.xml"));
        assertThat(parseResult.getParamValue("group")).isEqualTo("cluster");
        assertThat(parseResult.getParamValue("xml-file")).isEqualTo("my.xml");
        parseResult = ExportClusterConfigurationCommandTest.gfsh.parse(((EXPORT_SHARED_CONFIG) + " --group=''"));
        assertThat(parseResult.getParamValue("group")).isEqualTo("cluster");
    }

    @Test
    public void preValidation() {
        ExportClusterConfigurationCommandTest.gfsh.executeAndAssertThat(command, ((EXPORT_SHARED_CONFIG) + " --group='group1,group2'")).statusIsError().containsOutput("Only a single group name is supported");
        ExportClusterConfigurationCommandTest.gfsh.executeAndAssertThat(command, ((EXPORT_SHARED_CONFIG) + " --zip-file-name=b.zip --xml-file=ab.xml")).statusIsError().containsOutput("Zip file and xml File can't both be specified");
        ExportClusterConfigurationCommandTest.gfsh.executeAndAssertThat(command, ((EXPORT_SHARED_CONFIG) + " --zip-file-name=b.zip --group=group1")).statusIsError().containsOutput("zip file can not be exported with a specific group");
        ExportClusterConfigurationCommandTest.gfsh.executeAndAssertThat(command, ((EXPORT_SHARED_CONFIG) + " --zip-file-name=b.zip")).statusIsSuccess();
    }

    @Test
    public void clusterConfigurationNotRunning() {
        Mockito.doReturn(false).when(command).isSharedConfigurationRunning();
        ExportClusterConfigurationCommandTest.gfsh.executeAndAssertThat(command, EXPORT_SHARED_CONFIG).statusIsError().containsOutput("Cluster configuration service is not running");
    }

    @Test
    public void groupNotExist() {
        Mockito.when(ccService.getConfiguration("groupA")).thenReturn(null);
        ExportClusterConfigurationCommandTest.gfsh.executeAndAssertThat(command, ((EXPORT_SHARED_CONFIG) + " --group=groupA")).statusIsError().containsOutput("No cluster configuration for 'groupA'.");
    }

    @Test
    public void get() {
        Mockito.when(ccService.getConfiguration(ArgumentMatchers.any())).thenReturn(configuration);
        configuration.setCacheXmlContent(ExportClusterConfigurationCommandTest.CLUSTER_XML);
        Properties properties = new Properties();
        properties.put("key1", "value1");
        properties.put("key2", "value2");
        configuration.setGemfireProperties(properties);
        configuration.addJarNames(new String[]{ "jar1.jar", "jar2.jar" });
        ExportClusterConfigurationCommandTest.gfsh.executeAndAssertThat(command, EXPORT_SHARED_CONFIG).statusIsSuccess().containsOutput("cluster.xml:").containsOutput("Properties:").containsOutput("Jars:").containsOutput("jar1.jar, jar2.jar").containsOutput("<?xml version=\\\"1.0\\\"").containsOutput("</cache>");
    }
}

