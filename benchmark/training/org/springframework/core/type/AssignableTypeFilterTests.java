/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.core.type;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;


/**
 *
 *
 * @author Ramnivas Laddad
 * @author Juergen Hoeller
 */
public class AssignableTypeFilterTests {
    @Test
    public void directMatch() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        String classUnderTest = "org.springframework.core.type.AssignableTypeFilterTests$TestNonInheritingClass";
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);
        AssignableTypeFilter matchingFilter = new AssignableTypeFilter(AssignableTypeFilterTests.TestNonInheritingClass.class);
        AssignableTypeFilter notMatchingFilter = new AssignableTypeFilter(AssignableTypeFilterTests.TestInterface.class);
        Assert.assertFalse(notMatchingFilter.match(metadataReader, metadataReaderFactory));
        Assert.assertTrue(matchingFilter.match(metadataReader, metadataReaderFactory));
    }

    @Test
    public void interfaceMatch() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        String classUnderTest = "org.springframework.core.type.AssignableTypeFilterTests$TestInterfaceImpl";
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);
        AssignableTypeFilter filter = new AssignableTypeFilter(AssignableTypeFilterTests.TestInterface.class);
        Assert.assertTrue(filter.match(metadataReader, metadataReaderFactory));
        ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
    }

    @Test
    public void superClassMatch() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        String classUnderTest = "org.springframework.core.type.AssignableTypeFilterTests$SomeDaoLikeImpl";
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);
        AssignableTypeFilter filter = new AssignableTypeFilter(AssignableTypeFilterTests.SimpleJdbcDaoSupport.class);
        Assert.assertTrue(filter.match(metadataReader, metadataReaderFactory));
        ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
    }

    @Test
    public void interfaceThroughSuperClassMatch() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        String classUnderTest = "org.springframework.core.type.AssignableTypeFilterTests$SomeDaoLikeImpl";
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);
        AssignableTypeFilter filter = new AssignableTypeFilter(AssignableTypeFilterTests.JdbcDaoSupport.class);
        Assert.assertTrue(filter.match(metadataReader, metadataReaderFactory));
        ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
    }

    // We must use a standalone set of types to ensure that no one else is loading them
    // and interfere with ClassloadingAssertions.assertClassNotLoaded()
    private static class TestNonInheritingClass {}

    private interface TestInterface {}

    @SuppressWarnings("unused")
    private static class TestInterfaceImpl implements AssignableTypeFilterTests.TestInterface {}

    private interface SomeDaoLikeInterface {}

    @SuppressWarnings("unused")
    private static class SomeDaoLikeImpl extends AssignableTypeFilterTests.SimpleJdbcDaoSupport implements AssignableTypeFilterTests.SomeDaoLikeInterface {}

    private interface JdbcDaoSupport {}

    private static class SimpleJdbcDaoSupport implements AssignableTypeFilterTests.JdbcDaoSupport {}
}
