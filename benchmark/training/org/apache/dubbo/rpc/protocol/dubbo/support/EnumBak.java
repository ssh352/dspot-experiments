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
package org.apache.dubbo.rpc.protocol.dubbo.support;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EnumBak {
    private Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

    private ProxyFactory proxy = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    @Test
    public void testNormal() {
        int port = NetUtils.getAvailablePort();
        URL serviceurl = URL.valueOf((((((("dubbo://127.0.0.1:" + port) + "/test?proxy=jdk") + "&interface=") + (DemoService.class.getName())) + "&timeout=") + (Integer.MAX_VALUE)));
        DemoService demo = new DemoServiceImpl();
        Invoker<DemoService> invoker = proxy.getInvoker(demo, DemoService.class, serviceurl);
        protocol.export(invoker);
        URL consumerurl = serviceurl;
        Invoker<DemoService> reference = protocol.refer(DemoService.class, consumerurl);
        DemoService demoProxy = ((DemoService) (proxy.getProxy(reference)));
        // System.out.println(demoProxy.getThreadName());
        Assertions.assertEquals(((byte) (-128)), demoProxy.getbyte(((byte) (-128))));
        // invoker.destroy();
        reference.destroy();
    }

    @Test
    public void testNormalEnum() {
        int port = NetUtils.getAvailablePort();
        URL serviceurl = URL.valueOf(((("dubbo://127.0.0.1:" + port) + "/test?timeout=") + (Integer.MAX_VALUE)));
        DemoService demo = new DemoServiceImpl();
        Invoker<DemoService> invoker = proxy.getInvoker(demo, DemoService.class, serviceurl);
        protocol.export(invoker);
        URL consumerurl = serviceurl;
        Invoker<DemoService> reference = protocol.refer(DemoService.class, consumerurl);
        DemoService demoProxy = ((DemoService) (proxy.getProxy(reference)));
        Type type = demoProxy.enumlength(Type.High);
        System.out.println(type);
        Assertions.assertEquals(Type.High, type);
        invoker.destroy();
        reference.destroy();
    }

    @Test
    public void testGenericEnum() throws InterruptedException {
        int port = NetUtils.getAvailablePort();
        URL serviceurl = URL.valueOf(((("dubbo://127.0.0.1:" + port) + "/test?timeout=") + (Integer.MAX_VALUE)));
        DemoService demo = new DemoServiceImpl();
        Invoker<DemoService> invoker = proxy.getInvoker(demo, DemoService.class, serviceurl);
        protocol.export(invoker);
        URL consumerurl = serviceurl;
        Invoker<GenericService> reference = protocol.refer(GenericService.class, consumerurl);
        GenericService demoProxy = ((GenericService) (proxy.getProxy(reference)));
        Object obj = demoProxy.$invoke("enumlength", new String[]{ Type[].class.getName() }, new Object[]{ new Type[]{ Type.High, Type.High } });
        System.out.println(("obj---------->" + obj));
        invoker.destroy();
        reference.destroy();
    }
}
