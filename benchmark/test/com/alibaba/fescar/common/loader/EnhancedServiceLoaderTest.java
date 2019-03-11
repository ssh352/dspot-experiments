/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.alibaba.fescar.common.loader;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * The type Enhanced service loader test.
 *
 * @author Otis.z
 * @unknown 2019 /2/26
 */
public class EnhancedServiceLoaderTest {
    /**
     * Test load by class and class loader.
     */
    @Test
    public void testLoadByClassAndClassLoader() {
        Hello load = EnhancedServiceLoader.load(Hello.class, Hello.class.getClassLoader());
        Assert.assertEquals(load.say(), "Bonjour");
    }

    /**
     * Test load exception.
     */
    @Test(expected = EnhancedServiceNotFoundException.class)
    public void testLoadException() {
        EnhancedServiceLoaderTest load = EnhancedServiceLoader.load(EnhancedServiceLoaderTest.class);
    }

    /**
     * Test load by class.
     */
    @Test
    public void testLoadByClass() {
        Hello load = EnhancedServiceLoader.load(Hello.class);
        assertThat(load.say()).isEqualTo("Bonjour");
    }

    /**
     * Test load by class and activate name.
     */
    @Test
    public void testLoadByClassAndActivateName() {
        Hello englishHello = EnhancedServiceLoader.load(Hello.class, "EnglishHello");
        assertThat(englishHello.say()).isEqualTo("hello!");
    }

    /**
     * Test load by class and class loader and activate name.
     */
    @Test
    public void testLoadByClassAndClassLoaderAndActivateName() {
        Hello englishHello = EnhancedServiceLoader.load(Hello.class, "EnglishHello", EnhancedServiceLoaderTest.class.getClassLoader());
        assertThat(englishHello.say()).isEqualTo("hello!");
    }

    /**
     * Gets all extension class.
     */
    @Test
    public void getAllExtensionClass() {
        List<Class> allExtensionClass = EnhancedServiceLoader.getAllExtensionClass(Hello.class);
        assertThat(allExtensionClass.get(2).getSimpleName()).isEqualTo(FrenchHello.class.getSimpleName());
        assertThat(allExtensionClass.get(1).getSimpleName()).isEqualTo(EnglishHello.class.getSimpleName());
        assertThat(allExtensionClass.get(0).getSimpleName()).isEqualTo(ChineseHello.class.getSimpleName());
    }

    /**
     * Gets all extension class 1.
     */
    @Test
    public void getAllExtensionClass1() {
        List<Class> allExtensionClass = EnhancedServiceLoader.getAllExtensionClass(Hello.class, ClassLoader.getSystemClassLoader());
        assertThat(allExtensionClass).isNotEmpty();
    }
}

