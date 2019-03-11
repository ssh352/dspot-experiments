/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package tests.api.java.lang.reflect;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import junit.framework.TestCase;
import tests.support.Support_Proxy_I1;
import tests.support.Support_Proxy_I2;
import tests.support.Support_Proxy_ParentException;
import tests.support.Support_Proxy_SubException;


public class ProxyTest extends TestCase {
    /* When multiple interfaces define the same method, the list of thrown
    exceptions are those which can be mapped to another exception in the
    other method:

    String foo(String s) throws SubException, LinkageError;

    UndeclaredThrowableException wrappers any checked exception which is not
    in the merged list. So ParentException would be wrapped, BUT LinkageError
    would not be since its not an Error/RuntimeException.

    interface I1 { String foo(String s) throws ParentException, LinkageError; }
    interface I2 { String foo(String s) throws SubException, Error; }
     */
    interface Broken1 {
        public float method(float _number0, float _number1);
    }

    class Broken1Invoke implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return args[1];
        }
    }

    class ProxyCoonstructorTest extends Proxy {
        protected ProxyCoonstructorTest(InvocationHandler h) {
            super(h);
        }
    }

    /**
     * java.lang.reflect.Proxy#getProxyClass(java.lang.ClassLoader,
     *        java.lang.Class[])
     */
    public void test_getProxyClassLjava_lang_ClassLoader$Ljava_lang_Class() {
        Class proxy = Proxy.getProxyClass(Support_Proxy_I1.class.getClassLoader(), new Class[]{ Support_Proxy_I1.class });
        TestCase.assertTrue("Did not create a Proxy subclass ", ((proxy.getSuperclass()) == (Proxy.class)));
        TestCase.assertTrue("Does not believe its a Proxy class ", Proxy.isProxyClass(proxy));
        TestCase.assertTrue("Does not believe it's a Proxy class ", Proxy.isProxyClass(Proxy.getProxyClass(null, new Class[]{ Comparable.class })));
        try {
            Proxy.getProxyClass(Support_Proxy_I1.class.getClassLoader(), ((Class<?>[]) (null)));
            TestCase.fail("NPE expected");
        } catch (NullPointerException expected) {
        }
        try {
            Proxy.getProxyClass(Support_Proxy_I1.class.getClassLoader(), new Class<?>[]{ Support_Proxy_I1.class, null });
            TestCase.fail("NPE expected");
        } catch (NullPointerException expected) {
        }
    }

    /**
     * java.lang.reflect.Proxy#Proxy(java.lang.reflect.InvocationHandler)
     */
    public void test_ProxyLjava_lang_reflect_InvocationHandler() {
        TestCase.assertNotNull(new ProxyTest.ProxyCoonstructorTest(new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        }));
    }

    /**
     * java.lang.reflect.Proxy#newProxyInstance(java.lang.ClassLoader,
     *        java.lang.Class[], java.lang.reflect.InvocationHandler)
     */
    public void test_newProxyInstanceLjava_lang_ClassLoader$Ljava_lang_ClassLjava_lang_reflect_InvocationHandler() {
        Object p = Proxy.newProxyInstance(Support_Proxy_I1.class.getClassLoader(), new Class[]{ Support_Proxy_I1.class, Support_Proxy_I2.class }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("equals"))
                    return new Boolean((proxy == (args[0])));

                if (method.getName().equals("array"))
                    return new int[]{ ((int) (((long[]) (args[0]))[1])), -1 };

                if (method.getName().equals("string")) {
                    if ("".equals(args[0]))
                        throw new Support_Proxy_SubException();

                    if ("clone".equals(args[0]))
                        throw new Support_Proxy_ParentException();

                    if ("error".equals(args[0]))
                        throw new ArrayStoreException();

                    if ("any".equals(args[0]))
                        throw new IllegalAccessException();

                }
                return null;
            }
        });
        Support_Proxy_I1 proxy = ((Support_Proxy_I1) (p));
        TestCase.assertTrue("Failed identity test ", proxy.equals(proxy));
        TestCase.assertTrue("Failed not equals test ", (!(proxy.equals(""))));
        int[] result = ((int[]) (proxy.array(new long[]{ 100L, -200L })));
        TestCase.assertEquals("Failed primitive type conversion test ", (-200), result[0]);
        boolean worked = false;
        try {
            proxy.string("");
        } catch (Support_Proxy_SubException e) {
            worked = true;
        } catch (Support_Proxy_ParentException e) {
            // is never thrown
        }
        TestCase.assertTrue("Problem converting exception ", worked);
        worked = false;
        try {
            proxy.string("clone");
        } catch (Support_Proxy_ParentException e) {
            // is never thrown
        } catch (UndeclaredThrowableException e) {
            worked = true;
        }
        TestCase.assertTrue("Problem converting exception ", worked);
        worked = false;
        try {
            proxy.string("error");
        } catch (Support_Proxy_ParentException e) {
            // is never thrown
        } catch (UndeclaredThrowableException e) {
        } catch (RuntimeException e) {
            worked = (e.getClass()) == (ArrayStoreException.class);
        }
        TestCase.assertTrue("Problem converting exception ", worked);
        worked = false;
        try {
            proxy.string("any");
        } catch (Support_Proxy_ParentException e) {
            // is never thrown
        } catch (UndeclaredThrowableException e) {
            worked = true;
        }
        TestCase.assertTrue("Problem converting exception ", worked);
        ProxyTest.Broken1 proxyObject = null;
        try {
            proxyObject = ((ProxyTest.Broken1) (Proxy.newProxyInstance(ProxyTest.Broken1.class.getClassLoader(), new Class[]{ ProxyTest.Broken1.class }, new ProxyTest.Broken1Invoke())));
        } catch (Throwable e) {
            TestCase.fail(((("Failed to create proxy for class: " + (ProxyTest.Broken1.class)) + " - ") + e));
        }
        float brokenResult = proxyObject.method(2.1F, 5.8F);
        TestCase.assertTrue("Invalid invoke result", (brokenResult == 5.8F));
    }

    /**
     * java.lang.reflect.Proxy#isProxyClass(java.lang.Class)
     */
    public void test_isProxyClassLjava_lang_Class() {
        Class proxy = Proxy.getProxyClass(Support_Proxy_I1.class.getClassLoader(), new Class[]{ Support_Proxy_I1.class });
        class Fake extends Proxy {
            Fake() {
                super(null);
            }
        }
        Proxy fake = new Proxy(new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        }) {};
        TestCase.assertTrue("Does not believe its a Proxy class ", Proxy.isProxyClass(proxy));
        TestCase.assertTrue("Proxy subclasses do not count ", (!(Proxy.isProxyClass(Fake.class))));
        TestCase.assertTrue("Is not a runtime generated Proxy class ", (!(Proxy.isProxyClass(fake.getClass()))));
        boolean thrown = false;
        try {
            Proxy.isProxyClass(null);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        TestCase.assertTrue("NPE not thrown.", thrown);
    }

    /**
     * java.lang.reflect.Proxy#getInvocationHandler(java.lang.Object)
     */
    public void test_getInvocationHandlerLjava_lang_Object() {
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        };
        Object p = Proxy.newProxyInstance(Support_Proxy_I1.class.getClassLoader(), new Class[]{ Support_Proxy_I1.class }, handler);
        TestCase.assertTrue("Did not return invocation handler ", ((Proxy.getInvocationHandler(p)) == handler));
        boolean aborted = false;
        try {
            Proxy.getInvocationHandler("");
        } catch (IllegalArgumentException e) {
            aborted = true;
        }
        TestCase.assertTrue("Did not detect non proxy object ", aborted);
    }

    // Regression Test for HARMONY-2355
    public void test_newProxyInstance_withCompatibleReturnTypes() {
        Object o = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{ ProxyTest.ITestReturnObject.class, ProxyTest.ITestReturnString.class }, new ProxyTest.TestProxyHandler(new ProxyTest.TestProxyImpl()));
        TestCase.assertNotNull(o);
    }

    public void test_newProxyInstance_withNonCompatibleReturnTypes() {
        try {
            Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{ ProxyTest.ITestReturnInteger.class, ProxyTest.ITestReturnString.class }, new ProxyTest.TestProxyHandler(new ProxyTest.TestProxyImpl()));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public static interface ITestReturnObject {
        Object f();
    }

    public static interface ITestReturnString {
        String f();
    }

    public static interface ITestReturnInteger {
        Integer f();
    }

    public static class TestProxyImpl implements ProxyTest.ITestReturnObject , ProxyTest.ITestReturnString {
        public String f() {
            // do nothing
            return null;
        }
    }

    public static class TestProxyHandler implements InvocationHandler {
        private Object proxied;

        public TestProxyHandler(Object object) {
            proxied = object;
        }

        public Object invoke(Object object, Method method, Object[] args) throws Throwable {
            // do nothing
            return method.invoke(proxied, args);
        }
    }
}

