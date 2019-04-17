/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.aop.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.aop.interceptor.SerializableNopInterceptor;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.Person;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.SerializationTestUtils;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class RegexpMethodPointcutAdvisorIntegrationTests {
    private static final Resource CONTEXT = qualifiedResource(RegexpMethodPointcutAdvisorIntegrationTests.class, "context.xml");

    @Test
    public void testSinglePattern() throws Throwable {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(RegexpMethodPointcutAdvisorIntegrationTests.CONTEXT);
        ITestBean advised = ((ITestBean) (bf.getBean("settersAdvised")));
        // Interceptor behind regexp advisor
        NopInterceptor nop = ((NopInterceptor) (bf.getBean("nopInterceptor")));
        Assert.assertEquals(0, nop.getCount());
        int newAge = 12;
        // Not advised
        advised.exceptional(null);
        Assert.assertEquals(0, nop.getCount());
        advised.setAge(newAge);
        Assert.assertEquals(newAge, advised.getAge());
        // Only setter fired
        Assert.assertEquals(1, nop.getCount());
    }

    @Test
    public void testMultiplePatterns() throws Throwable {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(RegexpMethodPointcutAdvisorIntegrationTests.CONTEXT);
        // This is a CGLIB proxy, so we can proxy it to the target class
        TestBean advised = ((TestBean) (bf.getBean("settersAndAbsquatulateAdvised")));
        // Interceptor behind regexp advisor
        NopInterceptor nop = ((NopInterceptor) (bf.getBean("nopInterceptor")));
        Assert.assertEquals(0, nop.getCount());
        int newAge = 12;
        // Not advised
        advised.exceptional(null);
        Assert.assertEquals(0, nop.getCount());
        // This is proxied
        advised.absquatulate();
        Assert.assertEquals(1, nop.getCount());
        advised.setAge(newAge);
        Assert.assertEquals(newAge, advised.getAge());
        // Only setter fired
        Assert.assertEquals(2, nop.getCount());
    }

    @Test
    public void testSerialization() throws Throwable {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(RegexpMethodPointcutAdvisorIntegrationTests.CONTEXT);
        // This is a CGLIB proxy, so we can proxy it to the target class
        Person p = ((Person) (bf.getBean("serializableSettersAdvised")));
        // Interceptor behind regexp advisor
        NopInterceptor nop = ((NopInterceptor) (bf.getBean("nopInterceptor")));
        Assert.assertEquals(0, nop.getCount());
        int newAge = 12;
        // Not advised
        Assert.assertEquals(0, p.getAge());
        Assert.assertEquals(0, nop.getCount());
        // This is proxied
        p.setAge(newAge);
        Assert.assertEquals(1, nop.getCount());
        p.setAge(newAge);
        Assert.assertEquals(newAge, p.getAge());
        // Only setter fired
        Assert.assertEquals(2, nop.getCount());
        // Serialize and continue...
        p = ((Person) (SerializationTestUtils.serializeAndDeserialize(p)));
        Assert.assertEquals(newAge, p.getAge());
        // Remembers count, but we need to get a new reference to nop...
        nop = ((SerializableNopInterceptor) (getAdvisors()[0].getAdvice()));
        Assert.assertEquals(2, nop.getCount());
        Assert.assertEquals("serializableSettersAdvised", p.getName());
        p.setAge((newAge + 1));
        Assert.assertEquals(3, nop.getCount());
        Assert.assertEquals((newAge + 1), p.getAge());
    }
}
