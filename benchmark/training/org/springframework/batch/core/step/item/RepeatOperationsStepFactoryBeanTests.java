/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.step.item;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.EmptyItemWriter;
import org.springframework.batch.core.step.JobRepositorySupport;
import org.springframework.batch.core.step.factory.SimpleStepFactoryBean;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatOperations;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;


/**
 *
 *
 * @author Dave Syer
 */
public class RepeatOperationsStepFactoryBeanTests extends TestCase {
    private SimpleStepFactoryBean<String, String> factory = new SimpleStepFactoryBean();

    private List<String> list;

    private JobExecution jobExecution = new JobExecution(new JobInstance(0L, "job"), new JobParameters());

    public void testType() throws Exception {
        TestCase.assertTrue(Step.class.isAssignableFrom(factory.getObjectType()));
    }

    public void testStepOperationsWithoutChunkListener() throws Exception {
        factory.setItemReader(new org.springframework.batch.item.support.ListItemReader(new ArrayList()));
        factory.setItemWriter(new EmptyItemWriter());
        factory.setJobRepository(new JobRepositorySupport());
        factory.setTransactionManager(new ResourcelessTransactionManager());
        factory.setStepOperations(new RepeatOperations() {
            @Override
            public RepeatStatus iterate(RepeatCallback callback) {
                list = new ArrayList<>();
                list.add("foo");
                return RepeatStatus.FINISHED;
            }
        });
        Step step = factory.getObject();
        step.execute(new org.springframework.batch.core.StepExecution(step.getName(), jobExecution));
        TestCase.assertEquals(1, list.size());
    }
}

