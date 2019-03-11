/**
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
package org.flowable.engine.test.cfg;


import org.apache.ibatis.datasource.pooled.PoolState;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.flowable.engine.impl.test.AbstractTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Zheng Ji
 */
public class ForceCloseMybatisConnectionPoolTest {
    @Test
    public void testForceCloseMybatisConnectionPoolTrue() {
        // given
        // that the process engine is configured with forceCloseMybatisConnectionPool = true
        StandaloneInMemProcessEngineConfiguration standaloneInMemProcessEngineConfiguration = new StandaloneInMemProcessEngineConfiguration();
        standaloneInMemProcessEngineConfiguration.setJdbcUrl(("jdbc:h2:mem:flowable-bpmn-" + (this.getClass().getName())));
        standaloneInMemProcessEngineConfiguration.setForceCloseMybatisConnectionPool(true);
        ProcessEngine processEngine = standaloneInMemProcessEngineConfiguration.buildProcessEngine();
        PooledDataSource pooledDataSource = ((PooledDataSource) (standaloneInMemProcessEngineConfiguration.getDataSource()));
        PoolState state = pooledDataSource.getPoolState();
        AbstractTestCase.assertTrue(((state.getIdleConnectionCount()) > 0));
        // then
        // if the process engine is closed
        processEngine.close();
        // the idle connections are closed
        AbstractTestCase.assertEquals(0, state.getIdleConnectionCount());
    }

    @Test
    public void testForceCloseMybatisConnectionPoolFalse() {
        // given
        // that the process engine is configured with forceCloseMybatisConnectionPool = false
        StandaloneInMemProcessEngineConfiguration standaloneInMemProcessEngineConfiguration = new StandaloneInMemProcessEngineConfiguration();
        standaloneInMemProcessEngineConfiguration.setJdbcUrl(("jdbc:h2:mem:flowable-bpmn-" + (this.getClass().getName())));
        standaloneInMemProcessEngineConfiguration.setForceCloseMybatisConnectionPool(false);
        ProcessEngine processEngine = standaloneInMemProcessEngineConfiguration.buildProcessEngine();
        PooledDataSource pooledDataSource = ((PooledDataSource) (standaloneInMemProcessEngineConfiguration.getDataSource()));
        PoolState state = pooledDataSource.getPoolState();
        AbstractTestCase.assertTrue(((state.getIdleConnectionCount()) > 0));
        // then
        // if the process engine is closed
        processEngine.close();
        // the idle connections are not closed
        AbstractTestCase.assertTrue(((state.getIdleConnectionCount()) > 0));
        pooledDataSource.forceCloseAll();
        AbstractTestCase.assertEquals(0, state.getIdleConnectionCount());
    }
}

