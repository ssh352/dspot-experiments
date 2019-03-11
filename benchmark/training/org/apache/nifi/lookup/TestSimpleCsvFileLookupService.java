/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.lookup;


import SimpleCsvFileLookupService.CHARSET;
import SimpleCsvFileLookupService.CSV_FILE;
import SimpleCsvFileLookupService.CSV_FORMAT;
import SimpleCsvFileLookupService.LOOKUP_KEY_COLUMN;
import SimpleCsvFileLookupService.LOOKUP_VALUE_COLUMN;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSimpleCsvFileLookupService {
    static final Optional<String> EMPTY_STRING = Optional.empty();

    @Test
    public void testSimpleCsvFileLookupService() throws IOException, LookupFailureException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final SimpleCsvFileLookupService service = new SimpleCsvFileLookupService();
        runner.addControllerService("csv-file-lookup-service", service);
        runner.setProperty(service, CSV_FILE, "src/test/resources/test.csv");
        runner.setProperty(service, CSV_FORMAT, "RFC4180");
        runner.setProperty(service, LOOKUP_KEY_COLUMN, "key");
        runner.setProperty(service, LOOKUP_VALUE_COLUMN, "value");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final SimpleCsvFileLookupService lookupService = ((SimpleCsvFileLookupService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("csv-file-lookup-service")));
        Assert.assertThat(lookupService, CoreMatchers.instanceOf(LookupService.class));
        final Optional<String> property1 = lookupService.lookup(Collections.singletonMap("key", "property.1"));
        Assert.assertEquals(Optional.of("this is property 1"), property1);
        final Optional<String> property2 = lookupService.lookup(Collections.singletonMap("key", "property.2"));
        Assert.assertEquals(Optional.of("this is property 2"), property2);
        final Optional<String> property3 = lookupService.lookup(Collections.singletonMap("key", "property.3"));
        Assert.assertEquals(TestSimpleCsvFileLookupService.EMPTY_STRING, property3);
    }

    @Test
    public void testSimpleCsvFileLookupServiceWithCharset() throws IOException, LookupFailureException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final SimpleCsvFileLookupService service = new SimpleCsvFileLookupService();
        runner.addControllerService("csv-file-lookup-service", service);
        runner.setProperty(service, CSV_FILE, "src/test/resources/test_Windows-31J.csv");
        runner.setProperty(service, CSV_FORMAT, "RFC4180");
        runner.setProperty(service, CHARSET, "Windows-31J");
        runner.setProperty(service, LOOKUP_KEY_COLUMN, "key");
        runner.setProperty(service, LOOKUP_VALUE_COLUMN, "value");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final Optional<String> property1 = service.lookup(Collections.singletonMap("key", "property.1"));
        Assert.assertThat(property1.isPresent(), CoreMatchers.is(true));
        Assert.assertThat(property1.get(), CoreMatchers.is("this is property \uff11"));
    }
}

