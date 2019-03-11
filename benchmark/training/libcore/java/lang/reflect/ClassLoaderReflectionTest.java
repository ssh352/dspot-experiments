/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.lang.reflect;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import junit.framework.TestCase;


/**
 * This class creates another class loader to load multiple copies of various
 * classes into the VM at once. Then it verifies that reflection resolves the
 * class names using the correct class loader.
 */
public final class ClassLoaderReflectionTest extends TestCase {
    /* Each of these class instances points to a different copy of the class
    than the one in the application class loader!
     */
    private Class<?> aClass;

    private Class<?> aListClass;

    private Class<?> bClass;

    private Class<?> bStringClass;

    private Class<?> cClass;

    private Class<?> dClass;

    private Class<?> eClass;

    private Class<?> fClass;

    public void testLoadOneClassInTwoClassLoadersSimultaneously() throws Exception {
        TestCase.assertEquals(aClass.getName(), ClassLoaderReflectionTest.A.class.getName());
        TestCase.assertNotSame(aClass, ClassLoaderReflectionTest.A.class);
    }

    public void testField() throws Exception {
        TestCase.assertEquals(aClass, aListClass.getDeclaredField("field").getType());
    }

    /**
     * http://code.google.com/p/android/issues/detail?id=10111
     */
    public void testGenericSuperclassParameter() throws Exception {
        assertParameterizedType(aListClass.getGenericSuperclass(), ArrayList.class, aClass);
    }

    public void testGenericSuperclassRawType() throws Exception {
        assertParameterizedType(bStringClass.getGenericSuperclass(), bClass, String.class);
    }

    public void testTypeParameters() throws Exception {
        TypeVariable<? extends Class<?>>[] typeVariables = cClass.getTypeParameters();
        TestCase.assertEquals(2, typeVariables.length);
        assertTypeVariable(typeVariables[0], "K", String.class);
        assertTypeVariable(typeVariables[1], "V", aClass);
    }

    public void testGenericInterfaces() throws Exception {
        Type[] types = eClass.getGenericInterfaces();
        TestCase.assertEquals(2, types.length);
        // TODO: this test incorrectly assumes that interfaces will be returned in source order!
        assertParameterizedType(types[0], Callable.class, aClass);
        assertParameterizedType(types[1], dClass, aClass);
    }

    public void testFieldGenericType() throws Exception {
        Field bString = fClass.getDeclaredField("bString");
        assertParameterizedType(bString.getGenericType(), bClass, String.class);
        Field listA = fClass.getDeclaredField("listA");
        assertParameterizedType(listA.getGenericType(), List.class, aClass);
    }

    public void testConstructorGenericType() throws Exception {
        Constructor<?> constructor = fClass.getDeclaredConstructors()[0];
        Type[] parameters = constructor.getGenericParameterTypes();
        assertParameterizedType(parameters[0], bClass, String.class);
        assertParameterizedType(parameters[1], List.class, aClass);
    }

    public void testMethodGenericReturnType() throws Exception {
        Method method = fClass.getDeclaredMethod("method", bClass, List.class);
        assertParameterizedType(method.getGenericReturnType(), bClass, String.class);
    }

    public void testMethodGenericParameterTypes() throws Exception {
        Method method = fClass.getDeclaredMethod("method", bClass, List.class);
        Type[] types = method.getGenericParameterTypes();
        TestCase.assertEquals(2, types.length);
        assertParameterizedType(types[0], bClass, String.class);
        assertParameterizedType(types[1], List.class, aClass);
    }

    static class A {}

    static class B<T> {
        T field;
    }

    static class C<K extends String, V extends ClassLoaderReflectionTest.A> {}

    interface D<T> {}

    class E implements Callable<ClassLoaderReflectionTest.A> , ClassLoaderReflectionTest.D<ClassLoaderReflectionTest.A> {
        public ClassLoaderReflectionTest.A call() throws Exception {
            return null;
        }
    }

    class F {
        ClassLoaderReflectionTest.B<String> bString;

        List<ClassLoaderReflectionTest.A> listA;

        F(ClassLoaderReflectionTest.B<String> parameter, List<ClassLoaderReflectionTest.A> anotherParameter) {
        }

        ClassLoaderReflectionTest.B<String> method(ClassLoaderReflectionTest.B<String> parameter, List<ClassLoaderReflectionTest.A> anotherParameter) {
            return null;
        }
    }

    static class AList extends ArrayList<ClassLoaderReflectionTest.A> {
        ClassLoaderReflectionTest.A field;
    }

    static class BString extends ClassLoaderReflectionTest.B<String> {}
}

