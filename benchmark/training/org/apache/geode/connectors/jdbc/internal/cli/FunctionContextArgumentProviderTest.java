/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.connectors.jdbc.internal.cli;


import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.connectors.jdbc.internal.JdbcConnectorService;
import org.apache.geode.distributed.DistributedMember;
import org.junit.Test;
import org.mockito.Mockito;


public class FunctionContextArgumentProviderTest {
    private FunctionContext<?> context;

    private DistributedMember distributedMember;

    private ResultSender<Object> resultSender;

    private JdbcConnectorService service;

    private FunctionContextArgumentProvider jdbcCommandFunctionContext;

    @Test
    public void getMemberReturnsMemberNameInsteadOfId() throws Exception {
        Mockito.when(distributedMember.getId()).thenReturn("myId");
        Mockito.when(distributedMember.getName()).thenReturn("myName");
        String member = jdbcCommandFunctionContext.getMember(context);
        assertThat(member).isEqualTo("myName");
    }

    @Test
    public void getMemberReturnsMemberIdIfNameIsMissing() throws Exception {
        Mockito.when(distributedMember.getId()).thenReturn("myId");
        String member = jdbcCommandFunctionContext.getMember(context);
        assertThat(member).isEqualTo("myId");
    }
}

