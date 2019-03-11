/**
 * Copyright ? 2014 Florian Schmaus
 *
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
package org.jivesoftware.smackx;


import java.util.List;
import org.jivesoftware.smack.experimental.ExperimentalInitializer;
import org.junit.Assert;
import org.junit.Test;


public class ExperimentalInitializerTest {
    @Test
    public void testExperimentalInitializer() {
        ExperimentalInitializer epi = new ExperimentalInitializer();
        List<Exception> exceptions = epi.initialize();
        Assert.assertTrue(((exceptions.size()) == 0));
    }
}

