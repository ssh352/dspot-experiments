/**
 * Copyright Terracotta, Inc.
 *
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
 */
package org.ehcache.clustered.client;


import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author GGIB
 */
public class XmlUnknownCacheTest {
    @Test
    public void testGetUnknownCache() {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(this.getClass().getResource("/configs/unknown-cluster-cache.xml"));
        Assert.assertThat(xmlConfiguration.getCacheConfigurations().keySet(), Matchers.contains("unknownCache"));
    }

    @Test
    public void testGetUnknownCacheInvalidAttribute() {
        try {
            new XmlConfiguration(this.getClass().getResource("/configs/unknown-cluster-cache-invalid-attribute.xml"));
            Assert.fail("Expected XmlConfigurationException");
        } catch (XmlConfigurationException xce) {
            Assert.assertThat(xce.getCause().getMessage(), Matchers.endsWith("Attribute 'unit' is not allowed to appear in element 'tc:clustered'."));
        }
    }

    @Test
    public void testGetUnknownCacheInvalidElement() {
        try {
            new XmlConfiguration(this.getClass().getResource("/configs/unknown-cluster-cache-invalid-element.xml"));
            Assert.fail("Expected XmlConfigurationException");
        } catch (XmlConfigurationException xce) {
            Assert.assertThat(xce.getCause().getMessage(), Matchers.endsWith("Element 'tc:clustered' must have no character or element information item [children], because the type's content type is empty."));
        }
    }
}

