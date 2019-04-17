/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.beans.factory.parsing;


import org.apache.commons.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.io.DescriptiveResource;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class FailFastProblemReporterTests {
    @Test(expected = BeanDefinitionParsingException.class)
    public void testError() throws Exception {
        FailFastProblemReporter reporter = new FailFastProblemReporter();
        reporter.error(new Problem("VGER", new Location(new DescriptiveResource("here")), null, new IllegalArgumentException()));
    }

    @Test
    public void testWarn() throws Exception {
        Problem problem = new Problem("VGER", new Location(new DescriptiveResource("here")), null, new IllegalArgumentException());
        Log log = Mockito.mock(Log.class);
        FailFastProblemReporter reporter = new FailFastProblemReporter();
        reporter.setLogger(log);
        reporter.warning(problem);
        Mockito.verify(log).warn(ArgumentMatchers.any(), ArgumentMatchers.isA(IllegalArgumentException.class));
    }
}
