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
package org.apache.ambari.server.security.ldap;


import AmbariLdapDataPopulator.LdapUserContextMapper;
import GroupType.LDAP;
import LdapUsernameCollisionHandlingBehavior.SKIP;
import com.google.common.collect.Sets;
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.directory.SearchControls;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.ldap.domain.AmbariLdapConfiguration;
import org.apache.ambari.server.orm.entities.GroupEntity;
import org.apache.ambari.server.security.authorization.AmbariLdapUtils;
import org.apache.ambari.server.security.authorization.Group;
import org.apache.ambari.server.security.authorization.LdapServerProperties;
import org.apache.ambari.server.security.authorization.User;
import org.apache.ambari.server.security.authorization.Users;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;

import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@PrepareForTest(AmbariLdapUtils.class)
public class AmbariLdapDataPopulatorTest {
    public static class AmbariLdapDataPopulatorTestInstance extends AmbariLdapDataPopulatorTest.TestAmbariLdapDataPopulator {
        public AmbariLdapDataPopulatorTestInstance(Provider<AmbariLdapConfiguration> configurationProvider, Users users) {
            super(configurationProvider, users);
        }

        @Override
        protected LdapTemplate loadLdapTemplate() {
            return ldapTemplate;
        }
    }

    public static class TestAmbariLdapDataPopulator extends AmbariLdapDataPopulator {
        protected LdapTemplate ldapTemplate;

        private LdapContextSource ldapContextSource;

        private PagedResultsDirContextProcessor processor;

        public TestAmbariLdapDataPopulator(Provider<AmbariLdapConfiguration> configurationProvider, Users users) {
            super(configurationProvider, users);
        }

        @Override
        protected LdapContextSource createLdapContextSource() {
            return ldapContextSource;
        }

        @Override
        protected LdapTemplate createLdapTemplate(LdapContextSource ldapContextSource) {
            this.ldapContextSource = ldapContextSource;
            return ldapTemplate;
        }

        @Override
        protected PagedResultsDirContextProcessor createPagingProcessor() {
            return processor;
        }

        public void setLdapContextSource(LdapContextSource ldapContextSource) {
            this.ldapContextSource = ldapContextSource;
        }

        public void setProcessor(PagedResultsDirContextProcessor processor) {
            this.processor = processor;
        }

        public void setLdapTemplate(LdapTemplate ldapTemplate) {
            this.ldapTemplate = ldapTemplate;
        }

        public LdapServerProperties getLdapServerProperties() {
            return this.ldapServerProperties;
        }

        public void setLdapServerProperties(LdapServerProperties ldapServerProperties) {
            this.ldapServerProperties = ldapServerProperties;
        }

        public LdapContextSource getLdapContextSource() {
            return ldapContextSource;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsLdapEnabled_badConfiguration() {
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        final AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        final Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.ldapEnabled()).andReturn(true);
        expect(ldapTemplate.search(EasyMock.<String>anyObject(), EasyMock.anyObject(), EasyMock.<AttributesMapper>anyObject())).andThrow(new NullPointerException()).once();
        replay(ldapTemplate, configurationProvider, configuration, ldapServerProperties);
        final AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        Assert.assertFalse(isLdapEnabled());
        verify(populator.loadLdapTemplate(), configurationProvider, configuration);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReferralMethod() {
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        final AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        final Users users = createNiceMock(Users.class);
        LdapContextSource ldapContextSource = createNiceMock(LdapContextSource.class);
        List<String> ldapUrls = Collections.singletonList("url");
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.getLdapUrls()).andReturn(ldapUrls).anyTimes();
        expect(ldapServerProperties.getReferralMethod()).andReturn("follow");
        ldapContextSource.setReferral("follow");
        ldapTemplate.setIgnorePartialResultException(true);
        replay(ldapTemplate, configurationProvider, configuration, ldapServerProperties, ldapContextSource);
        final AmbariLdapDataPopulatorTest.TestAmbariLdapDataPopulator populator = new AmbariLdapDataPopulatorTest.TestAmbariLdapDataPopulator(configurationProvider, users);
        populator.setLdapContextSource(ldapContextSource);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        loadLdapTemplate();
        verify(ldapTemplate, configurationProvider, configuration, ldapServerProperties, ldapContextSource);
    }

