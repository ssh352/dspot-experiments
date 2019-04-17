/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.core.scope;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.beans.BeansException;
import org.springframework.context.support.StaticApplicationContext;


/**
 *
 *
 * @author Dave Syer
 * @author Jimmy Praet
 */
public class JobScopeTests {
    private JobScope scope = new JobScope();

    private JobExecution jobExecution = new JobExecution(0L);

    private JobContext context;

    @Test
    public void testGetWithNoContext() throws Exception {
        final String foo = "bar";
        JobSynchronizationManager.release();
        try {
            scope.get("foo", new org.springframework.beans.factory.ObjectFactory<String>() {
                @Override
                public String getObject() throws BeansException {
                    return foo;
                }
            });
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testGetWithNothingAlreadyThere() {
        final String foo = "bar";
        Object value = scope.get("foo", new org.springframework.beans.factory.ObjectFactory<String>() {
            @Override
            public String getObject() throws BeansException {
                return foo;
            }
        });
        Assert.assertEquals(foo, value);
        Assert.assertTrue(context.hasAttribute("foo"));
    }

    @Test
    public void testGetWithSomethingAlreadyThere() {
        context.setAttribute("foo", "bar");
        Object value = scope.get("foo", new org.springframework.beans.factory.ObjectFactory<String>() {
            @Override
            public String getObject() throws BeansException {
                return null;
            }
        });
        Assert.assertEquals("bar", value);
        Assert.assertTrue(context.hasAttribute("foo"));
    }

    @Test
    public void testGetConversationId() {
        String id = scope.getConversationId();
        Assert.assertNotNull(id);
    }

    @Test
    public void testRegisterDestructionCallback() {
        final List<String> list = new ArrayList<>();
        context.setAttribute("foo", "bar");
        scope.registerDestructionCallback("foo", new Runnable() {
            @Override
            public void run() {
                list.add("foo");
            }
        });
        Assert.assertEquals(0, list.size());
        // When the context is closed, provided the attribute exists the
        // callback is called...
        context.close();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testRegisterAnotherDestructionCallback() {
        final List<String> list = new ArrayList<>();
        context.setAttribute("foo", "bar");
        scope.registerDestructionCallback("foo", new Runnable() {
            @Override
            public void run() {
                list.add("foo");
            }
        });
        scope.registerDestructionCallback("foo", new Runnable() {
            @Override
            public void run() {
                list.add("bar");
            }
        });
        Assert.assertEquals(0, list.size());
        // When the context is closed, provided the attribute exists the
        // callback is called...
        context.close();
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testRemove() {
        context.setAttribute("foo", "bar");
        scope.remove("foo");
        Assert.assertFalse(context.hasAttribute("foo"));
    }

    @Test
    public void testOrder() throws Exception {
        Assert.assertEquals(Integer.MAX_VALUE, scope.getOrder());
        scope.setOrder(11);
        Assert.assertEquals(11, scope.getOrder());
    }

    @Test
    @SuppressWarnings("resource")
    public void testName() throws Exception {
        scope.setName("foo");
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        scope.postProcessBeanFactory(beanFactory.getDefaultListableBeanFactory());
        String[] scopes = beanFactory.getDefaultListableBeanFactory().getRegisteredScopeNames();
        Assert.assertEquals(1, scopes.length);
        Assert.assertEquals("foo", scopes[0]);
    }
}
