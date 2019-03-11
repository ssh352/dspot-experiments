/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.csi.adaptor;


import CsiAdaptorProtos.GetPluginInfoRequest;
import CsiAdaptorProtos.GetPluginInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetPluginInfoRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetPluginInfoResponsePBImpl;
import org.apache.hadoop.yarn.proto.CsiAdaptorProtos;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify the integrity of GetPluginInfoRequest and GetPluginInfoResponse.
 */
public class TestGetPluginInfoRequestResponse {
    @Test
    public void testGetPluginInfoRequestPBRecord() {
        CsiAdaptorProtos.GetPluginInfoRequest requestProto = GetPluginInfoRequest.newBuilder().build();
        GetPluginInfoRequestPBImpl pbImpl = new GetPluginInfoRequestPBImpl(requestProto);
        Assert.assertNotNull(pbImpl);
        Assert.assertEquals(requestProto, pbImpl.getProto());
    }

    @Test
    public void testGetPluginInfoResponsePBRecord() {
        CsiAdaptorProtos.GetPluginInfoResponse responseProto = GetPluginInfoResponse.newBuilder().setName("test-driver").setVendorVersion("1.0.1").build();
        GetPluginInfoResponsePBImpl pbImpl = new GetPluginInfoResponsePBImpl(responseProto);
        Assert.assertEquals("test-driver", pbImpl.getDriverName());
        Assert.assertEquals("1.0.1", pbImpl.getVersion());
        Assert.assertEquals(responseProto, pbImpl.getProto());
        org.apache.hadoop.yarn.api.protocolrecords.GetPluginInfoResponse pbImpl2 = GetPluginInfoResponsePBImpl.newInstance("test-driver", "1.0.1");
        Assert.assertEquals("test-driver", pbImpl2.getDriverName());
        Assert.assertEquals("1.0.1", pbImpl2.getVersion());
        CsiAdaptorProtos.GetPluginInfoResponse proto = getProto();
        Assert.assertEquals("test-driver", proto.getName());
        Assert.assertEquals("1.0.1", proto.getVendorVersion());
    }
}

