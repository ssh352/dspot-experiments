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
package org.apache.camel.component.jolt;


import org.apache.camel.support.ResourceHelper;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.IOHelper;
import org.junit.Test;


/**
 * Unit test based on the first sample test from the JOLT project.
 */
public class JoltFirstSampleTest extends CamelTestSupport {
    @Test
    public void testFirstSampleJolt() throws Exception {
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);
        // Remove the last newline added by IOHelper.loadText()
        getMockEndpoint("mock:result").expectedBodiesReceived(IOHelper.loadText(ResourceHelper.resolveMandatoryResourceAsInputStream(context, "org/apache/camel/component/jolt/firstSample/output.json")).trim());
        sendBody("direct://start", ResourceHelper.resolveMandatoryResourceAsInputStream(context, "org/apache/camel/component/jolt/firstSample/input.json"));
        assertMockEndpointsSatisfied();
    }
}
