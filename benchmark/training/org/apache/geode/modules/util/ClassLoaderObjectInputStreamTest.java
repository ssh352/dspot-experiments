/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.modules.util;


import Constants.ACC_PUBLIC;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


@SuppressWarnings("unused")
public class ClassLoaderObjectInputStreamTest {
    private ClassLoader originalTCCL;

    private ClassLoader newTCCL;

    private String classToLoad;

    private Object instanceOfTCCLClass;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void resolveClassFromTCCLThrowsIfTCCLDisabled() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this.instanceOfTCCLClass);
        oos.close();
        ObjectInputStream ois = new ClassLoaderObjectInputStream(new ByteArrayInputStream(baos.toByteArray()), getClass().getClassLoader());
        assertThatThrownBy(() -> ois.readObject()).isExactlyInstanceOf(ClassNotFoundException.class);
    }

    @Test
    public void resolveClassFindsClassFromTCCLIfTCCLEnabled() throws Exception {
        Thread.currentThread().setContextClassLoader(this.newTCCL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this.instanceOfTCCLClass);
        oos.close();
        ObjectInputStream ois = new ClassLoaderObjectInputStream(new ByteArrayInputStream(baos.toByteArray()), getClass().getClassLoader());
        Object objectFromTCCL = ois.readObject();
        assertThat(objectFromTCCL).isNotNull();
        assertThat(objectFromTCCL.getClass()).isNotNull();
        assertThat(objectFromTCCL.getClass().getName()).isEqualTo(this.classToLoad);
    }

    /**
     * Custom class loader which uses BCEL to always dynamically generate a class for any class name
     * it tries to load.
     */
    private static class GeneratingClassLoader extends ClassLoader {
        /**
         * Currently unused but potentially useful for some future test. This causes this loader to only
         * generate a class that the parent could not find.
         *
         * @param parent
         * 		the parent class loader to check with first
         */
        public GeneratingClassLoader(ClassLoader parent) {
            super(parent);
        }

        /**
         * Specifies no parent to ensure that this loader generates the named class.
         */
        public GeneratingClassLoader() {
            super(null);// no parent

        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            ClassGen cg = new ClassGen(name, Object.class.getName(), "<generated>", ((Constants.ACC_PUBLIC) | (Constants.ACC_SUPER)), new String[]{ Serializable.class.getName() });
            cg.addEmptyConstructor(ACC_PUBLIC);
            JavaClass jClazz = cg.getJavaClass();
            byte[] bytes = jClazz.getBytes();
            return defineClass(jClazz.getClassName(), bytes, 0, bytes.length);
        }

        @Override
        protected URL findResource(String name) {
            URL url = null;
            try {
                url = getTempFile().getAbsoluteFile().toURI().toURL();
                System.out.println(("GeneratingClassLoader#findResource returning " + url));
            } catch (IOException e) {
                throw new Error(e);
            }
            return url;
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            URL url;
            try {
                url = getTempFile().getAbsoluteFile().toURI().toURL();
                System.out.println(("GeneratingClassLoader#findResources returning " + url));
            } catch (IOException e) {
                throw new Error(e);
            }
            Vector<URL> urls = new Vector<URL>();
            urls.add(url);
            return urls.elements();
        }

        protected File getTempFile() {
            return null;
        }
    }
}
