/**
 * Copyright (c) 2018. AxonIQ
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.axonserver.connector.query.subscription;


import FluxSink.OverflowStrategy;
import io.axoniq.axonserver.grpc.query.QueryResponse;
import io.axoniq.axonserver.grpc.query.QueryUpdate;
import io.axoniq.axonserver.grpc.query.SubscriptionQuery;
import io.axoniq.axonserver.grpc.query.SubscriptionQueryRequest;
import io.axoniq.axonserver.grpc.query.SubscriptionQueryResponse;
import java.util.ArrayList;
import java.util.List;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.queryhandling.SubscriptionQueryBackpressure;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Sara Pellegrini on 18/06/2018.
 * sara.pellegrini@gmail.com
 */
public class AxonServerSubscriptionQueryResultTest {
    private SubscriptionQuery queryMessage;

    private FakeStreamObserver<SubscriptionQueryRequest> requestObserver;

    private AxonServerConfiguration configuration;

    private SubscriptionQueryResponse update;

    private SubscriptionQueryResponse initialResult;

    @Test
    public void testSubscribeUpdates() {
        SubscriptionQueryBackpressure backPressure = new SubscriptionQueryBackpressure(OverflowStrategy.ERROR);
        AxonServerSubscriptionQueryResult target = new AxonServerSubscriptionQueryResult(queryMessage, ( responseStream) -> requestObserver, configuration, backPressure, 10, () -> {
        });
        target.onNext(update);
        target.onNext(update);
        List<QueryUpdate> updates = new ArrayList<>();
        target.get().updates().subscribe(updates::add);
        Assert.assertEquals(2, updates.size());
    }

    @Test
    public void testSubscribeInitialResponse() {
        SubscriptionQueryBackpressure backPressure = new SubscriptionQueryBackpressure(OverflowStrategy.ERROR);
        AxonServerSubscriptionQueryResult target = new AxonServerSubscriptionQueryResult(queryMessage, ( responseStream) -> requestObserver, configuration, backPressure, 10, () -> {
        });
        List<QueryResponse> result = new ArrayList<>();
        target.get().initialResult().subscribe(result::add);
        target.onNext(initialResult);
        target.onNext(initialResult);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testErrorOverflowStrategy() {
        SubscriptionQueryBackpressure backPressure = new SubscriptionQueryBackpressure(OverflowStrategy.ERROR);
        AxonServerSubscriptionQueryResult target = new AxonServerSubscriptionQueryResult(queryMessage, ( responseStream) -> requestObserver, configuration, backPressure, 2, () -> {
        });
        target.onNext(update);
        target.onNext(update);
        target.onNext(update);
        Assert.assertEquals(1, requestObserver.completedCount());
    }
}

