/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.mr.security;


import EsTokenIdentifier.KIND_NAME;
import java.util.ServiceLoader;
import org.apache.hadoop.security.token.TokenRenewer;
import org.junit.Assert;
import org.junit.Test;

import static EsTokenIdentifier.KIND_NAME;


public class EsTokenIdentifierTest {
    @Test
    public void testSPI() {
        ServiceLoader<TokenRenewer> tokenRenewers = ServiceLoader.load(TokenRenewer.class);
        for (TokenRenewer tokenRenewer : tokenRenewers) {
            if (tokenRenewer.handleKind(KIND_NAME)) {
                return;
            }
        }
        Assert.fail(("Could not find token renewer for " + (KIND_NAME)));
    }
}

