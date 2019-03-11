/**
 * Copyright 2013 the original author or authors.
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
package org.springframework.batch.test;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;


/**
 *
 *
 * @author Dave Syer
 * @since 2.1
 */
@ContextConfiguration
public class JobScopeTestExecutionListenerTests {
    private JobScopeTestExecutionListener listener = new JobScopeTestExecutionListener();

    @Test
    public void testDefaultJobContext() throws Exception {
        TestContext testContext = getTestContext(new Object());
        listener.prepareTestInstance(testContext);
        listener.beforeTestMethod(testContext);
        JobContext context = JobSynchronizationManager.getContext();
        Assert.assertNotNull(context);
        listener.afterTestMethod(testContext);
        Assert.assertNull(JobSynchronizationManager.getContext());
    }

    @Test
    public void testWithJobExecutionFactory() throws Exception {
        testExecutionContext(new JobScopeTestExecutionListenerTests.WithJobExecutionFactory());
    }

    @Test
    public void testWithParameters() throws Exception {
        testJobParameters(new JobScopeTestExecutionListenerTests.WithJobExecutionFactory());
    }

    @SuppressWarnings("unused")
    private static class WithJobExecutionFactory {
        public JobExecution getJobExecution() {
            JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution("job", 11L, 123L, new JobParametersBuilder().addString("foo", "spam").toJobParameters());
            jobExecution.getExecutionContext().putString("foo", "bar");
            return jobExecution;
        }
    }

    private final class MockTestContextManager extends TestContextManager {
        private MockTestContextManager(Object target, Class<?> testClass) throws Exception {
            super(testClass);
            prepareTestInstance(target);
        }

        public TestContext getContext() {
            return getTestContext();
        }
    }
}

