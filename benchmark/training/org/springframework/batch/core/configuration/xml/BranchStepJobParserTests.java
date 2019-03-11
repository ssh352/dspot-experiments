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
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class BranchStepJobParserTests {
    @Autowired
    private Job job;

    @Autowired
    private JobRepository jobRepository;

    @Test
    public void testBranchStep() throws Exception {
        Assert.assertNotNull(job);
        JobExecution jobExecution = jobRepository.createJobExecution(job.getName(), new JobParameters());
        job.execute(jobExecution);
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
        List<String> names = new ArrayList<>();
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            names.add(stepExecution.getStepName());
        }
        Assert.assertEquals("[s1, s3]", names.toString());
    }
}

