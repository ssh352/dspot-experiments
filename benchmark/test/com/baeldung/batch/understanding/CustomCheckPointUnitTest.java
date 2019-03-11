package com.baeldung.batch.understanding;


import BatchStatus.COMPLETED;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class CustomCheckPointUnitTest {
    @Test
    public void givenChunk_whenCustomCheckPoint_thenCommitCountIsThree() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("customCheckPoint", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        for (StepExecution stepExecution : jobOperator.getStepExecutions(executionId)) {
            if (stepExecution.getStepName().equals("firstChunkStep")) {
                jobOperator.getStepExecutions(executionId).stream().map(BatchTestHelper::getCommitCount).forEach(( count) -> assertEquals(3L, count.longValue()));
            }
        }
        Assertions.assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }
}

