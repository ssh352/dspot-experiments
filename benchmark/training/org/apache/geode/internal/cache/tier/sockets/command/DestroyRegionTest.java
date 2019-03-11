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
package org.apache.geode.internal.cache.tier.sockets.command;


import Operation.MANAGE;
import Resource.DATA;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.tier.sockets.CacheServerStats;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.Part;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.apache.geode.internal.security.AuthorizeRequest;
import org.apache.geode.internal.security.SecurityService;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class DestroyRegionTest {
    private static final String REGION_NAME = "region1";

    private static final String KEY = "key1";

    private static final Object CALLBACK_ARG = "arg";

    private static final byte[] EVENT = new byte[8];

    @Mock
    private SecurityService securityService;

    @Mock
    private Message message;

    @Mock
    private ServerConnection serverConnection;

    @Mock
    private AuthorizeRequest authzRequest;

    @Mock
    private LocalRegion region;

    @Mock
    private InternalCache cache;

    @Mock
    private CacheServerStats cacheServerStats;

    @Mock
    private Message errorResponseMessage;

    @Mock
    private Message responseMessage;

    @Mock
    private Part regionNamePart;

    @Mock
    private Part eventPart;

    @Mock
    private Part callbackArgPart;

    @InjectMocks
    private DestroyRegion destroyRegion;

    @Test
    public void noSecurityShouldSucceed() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(false);
        this.destroyRegion.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.securityService).authorize(DATA, MANAGE);
        Mockito.verify(this.responseMessage).send(this.serverConnection);
    }

    @Test
    public void integratedSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(true);
        this.destroyRegion.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.securityService).authorize(DATA, MANAGE);
        Mockito.verify(this.responseMessage).send(this.serverConnection);
    }

    @Test
    public void integratedSecurityShouldFailIfNotAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(true);
        Mockito.doThrow(new NotAuthorizedException("")).when(this.securityService).authorize(DATA, MANAGE);
        this.destroyRegion.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.errorResponseMessage).send(this.serverConnection);
    }

    @Test
    public void oldSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(false);
        this.destroyRegion.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.authzRequest).destroyRegionAuthorize(ArgumentMatchers.eq(DestroyRegionTest.REGION_NAME), ArgumentMatchers.eq(DestroyRegionTest.CALLBACK_ARG));
        Mockito.verify(this.responseMessage).send(this.serverConnection);
    }

    @Test
    public void oldSecurityShouldFailIfNotAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(false);
        Mockito.doThrow(new NotAuthorizedException("")).when(this.authzRequest).destroyRegionAuthorize(ArgumentMatchers.eq(DestroyRegionTest.REGION_NAME), ArgumentMatchers.eq(DestroyRegionTest.CALLBACK_ARG));
        this.destroyRegion.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.authzRequest).destroyRegionAuthorize(ArgumentMatchers.eq(DestroyRegionTest.REGION_NAME), ArgumentMatchers.eq(DestroyRegionTest.CALLBACK_ARG));
        Mockito.verify(this.errorResponseMessage).send(this.serverConnection);
    }
}

