/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.maven;


import Constants.XML_SCHEMA_NAMESPACE_PREFIX;
import Constants.XML_SCHEMA_NAMESPACE_URI;
import org.junit.Assert;
import org.junit.Test;


public class CamelSpringNamespaceTest {
    private CamelSpringNamespace camelSpringNamespace = new CamelSpringNamespace();

    @Test
    public void testSchemaNamespace() throws Exception {
        Assert.assertEquals(XML_SCHEMA_NAMESPACE_URI, camelSpringNamespace.getNamespaceURI(XML_SCHEMA_NAMESPACE_PREFIX));
        Assert.assertNull(camelSpringNamespace.getNamespaceURI("unregisterdPrefix"));
    }

    @Test
    public void testGetPrefix() throws Exception {
        try {
            camelSpringNamespace.getPrefix(XML_SCHEMA_NAMESPACE_URI);
            Assert.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // Expected.
        }
    }

    @Test
    public void testGetPrefixes() throws Exception {
        try {
            camelSpringNamespace.getPrefixes(XML_SCHEMA_NAMESPACE_URI);
            Assert.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // Expected.
        }
    }
}
