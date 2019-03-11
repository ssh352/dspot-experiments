/**
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
package org.flowable.http.bpmn.validation;


import java.util.HashMap;
import java.util.Map;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.test.Deployment;
import org.flowable.http.HttpActivityExecutor;
import org.flowable.http.bpmn.HttpServiceTaskTestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Harsha Teja Kanna
 */
public class HttpServiceTaskValidationTest extends HttpServiceTaskTestCase {
    @Test
    @Deployment
    public void testInvalidProcess() {
        try {
            runtimeService.startProcessInstanceByKey("validateProcess");
            fail("FlowableException expected");
        } catch (Exception e) {
            assertTrue((e instanceof FlowableException));
            assertEquals(HttpActivityExecutor.HTTP_TASK_REQUEST_METHOD_INVALID, e.getMessage());
        }
    }

    @Test
    @Deployment
    public void testInvalidHeaders() {
        try {
            runtimeService.startProcessInstanceByKey("invalidHeaders");
            fail("FlowableException expected");
        } catch (Exception e) {
            assertTrue((e instanceof FlowableException));
            assertEquals(HttpActivityExecutor.HTTP_TASK_REQUEST_HEADERS_INVALID, e.getMessage());
        }
    }

    @Test
    @Deployment
    public void testInvalidFlags() {
        try {
            runtimeService.startProcessInstanceByKey("invalidFlags");
            fail("FlowableException expected");
        } catch (Exception e) {
            assertTrue((e instanceof FlowableException));
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.is("String value \"Accept application/json\" is not alloved in boolean expression"));
        }
    }

    @Test
    @Deployment
    public void testInvalidTimeout() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestTimeout", "invalid");
        try {
            runtimeService.startProcessInstanceByKey("invalidTimeout", variables);
            fail("FlowableException expected");
        } catch (Exception e) {
            assertTrue((e instanceof FlowableException));
            assertTextPresent(HttpActivityExecutor.HTTP_TASK_REQUEST_FIELD_INVALID, e.getMessage());
        }
    }
}

