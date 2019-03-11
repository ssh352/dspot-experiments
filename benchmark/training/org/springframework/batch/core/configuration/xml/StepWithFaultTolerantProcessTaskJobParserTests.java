/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.core.configuration.xml;


import BatchStatus.COMPLETED;
import Isolation.DEFAULT;
import Propagation.REQUIRED;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.RetryListener;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Thomas Risberg
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class StepWithFaultTolerantProcessTaskJobParserTests {
    @Autowired
    private Job job;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TestReader reader;

    @Autowired
    @Qualifier("listener")
    private TestListener listener;

    @Autowired
    private TestRetryListener retryListener;

    @Autowired
    private TestProcessor processor;

    @Autowired
    private TestWriter writer;

    @Autowired
    private StepParserStepFactoryBean<?, ?> factory;

    @SuppressWarnings("unchecked")
    @Test
    public void testStepWithTask() throws Exception {
        Assert.assertNotNull(job);
        Object ci = ReflectionTestUtils.getField(factory, "commitInterval");
        Assert.assertEquals("wrong chunk-size:", 10, ci);
        Object sl = ReflectionTestUtils.getField(factory, "skipLimit");
        Assert.assertEquals("wrong skip-limit:", 20, sl);
        Object rl = ReflectionTestUtils.getField(factory, "retryLimit");
        Assert.assertEquals("wrong retry-limit:", 3, rl);
        Object cc = ReflectionTestUtils.getField(factory, "cacheCapacity");
        Assert.assertEquals("wrong cache-capacity:", 100, cc);
        Assert.assertEquals("wrong transaction-attribute:", REQUIRED, ReflectionTestUtils.getField(factory, "propagation"));
        Assert.assertEquals("wrong transaction-attribute:", DEFAULT, ReflectionTestUtils.getField(factory, "isolation"));
        Assert.assertEquals("wrong transaction-attribute:", 10, ReflectionTestUtils.getField(factory, "transactionTimeout"));
        Object txq = ReflectionTestUtils.getField(factory, "readerTransactionalQueue");
        Assert.assertEquals("wrong reader-transactional-queue:", true, txq);
        Object te = ReflectionTestUtils.getField(factory, "taskExecutor");
        Assert.assertEquals("wrong task-executor:", ConcurrentTaskExecutor.class, te.getClass());
        Object listeners = ReflectionTestUtils.getField(factory, "stepExecutionListeners");
        Assert.assertEquals("wrong number of listeners:", 2, ((Set<StepExecutionListener>) (listeners)).size());
        Object retryListeners = ReflectionTestUtils.getField(factory, "retryListeners");
        Assert.assertEquals("wrong number of retry-listeners:", 2, ((RetryListener[]) (retryListeners)).length);
        Object streams = ReflectionTestUtils.getField(factory, "streams");
        Assert.assertEquals("wrong number of streams:", 1, ((ItemStream[]) (streams)).length);
        JobExecution jobExecution = jobRepository.createJobExecution(job.getName(), new JobParameters());
        job.execute(jobExecution);
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
        Assert.assertTrue(reader.isExecuted());
        Assert.assertTrue(reader.isOpened());
        Assert.assertTrue(processor.isExecuted());
        Assert.assertTrue(writer.isExecuted());
        Assert.assertTrue(listener.isExecuted());
        Assert.assertTrue(retryListener.isExecuted());
    }
}

