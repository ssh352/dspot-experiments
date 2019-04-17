/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.kubernetes.discovery;


import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Oleg Vyukov
 */
public class KubernetesCatalogServicesWatchConfigurationTest {
    private ConfigurableApplicationContext context;

    @Test
    public void kubernetesCatalogWatchDisabled() throws Exception {
        setup("spring.cloud.kubernetes.discovery.catalog-services-watch.enabled=false");
        assertThat(this.context.containsBean("kubernetesCatalogWatch")).isFalse();
    }

    @Test
    public void kubernetesCatalogWatchWhenKubernetesDisabled() throws Exception {
        setup("spring.cloud.kubernetes.enabled=false");
        assertThat(this.context.containsBean("kubernetesCatalogWatch")).isFalse();
    }

    @Test
    public void kubernetesCatalogWatchDefaultEnabled() throws Exception {
        setup();
        assertThat(this.context.containsBean("kubernetesCatalogWatch")).isTrue();
    }

    @Configuration
    static class KubernetesClientTestConfiguration {
        @Bean
        KubernetesClient kubernetesClient() {
            return Mockito.mock(KubernetesClient.class);
        }
    }
}
