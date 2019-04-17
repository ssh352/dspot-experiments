/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.impl.querycache;


import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.Member;
import com.hazelcast.map.impl.querycache.NodeQueryCacheContextTest.QuerySchedulerRepetitionTask;
import com.hazelcast.map.impl.querycache.NodeQueryCacheContextTest.QuerySchedulerTask;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheScheduler;
import com.hazelcast.nio.Address;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientQueryCacheContextTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TestHazelcastFactory factory;

    private QueryCacheContext context;

    @Test(expected = UnsupportedOperationException.class)
    public void testDestroy() {
        context.destroy();
    }

    @Test
    public void testGetMemberList() {
        Collection<Member> memberList = context.getMemberList();
        Assert.assertNotNull(memberList);
        Assert.assertEquals(1, memberList.size());
    }

    @Test
    public void testPartitionId() {
        int partitionId = context.getPartitionId("myKey");
        Assert.assertTrue((partitionId >= 0));
    }

    @Test
    public void testGetQueryCacheScheduler() {
        QueryCacheScheduler scheduler = context.getQueryCacheScheduler();
        Assert.assertNotNull(scheduler);
        final QuerySchedulerTask task = new QuerySchedulerTask();
        scheduler.execute(task);
        final QuerySchedulerRepetitionTask repetitionTask = new QuerySchedulerRepetitionTask();
        scheduler.scheduleWithRepetition(repetitionTask, 1);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(task.executed);
                Assert.assertTrue(((repetitionTask.counter.get()) > 1));
            }
        });
        scheduler.shutdown();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPublisherContext() {
        context.getPublisherContext();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetThisNodesAddress() {
        context.getThisNodesAddress();
    }

    @Test(expected = NullPointerException.class)
    public void testInvokerWrapper_invokeOnAllPartitions_whenExceptionOccurs_thenExceptionIsRethrown() throws Exception {
        ClientMessage request = Mockito.mock(ClientMessage.class);
        Mockito.when(request.setCorrelationId(((Long) (ArgumentMatchers.any())))).thenThrow(NullPointerException.class);
        context.getInvokerWrapper().invokeOnAllPartitions(request);
    }

    @Test(expected = NullPointerException.class)
    public void testInvokerWrapper_invokeOnTarget_whenExceptionOccurs_thenExceptionIsRethrown() throws Exception {
        ClientMessage request = Mockito.mock(ClientMessage.class);
        Mockito.when(request.setCorrelationId(((Long) (ArgumentMatchers.any())))).thenThrow(NullPointerException.class);
        Address address = new Address();
        context.getInvokerWrapper().invokeOnTarget(request, address);
    }

    @Test(expected = NullPointerException.class)
    public void testInvokerWrapper_invokeOnTarget_whenRequestIsNull_thenThrowException() {
        Address address = new Address();
        context.getInvokerWrapper().invokeOnTarget(null, address);
    }

    @Test(expected = NullPointerException.class)
    public void testInvokerWrapper_invokeOnTarget_whenAddressIsNull_thenThrowException() {
        context.getInvokerWrapper().invokeOnTarget(new Object(), null);
    }

    @Test(expected = NullPointerException.class)
    public void testInvokerWrapper_invoke_whenExceptionOccurs_thenExceptionIsRethrown() throws Exception {
        ClientMessage request = Mockito.mock(ClientMessage.class);
        Mockito.when(request.setCorrelationId(((Long) (ArgumentMatchers.any())))).thenThrow(NullPointerException.class);
        context.getInvokerWrapper().invoke(request);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInvokerWrapper_executeOperation() {
        context.getInvokerWrapper().executeOperation(null);
    }
}
