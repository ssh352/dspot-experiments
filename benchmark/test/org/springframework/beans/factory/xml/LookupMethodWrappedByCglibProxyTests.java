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
package org.springframework.beans.factory.xml;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;


/**
 * Tests lookup methods wrapped by a CGLIB proxy (see SPR-391).
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class LookupMethodWrappedByCglibProxyTests {
    private static final Class<?> CLASS = LookupMethodWrappedByCglibProxyTests.class;

    private static final String CLASSNAME = LookupMethodWrappedByCglibProxyTests.CLASS.getSimpleName();

    private static final String CONTEXT = (LookupMethodWrappedByCglibProxyTests.CLASSNAME) + "-context.xml";

    private ApplicationContext applicationContext;

    @Test
    public void testAutoProxiedLookup() {
        OverloadLookup olup = ((OverloadLookup) (applicationContext.getBean("autoProxiedOverload")));
        ITestBean jenny = olup.newTestBean();
        Assert.assertEquals("Jenny", jenny.getName());
        Assert.assertEquals("foo", olup.testMethod());
        assertInterceptorCount(2);
    }

    @Test
    public void testRegularlyProxiedLookup() {
        OverloadLookup olup = ((OverloadLookup) (applicationContext.getBean("regularlyProxiedOverload")));
        ITestBean jenny = olup.newTestBean();
        Assert.assertEquals("Jenny", jenny.getName());
        Assert.assertEquals("foo", olup.testMethod());
        assertInterceptorCount(2);
    }
}
