/**
 * Copyright 2009-2010 the original author or authors.
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
package org.springframework.batch.core.partition.support;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STARTING;
import ExitStatus.EXECUTING;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;


public class DefaultStepExecutionAggregatorTests {
    private StepExecutionAggregator aggregator = new DefaultStepExecutionAggregator();

    private JobExecution jobExecution = new JobExecution(11L);

    private StepExecution result = jobExecution.createStepExecution("aggregate");

    private StepExecution stepExecution1 = jobExecution.createStepExecution("foo:1");

    private StepExecution stepExecution2 = jobExecution.createStepExecution("foo:2");

    @Test
    public void testAggregateEmpty() {
        aggregator.aggregate(result, Collections.<StepExecution>emptySet());
    }

    @Test
    public void testAggregateNull() {
        aggregator.aggregate(result, null);
    }

    @Test
    public void testAggregateStatusSunnyDay() {
        stepExecution1.setStatus(COMPLETED);
        stepExecution2.setStatus(COMPLETED);
        aggregator.aggregate(result, Arrays.<StepExecution>asList(stepExecution1, stepExecution2));
        Assert.assertNotNull(result);
        Assert.assertEquals(STARTING, result.getStatus());
    }

    @Test
    public void testAggregateStatusFromFailure() {
        result.setStatus(FAILED);
        stepExecution1.setStatus(COMPLETED);
        stepExecution2.setStatus(COMPLETED);
        aggregator.aggregate(result, Arrays.<StepExecution>asList(stepExecution1, stepExecution2));
        Assert.assertNotNull(result);
        Assert.assertEquals(FAILED, result.getStatus());
    }

    @Test
    public void testAggregateStatusIncomplete() {
        stepExecution1.setStatus(COMPLETED);
        stepExecution2.setStatus(FAILED);
        aggregator.aggregate(result, Arrays.<StepExecution>asList(stepExecution1, stepExecution2));
        Assert.assertNotNull(result);
        Assert.assertEquals(FAILED, result.getStatus());
    }

    @Test
    public void testAggregateExitStatusSunnyDay() {
        stepExecution1.setExitStatus(EXECUTING);
        stepExecution2.setExitStatus(ExitStatus.FAILED);
        aggregator.aggregate(result, Arrays.<StepExecution>asList(stepExecution1, stepExecution2));
        Assert.assertNotNull(result);
        Assert.assertEquals(ExitStatus.FAILED.and(EXECUTING), result.getExitStatus());
    }

    @Test
    public void testAggregateCountsSunnyDay() {
        stepExecution1.setCommitCount(1);
        stepExecution1.setFilterCount(2);
        stepExecution1.setProcessSkipCount(3);
        stepExecution1.setReadCount(4);
        stepExecution1.setReadSkipCount(5);
        stepExecution1.setRollbackCount(6);
        stepExecution1.setWriteCount(7);
        stepExecution1.setWriteSkipCount(8);
        stepExecution2.setCommitCount(11);
        stepExecution2.setFilterCount(12);
        stepExecution2.setProcessSkipCount(13);
        stepExecution2.setReadCount(14);
        stepExecution2.setReadSkipCount(15);
        stepExecution2.setRollbackCount(16);
        stepExecution2.setWriteCount(17);
        stepExecution2.setWriteSkipCount(18);
        aggregator.aggregate(result, Arrays.<StepExecution>asList(stepExecution1, stepExecution2));
        Assert.assertEquals(12, result.getCommitCount());
        Assert.assertEquals(14, result.getFilterCount());
        Assert.assertEquals(16, result.getProcessSkipCount());
        Assert.assertEquals(18, result.getReadCount());
        Assert.assertEquals(20, result.getReadSkipCount());
        Assert.assertEquals(22, result.getRollbackCount());
        Assert.assertEquals(24, result.getWriteCount());
        Assert.assertEquals(26, result.getWriteSkipCount());
    }
}

