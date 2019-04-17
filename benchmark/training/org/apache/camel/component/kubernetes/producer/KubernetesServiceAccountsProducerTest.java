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
package org.apache.camel.component.kubernetes.producer;


import KubernetesConstants.KUBERNETES_NAMESPACE_NAME;
import KubernetesConstants.KUBERNETES_SERVICE_ACCOUNTS_LABELS;
import KubernetesConstants.KUBERNETES_SERVICE_ACCOUNT_NAME;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceAccountListBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.kubernetes.KubernetesTestSupport;
import org.junit.Rule;
import org.junit.Test;


public class KubernetesServiceAccountsProducerTest extends KubernetesTestSupport {
    @Rule
    public KubernetesServer server = new KubernetesServer();

    @Test
    public void listTest() throws Exception {
        server.expect().withPath("/api/v1/serviceaccounts").andReturn(200, new ServiceAccountListBuilder().addNewItem().and().addNewItem().and().addNewItem().and().build()).once();
        List<ServiceAccount> result = template.requestBody("direct:list", "", List.class);
        assertEquals(3, result.size());
    }

    @Test
    public void listByLabelsTest() throws Exception {
        server.expect().withPath(("/api/v1/serviceaccounts?labelSelector=" + (KubernetesTestSupport.toUrlEncoded("key1=value1,key2=value2")))).andReturn(200, new ServiceAccountListBuilder().addNewItem().and().addNewItem().and().addNewItem().and().build()).once();
        Exchange ex = template.request("direct:listByLabels", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, String> labels = new HashMap<>();
                labels.put("key1", "value1");
                labels.put("key2", "value2");
                exchange.getIn().setHeader(KUBERNETES_SERVICE_ACCOUNTS_LABELS, labels);
            }
        });
        List<ServiceAccount> result = ex.getOut().getBody(List.class);
        assertEquals(3, result.size());
    }

    @Test
    public void createAndDeleteServiceAccount() throws Exception {
        ServiceAccount pod1 = new ServiceAccountBuilder().withNewMetadata().withName("sa1").withNamespace("test").and().build();
        server.expect().withPath("/api/v1/namespaces/test/serviceaccounts/sa1").andReturn(200, pod1).once();
        Exchange ex = template.request("direct:delete", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KUBERNETES_NAMESPACE_NAME, "test");
                exchange.getIn().setHeader(KUBERNETES_SERVICE_ACCOUNT_NAME, "sa1");
            }
        });
        boolean secDeleted = ex.getOut().getBody(Boolean.class);
        assertTrue(secDeleted);
    }
}