    @Test
    public void testIsLdapEnabled_reallyEnabled() {
        @SuppressWarnings("unchecked")
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        final AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        final Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.ldapEnabled()).andReturn(true);
        expect(ldapTemplate.search(EasyMock.<String>anyObject(), EasyMock.anyObject(), EasyMock.<AttributesMapper>anyObject())).andReturn(Collections.emptyList()).once();
        replay(ldapTemplate, configurationProvider, configuration);
        final AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        Assert.assertTrue(isLdapEnabled());
        verify(populator.loadLdapTemplate(), configurationProvider, configuration);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsLdapEnabled_reallyDisabled() {
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        final AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        final Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.ldapEnabled()).andReturn(false);
        replay(ldapTemplate, ldapServerProperties, configurationProvider, configuration);
        final AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        Assert.assertFalse(isLdapEnabled());
        verify(populator.loadLdapTemplate(), populator.getLdapServerProperties(), configurationProvider, configuration);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void synchronizeExistingLdapGroups() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        Group group3 = createNiceMock(Group.class);
        Group group4 = createNiceMock(Group.class);
        Group group5 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group3.getGroupName()).andReturn("group3").anyTimes();
        expect(group4.getGroupName()).andReturn("group4").anyTimes();
        expect(group5.getGroupName()).andReturn("group5").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        expect(group3.isLdapGroup()).andReturn(false).anyTimes();
        expect(group4.isLdapGroup()).andReturn(true).anyTimes();
        expect(group5.isLdapGroup()).andReturn(true).anyTimes();
        List<Group> groupList = Arrays.asList(group1, group2, group3, group4, group5);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(users.getAllGroups()).andReturn(groupList);
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2, group3, group4, group5);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapGroups").addMockedMethod("refreshGroupMembers").withConstructor(configurationProvider, users).createNiceMock();
        expect(getLdapGroups("group2")).andReturn(Collections.emptySet());
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        LdapBatchDto batchInfo = new LdapBatchDto();
        populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup1), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
        expectLastCall();
        expect(getLdapGroups("group4")).andReturn(Collections.singleton(externalGroup1));
        expect(getLdapGroups("group5")).andReturn(Collections.emptySet());
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeExistingLdapGroups(batchInfo, false);
        verifyGroupsInSet(result.getGroupsToBeRemoved(), Sets.newHashSet("group2", "group5"));
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeExistingLdapGroups_removeDuringIteration() throws Exception {
        // GIVEN
        Group group1 = createNiceMock(Group.class);
        expect(group1.getGroupId()).andReturn(1).anyTimes();
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group1.isLdapGroup()).andReturn(true).anyTimes();
        Group group2 = createNiceMock(Group.class);
        expect(group2.getGroupId()).andReturn(2).anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        expect(users.getAllGroups()).andReturn(Arrays.asList(group1, group2));
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        expect(configuration.getLdapServerProperties()).andReturn(new LdapServerProperties()).anyTimes();
        LdapGroupDto group1Dto = new LdapGroupDto();
        group1Dto.setGroupName("group1");
        group1Dto.setMemberAttributes(Sets.newHashSet("group2"));
        Set<LdapGroupDto> groupDtos1 = Sets.newHashSet();
        groupDtos1.add(group1Dto);
        LdapGroupDto group2Dto = new LdapGroupDto();
        group2Dto.setGroupName("group2");
        group2Dto.setMemberAttributes(Collections.emptySet());
        Set<LdapGroupDto> groupDtos2 = Sets.newHashSet();
        groupDtos2.add(group2Dto);
        LdapBatchDto batchInfo = new LdapBatchDto();
        replay(configurationProvider, configuration, users, group1, group2);
        AmbariLdapDataPopulator dataPopulator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).withConstructor(configurationProvider, users).addMockedMethod("getLdapGroups").addMockedMethod("getLdapUserByMemberAttr").addMockedMethod("getLdapGroupByMemberAttr").createNiceMock();
        expect(dataPopulator.getLdapUserByMemberAttr(anyString())).andReturn(null).anyTimes();
        expect(dataPopulator.getLdapGroupByMemberAttr("group2")).andReturn(group2Dto);
        expect(dataPopulator.getLdapGroups("group1")).andReturn(groupDtos1).anyTimes();
        expect(dataPopulator.getLdapGroups("group2")).andReturn(groupDtos2).anyTimes();
        replay(dataPopulator);
        // WHEN
        dataPopulator.synchronizeExistingLdapGroups(batchInfo, false);
        // THEN
        verify(dataPopulator, group1, group2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeLdapGroups_allExist() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        Group group3 = createNiceMock(Group.class);
        Group group4 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group3.getGroupName()).andReturn("group3").anyTimes();
        expect(group4.getGroupName()).andReturn("group4").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        expect(group3.isLdapGroup()).andReturn(true).anyTimes();
        expect(group4.isLdapGroup()).andReturn(false).anyTimes();
        List<Group> groupList = Arrays.asList(group1, group2, group3, group4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllGroups()).andReturn(groupList);
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2, group3, group4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapGroups").addMockedMethod("refreshGroupMembers").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup2 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup3 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup4 = createNiceMock(LdapGroupDto.class);
        expect(externalGroup1.getGroupName()).andReturn("group1").anyTimes();
        expect(externalGroup2.getGroupName()).andReturn("group2").anyTimes();
        expect(externalGroup3.getGroupName()).andReturn("xgroup1").anyTimes();
        expect(externalGroup4.getGroupName()).andReturn("xgroup2").anyTimes();
        replay(externalGroup1, externalGroup2, externalGroup3, externalGroup4);
        LdapBatchDto batchInfo = new LdapBatchDto();
        Set<LdapGroupDto> externalGroups = createSet(externalGroup3, externalGroup4);
        for (LdapGroupDto externalGroup : externalGroups) {
            populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
            expectLastCall();
        }
        populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup1), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
        expectLastCall();
        populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup2), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
        expectLastCall();
        expect(getLdapGroups("x*")).andReturn(externalGroups);
        expect(getLdapGroups("group1")).andReturn(Collections.singleton(externalGroup1));
        expect(getLdapGroups("group2")).andReturn(Collections.singleton(externalGroup2));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeLdapGroups(createSet("x*", "group1", "group2"), batchInfo, false);
        verifyGroupsInSet(result.getGroupsToBecomeLdap(), Sets.newHashSet("group1"));
        verifyGroupsInSet(result.getGroupsToBeCreated(), Sets.newHashSet("xgroup1", "xgroup2"));
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        verifyGroupsInSet(result.getGroupsProcessedInternal(), Sets.newHashSet("group1", "group2"));
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeLdapGroups_add() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        Group group3 = createNiceMock(Group.class);
        Group group4 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group3.getGroupName()).andReturn("group3").anyTimes();
        expect(group4.getGroupName()).andReturn("group4").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        expect(group3.isLdapGroup()).andReturn(true).anyTimes();
        expect(group4.isLdapGroup()).andReturn(false).anyTimes();
        List<Group> groupList = Arrays.asList(group1, group2, group3, group4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllGroups()).andReturn(groupList);
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2, group3, group4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapGroups").addMockedMethod("refreshGroupMembers").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup2 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup3 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup4 = createNiceMock(LdapGroupDto.class);
        expect(externalGroup1.getGroupName()).andReturn("group1").anyTimes();
        expect(externalGroup2.getGroupName()).andReturn("group2").anyTimes();
        expect(externalGroup3.getGroupName()).andReturn("xgroup1").anyTimes();
        expect(externalGroup4.getGroupName()).andReturn("xgroup2").anyTimes();
        replay(externalGroup1, externalGroup2, externalGroup3, externalGroup4);
        LdapBatchDto batchInfo = new LdapBatchDto();
        Set<LdapGroupDto> externalGroups = createSet(externalGroup3, externalGroup4);
        for (LdapGroupDto externalGroup : externalGroups) {
            populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
            expectLastCall();
        }
        populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup2), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
        expectLastCall();
        expect(getLdapGroups("x*")).andReturn(externalGroups);
        expect(getLdapGroups("group2")).andReturn(Collections.singleton(externalGroup2));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeLdapGroups(createSet("x*", "group2"), batchInfo, false);
        verifyGroupsInSet(result.getGroupsToBeCreated(), Sets.newHashSet("xgroup1", "xgroup2"));
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeLdapGroups_update() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        Group group3 = createNiceMock(Group.class);
        Group group4 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group3.getGroupName()).andReturn("group3").anyTimes();
        expect(group4.getGroupName()).andReturn("group4").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        expect(group3.isLdapGroup()).andReturn(true).anyTimes();
        expect(group4.isLdapGroup()).andReturn(false).anyTimes();
        List<Group> groupList = Arrays.asList(group1, group2, group3, group4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllGroups()).andReturn(groupList);
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2, group3, group4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapGroups").addMockedMethod("refreshGroupMembers").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup2 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup3 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup4 = createNiceMock(LdapGroupDto.class);
        expect(externalGroup1.getGroupName()).andReturn("group1").anyTimes();
        expect(externalGroup2.getGroupName()).andReturn("group2").anyTimes();
        expect(externalGroup3.getGroupName()).andReturn("group3").anyTimes();
        expect(externalGroup4.getGroupName()).andReturn("group4").anyTimes();
        replay(externalGroup1, externalGroup2, externalGroup3, externalGroup4);
        LdapBatchDto batchInfo = new LdapBatchDto();
        Set<LdapGroupDto> externalGroups = createSet(externalGroup1, externalGroup2, externalGroup3, externalGroup4);
        for (LdapGroupDto externalGroup : externalGroups) {
            populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
            expectLastCall();
        }
        expect(getLdapGroups("group*")).andReturn(externalGroups);
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeLdapGroups(createSet("group*"), batchInfo, false);
        verifyGroupsInSet(result.getGroupsToBecomeLdap(), Sets.newHashSet("group1", "group4"));
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = AmbariException.class)
    public void testSynchronizeLdapGroups_absent() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        Group group3 = createNiceMock(Group.class);
        Group group4 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group3.getGroupName()).andReturn("group3").anyTimes();
        expect(group4.getGroupName()).andReturn("group4").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        expect(group3.isLdapGroup()).andReturn(true).anyTimes();
        expect(group4.isLdapGroup()).andReturn(false).anyTimes();
        List<Group> groupList = Arrays.asList(group1, group2, group3, group4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllGroups()).andReturn(groupList);
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2, group3, group4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapGroups").addMockedMethod("refreshGroupMembers").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup2 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup3 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup4 = createNiceMock(LdapGroupDto.class);
        expect(externalGroup1.getGroupName()).andReturn("group1").anyTimes();
        expect(externalGroup2.getGroupName()).andReturn("group2").anyTimes();
        expect(externalGroup3.getGroupName()).andReturn("xgroup1").anyTimes();
        expect(externalGroup4.getGroupName()).andReturn("xgroup2").anyTimes();
        replay(externalGroup1, externalGroup2, externalGroup3, externalGroup4);
        LdapBatchDto batchInfo = new LdapBatchDto();
        Set<LdapGroupDto> externalGroups = createSet(externalGroup3, externalGroup4);
        expect(getLdapGroups("x*")).andReturn(externalGroups);
        expect(getLdapGroups("group1")).andReturn(Collections.emptySet());
        expect(getLdapGroups("group2")).andReturn(Collections.singleton(externalGroup2));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        populator.synchronizeLdapGroups(createSet("x*", "group1", "group2"), batchInfo, false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapGroups() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        Group group3 = createNiceMock(Group.class);
        Group group4 = createNiceMock(Group.class);
        Group group5 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group3.getGroupName()).andReturn("group3").anyTimes();
        expect(group4.getGroupName()).andReturn("group4").anyTimes();
        expect(group5.getGroupName()).andReturn("group5").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        expect(group3.isLdapGroup()).andReturn(false).anyTimes();
        expect(group4.isLdapGroup()).andReturn(true).anyTimes();
        expect(group5.isLdapGroup()).andReturn(false).anyTimes();
        List<Group> groupList = Arrays.asList(group1, group2, group3, group4, group5);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllGroups()).andReturn(groupList);
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2, group3, group4, group5);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getExternalLdapGroupInfo").addMockedMethod("refreshGroupMembers").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup2 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup3 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup4 = createNiceMock(LdapGroupDto.class);
        expect(externalGroup1.getGroupName()).andReturn("group4").anyTimes();
        expect(externalGroup2.getGroupName()).andReturn("group3").anyTimes();
        expect(externalGroup3.getGroupName()).andReturn("group6").anyTimes();
        expect(externalGroup4.getGroupName()).andReturn("group7").anyTimes();
        LdapBatchDto batchInfo = new LdapBatchDto();
        Set<LdapGroupDto> externalGroups = createSet(externalGroup1, externalGroup2, externalGroup3, externalGroup4);
        for (LdapGroupDto externalGroup : externalGroups) {
            populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
            expectLastCall();
        }
        expect(getExternalLdapGroupInfo()).andReturn(externalGroups);
        replay(externalGroup1, externalGroup2, externalGroup3, externalGroup4);
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapGroups(batchInfo, false);
        verifyGroupsInSet(result.getGroupsToBeRemoved(), Sets.newHashSet("group2"));
        verifyGroupsInSet(result.getGroupsToBecomeLdap(), Sets.newHashSet("group3"));
        verifyGroupsInSet(result.getGroupsToBeCreated(), Sets.newHashSet("group6", "group7"));
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapGroups_add() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(false).anyTimes();
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllGroups()).andReturn(Arrays.asList(group1, group2));
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("refreshGroupMembers").addMockedMethod("getExternalLdapGroupInfo").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup2 = createNiceMock(LdapGroupDto.class);
        expect(externalGroup1.getGroupName()).andReturn("group4").anyTimes();
        expect(externalGroup2.getGroupName()).andReturn("group3").anyTimes();
        LdapBatchDto batchInfo = new LdapBatchDto();
        Set<LdapGroupDto> externalGroups = createSet(externalGroup1, externalGroup2);
        for (LdapGroupDto externalGroup : externalGroups) {
            populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
            expectLastCall();
        }
        expect(getExternalLdapGroupInfo()).andReturn(externalGroups);
        replay(externalGroup1, externalGroup2);
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapGroups(batchInfo, false);
        verifyGroupsInSet(result.getGroupsToBeCreated(), Sets.newHashSet("group3", "group4"));
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapGroups_remove() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        Group group3 = createNiceMock(Group.class);
        Group group4 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group3.getGroupName()).andReturn("group3").anyTimes();
        expect(group4.getGroupName()).andReturn("group4").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        expect(group3.isLdapGroup()).andReturn(true).anyTimes();
        expect(group4.isLdapGroup()).andReturn(true).anyTimes();
        List<Group> groupList = Arrays.asList(group1, group2, group3, group4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllGroups()).andReturn(groupList);
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2, group3, group4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("refreshGroupMembers").addMockedMethod("getExternalLdapGroupInfo").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        expect(externalGroup1.getGroupName()).andReturn("group3").anyTimes();
        LdapBatchDto batchInfo = new LdapBatchDto();
        Set<LdapGroupDto> externalGroups = createSet(externalGroup1);
        for (LdapGroupDto externalGroup : externalGroups) {
            populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
            expectLastCall();
        }
        expect(getExternalLdapGroupInfo()).andReturn(externalGroups);
        replay(populator);
        replay(externalGroup1);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapGroups(batchInfo, false);
        verifyGroupsInSet(result.getGroupsToBeRemoved(), Sets.newHashSet("group2", "group4"));
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapGroups_update() throws Exception {
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        Group group3 = createNiceMock(Group.class);
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        expect(group3.getGroupName()).andReturn("group3").anyTimes();
        expect(group1.isLdapGroup()).andReturn(false).anyTimes();
        expect(group2.isLdapGroup()).andReturn(false).anyTimes();
        expect(group3.isLdapGroup()).andReturn(false).anyTimes();
        List<Group> groupList = Arrays.asList(group1, group2, group3);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllGroups()).andReturn(groupList);
        expect(users.getAllUsers()).andReturn(Collections.emptyList());
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(group1, group2, group3);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("refreshGroupMembers").addMockedMethod("getExternalLdapGroupInfo").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup1 = createNiceMock(LdapGroupDto.class);
        LdapGroupDto externalGroup2 = createNiceMock(LdapGroupDto.class);
        expect(externalGroup1.getGroupName()).andReturn("group2").anyTimes();
        expect(externalGroup2.getGroupName()).andReturn("group3").anyTimes();
        LdapBatchDto batchInfo = new LdapBatchDto();
        Set<LdapGroupDto> externalGroups = createSet(externalGroup1, externalGroup2);
        for (LdapGroupDto externalGroup : externalGroups) {
            populator.refreshGroupMembers(eq(batchInfo), eq(externalGroup), EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyObject(), anyBoolean(), eq(false));
            expectLastCall();
        }
        expect(getExternalLdapGroupInfo()).andReturn(externalGroups);
        replay(populator);
        replay(externalGroup1, externalGroup2);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapGroups(batchInfo, false);
        verifyGroupsInSet(result.getGroupsToBecomeLdap(), Sets.newHashSet("group2", "group3"));
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapUsers() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        User user4 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("synced_user1").anyTimes();
        expect(user2.getUserName()).andReturn("synced_user2").anyTimes();
        expect(user3.getUserName()).andReturn("unsynced_user1").anyTimes();
        expect(user4.getUserName()).andReturn("unsynced_user2").anyTimes();
        expect(user1.isLdapUser()).andReturn(true).anyTimes();
        expect(user2.isLdapUser()).andReturn(true).anyTimes();
        expect(user3.isLdapUser()).andReturn(false).anyTimes();
        expect(user4.isLdapUser()).andReturn(false).anyTimes();
        List<User> userList = Arrays.asList(user1, user2, user3, user4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user3, user2, user4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getExternalLdapUserInfo").withConstructor(configurationProvider, users).createNiceMock();
        LdapUserDto externalUser1 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser3 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser4 = createNiceMock(LdapUserDto.class);
        expect(externalUser1.getUserName()).andReturn("synced_user2").anyTimes();
        expect(externalUser2.getUserName()).andReturn("unsynced_user2").anyTimes();
        expect(externalUser3.getUserName()).andReturn("external_user1").anyTimes();
        expect(externalUser4.getUserName()).andReturn("external_user2").anyTimes();
        replay(externalUser1, externalUser2, externalUser3, externalUser4);
        expect(getExternalLdapUserInfo()).andReturn(createSet(externalUser1, externalUser2, externalUser3, externalUser4));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapUsers(new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersToBeRemoved(), Sets.newHashSet("synced_user1"));
        verifyUsersInSet(result.getUsersToBeCreated(), Sets.newHashSet("external_user1", "external_user2"));
        verifyUsersInSet(result.getUsersToBecomeLdap(), Sets.newHashSet("unsynced_user2"));
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapSkipLocal() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("local1").anyTimes();
        expect(user2.getUserName()).andReturn("local2").anyTimes();
        expect(user3.getUserName()).andReturn("ldap1").anyTimes();
        expect(user1.isLdapUser()).andReturn(false).anyTimes();
        expect(user2.isLdapUser()).andReturn(false).anyTimes();
        expect(user3.isLdapUser()).andReturn(true).anyTimes();
        List<User> userList = Arrays.asList(user1, user2, user3);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.syncCollisionHandlingBehavior()).andReturn(SKIP).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user3, user2);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getExternalLdapUserInfo").withConstructor(configurationProvider, users).createNiceMock();
        LdapUserDto externalUser1 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser3 = createNiceMock(LdapUserDto.class);
        expect(externalUser1.getUserName()).andReturn("local1").anyTimes();
        expect(externalUser2.getUserName()).andReturn("local2").anyTimes();
        expect(externalUser3.getUserName()).andReturn("ldap1").anyTimes();
        replay(externalUser1, externalUser2, externalUser3);
        expect(getExternalLdapUserInfo()).andReturn(createSet(externalUser1, externalUser2, externalUser3));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapUsers(new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersSkipped(), Sets.newHashSet("local1", "local2"));
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapUsers_add() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("user1").anyTimes();
        expect(user2.getUserName()).andReturn("user2").anyTimes();
        expect(user1.isLdapUser()).andReturn(false).anyTimes();
        expect(user2.isLdapUser()).andReturn(false).anyTimes();
        List<User> userList = Arrays.asList(user1, user2);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user2);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getExternalLdapUserInfo").withConstructor(configurationProvider, users).createNiceMock();
        LdapUserDto externalUser1 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        expect(externalUser1.getUserName()).andReturn("user3").anyTimes();
        expect(externalUser2.getUserName()).andReturn("user4").anyTimes();
        replay(externalUser1, externalUser2);
        expect(getExternalLdapUserInfo()).andReturn(createSet(externalUser1, externalUser2));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapUsers(new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersToBeCreated(), Sets.newHashSet("user3", "user4"));
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapUsers_remove() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("user1").anyTimes();
        expect(user2.getUserName()).andReturn("user2").anyTimes();
        expect(user3.getUserName()).andReturn("user3").anyTimes();
        expect(user1.isLdapUser()).andReturn(true).anyTimes();
        expect(user2.isLdapUser()).andReturn(false).anyTimes();
        expect(user3.isLdapUser()).andReturn(true).anyTimes();
        List<User> userList = Arrays.asList(user1, user2, user3);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user2, user3);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getExternalLdapUserInfo").withConstructor(configurationProvider, users).createNiceMock();
        expect(getExternalLdapUserInfo()).andReturn(Collections.emptySet());
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapUsers(new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersToBeRemoved(), Sets.newHashSet("user3", "user1"));
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeAllLdapUsers_update() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("user1").anyTimes();
        expect(user2.getUserName()).andReturn("user2").anyTimes();
        expect(user3.getUserName()).andReturn("user3").anyTimes();
        expect(user1.isLdapUser()).andReturn(true).anyTimes();
        expect(user2.isLdapUser()).andReturn(false).anyTimes();
        expect(user3.isLdapUser()).andReturn(false).anyTimes();
        List<User> userList = Arrays.asList(user1, user2, user3);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user2, user3);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getExternalLdapUserInfo").withConstructor(configurationProvider, users).createNiceMock();
        LdapUserDto externalUser1 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        expect(externalUser1.getUserName()).andReturn("user1").anyTimes();
        expect(externalUser2.getUserName()).andReturn("user3").anyTimes();
        replay(externalUser1, externalUser2);
        expect(getExternalLdapUserInfo()).andReturn(createSet(externalUser1, externalUser2));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeAllLdapUsers(new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersToBecomeLdap(), Sets.newHashSet("user3"));
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeExistingLdapUsers() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        User user4 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("synced_user1").anyTimes();
        expect(user2.getUserName()).andReturn("synced_user2").anyTimes();
        expect(user3.getUserName()).andReturn("unsynced_user1").anyTimes();
        expect(user4.getUserName()).andReturn("unsynced_user2").anyTimes();
        expect(user1.isLdapUser()).andReturn(true).anyTimes();
        expect(user2.isLdapUser()).andReturn(true).anyTimes();
        expect(user3.isLdapUser()).andReturn(false).anyTimes();
        expect(user4.isLdapUser()).andReturn(false).anyTimes();
        List<User> userList = Arrays.asList(user1, user2, user3, user4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user2, user3, user4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapUsers").withConstructor(configurationProvider, users).createNiceMock();
        expect(getLdapUsers("synced_user1")).andReturn(Collections.emptySet());
        expect(getLdapUsers("synced_user2")).andReturn(Collections.singleton(createNiceMock(LdapUserDto.class)));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeExistingLdapUsers(new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersToBeRemoved(), Sets.newHashSet("synced_user1"));
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeLdapUsers_allExist() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        User user4 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("user1").anyTimes();
        expect(user2.getUserName()).andReturn("user2").anyTimes();
        expect(user3.getUserName()).andReturn("user5").anyTimes();
        expect(user4.getUserName()).andReturn("user6").anyTimes();
        expect(user1.isLdapUser()).andReturn(false).anyTimes();
        expect(user2.isLdapUser()).andReturn(true).anyTimes();
        expect(user3.isLdapUser()).andReturn(true).anyTimes();
        expect(user4.isLdapUser()).andReturn(false).anyTimes();
        List<User> userList = Arrays.asList(user1, user2, user3, user4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user2, user3, user4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapUsers").withConstructor(configurationProvider, users).createNiceMock();
        LdapUserDto externalUser1 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser3 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser4 = createNiceMock(LdapUserDto.class);
        expect(externalUser1.getUserName()).andReturn("user1").anyTimes();
        expect(externalUser2.getUserName()).andReturn("user2").anyTimes();
        expect(externalUser3.getUserName()).andReturn("xuser3").anyTimes();
        expect(externalUser4.getUserName()).andReturn("xuser4").anyTimes();
        replay(externalUser1, externalUser2, externalUser3, externalUser4);
        expect(getLdapUsers("xuser*")).andReturn(createSet(externalUser3, externalUser4));
        expect(getLdapUsers("user1")).andReturn(Collections.singleton(externalUser1));
        expect(getLdapUsers("user2")).andReturn(Collections.singleton(externalUser2));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeLdapUsers(createSet("user1", "user2", "xuser*"), new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersToBeCreated(), Sets.newHashSet("xuser3", "xuser4"));
        verifyUsersInSet(result.getUsersToBecomeLdap(), Sets.newHashSet("user1"));
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeLdapUsers_add() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        User user4 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("user1").anyTimes();
        expect(user2.getUserName()).andReturn("user2").anyTimes();
        expect(user3.getUserName()).andReturn("user5").anyTimes();
        expect(user4.getUserName()).andReturn("user6").anyTimes();
        expect(user1.isLdapUser()).andReturn(false).anyTimes();
        expect(user2.isLdapUser()).andReturn(true).anyTimes();
        expect(user3.isLdapUser()).andReturn(true).anyTimes();
        expect(user4.isLdapUser()).andReturn(false).anyTimes();
        List<User> userList = Arrays.asList(user1, user2, user3, user4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user2, user3, user4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapUsers").withConstructor(configurationProvider, users).createNiceMock();
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser3 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser4 = createNiceMock(LdapUserDto.class);
        expect(externalUser2.getUserName()).andReturn("user2").anyTimes();
        expect(externalUser3.getUserName()).andReturn("xuser3").anyTimes();
        expect(externalUser4.getUserName()).andReturn("xuser4").anyTimes();
        replay(externalUser2, externalUser3, externalUser4);
        expect(getLdapUsers("xuser*")).andReturn(createSet(externalUser3, externalUser4));
        expect(getLdapUsers("user2")).andReturn(Collections.singleton(externalUser2));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeLdapUsers(createSet("user2", "xuser*"), new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersToBeCreated(), Sets.newHashSet("xuser3", "xuser4"));
        Assert.assertTrue(result.getUsersToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSynchronizeLdapUsers_update() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        User user4 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("user1").anyTimes();
        expect(user2.getUserName()).andReturn("user2").anyTimes();
        expect(user3.getUserName()).andReturn("user5").anyTimes();
        expect(user4.getUserName()).andReturn("user6").anyTimes();
        expect(user1.isLdapUser()).andReturn(false).anyTimes();
        expect(user2.isLdapUser()).andReturn(true).anyTimes();
        expect(user3.isLdapUser()).andReturn(true).anyTimes();
        expect(user4.isLdapUser()).andReturn(false).anyTimes();
        List<User> userList = Arrays.asList(user1, user2, user3, user4);
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(users.getAllUsers()).andReturn(userList);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user2, user3, user4);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapUsers").withConstructor(configurationProvider, users).createNiceMock();
        LdapUserDto externalUser1 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser3 = createNiceMock(LdapUserDto.class);
        expect(externalUser1.getUserName()).andReturn("user1").anyTimes();
        expect(externalUser2.getUserName()).andReturn("user2").anyTimes();
        expect(externalUser3.getUserName()).andReturn("user6").anyTimes();
        replay(externalUser2, externalUser3, externalUser1);
        expect(getLdapUsers("user1")).andReturn(Collections.singleton(externalUser1));
        expect(getLdapUsers("user2")).andReturn(Collections.singleton(externalUser2));
        expect(getLdapUsers("user6")).andReturn(Collections.singleton(externalUser3));
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        LdapBatchDto result = populator.synchronizeLdapUsers(createSet("user2", "user1", "user6"), new LdapBatchDto(), false);
        verifyUsersInSet(result.getUsersToBecomeLdap(), Sets.newHashSet("user1", "user6"));
        Assert.assertTrue(result.getUsersToBeCreated().isEmpty());
        Assert.assertTrue(result.getUsersToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(result.getGroupsToBeCreated().isEmpty());
        Assert.assertTrue(result.getGroupsToBecomeLdap().isEmpty());
        Assert.assertTrue(result.getMembershipToAdd().isEmpty());
        Assert.assertTrue(result.getMembershipToRemove().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = AmbariException.class)
    public void testSynchronizeLdapUsers_absent() throws Exception {
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapUsers").withConstructor(configurationProvider, users).createNiceMock();
        LdapUserDto externalUser1 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser3 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser4 = createNiceMock(LdapUserDto.class);
        expect(externalUser1.getUserName()).andReturn("user1").anyTimes();
        expect(externalUser2.getUserName()).andReturn("user2").anyTimes();
        expect(externalUser3.getUserName()).andReturn("xuser3").anyTimes();
        expect(externalUser4.getUserName()).andReturn("xuser4").anyTimes();
        replay(externalUser1, externalUser2, externalUser3, externalUser4);
        expect(getLdapUsers("xuser*")).andReturn(createSet(externalUser3, externalUser4));
        expect(getLdapUsers("user1")).andReturn(Collections.singleton(externalUser1));
        expect(getLdapUsers("user2")).andReturn(Collections.emptySet());
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        populator.synchronizeLdapUsers(createSet("user1", "user2", "xuser*"), new LdapBatchDto(), false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRefreshGroupMembers() throws Exception {
        User user1 = createNiceMock(User.class);
        User user2 = createNiceMock(User.class);
        User user3 = createNiceMock(User.class);
        User user4 = createNiceMock(User.class);
        expect(user1.getUserName()).andReturn("user1").anyTimes();
        expect(user2.getUserName()).andReturn("user2").anyTimes();
        expect(user3.getUserName()).andReturn("user3").anyTimes();
        expect(user4.getUserName()).andReturn("user4").anyTimes();
        expect(user1.isLdapUser()).andReturn(false).anyTimes();
        expect(user2.isLdapUser()).andReturn(true).anyTimes();
        expect(user3.isLdapUser()).andReturn(true).anyTimes();
        expect(user4.isLdapUser()).andReturn(false).anyTimes();
        Group group1 = createNiceMock(Group.class);
        Group group2 = createNiceMock(Group.class);
        expect(group1.isLdapGroup()).andReturn(true).anyTimes();
        expect(group2.isLdapGroup()).andReturn(true).anyTimes();
        expect(group1.getGroupName()).andReturn("group1").anyTimes();
        expect(group2.getGroupName()).andReturn("group2").anyTimes();
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(ldapServerProperties.getGroupNamingAttr()).andReturn("cn").anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("uid").anyTimes();
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration);
        replay(user1, user2, user3, user4);
        replay(group1, group2);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = createMockBuilder(AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance.class).addMockedMethod("getLdapUserByMemberAttr").addMockedMethod("getLdapGroupByMemberAttr").addMockedMethod("getInternalMembers").withConstructor(configurationProvider, users).createNiceMock();
        LdapGroupDto externalGroup = createNiceMock(LdapGroupDto.class);
        expect(externalGroup.getGroupName()).andReturn("group1").anyTimes();
        expect(externalGroup.getMemberAttributes()).andReturn(createSet("user1", "user2", "user4", "user6")).anyTimes();
        replay(externalGroup);
        Map<String, User> internalMembers = new HashMap<>();
        internalMembers.put("user1", user1);
        internalMembers.put("user3", user3);
        internalMembers.put("user4", user4);
        LdapBatchDto batchInfo = new LdapBatchDto();
        LdapUserDto externalUser1 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser2 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser3 = createNiceMock(LdapUserDto.class);
        LdapUserDto externalUser4 = createNiceMock(LdapUserDto.class);
        expect(externalUser1.getUserName()).andReturn("user1").anyTimes();
        expect(externalUser2.getUserName()).andReturn("user2").anyTimes();
        expect(externalUser3.getUserName()).andReturn("user4").anyTimes();
        expect(externalUser4.getUserName()).andReturn("user6").anyTimes();
        replay(externalUser1, externalUser2, externalUser3, externalUser4);
        expect(getLdapUserByMemberAttr("user1")).andReturn(externalUser1).anyTimes();
        expect(getLdapUserByMemberAttr("user2")).andReturn(externalUser2).anyTimes();
        expect(getLdapUserByMemberAttr("user4")).andReturn(null).anyTimes();
        expect(getLdapGroupByMemberAttr("user4")).andReturn(externalGroup).anyTimes();
        expect(getLdapUserByMemberAttr("user6")).andReturn(externalUser4).anyTimes();
        expect(getInternalMembers("group1")).andReturn(internalMembers).anyTimes();
        replay(populator);
        populator.setLdapTemplate(ldapTemplate);
        populator.setLdapServerProperties(ldapServerProperties);
        Map<String, User> internalUsers = new HashMap<>();
        internalUsers.putAll(internalMembers);
        internalUsers.put("user2", user2);
        Map<String, Group> internalGroups = new HashMap<>();
        internalGroups.put("group2", group2);
        populator.refreshGroupMembers(batchInfo, externalGroup, internalUsers, internalGroups, null, true, false);
        verifyMembershipInSet(batchInfo.getMembershipToAdd(), Sets.newHashSet("user1", "user2", "user6"));
        verifyMembershipInSet(batchInfo.getMembershipToRemove(), Sets.newHashSet("user3", "user4"));
        verifyUsersInSet(batchInfo.getUsersToBeCreated(), Sets.newHashSet("user6"));
        verifyUsersInSet(batchInfo.getUsersToBecomeLdap(), Sets.newHashSet("user1"));
        Assert.assertTrue(batchInfo.getGroupsToBecomeLdap().isEmpty());
        verifyGroupsInSet(batchInfo.getGroupsToBeCreated(), Sets.newHashSet("group1"));
        Assert.assertTrue(batchInfo.getGroupsToBeRemoved().isEmpty());
        Assert.assertTrue(batchInfo.getUsersToBeRemoved().isEmpty());
        verify(populator.loadLdapTemplate(), populator);
    }

    @Test
    @SuppressWarnings({ "serial", "unchecked" })
    public void testCleanUpLdapUsersWithoutGroup() throws AmbariException {
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        final AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        final Users users = createNiceMock(Users.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        final GroupEntity ldapGroup = new GroupEntity();
        ldapGroup.setGroupId(1);
        ldapGroup.setGroupName("ldapGroup");
        ldapGroup.setGroupType(LDAP);
        ldapGroup.setMemberEntities(new HashSet());
        final User ldapUserWithoutGroup = createLdapUserWithoutGroup();
        final User ldapUserWithGroup = createLdapUserWithGroup(ldapGroup);
        final User localUserWithoutGroup = createLocalUserWithoutGroup();
        final User localUserWithGroup = createLocalUserWithGroup(ldapGroup);
        final List<User> allUsers = new ArrayList<User>() {
            {
                add(ldapUserWithoutGroup);
                add(ldapUserWithGroup);
                add(localUserWithoutGroup);
                add(localUserWithGroup);
            }
        };
        expect(users.getAllUsers()).andReturn(new ArrayList(allUsers));
        final List<User> removedUsers = new ArrayList<>();
        final Capture<User> userCapture = EasyMock.newCapture();
        users.removeUser(capture(userCapture));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                removedUsers.add(userCapture.getValue());
                allUsers.remove(userCapture.getValue());
                return null;
            }
        });
        replay(users, configurationProvider, configuration);
        final AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        populator.setLdapTemplate(createNiceMock(LdapTemplate.class));
        populator.setLdapServerProperties(createNiceMock(LdapServerProperties.class));
        cleanUpLdapUsersWithoutGroup();
        assertEquals(removedUsers.size(), 1);
        assertEquals(allUsers.size(), 3);
        Assert.assertTrue(allUsers.contains(ldapUserWithGroup));
        Assert.assertTrue(allUsers.contains(localUserWithoutGroup));
        Assert.assertTrue(allUsers.contains(localUserWithGroup));
        org.junit.Assert.assertEquals(removedUsers.get(0), ldapUserWithoutGroup);
        verify(users);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetLdapUserByMemberAttr() throws Exception {
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        Capture<ContextMapper> contextMapperCapture = EasyMock.newCapture();
        PagedResultsDirContextProcessor processor = createNiceMock(PagedResultsDirContextProcessor.class);
        PagedResultsCookie cookie = createNiceMock(PagedResultsCookie.class);
        LdapUserDto dto = new LdapUserDto();
        List<LdapUserDto> list = new LinkedList<>();
        list.add(dto);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.isPaginationEnabled()).andReturn(true).anyTimes();
        expect(ldapServerProperties.getUserObjectClass()).andReturn("objectClass").anyTimes();
        expect(ldapServerProperties.getDnAttribute()).andReturn("dn").anyTimes();
        expect(ldapServerProperties.getBaseDN()).andReturn("cn=testUser,ou=Ambari,dc=SME,dc=support,dc=com").anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("uid").anyTimes();
        expect(processor.getCookie()).andReturn(cookie).anyTimes();
        expect(cookie.getCookie()).andReturn(null).anyTimes();
        expect(ldapTemplate.search(eq(LdapUtils.newLdapName("cn=testUser,ou=Ambari,dc=SME,dc=support,dc=com")), eq("(&(objectClass=objectClass)(uid=foo))"), anyObject(SearchControls.class), capture(contextMapperCapture), eq(processor))).andReturn(list);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration, processor, cookie);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        populator.setLdapTemplate(ldapTemplate);
        populator.setProcessor(processor);
        org.junit.Assert.assertEquals(dto, getLdapUserByMemberAttr("foo"));
        verify(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration, processor, cookie);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetLdapUserByMemberAttrNoPagination() throws Exception {
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapTemplate ldapTemplate = createNiceMock(LdapTemplate.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        Capture<ContextMapper> contextMapperCapture = EasyMock.newCapture();
        PagedResultsDirContextProcessor processor = createNiceMock(PagedResultsDirContextProcessor.class);
        PagedResultsCookie cookie = createNiceMock(PagedResultsCookie.class);
        LdapUserDto dto = new LdapUserDto();
        List<LdapUserDto> list = new LinkedList<>();
        list.add(dto);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.isPaginationEnabled()).andReturn(false).anyTimes();
        expect(ldapServerProperties.getUserObjectClass()).andReturn("objectClass").anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("uid").anyTimes();
        expect(ldapServerProperties.getDnAttribute()).andReturn("dn").anyTimes();
        expect(ldapServerProperties.getBaseDN()).andReturn("cn=testUser,ou=Ambari,dc=SME,dc=support,dc=com").anyTimes();
        expect(ldapTemplate.search(eq(LdapUtils.newLdapName("cn=testUser,ou=Ambari,dc=SME,dc=support,dc=com")), eq("(&(objectClass=objectClass)(uid=foo))"), anyObject(SearchControls.class), capture(contextMapperCapture))).andReturn(list);
        replay(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration, processor, cookie);
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        populator.setLdapTemplate(ldapTemplate);
        populator.setProcessor(processor);
        org.junit.Assert.assertEquals(dto, getLdapUserByMemberAttr("foo"));
        verify(ldapTemplate, ldapServerProperties, users, configurationProvider, configuration, processor, cookie);
    }

    @Test
    public void testLdapUserContextMapper_uidIsNull() throws Exception {
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("cn").once();
        expect(ldapServerProperties.getBaseDN()).andReturn("dc=SME,dc=support,dc=com").anyTimes();
        DirContextAdapter adapter = createNiceMock(DirContextAdapter.class);
        expect(adapter.getStringAttribute("cn")).andReturn("testUser");
        expect(adapter.getStringAttribute("uid")).andReturn(null);
        expect(adapter.getNameInNamespace()).andReturn("cn=testUser,ou=Ambari,dc=SME,dc=support,dc=com");
        PowerMock.mockStatic(AmbariLdapUtils.class);
        expect(AmbariLdapUtils.isLdapObjectOutOfScopeFromBaseDn(adapter, "dc=SME,dc=support,dc=com")).andReturn(false).anyTimes();
        replay(adapter, ldapServerProperties);
        PowerMock.replayAll();
        AmbariLdapDataPopulator.LdapUserContextMapper ldapUserContextMapper = new AmbariLdapDataPopulator.LdapUserContextMapper(ldapServerProperties);
        LdapUserDto userDto = ((LdapUserDto) (ldapUserContextMapper.mapFromContext(adapter)));
        Assert.assertNotNull(userDto);
        Assert.assertNull(userDto.getUid());
        assertEquals("testuser", userDto.getUserName());
        assertEquals("cn=testuser,ou=ambari,dc=sme,dc=support,dc=com", userDto.getDn());
    }

    @Test
    public void testLdapUserContextMapper_uidAndUsernameAreNull() throws Exception {
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("cn").once();
        DirContextAdapter adapter = createNiceMock(DirContextAdapter.class);
        expect(adapter.getStringAttribute("cn")).andReturn(null);
        expect(adapter.getStringAttribute("uid")).andReturn(null);
        replay(ldapServerProperties, adapter);
        AmbariLdapDataPopulator.LdapUserContextMapper ldapUserContextMapper = new AmbariLdapDataPopulator.LdapUserContextMapper(ldapServerProperties);
        Assert.assertNull(ldapUserContextMapper.mapFromContext(adapter));
    }

    @Test
    public void testLdapUserContextMapper() throws Exception {
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("cn").once();
        expect(ldapServerProperties.getBaseDN()).andReturn("dc=SME,dc=support,dc=com").anyTimes();
        DirContextAdapter adapter = createNiceMock(DirContextAdapter.class);
        expect(adapter.getStringAttribute("cn")).andReturn("testUser");
        expect(adapter.getStringAttribute("uid")).andReturn("UID1");
        expect(adapter.getNameInNamespace()).andReturn("cn=testUser,ou=Ambari,dc=SME,dc=support,dc=com");
        PowerMock.mockStatic(AmbariLdapUtils.class);
        expect(AmbariLdapUtils.isLdapObjectOutOfScopeFromBaseDn(adapter, "dc=SME,dc=support,dc=com")).andReturn(false).anyTimes();
        replay(ldapServerProperties, adapter);
        PowerMock.replayAll();
        AmbariLdapDataPopulator.LdapUserContextMapper ldapUserContextMapper = new AmbariLdapDataPopulator.LdapUserContextMapper(ldapServerProperties);
        LdapUserDto userDto = ((LdapUserDto) (ldapUserContextMapper.mapFromContext(adapter)));
        Assert.assertNotNull(userDto);
        assertEquals("uid1", userDto.getUid());
        assertEquals("testuser", userDto.getUserName());
        assertEquals("cn=testuser,ou=ambari,dc=sme,dc=support,dc=com", userDto.getDn());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsMemberAttributeBaseDn() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("UID");
        expect(ldapServerProperties.getGroupNamingAttr()).andReturn("CN");
        replay(configurationProvider, configuration, users, ldapServerProperties);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        boolean result = isMemberAttributeBaseDn("CN=mygroupname,OU=myOrganizationUnit,DC=apache,DC=org");
        // THEN
        Assert.assertTrue(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsMemberAttributeBaseDn_withUidMatch() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("UID");
        expect(ldapServerProperties.getGroupNamingAttr()).andReturn("CN");
        replay(configurationProvider, configuration, users, ldapServerProperties);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        boolean result = isMemberAttributeBaseDn("uid=myuid,OU=myOrganizationUnit,DC=apache,DC=org");
        // THEN
        Assert.assertTrue(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsMemberAttributeBaseDn_withLowerAndUpperCaseValue() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("uid");
        expect(ldapServerProperties.getGroupNamingAttr()).andReturn("CN");
        replay(configurationProvider, configuration, users, ldapServerProperties);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        boolean result = isMemberAttributeBaseDn("cn=mygroupname,OU=myOrganizationUnit,DC=apache,DC=org");
        // THEN
        Assert.assertTrue(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsMemberAttributeBaseDn_withWrongAttribute() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("uid");
        expect(ldapServerProperties.getGroupNamingAttr()).andReturn("CN");
        replay(configurationProvider, configuration, users, ldapServerProperties);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        boolean result = isMemberAttributeBaseDn("cnn=mygroupname,OU=myOrganizationUnit,DC=apache,DC=org");
        // THEN
        Assert.assertFalse(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsMemberAttributeBaseDn_withEmptyValues() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("");
        expect(ldapServerProperties.getGroupNamingAttr()).andReturn(null);
        replay(configurationProvider, configuration, users, ldapServerProperties);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        boolean result = isMemberAttributeBaseDn("cnn=mygroupname,OU=myOrganizationUnit,DC=apache,DC=org");
        // THEN
        Assert.assertFalse(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsMemberAttributeBaseDn_withDifferentUserAndGroupNameAttribute() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        Users users = createNiceMock(Users.class);
        LdapServerProperties ldapServerProperties = createNiceMock(LdapServerProperties.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        expect(configuration.getLdapServerProperties()).andReturn(ldapServerProperties).anyTimes();
        expect(ldapServerProperties.getUsernameAttribute()).andReturn("sAMAccountName");
        expect(ldapServerProperties.getGroupNamingAttr()).andReturn("groupOfNames");
        replay(configurationProvider, configuration, users, ldapServerProperties);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        boolean result = isMemberAttributeBaseDn("cn=mygroupname,OU=myOrganizationUnit,DC=apache,DC=org");
        // THEN
        Assert.assertTrue(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetUniqueIdMemberPattern() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        String syncUserMemberPattern = "(?<sid>.*);(?<guid>.*);(?<member>.*)";
        String memberAttribute = "<SID=...>;<GUID=...>;cn=member,dc=apache,dc=org";
        replay(configurationProvider, configuration);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        String result = populator.getUniqueIdByMemberPattern(memberAttribute, syncUserMemberPattern);
        // THEN
        assertEquals("cn=member,dc=apache,dc=org", result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetUniqueIdByMemberPatternWhenPatternIsWrong() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        String syncUserMemberPattern = "(?<sid>.*);(?<guid>.*);(?<mem>.*)";
        String memberAttribute = "<SID=...>;<GUID=...>;cn=member,dc=apache,dc=org";
        replay(configurationProvider, configuration);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        String result = populator.getUniqueIdByMemberPattern(memberAttribute, syncUserMemberPattern);
        // THEN
        assertEquals(memberAttribute, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetUniqueIdByMemberPatternWhenPatternIsEmpty() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        String memberAttribute = "<SID=...>;<GUID=...>;cn=member,dc=apache,dc=org";
        replay(configurationProvider, configuration);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        String result = populator.getUniqueIdByMemberPattern(memberAttribute, "");
        // THEN
        assertEquals(memberAttribute, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetUniqueIdByMemberPatternWhenMembershipAttributeIsNull() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        String syncUserMemberPattern = "(?<sid>.*);(?<guid>.*);(?<member>.*)";
        replay(configurationProvider, configuration);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        String result = getUniqueIdByMemberPattern(null, syncUserMemberPattern);
        // THEN
        Assert.assertNull(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateCustomMemberFilter() {
        // GIVEN
        final Provider<AmbariLdapConfiguration> configurationProvider = createNiceMock(Provider.class);
        AmbariLdapConfiguration configuration = createNiceMock(AmbariLdapConfiguration.class);
        expect(configurationProvider.get()).andReturn(configuration).anyTimes();
        Users users = createNiceMock(Users.class);
        replay(configurationProvider, configuration);
        // WHEN
        AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance populator = new AmbariLdapDataPopulatorTest.AmbariLdapDataPopulatorTestInstance(configurationProvider, users);
        Filter result = createCustomMemberFilter("myUid", "(&(objectclass=posixaccount)(uid={member}))");
        // THEN
        assertEquals("(&(objectclass=posixaccount)(uid=myUid))", result.encode());
    }

    private static int userIdCounter = 1;
}
