package org.springframework.batch.integration.launch;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.JobSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = { "/job-execution-context.xml" })
public class JobLaunchingMessageHandlerTests extends AbstractJUnit4SpringContextTests {
    JobLaunchRequestHandler messageHandler;

    JobLaunchingMessageHandlerTests.StubJobLauncher jobLauncher;

    @Test
    public void testSimpleDelivery() throws Exception {
        messageHandler.launch(new JobLaunchRequest(new JobSupport("testjob"), null));
        Assert.assertEquals("Wrong job count", 1, jobLauncher.jobs.size());
        Assert.assertEquals("Wrong job name", jobLauncher.jobs.get(0).getName(), "testjob");
    }

    private static class StubJobLauncher implements JobLauncher {
        List<Job> jobs = new ArrayList<>();

        List<JobParameters> parameters = new ArrayList<>();

        AtomicLong jobId = new AtomicLong();

        public JobExecution run(Job job, JobParameters jobParameters) {
            jobs.add(job);
            parameters.add(jobParameters);
            return new JobExecution(new org.springframework.batch.core.JobInstance(jobId.getAndIncrement(), job.getName()), jobParameters);
        }
    }
}

