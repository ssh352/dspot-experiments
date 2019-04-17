/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.classloader;


import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.core.classloader.javassist.ClassPathAdjuster;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.reflect.Whitebox;


@RunWith(Parameterized.class)
public class MockClassLoaderTest {
    private final MockClassLoaderFactory mockClassLoaderFactory;

    private final Class<? extends MockClassLoader> clazz;

    private final MockTransformerChain mockTransformerChain;

    public MockClassLoaderTest(Class<? extends MockClassLoader> clazz, MockTransformer transformer) {
        this.mockClassLoaderFactory = new MockClassLoaderFactory(clazz);
        this.clazz = clazz;
        this.mockTransformerChain = DefaultMockTransformerChain.newBuilder().append(transformer).build();
    }

    @Test
    public void should_load_and_modify_class_from_package_which_specified() throws Exception {
        String className = "powermock.test.support.ClassForMockClassLoaderTestCase";
        MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{ className });
        mockClassLoader.setMockTransformerChain(mockTransformerChain);
        Class<?> clazz = Class.forName(className, false, mockClassLoader);
        assertClassIsLoaded(clazz, mockClassLoader);
        assertThatInstanceCouldBeCreateAndMethodReturnMockedValue(clazz);
    }

    @Test
    public void should_load_and_not_modify_class_from_package_which_are_not_specified_as_ignored_or_class_to_mock() throws Exception {
        String className = "powermock.test.support.ClassForMockClassLoaderTestCase";
        MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0]);
        mockClassLoader.setMockTransformerChain(mockTransformerChain);
        Class<?> clazz = Class.forName(className, false, mockClassLoader);
        assertClassIsLoaded(clazz, mockClassLoader);
        assertThatInstanceCouldBeCreateAndMethodReturnNotMockedValue(clazz);
    }

    @Test
    public void should_load_system_classes() throws Exception {
        MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{ "org.mytest.myclass" });
        Class<?> clazz = Class.forName("java.lang.String", false, mockClassLoader);
        assertThat(clazz).as("System class is loaded").isEqualTo(String.class);
    }

    @Test
    public void should_load_defined_class() throws Exception {
        final String className = "my.ABCTestClass";
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{ className });
        Whitebox.invokeMethod(mockClassLoader, "defineClass", className, MockClassLoaderTest.DynamicClassHolder.classBytes, 0, MockClassLoaderTest.DynamicClassHolder.classBytes.length, this.getClass().getProtectionDomain());
        Class<?> clazz = Class.forName(className, false, mockClassLoader);
        assertThat(clazz).as("Defined class is loaded").isNotNull();
    }

    @Test
    public void should_ignore_pagackage_added_powerMockIgnore_Annotated() throws Exception {
        MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{ "org.ikk.Jux" });
        MockClassLoaderConfiguration configuration = mockClassLoader.getConfiguration();
        Whitebox.setInternalState(configuration, "deferPackages", new String[]{ "*mytest*" }, MockClassLoaderConfiguration.class);
        Assert.assertFalse(configuration.shouldModify("org.mytest.myclass"));
    }

    @Test
    public void powerMockIgnoreAnnotatedPackagesHavePrecedenceOverPrepareEverythingForTest() throws Exception {
        MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{ MockClassLoader.MODIFY_ALL_CLASSES });
        MockClassLoaderConfiguration configuration = mockClassLoader.getConfiguration();
        Whitebox.setInternalState(configuration, "deferPackages", new String[]{ "*mytest*" }, MockClassLoaderConfiguration.class);
        Assert.assertFalse(configuration.shouldModify("org.mytest.myclass"));
    }

    @Test
    public void should_find_and_return_a_one_resource_which_exist() throws Exception {
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0]);
        // Force a ClassLoader that can find 'foo/bar/baz/test.txt' into
        // mockClassLoader.deferTo.
        mockClassLoader.deferTo = new ResourcePrefixClassLoader(getClass().getClassLoader(), "org/powermock/core/classloader/");
        // MockClassLoader will only be able to find 'foo/bar/baz/test.txt' if it
        // properly defers the resource lookup to its deferTo ClassLoader.
        URL resource = mockClassLoader.getResource("foo/bar/baz/test.txt");
        assertThat(resource).isNotNull();
        assertThat(resource.getPath()).endsWith("test.txt");
    }

    @Test
    public void should_find_and_return_resources_which_exist() throws Exception {
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0]);
        // Force a ClassLoader that can find 'foo/bar/baz/test.txt' into
        // mockClassLoader.deferTo.
        mockClassLoader.deferTo = new ResourcePrefixClassLoader(getClass().getClassLoader(), "org/powermock/core/classloader/");
        // MockClassLoader will only be able to find 'foo/bar/baz/test.txt' if it
        // properly defers the resources lookup to its deferTo ClassLoader.
        Enumeration<URL> resources = mockClassLoader.getResources("foo/bar/baz/test.txt");
        assertThat(resources.nextElement().getPath()).endsWith("test.txt");
    }

    @Test
    public void resourcesNotDoubled() throws Exception {
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0]);
        // mockClassLoader.setMockTransformerChain(transformerChain);
        // MockClassLoader will only be able to find 'foo/bar/baz/test.txt' if it
        // properly defers the resources lookup to its deferTo ClassLoader.
        Enumeration<URL> resources = mockClassLoader.getResources("org/powermock/core/classloader/foo/bar/baz/test.txt");
        assertThat(resources.nextElement().getPath()).endsWith("test.txt");
        assertThat(resources.hasMoreElements()).isFalse();
    }

    @Test
    public void canFindDynamicClassFromAdjustedClasspath() throws Exception {
        Assume.assumeThat(clazz.getName(), CoreMatchers.equalTo(JavassistMockClassLoader.class.getName()));
        // Construct MockClassLoader with @UseClassPathAdjuster annotation.
        // It activates our MyClassPathAdjuster class which appends our dynamic
        // class to the MockClassLoader's classpool.
        UseClassPathAdjuster useClassPathAdjuster = new MockClassLoaderTest.TestUseClassPathAdjuster();
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0], useClassPathAdjuster);
        // setup custom classloader providing our dynamic class, for MockClassLoader to defer to
        mockClassLoader.deferTo = new ClassLoader(getClass().getClassLoader()) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (name.equals(MockClassLoaderTest.DynamicClassHolder.clazz.getName())) {
                    return MockClassLoaderTest.DynamicClassHolder.clazz;
                }
                return super.loadClass(name);
            }
        };
        // verify that MockClassLoader can successfully load the class
        Class<?> dynamicTestClass = Class.forName(MockClassLoaderTest.DynamicClassHolder.clazz.getName(), false, mockClassLoader);
        assertThat(dynamicTestClass).isNotSameAs(MockClassLoaderTest.DynamicClassHolder.clazz);
    }

    @Test
    public void should_autobox_primitive_values() throws Exception {
        String name = (this.getClass().getPackage().getName()) + ".HardToTransform";
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{ name });
        Class<?> c = mockClassLoader.loadClass(name);
        Object object = c.newInstance();
        Whitebox.invokeMethod(object, "run");
        assertThat(5).isEqualTo(Whitebox.invokeMethod(object, "testInt"));
        assertThat(5L).isEqualTo(Whitebox.invokeMethod(object, "testLong"));
        assertThat(5.0F).isEqualTo(Whitebox.invokeMethod(object, "testFloat"));
        assertThat(5.0).isEqualTo(Whitebox.invokeMethod(object, "testDouble"));
        assertThat(new Short("5")).isEqualTo(Whitebox.invokeMethod(object, "testShort"));
        assertThat(new Byte("5")).isEqualTo(Whitebox.invokeMethod(object, "testByte"));
        assertThat(true).isEqualTo(Whitebox.invokeMethod(object, "testBoolean"));
        assertThat('5').isEqualTo(Whitebox.invokeMethod(object, "testChar"));
        assertThat("5").isEqualTo(Whitebox.invokeMethod(object, "testString"));
    }

    // helper class for canFindDynamicClassFromAdjustedClasspath()
    public static class MyClassPathAdjuster implements ClassPathAdjuster {
        public void adjustClassPath(ClassPool classPool) {
            classPool.appendClassPath(new ByteArrayClassPath(MockClassLoaderTest.DynamicClassHolder.clazz.getName(), MockClassLoaderTest.DynamicClassHolder.classBytes));
        }
    }

    // helper class for canFindDynamicClassFromAdjustedClasspath()
    static class DynamicClassHolder {
        static final byte[] classBytes;

        static final Class<?> clazz;

        static {
            try {
                // construct a new class dynamically
                ClassPool cp = ClassPool.getDefault();
                final CtClass ctClass = cp.makeClass("my.ABCTestClass");
                classBytes = ctClass.toBytecode();
                clazz = ctClass.toClass();
            } catch (Exception e) {
                throw new RuntimeException("Problem constructing custom class", e);
            }
        }
    }

    public static class TestUseClassPathAdjuster implements UseClassPathAdjuster {
        public Class<? extends Annotation> annotationType() {
            return UseClassPathAdjuster.class;
        }

        public Class<? extends ClassPathAdjuster> value() {
            return MockClassLoaderTest.MyClassPathAdjuster.class;
        }
    }

    private static class JavassistMockTransformer implements MockTransformer<CtClass> {
        @Override
        public ClassWrapper<CtClass> transform(final ClassWrapper<CtClass> clazz) throws Exception {
            CtClass ctClass = clazz.unwrap();
            for (CtMethod ctMethod : ctClass.getMethods()) {
                CtClass returnType = ctMethod.getReturnType();
                if (returnType.getName().equals(String.class.getName())) {
                    ctMethod.setBody("return null;");
                }
            }
            return clazz;
        }
    }
}
