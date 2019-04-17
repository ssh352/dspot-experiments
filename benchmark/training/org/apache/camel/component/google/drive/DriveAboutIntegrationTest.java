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
package org.apache.camel.component.google.drive;


import com.google.api.services.drive.model.About;
import org.apache.camel.component.google.drive.internal.DriveAboutApiMethod;
import org.apache.camel.component.google.drive.internal.GoogleDriveApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for com.google.api.services.drive.Drive$About APIs.
 */
public class DriveAboutIntegrationTest extends AbstractGoogleDriveTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DriveAboutIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleDriveApiCollection.getCollection().getApiName(DriveAboutApiMethod.class).getName();

    @Test
    public void testGet() throws Exception {
        final About result = requestBody("direct://GET", null);
        assertNotNull("get result", result);
        DriveAboutIntegrationTest.LOG.debug(("get: " + result));
    }
}
