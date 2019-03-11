/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.audit.request.creator;


import Request.Type.GET;
import Resource.Type.Cluster;
import ResultStatus.STATUS;
import junit.framework.Assert;
import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.audit.event.AuditEvent;
import org.apache.ambari.server.audit.event.request.BlueprintExportRequestAuditEvent;
import org.apache.ambari.server.audit.request.eventcreator.BlueprintExportEventCreator;
import org.junit.Test;


public class BlueprintExportEventCreatorTest extends AuditEventCreatorTestBase {
    @Test
    public void getTest() {
        BlueprintExportEventCreator creator = new BlueprintExportEventCreator();
        Request request = AuditEventCreatorTestHelper.createRequest(GET, Cluster, null, null, "?format=blueprint");
        Result result = AuditEventCreatorTestHelper.createResult(new org.apache.ambari.server.api.services.ResultStatus(STATUS.OK));
        AuditEvent event = AuditEventCreatorTestHelper.getEvent(creator, request, result);
        String actual = event.getAuditMessage();
        String expected = ("User(" + (AuditEventCreatorTestBase.userName)) + "), RemoteIp(1.2.3.4), Operation(Blueprint export), RequestType(GET), url(http://example.com:8080/api/v1/test?format=blueprint), ResultStatus(200 OK)";
        Assert.assertTrue("Class mismatch", (event instanceof BlueprintExportRequestAuditEvent));
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(actual.contains(AuditEventCreatorTestBase.userName));
    }
}

