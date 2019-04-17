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
package org.ehcache.clustered.client.internal.config.xml;


import Consistency.STRONG;
import org.ehcache.clustered.client.config.builders.ClusteredStoreConfigurationBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xmlunit.diff.ElementSelectors;


public class ClusteringCacheServiceConfigurationParserTest {
    @Test
    public void testTranslateServiceStoreConfiguration() {
        ClusteringCacheServiceConfigurationParser configurationTranslator = new ClusteringCacheServiceConfigurationParser();
        Node retNode = configurationTranslator.unparseServiceConfiguration(ClusteredStoreConfigurationBuilder.withConsistency(STRONG).build());
        String inputString = "<tc:clustered-store consistency = \"strong\" " + "xmlns:tc = \"http://www.ehcache.org/v3/clustered\"></tc:clustered-store>";
        Assert.assertThat(retNode, isSimilarTo(inputString).ignoreComments().ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }
}
