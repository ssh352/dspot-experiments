/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.beanstalk.integration;


import Headers.JOB_ID;
import Headers.RESULT;
import com.surftools.BeanstalkClient.Job;
import java.io.IOException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class DeleteProducerIntegrationTest extends BeanstalkCamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate direct;

    @Test
    public void testDelete() throws IOException, InterruptedException {
        long jobId = writer.put(0, 0, 5, new byte[0]);
        assertTrue("Valid Job Id", (jobId > 0));
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.allMessages().header(JOB_ID).isNotNull();
        resultEndpoint.allMessages().header(RESULT).isEqualTo(true);
        direct.sendBodyAndHeader(null, JOB_ID, jobId);
        assertMockEndpointsSatisfied();
        final Long messageJobId = resultEndpoint.getReceivedExchanges().get(0).getIn().getHeader(JOB_ID, Long.class);
        assertNotNull("Job ID in message", messageJobId);
        assertEquals("Message Job ID equals", jobId, messageJobId.longValue());
        final Job job = reader.peek(jobId);
        assertNull("Job has been deleted", job);
    }

    @Test(expected = CamelExecutionException.class)
    public void testNoJobId() throws IOException, InterruptedException {
        resultEndpoint.expectedMessageCount(0);
        direct.sendBody(new byte[0]);
        resultEndpoint.assertIsSatisfied();
        assertListSize("Number of exceptions", resultEndpoint.getFailures(), 1);
    }
}
