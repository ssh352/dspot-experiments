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
package org.apache.ambari.server.controller.internal;


import Resource.Type;
import StackConfigurationResourceProvider.PROPERTY_DEPENDS_ON_PROPERTY_ID;
import StackConfigurationResourceProvider.PROPERTY_DESCRIPTION_PROPERTY_ID;
import StackConfigurationResourceProvider.PROPERTY_FINAL_PROPERTY_ID;
import StackConfigurationResourceProvider.PROPERTY_NAME_PROPERTY_ID;
import StackConfigurationResourceProvider.PROPERTY_TYPE_PROPERTY_ID;
import StackConfigurationResourceProvider.PROPERTY_VALUE_PROPERTY_ID;
import StackConfigurationResourceProvider.SERVICE_NAME_PROPERTY_ID;
import StackConfigurationResourceProvider.STACK_NAME_PROPERTY_ID;
import StackConfigurationResourceProvider.STACK_VERSION_PROPERTY_ID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.StackConfigurationResponse;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.ambari.server.controller.internal.AbstractResourceProviderTest.Matcher.getStackConfigurationRequestSet;


public class StackConfigurationResourceProviderTest {
    private static final String PROPERTY_NAME = "name";

    private static final String PROPERTY_VALUE = "value";

    private static final String PROPERTY_DESC = "Desc";

    private static final String TYPE = "type.xml";

    @Test
    public void testGetResources() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("final", "true");
        Resource.Type type = Type.StackConfiguration;
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Set<StackConfigurationResponse> allResponse = new HashSet<>();
        allResponse.add(new StackConfigurationResponse(StackConfigurationResourceProviderTest.PROPERTY_NAME, StackConfigurationResourceProviderTest.PROPERTY_VALUE, StackConfigurationResourceProviderTest.PROPERTY_DESC, StackConfigurationResourceProviderTest.TYPE, attributes));
        // set expectations
        expect(managementController.getStackConfigurations(getStackConfigurationRequestSet(null, null, null, null))).andReturn(allResponse).times(1);
        // replay
        replay(managementController);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(STACK_NAME_PROPERTY_ID);
        propertyIds.add(STACK_VERSION_PROPERTY_ID);
        propertyIds.add(SERVICE_NAME_PROPERTY_ID);
        propertyIds.add(PROPERTY_NAME_PROPERTY_ID);
        propertyIds.add(PROPERTY_VALUE_PROPERTY_ID);
        propertyIds.add(PROPERTY_DESCRIPTION_PROPERTY_ID);
        propertyIds.add(PROPERTY_TYPE_PROPERTY_ID);
        propertyIds.add(PROPERTY_FINAL_PROPERTY_ID);
        propertyIds.add(PROPERTY_DEPENDS_ON_PROPERTY_ID);
        // create the request
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // get all ... no predicate
        Set<Resource> resources = provider.getResources(request, null);
        Assert.assertEquals(allResponse.size(), resources.size());
        for (Resource resource : resources) {
            String propertyName = ((String) (resource.getPropertyValue(PROPERTY_NAME_PROPERTY_ID)));
            String propertyValue = ((String) (resource.getPropertyValue(PROPERTY_VALUE_PROPERTY_ID)));
            String propertyDesc = ((String) (resource.getPropertyValue(PROPERTY_DESCRIPTION_PROPERTY_ID)));
            String propertyType = ((String) (resource.getPropertyValue(PROPERTY_TYPE_PROPERTY_ID)));
            String propertyIsFinal = ((String) (resource.getPropertyValue(PROPERTY_FINAL_PROPERTY_ID)));
            String propertyDependencies = ((String) (resource.getPropertyValue(PROPERTY_DEPENDS_ON_PROPERTY_ID)));
            Assert.assertEquals(StackConfigurationResourceProviderTest.PROPERTY_NAME, propertyName);
            Assert.assertEquals(StackConfigurationResourceProviderTest.PROPERTY_VALUE, propertyValue);
            Assert.assertEquals(StackConfigurationResourceProviderTest.PROPERTY_DESC, propertyDesc);
            Assert.assertEquals(StackConfigurationResourceProviderTest.TYPE, propertyType);
            Assert.assertEquals("true", propertyIsFinal);
            Assert.assertNull(propertyDependencies);
        }
        // verify
        verify(managementController);
    }

    @Test
    public void testGetResources_noFinal() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        Resource.Type type = Type.StackConfiguration;
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Set<StackConfigurationResponse> allResponse = new HashSet<>();
        allResponse.add(new StackConfigurationResponse(StackConfigurationResourceProviderTest.PROPERTY_NAME, StackConfigurationResourceProviderTest.PROPERTY_VALUE, StackConfigurationResourceProviderTest.PROPERTY_DESC, StackConfigurationResourceProviderTest.TYPE, attributes));
        // set expectations
        expect(managementController.getStackConfigurations(getStackConfigurationRequestSet(null, null, null, null))).andReturn(allResponse).times(1);
        // replay
        replay(managementController);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(STACK_NAME_PROPERTY_ID);
        propertyIds.add(STACK_VERSION_PROPERTY_ID);
        propertyIds.add(SERVICE_NAME_PROPERTY_ID);
        propertyIds.add(PROPERTY_NAME_PROPERTY_ID);
        propertyIds.add(PROPERTY_VALUE_PROPERTY_ID);
        propertyIds.add(PROPERTY_DESCRIPTION_PROPERTY_ID);
        propertyIds.add(PROPERTY_TYPE_PROPERTY_ID);
        propertyIds.add(PROPERTY_FINAL_PROPERTY_ID);
        // create the request
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // get all ... no predicate
        Set<Resource> resources = provider.getResources(request, null);
        Assert.assertEquals(allResponse.size(), resources.size());
        for (Resource resource : resources) {
            String propertyName = ((String) (resource.getPropertyValue(PROPERTY_NAME_PROPERTY_ID)));
            String propertyValue = ((String) (resource.getPropertyValue(PROPERTY_VALUE_PROPERTY_ID)));
            String propertyDesc = ((String) (resource.getPropertyValue(PROPERTY_DESCRIPTION_PROPERTY_ID)));
            String propertyType = ((String) (resource.getPropertyValue(PROPERTY_TYPE_PROPERTY_ID)));
            String propertyIsFinal = ((String) (resource.getPropertyValue(PROPERTY_FINAL_PROPERTY_ID)));
            Assert.assertEquals(StackConfigurationResourceProviderTest.PROPERTY_NAME, propertyName);
            Assert.assertEquals(StackConfigurationResourceProviderTest.PROPERTY_VALUE, propertyValue);
            Assert.assertEquals(StackConfigurationResourceProviderTest.PROPERTY_DESC, propertyDesc);
            Assert.assertEquals(StackConfigurationResourceProviderTest.TYPE, propertyType);
            Assert.assertEquals("false", propertyIsFinal);
        }
        // verify
        verify(managementController);
    }
}

