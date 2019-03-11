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
package com.alipay.sofa.rpc.common.utils;


import java.lang.reflect.Array;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class ClassTypeUtilsTest {
    // ???
    Object anonymous = new Comparable<String>() {
        @Override
        public int compareTo(String o) {
            return 0;
        }
    };

    // ???
    private class MemberClass {}

    @Test
    public void testGetClass() {
        Assert.assertEquals(String.class, ClassTypeUtils.getClass("java.lang.String"));
        Assert.assertEquals(int.class, ClassTypeUtils.getClass("int"));
        Assert.assertEquals(boolean.class, ClassTypeUtils.getClass("boolean"));
        Assert.assertEquals(byte.class, ClassTypeUtils.getClass("byte"));
        Assert.assertEquals(char.class, ClassTypeUtils.getClass("char"));
        Assert.assertEquals(double.class, ClassTypeUtils.getClass("double"));
        Assert.assertEquals(float.class, ClassTypeUtils.getClass("float"));
        Assert.assertEquals(int.class, ClassTypeUtils.getClass("int"));
        Assert.assertEquals(long.class, ClassTypeUtils.getClass("long"));
        Assert.assertEquals(short.class, ClassTypeUtils.getClass("short"));
        Assert.assertEquals(void.class, ClassTypeUtils.getClass("void"));
        // ???
        class LocalType {}
        Assert.assertEquals(anonymous.getClass(), ClassTypeUtils.getClass("com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$1"));
        Assert.assertEquals(LocalType.class, ClassTypeUtils.getClass("com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$1LocalType"));
        Assert.assertEquals(ClassTypeUtilsTest.MemberClass.class, ClassTypeUtils.getClass("com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$MemberClass"));
        Assert.assertEquals(StaticClass.class, ClassTypeUtils.getClass("com.alipay.sofa.rpc.common.utils.StaticClass"));
        Assert.assertEquals(String[].class, ClassTypeUtils.getClass("java.lang.String[]"));
        Assert.assertEquals(boolean[].class, ClassTypeUtils.getClass("boolean[]"));
        Assert.assertEquals(byte[].class, ClassTypeUtils.getClass("byte[]"));
        Assert.assertEquals(char[].class, ClassTypeUtils.getClass("char[]"));
        Assert.assertEquals(double[].class, ClassTypeUtils.getClass("double[]"));
        Assert.assertEquals(float[].class, ClassTypeUtils.getClass("float[]"));
        Assert.assertEquals(int[].class, ClassTypeUtils.getClass("int[]"));
        Assert.assertEquals(long[].class, ClassTypeUtils.getClass("long[]"));
        Assert.assertEquals(short[].class, ClassTypeUtils.getClass("short[]"));
        Assert.assertEquals(Array.newInstance(anonymous.getClass(), 2, 3).getClass(), ClassTypeUtils.getClass("com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$1[][]"));
        Assert.assertEquals(LocalType[][].class, ClassTypeUtils.getClass("com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$1LocalType[][]"));
        Assert.assertEquals(ClassTypeUtilsTest.MemberClass[].class, ClassTypeUtils.getClass("com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$MemberClass[]"));
        Assert.assertEquals(StaticClass[].class, ClassTypeUtils.getClass("com.alipay.sofa.rpc.common.utils.StaticClass[]"));
        Assert.assertEquals(int[][].class, ClassTypeUtils.getClass("int[][]"));
        Assert.assertEquals(String[].class, ClassTypeUtils.getClass(String[].class.getName()));
        Assert.assertEquals(boolean[].class, ClassTypeUtils.getClass(boolean[].class.getName()));
        Assert.assertEquals(byte[].class, ClassTypeUtils.getClass(byte[].class.getName()));
        Assert.assertEquals(char[].class, ClassTypeUtils.getClass(char[].class.getName()));
        Assert.assertEquals(double[].class, ClassTypeUtils.getClass(double[].class.getName()));
        Assert.assertEquals(float[].class, ClassTypeUtils.getClass(float[].class.getName()));
        Assert.assertEquals(int[].class, ClassTypeUtils.getClass(int[].class.getName()));
        Assert.assertEquals(long[].class, ClassTypeUtils.getClass(long[].class.getName()));
        Assert.assertEquals(short[].class, ClassTypeUtils.getClass(short[].class.getName()));
        Assert.assertEquals(int[][].class, ClassTypeUtils.getClass(int[][].class.getName()));
    }

    @Test
    public void testGetTypeStr() {
        Assert.assertEquals(ClassTypeUtils.getTypeStr(String.class), "java.lang.String");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(boolean.class), "boolean");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(byte.class), "byte");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(char.class), "char");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(double.class), "double");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(float.class), "float");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(int.class), "int");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(long.class), "long");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(short.class), "short");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(void.class), "void");
        // ???
        class LocalType {}
        Assert.assertEquals(ClassTypeUtils.getTypeStr(anonymous.getClass()), "com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$1");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(LocalType.class), "com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$2LocalType");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(ClassTypeUtilsTest.MemberClass.class), "com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$MemberClass");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(StaticClass.class), "com.alipay.sofa.rpc.common.utils.StaticClass");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(String[][][].class), "java.lang.String[][][]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(boolean[].class), "boolean[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(byte[].class), "byte[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(char[].class), "char[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(double[].class), "double[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(float[].class), "float[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(int[].class), "int[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(long[].class), "long[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(short[].class), "short[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(Array.newInstance(anonymous.getClass(), 2, 3).getClass()), "com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$1[][]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(LocalType[][].class), "com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$2LocalType[][]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(ClassTypeUtilsTest.MemberClass[].class), "com.alipay.sofa.rpc.common.utils.ClassTypeUtilsTest$MemberClass[]");
        Assert.assertEquals(ClassTypeUtils.getTypeStr(StaticClass[].class), "com.alipay.sofa.rpc.common.utils.StaticClass[]");
        Assert.assertArrayEquals(ClassTypeUtils.getTypeStrs(new Class[]{ String[].class }), new String[]{ "java.lang.String[]" });
        Assert.assertArrayEquals(ClassTypeUtils.getTypeStrs(new Class[]{ String[].class }, false), new String[]{ "java.lang.String[]" });
        Assert.assertArrayEquals(ClassTypeUtils.getTypeStrs(new Class[]{ String[].class }, true), new String[]{ String[].class.getName() });
    }
}

