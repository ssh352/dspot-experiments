/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.core.oauth;


import com.google.common.collect.ImmutableMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.kairosdb.core.KairosRootConfig;


public class OAuthModuleTest {
    @Test
    public void testReadingProperties() {
        KairosRootConfig props = new KairosRootConfig();
        props.load(ImmutableMap.of("kairosdb.oauth.consumer.cust1", "ABC123"));
        props.load(ImmutableMap.of("kairosdb.oauth.consumerNot.cust1", "XYZ"));
        props.load(ImmutableMap.of("kairosdb.oauth.consumer.cust2", "EFG789"));
        OAuthModule module = new OAuthModule(props);
        ConsumerTokenStore tokenStore = module.getTokenStore();
        MatcherAssert.assertThat(tokenStore.getConsumerKeys().size(), Matchers.is(2));
        MatcherAssert.assertThat(tokenStore.getToken("cust1"), Matchers.is("ABC123"));
        MatcherAssert.assertThat(tokenStore.getToken("cust2"), Matchers.is("EFG789"));
    }
}
