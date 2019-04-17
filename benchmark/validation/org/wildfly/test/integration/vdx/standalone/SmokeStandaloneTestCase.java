/**
 * Copyright 2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.test.integration.vdx.standalone;


import java.nio.file.Files;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.StandaloneTests;
import org.wildfly.test.integration.vdx.utils.server.ServerConfig;


/**
 * Smoke test case - it tests whether Wildlfy/EAP test automation is working and basic VDX functionality.
 */
@RunAsClient
@RunWith(Arquillian.class)
@Category(StandaloneTests.class)
public class SmokeStandaloneTestCase extends TestBase {
    @Test
    @ServerConfig(configuration = "duplicate-attribute.xml")
    public void testWithExistingConfigInResources() throws Exception {
        container().tryStartAndWaitForFail();
        SmokeStandaloneTestCase.ensureDuplicateAttribute(container().getErrorMessageFromServerStart());
    }

    @Test
    @ServerConfig(configuration = "standalone-full-ha-to-damage.xml", xmlTransformationGroovy = "TypoInExtensions.groovy")
    public void typoInExtensionsWithConfigInResources() throws Exception {
        container().tryStartAndWaitForFail();
        SmokeStandaloneTestCase.ensureTypoInExtensions(container().getErrorMessageFromServerStart());
    }

    @Test
    @ServerConfig(configuration = "standalone-full-ha.xml", xmlTransformationGroovy = "AddNonExistentElementToMessagingSubsystem.groovy", subtreeName = "messaging", subsystemName = "messaging-activemq")
    public void addNonExistingElementToMessagingSubsystem() throws Exception {
        container().tryStartAndWaitForFail();
        SmokeStandaloneTestCase.ensureNonExistingElementToMessagingSubsystem(container().getErrorMessageFromServerStart());
    }

    @Test
    @ServerConfig(configuration = "empty.xml")
    public void emptyConfigFile() throws Exception {
        container().tryStartAndWaitForFail();
        TestBase.assertContains(String.join("\n", Files.readAllLines(container().getServerLogPath())), "OPVDX004: Failed to pretty print validation error: empty.xml has no content");
    }
}
