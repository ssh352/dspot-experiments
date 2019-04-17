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
package org.apache.ambari.server.orm.dao;


import RepositoryType.MAINT;
import RepositoryType.STANDARD;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.StackEntity;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;


/**
 * RepositoryVersionDAO unit tests.
 */
public class RepositoryVersionDAOTest {
    private static Injector injector;

    private static final StackId HDP_206 = new StackId("HDP", "2.0.6");

    private static final StackId OTHER_10 = new StackId("OTHER", "1.0");

    private static final StackId BAD_STACK = new StackId("BADSTACK", "1.0");

    private RepositoryVersionDAO repositoryVersionDAO;

    private StackDAO stackDAO;

    @Test
    public void testCreate() {
        UUID uuid = UUID.randomUUID();
        RepositoryVersionEntity first = createSingleRecord();
        Assert.assertNotNull(first);
        StackEntity stackEntity = stackDAO.find(first.getStackName(), first.getStackVersion());
        Assert.assertNotNull(stackEntity);
        // Assert the version must be unique
        RepositoryVersionEntity dupVersion = new RepositoryVersionEntity();
        dupVersion.setDisplayName(("display name " + uuid));
        dupVersion.addRepoOsEntities(new ArrayList());
        dupVersion.setStack(stackEntity);
        dupVersion.setVersion(first.getVersion());
        boolean exceptionThrown = false;
        try {
            repositoryVersionDAO.create(stackEntity, dupVersion.getVersion(), dupVersion.getDisplayName(), dupVersion.getRepoOsEntities());
        } catch (AmbariException e) {
            exceptionThrown = true;
            Assert.assertTrue(e.getMessage().contains("already exists"));
        }
        // Expected the exception to be thrown since the build version was reused in the second record.
        Assert.assertTrue(exceptionThrown);
        exceptionThrown = false;
        // The version must belong to the stack
        dupVersion.setVersion("2.3-1234");
        try {
            repositoryVersionDAO.create(stackEntity, dupVersion.getVersion(), dupVersion.getDisplayName(), dupVersion.getRepoOsEntities());
        } catch (AmbariException e) {
            exceptionThrown = true;
            Assert.assertTrue(e.getMessage().contains("needs to belong to stack"));
        }
        // Expected the exception to be thrown since the version does not belong to the stack.
        Assert.assertTrue(exceptionThrown);
        // Success
        dupVersion.setVersion(((stackEntity.getStackVersion()) + "-1234"));
        try {
            repositoryVersionDAO.create(stackEntity, dupVersion.getVersion(), dupVersion.getDisplayName(), dupVersion.getRepoOsEntities());
        } catch (AmbariException e) {
            Assert.fail("Did not expect a failure creating the Repository Version");
        }
    }

    @Test
    public void testFindByDisplayName() {
        createSingleRecord();
        Assert.assertNull(repositoryVersionDAO.findByDisplayName("non existing"));
        Assert.assertNotNull(repositoryVersionDAO.findByDisplayName("display name"));
    }

    @Test
    public void testFindByStackAndVersion() {
        createSingleRecord();
        Assert.assertNull(repositoryVersionDAO.findByStackAndVersion(RepositoryVersionDAOTest.BAD_STACK, "non existing"));
        Assert.assertNotNull(repositoryVersionDAO.findByStackAndVersion(RepositoryVersionDAOTest.HDP_206, "version"));
    }

    @Test
    public void testFindByStack() {
        createSingleRecord();
        Assert.assertEquals(0, repositoryVersionDAO.findByStack(RepositoryVersionDAOTest.BAD_STACK).size());
        Assert.assertEquals(1, repositoryVersionDAO.findByStack(RepositoryVersionDAOTest.HDP_206).size());
    }

    @Test
    public void testDelete() {
        createSingleRecord();
        Assert.assertNotNull(repositoryVersionDAO.findByStackAndVersion(RepositoryVersionDAOTest.HDP_206, "version"));
        final RepositoryVersionEntity entity = repositoryVersionDAO.findByStackAndVersion(RepositoryVersionDAOTest.HDP_206, "version");
        repositoryVersionDAO.remove(entity);
        Assert.assertNull(repositoryVersionDAO.findByStackAndVersion(RepositoryVersionDAOTest.HDP_206, "version"));
    }

    @Test
    public void testRemovePrefixFromVersion() {
        StackEntity hdp206StackEntity = stackDAO.find(RepositoryVersionDAOTest.HDP_206.getStackName(), RepositoryVersionDAOTest.HDP_206.getStackVersion());
        Assert.assertNotNull(hdp206StackEntity);
        final RepositoryVersionEntity hdp206RepoEntity = new RepositoryVersionEntity();
        hdp206RepoEntity.setDisplayName("HDP-2.0.6.0-1234");
        hdp206RepoEntity.addRepoOsEntities(new ArrayList());
        hdp206RepoEntity.setStack(hdp206StackEntity);
        hdp206RepoEntity.setVersion("HDP-2.0.6.0-1234");
        repositoryVersionDAO.create(hdp206RepoEntity);
        Assert.assertEquals("Failed to remove HDP stack prefix from version", "2.0.6.0-1234", hdp206RepoEntity.getVersion());
        Assert.assertNotNull(repositoryVersionDAO.findByDisplayName("HDP-2.0.6.0-1234"));
        Assert.assertNotNull(repositoryVersionDAO.findByStackAndVersion(RepositoryVersionDAOTest.HDP_206, "2.0.6.0-1234"));
        StackEntity other10StackEntity = stackDAO.find(RepositoryVersionDAOTest.OTHER_10.getStackName(), RepositoryVersionDAOTest.OTHER_10.getStackVersion());
        Assert.assertNotNull(other10StackEntity);
        final RepositoryVersionEntity other10RepoEntity = new RepositoryVersionEntity();
        other10RepoEntity.setDisplayName("OTHER-1.0.1.0-1234");
        other10RepoEntity.addRepoOsEntities(new ArrayList());
        other10RepoEntity.setStack(other10StackEntity);
        other10RepoEntity.setVersion("OTHER-1.0.1.0-1234");
        repositoryVersionDAO.create(other10RepoEntity);
        Assert.assertEquals("Failed to remove OTHER stack prefix from version", "1.0.1.0-1234", other10RepoEntity.getVersion());
        Assert.assertNotNull(repositoryVersionDAO.findByDisplayName("OTHER-1.0.1.0-1234"));
        Assert.assertNotNull(repositoryVersionDAO.findByStackAndVersion(RepositoryVersionDAOTest.OTHER_10, "1.0.1.0-1234"));
    }

    @Test
    public void testFindByStackAndType() {
        createSingleRecord();
        Assert.assertEquals(1, repositoryVersionDAO.findByStackAndType(RepositoryVersionDAOTest.HDP_206, STANDARD).size());
        Assert.assertEquals(0, repositoryVersionDAO.findByStackAndType(RepositoryVersionDAOTest.HDP_206, MAINT).size());
    }
}
