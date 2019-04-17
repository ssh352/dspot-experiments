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
package org.apache.camel.component.quartz2;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;


public class QuartzOneCamelContextRestartTest {
    private DefaultCamelContext camel1;

    @Test
    public void testOneCamelContextSuspendResume() throws Exception {
        MockEndpoint mock1 = camel1.getEndpoint("mock:one", MockEndpoint.class);
        mock1.expectedMinimumMessageCount(2);
        mock1.assertIsSatisfied();
        camel1.stop();
        // fetch mock endpoint again because we have stopped camel context
        mock1 = camel1.getEndpoint("mock:one", MockEndpoint.class);
        // should resume triggers when we start camel 1 again
        mock1.expectedMinimumMessageCount(3);
        camel1.start();
        mock1.assertIsSatisfied();
    }
}
