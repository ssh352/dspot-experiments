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
package com.alipay.sofa.rpc.codec.jackson;


import RemotingConstants.HEAD_METHOD_NAME;
import RemotingConstants.HEAD_RESPONSE_ERROR;
import RemotingConstants.HEAD_TARGET_APP;
import RemotingConstants.HEAD_TARGET_SERVICE;
import RemotingConstants.RPC_TRACE_NAME;
import com.alipay.sofa.rpc.codec.jackson.model.DemoRequest;
import com.alipay.sofa.rpc.codec.jackson.model.DemoResponse;
import com.alipay.sofa.rpc.codec.jackson.model.DemoService;
import com.alipay.sofa.rpc.common.RemotingConstants;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.transport.AbstractByteBuf;
import com.alipay.sofa.rpc.transport.ByteArrayWrapperByteBuf;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class JacksonSerializerTest {
    JacksonSerializer serializer = new JacksonSerializer();

    @Test
    public void encodeAndDecode() {
        boolean error = false;
        try {
            serializer.encode(null, null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setName("a");
        AbstractByteBuf byteBuf = serializer.encode(demoRequest, null);
        DemoRequest req2 = ((DemoRequest) (serializer.decode(byteBuf, DemoRequest.class, null)));
        Assert.assertEquals(demoRequest.getName(), req2.getName());
        AbstractByteBuf data = serializer.encode("xxx", null);
        String dst = ((String) (serializer.decode(data, String.class, null)));
        Assert.assertEquals("xxx", dst);
        error = false;
        try {
            serializer.encode(new Date(), null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue((!error));
        error = false;
        try {
            serializer.decode(data, null, null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
        error = false;
        try {
            serializer.decode(data, "", null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
    }

    @Test
    public void testSofaRequest() throws Exception {
        SofaRequest request = buildRequest();
        AbstractByteBuf data = serializer.encode(request, null);
        boolean error = false;
        try {
            serializer.decode(data, SofaRequest.class, null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
        error = false;
        try {
            serializer.decode(data, new SofaRequest(), null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
        Map<String, String> head = new HashMap<String, String>();
        head.put(HEAD_TARGET_SERVICE, ((DemoService.class.getCanonicalName()) + ":1.0"));
        head.put(HEAD_METHOD_NAME, "say");
        head.put(HEAD_TARGET_APP, "targetApp");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".a"), "xxx");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".b"), "yyy");
        head.put("unkown", "yes");
        SofaRequest newRequest = new SofaRequest();
        serializer.decode(data, newRequest, head);
        Assert.assertEquals(newRequest.getInterfaceName(), request.getInterfaceName());
        Assert.assertEquals(newRequest.getMethodName(), request.getMethodName());
        Assert.assertArrayEquals(newRequest.getMethodArgSigs(), request.getMethodArgSigs());
        Assert.assertEquals(newRequest.getMethodArgs().length, request.getMethodArgs().length);
        Assert.assertEquals("name", ((DemoRequest) (newRequest.getMethodArgs()[0])).getName());
        Assert.assertEquals(newRequest.getTargetServiceUniqueName(), request.getTargetServiceUniqueName());
        Assert.assertEquals(newRequest.getTargetAppName(), request.getTargetAppName());
        Assert.assertEquals(newRequest.getRequestProp(RPC_TRACE_NAME), request.getRequestProp(RPC_TRACE_NAME));
        // null request
        head = new HashMap<String, String>();
        head.put(HEAD_TARGET_SERVICE, ((DemoService.class.getCanonicalName()) + ":1.0"));
        head.put(HEAD_METHOD_NAME, "say");
        head.put(HEAD_TARGET_APP, "targetApp");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".a"), "xxx");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".b"), "yyy");
        newRequest = new SofaRequest();
        serializer.decode(new ByteArrayWrapperByteBuf(new byte[0]), newRequest, head);
        final Object[] methodArgs = newRequest.getMethodArgs();
        Assert.assertEquals(null, ((DemoRequest) (methodArgs[0])).getName());
    }

    @Test
    public void testSofaResponse() throws Exception {
        SofaResponse response = new SofaResponse();
        response.setAppResponse("1233");
        AbstractByteBuf data = serializer.encode(response, null);
        boolean error = false;
        try {
            serializer.decode(data, SofaResponse.class, null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
        error = false;
        try {
            serializer.decode(data, null, null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
        error = false;
        try {
            serializer.decode(data, new SofaResponse(), null);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
        // success response
        Map<String, String> head = new HashMap<String, String>();
        head.put(HEAD_TARGET_SERVICE, ((DemoService.class.getCanonicalName()) + ":1.0"));
        head.put(HEAD_METHOD_NAME, "say");
        head.put(HEAD_TARGET_APP, "targetApp");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".a"), "xxx");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".b"), "yyy");
        response = new SofaResponse();
        final DemoResponse response1 = new DemoResponse();
        response1.setWord("result");
        response.setAppResponse(response1);
        data = serializer.encode(response, null);
        SofaResponse newResponse = new SofaResponse();
        serializer.decode(data, newResponse, head);
        Assert.assertFalse(newResponse.isError());
        Assert.assertEquals(response.getAppResponse(), newResponse.getAppResponse());
        Assert.assertEquals("result", ((DemoResponse) (newResponse.getAppResponse())).getWord());
        // null response
        head = new HashMap<String, String>();
        head.put(HEAD_TARGET_SERVICE, ((DemoService.class.getCanonicalName()) + ":1.0"));
        head.put(HEAD_METHOD_NAME, "say");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".a"), "xxx");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".b"), "yyy");
        newResponse = new SofaResponse();
        serializer.decode(new ByteArrayWrapperByteBuf(new byte[0]), newResponse, head);
        Assert.assertFalse(newResponse.isError());
        Assert.assertNotNull(newResponse.getAppResponse());
        Assert.assertEquals(null, ((DemoResponse) (newResponse.getAppResponse())).getWord());
        // error response
        head = new HashMap<String, String>();
        head.put(HEAD_TARGET_SERVICE, ((DemoService.class.getCanonicalName()) + ":1.0"));
        head.put(HEAD_METHOD_NAME, "say");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".a"), "xxx");
        head.put(((RemotingConstants.RPC_TRACE_NAME) + ".b"), "yyy");
        head.put(HEAD_RESPONSE_ERROR, "true");
        response = new SofaResponse();
        response.setErrorMsg("1233");
        data = serializer.encode(response, null);
        newResponse = new SofaResponse();
        serializer.decode(data, newResponse, head);
        Assert.assertTrue(newResponse.isError());
        Assert.assertEquals(response.getErrorMsg(), newResponse.getErrorMsg());
    }
}

