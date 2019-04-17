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
package org.apache.camel.impl.verifier;


import Result.Status.ERROR;
import Result.Status.OK;
import Scope.PARAMETERS;
import VerificationError.StandardCode.ILLEGAL_PARAMETER_VALUE;
import VerificationError.StandardCode.MISSING_PARAMETER;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.extension.ComponentVerifierExtension;
import org.apache.camel.component.extension.ComponentVerifierExtension.Result;
import org.apache.camel.component.extension.verifier.DefaultComponentVerifierExtension;
import org.junit.Assert;
import org.junit.Test;


public class DefaultComponentVerifierTest extends ContextTestSupport {
    private ComponentVerifierExtension verifier;

    // *************************************
    // Tests
    // *************************************
    @Test
    public void testParameters() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("timerName", "dummy");
        parameters.put("period", "1s");
        Result result = verifier.verify(PARAMETERS, parameters);
        Assert.assertEquals(OK, result.getStatus());
    }

    @Test
    public void testParametersWithMissingMandatoryOptions() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("period", "1s");
        Result result = verifier.verify(PARAMETERS, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        Assert.assertEquals(MISSING_PARAMETER, result.getErrors().get(0).getCode());
        Assert.assertTrue(result.getErrors().get(0).getParameterKeys().contains("timerName"));
    }

    @Test
    public void testParametersWithWrongOptions() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("timerName", "dummy");
        parameters.put("period", "1s");
        parameters.put("fixedRate", "wrong");
        Result result = verifier.verify(PARAMETERS, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        Assert.assertEquals(ILLEGAL_PARAMETER_VALUE, result.getErrors().get(0).getCode());
        Assert.assertEquals("fixedRate has wrong value (wrong)", result.getErrors().get(0).getDescription());
        Assert.assertTrue(result.getErrors().get(0).getParameterKeys().contains("fixedRate"));
    }

    private class TestVerifier extends DefaultComponentVerifierExtension {
        public TestVerifier() {
            super("timer", context);
        }
    }
}
