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
package org.apache.ambari.server.api.resources;


import Resource.Type.StackConfigurationDependency;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * StackConfigurationResourceDefinition unit tests.
 */
public class StackConfigurationDefinitionTest {
    @Test
    public void testGetPluralName() {
        Assert.assertEquals("configurations", new StackConfigurationResourceDefinition().getPluralName());
    }

    @Test
    public void testGetSingularName() {
        Assert.assertEquals("configuration", new StackConfigurationResourceDefinition().getSingularName());
    }

    @Test
    public void testGetSubResourceDefinitions() {
        ResourceDefinition resource = new StackConfigurationResourceDefinition();
        Set<SubResourceDefinition> subResources = resource.getSubResourceDefinitions();
        Assert.assertEquals(1, subResources.size());
        Assert.assertTrue(includesType(subResources, StackConfigurationDependency));
    }
}

