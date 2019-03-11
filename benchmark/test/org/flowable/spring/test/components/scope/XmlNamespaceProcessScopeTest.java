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
package org.flowable.spring.test.components.scope;


import org.flowable.engine.ProcessEngine;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * tests the scoped beans
 *
 * @author Josh Long
 */
// Ignored for the moment. Josh is working on this.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/flowable/spring/test/components/ScopingTests-context.xml")
@Ignore
public class XmlNamespaceProcessScopeTest {
    private ProcessScopeTestEngine processScopeTestEngine;

    @Autowired
    private ProcessEngine processEngine;

    @Test
    public void testScopedProxyCreation() throws Throwable {
        processScopeTestEngine.testScopedProxyCreation();
    }
}

