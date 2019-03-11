/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.orm.dao;


import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import org.apache.ambari.server.orm.entities.MpackEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link MpackDAO}.
 */
public class MpackDAOTest {
    private Injector m_injector;

    private MpackDAO m_dao;

    @Test
    public void testCreateFind() {
        List<MpackEntity> eDefinitions = new ArrayList<>();
        // create 2 definitions
        for (int i = 1; i < 3; i++) {
            MpackEntity definition = new MpackEntity();
            definition.setId(((new Long(100)) + i));
            definition.setMpackName(("testMpack" + i));
            definition.setRegistryId(Long.valueOf(i));
            definition.setMpackVersion(("3.0.0.0-12" + i));
            definition.setMpackUri((("http://c6401.ambari.apache.org:8080/resources/mpacks-repo/testMpack" + i) + "-3.0.0.0-123.tar.gz"));
            eDefinitions.add(definition);
            m_dao.create(definition);
        }
        List<MpackEntity> definitions = m_dao.findAll();
        Assert.assertNotNull(definitions);
        Assert.assertEquals(2, definitions.size());
        definitions = m_dao.findByNameVersion("testMpack1", "3.0.0.0-121");
        Assert.assertEquals(1, definitions.size());
        Assert.assertEquals(new Long(101), ((Long) (definitions.get(0).getId())));
        MpackEntity entity = m_dao.findById(new Long(102));
        Assert.assertEquals(entity.getMpackName(), "testMpack2");
        Assert.assertEquals(entity.getMpackVersion(), "3.0.0.0-122");
    }
}

