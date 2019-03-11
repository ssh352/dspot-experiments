/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal;


import Regions.EU_CENTRAL_1;
import Regions.US_EAST_1;
import com.amazonaws.regions.CustomRegionFactory;
import com.amazonaws.regions.Region;
import org.junit.Assert;
import org.junit.Test;


public class DefaultServiceEndpointBuilderTest {
    @Test
    public void getServiceEndpoint_S3StandardRegion_HttpsProtocol() throws Exception {
        DefaultServiceEndpointBuilder endpointBuilder = new DefaultServiceEndpointBuilder("s3", "https").withRegion(Region.getRegion(US_EAST_1));
        Assert.assertEquals("https://s3.amazonaws.com", endpointBuilder.getServiceEndpoint().toString());
    }

    @Test
    public void getServiceEndpoint_S3StandardRegion_HttpProtocol() throws Exception {
        DefaultServiceEndpointBuilder endpointBuilder = new DefaultServiceEndpointBuilder("s3", "http").withRegion(Region.getRegion(US_EAST_1));
        Assert.assertEquals("http://s3.amazonaws.com", endpointBuilder.getServiceEndpoint().toString());
    }

    @Test
    public void getServiceEndpoint_S3NonStandardRegion_HttpProtocol() throws Exception {
        DefaultServiceEndpointBuilder endpointBuilder = new DefaultServiceEndpointBuilder("s3", "http").withRegion(Region.getRegion(EU_CENTRAL_1));
        Assert.assertEquals("http://s3.eu-central-1.amazonaws.com", endpointBuilder.getServiceEndpoint().toString());
    }

    @Test
    public void getServiceEndpoint_CustomRegion_HttpProtocol() {
        DefaultServiceEndpointBuilder endpointBuilder = new DefaultServiceEndpointBuilder("s3", "http").withRegion(CustomRegionFactory.getCustomRegion("test-region", "test.domain"));
        Assert.assertEquals("http://s3.test-region.test.domain", endpointBuilder.getServiceEndpoint().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getServiceEndpoint_CustomRegion_InvalidEndpoint() {
        DefaultServiceEndpointBuilder endpointBuilder = new DefaultServiceEndpointBuilder("s3", "http").withRegion(CustomRegionFactory.getCustomRegion("test-region", "\\invalid.domain"));
        endpointBuilder.getServiceEndpoint();
    }
}

