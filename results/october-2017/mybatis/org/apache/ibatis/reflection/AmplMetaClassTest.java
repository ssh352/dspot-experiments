/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection;


public class AmplMetaClassTest {
    private org.apache.ibatis.domain.misc.RichType rich = new org.apache.ibatis.domain.misc.RichType();

    java.util.Map<java.lang.String, org.apache.ibatis.domain.misc.RichType> map = new java.util.HashMap<java.lang.String, org.apache.ibatis.domain.misc.RichType>() {
        {
            put("richType", rich);
        }
    };

    public AmplMetaClassTest() {
        rich.setRichType(new org.apache.ibatis.domain.misc.RichType());
    }

    @org.junit.Test
    public void shouldTestDataTypeOfGenericMethod() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.generics.GenericConcrete.class, reflectorFactory);
        org.junit.Assert.assertEquals(java.lang.Long.class, meta.getGetterType("id"));
        org.junit.Assert.assertEquals(java.lang.Long.class, meta.getSetterType("id"));
    }

    @org.junit.Test
    public void shouldCheckGetterExistance() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        org.junit.Assert.assertTrue(meta.hasGetter("richField"));
        org.junit.Assert.assertTrue(meta.hasGetter("richProperty"));
        org.junit.Assert.assertTrue(meta.hasGetter("richList"));
        org.junit.Assert.assertTrue(meta.hasGetter("richMap"));
        org.junit.Assert.assertTrue(meta.hasGetter("richList[0]"));
        org.junit.Assert.assertTrue(meta.hasGetter("richType"));
        org.junit.Assert.assertTrue(meta.hasGetter("richType.richField"));
        org.junit.Assert.assertTrue(meta.hasGetter("richType.richProperty"));
        org.junit.Assert.assertTrue(meta.hasGetter("richType.richList"));
        org.junit.Assert.assertTrue(meta.hasGetter("richType.richMap"));
        org.junit.Assert.assertTrue(meta.hasGetter("richType.richList[0]"));
        org.junit.Assert.assertFalse(meta.hasGetter("[0]"));
    }

    @org.junit.Test
    public void shouldCheckSetterExistance() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        org.junit.Assert.assertTrue(meta.hasSetter("richField"));
        org.junit.Assert.assertTrue(meta.hasSetter("richProperty"));
        org.junit.Assert.assertTrue(meta.hasSetter("richList"));
        org.junit.Assert.assertTrue(meta.hasSetter("richMap"));
        org.junit.Assert.assertTrue(meta.hasSetter("richList[0]"));
        org.junit.Assert.assertTrue(meta.hasSetter("richType"));
        org.junit.Assert.assertTrue(meta.hasSetter("richType.richField"));
        org.junit.Assert.assertTrue(meta.hasSetter("richType.richProperty"));
        org.junit.Assert.assertTrue(meta.hasSetter("richType.richList"));
        org.junit.Assert.assertTrue(meta.hasSetter("richType.richMap"));
        org.junit.Assert.assertTrue(meta.hasSetter("richType.richList[0]"));
        org.junit.Assert.assertFalse(meta.hasSetter("[0]"));
    }

    @org.junit.Test
    public void shouldCheckTypeForEachGetter() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        org.junit.Assert.assertEquals(java.lang.String.class, meta.getGetterType("richField"));
        org.junit.Assert.assertEquals(java.lang.String.class, meta.getGetterType("richProperty"));
        org.junit.Assert.assertEquals(java.util.List.class, meta.getGetterType("richList"));
        org.junit.Assert.assertEquals(java.util.Map.class, meta.getGetterType("richMap"));
        org.junit.Assert.assertEquals(java.util.List.class, meta.getGetterType("richList[0]"));
        org.junit.Assert.assertEquals(org.apache.ibatis.domain.misc.RichType.class, meta.getGetterType("richType"));
        org.junit.Assert.assertEquals(java.lang.String.class, meta.getGetterType("richType.richField"));
        org.junit.Assert.assertEquals(java.lang.String.class, meta.getGetterType("richType.richProperty"));
        org.junit.Assert.assertEquals(java.util.List.class, meta.getGetterType("richType.richList"));
        org.junit.Assert.assertEquals(java.util.Map.class, meta.getGetterType("richType.richMap"));
        org.junit.Assert.assertEquals(java.util.List.class, meta.getGetterType("richType.richList[0]"));
    }

    @org.junit.Test
    public void shouldCheckTypeForEachSetter() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        org.junit.Assert.assertEquals(java.lang.String.class, meta.getSetterType("richField"));
        org.junit.Assert.assertEquals(java.lang.String.class, meta.getSetterType("richProperty"));
        org.junit.Assert.assertEquals(java.util.List.class, meta.getSetterType("richList"));
        org.junit.Assert.assertEquals(java.util.Map.class, meta.getSetterType("richMap"));
        org.junit.Assert.assertEquals(java.util.List.class, meta.getSetterType("richList[0]"));
        org.junit.Assert.assertEquals(org.apache.ibatis.domain.misc.RichType.class, meta.getSetterType("richType"));
        org.junit.Assert.assertEquals(java.lang.String.class, meta.getSetterType("richType.richField"));
        org.junit.Assert.assertEquals(java.lang.String.class, meta.getSetterType("richType.richProperty"));
        org.junit.Assert.assertEquals(java.util.List.class, meta.getSetterType("richType.richList"));
        org.junit.Assert.assertEquals(java.util.Map.class, meta.getSetterType("richType.richMap"));
        org.junit.Assert.assertEquals(java.util.List.class, meta.getSetterType("richType.richList[0]"));
    }

    @org.junit.Test
    public void shouldCheckGetterAndSetterNames() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        org.junit.Assert.assertEquals(5, meta.getGetterNames().length);
        org.junit.Assert.assertEquals(5, meta.getSetterNames().length);
    }

    @org.junit.Test
    public void shouldFindPropertyName() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        org.junit.Assert.assertEquals("richField", meta.findProperty("RICHfield"));
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd10() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // StatementAdd: add invocation of a method
        meta.getSetterNames();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd11_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_6 = "{ha!&Bcvg[?i!rb0/|]6";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // StatementAdd: add invocation of a method
            meta.getSetterType(__DSPOT_name_6);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd11 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd4() {
        java.lang.String __DSPOT_name_0 = "-*k},GdhscbCS@!x*zH_";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterAndSetterNames_sd4__6 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterAndSetterNames_sd4__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd5() {
        boolean __DSPOT_useCamelCaseMapping_2 = true;
        java.lang.String __DSPOT_name_1 = ",y(q2 5[gpbL[{$QV5:W";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterAndSetterNames_sd5__7 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterAndSetterNames_sd5__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd6_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_3 = "2[|+mr6#-VtX(r!Fs2l>";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_3);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd6 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd15_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_9 = "}8wu]&8(Dgh`l V!3a(!";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // StatementAdd: add invocation of a method
            meta.metaClassForProperty(__DSPOT_name_9);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd15 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd9_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_5 = ";0L`A=SO/woO!OKS@Rl&";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_5);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd9 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd12 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd12_sd187_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_93 = "-E+,N[v<l2kvDr1FoAgu";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterAndSetterNames_sd12__5 = // StatementAdd: add invocation of a method
            meta.hasDefaultConstructor();
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_93);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd12_sd187 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd15 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd15_failAssert4_sd446() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_191 = "fH=q(]@Dt@)l]!qOeddH";
            java.lang.String __DSPOT_name_9 = "}8wu]&8(Dgh`l V!3a(!";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // StatementAdd: add invocation of a method
            meta.metaClassForProperty(__DSPOT_name_9);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd15 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            meta.hasGetter(__DSPOT_name_191);
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd7 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd7_sd156() {
        java.lang.String __DSPOT_name_70 = ".p/60%FD2[JO=6vk:PDc";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // StatementAdd: add invocation of a method
        meta.getGetterNames();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterAndSetterNames_sd7_sd156__8 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_70);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterAndSetterNames_sd7_sd156__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd14 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd14_sd226() {
        boolean __DSPOT_useCamelCaseMapping_112 = false;
        java.lang.String __DSPOT_name_111 = "v*a[[KUdhw0!nnrtG]LM";
        java.lang.String __DSPOT_name_8 = "wpauR%h1,xavU[1Rvnj|";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterAndSetterNames_sd14__6 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterAndSetterNames_sd14_sd226__12 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_111, __DSPOT_useCamelCaseMapping_112);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterAndSetterNames_sd14_sd226__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd7 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd7_sd164() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // StatementAdd: add invocation of a method
        meta.getGetterNames();
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterAndSetterNames_sd7_sd164__7 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterAndSetterNames_sd7_sd164__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd10 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd10_sd181() {
        java.lang.String __DSPOT_name_88 = "M,.G+$]g)e+[it&{.K}M";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // StatementAdd: add invocation of a method
        meta.getSetterNames();
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterAndSetterNames_sd10_sd181__8 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_88);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterAndSetterNames_sd10_sd181__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd5 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd5_literalMutationString133() {
        boolean __DSPOT_useCamelCaseMapping_2 = true;
        java.lang.String __DSPOT_name_1 = ",y(q2 5[gpbL[{$V5:W";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterAndSetterNames_sd5__7 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterAndSetterNames_sd5__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd9 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd9_failAssert2_sd341 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd9_failAssert2_sd341_sd4820() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_152 = " <3bSwN_qZeEg/gcm:Ca";
            java.lang.String __DSPOT_name_5 = ";0L`A=SO/woO!OKS@Rl&";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_5);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd9 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            meta.findProperty(__DSPOT_name_152);
            // StatementAdd: add invocation of a method
            meta.getGetterNames();
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd10 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd10_sd181 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd10_sd181_literalMutationString6432() {
        java.lang.String __DSPOT_name_88 = "M,.G+$]g)e+[t&{.K}M";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // StatementAdd: add invocation of a method
        meta.getSetterNames();
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterAndSetterNames_sd10_sd181__8 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_88);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd5 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd5_sd149 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd5_sd149_sd5153() {
        boolean __DSPOT_useCamelCaseMapping_2 = true;
        java.lang.String __DSPOT_name_1 = ",y(q2 5[gpbL[{$QV5:W";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterAndSetterNames_sd5__7 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterAndSetterNames_sd5__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterAndSetterNames_sd5_sd149__11 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterAndSetterNames_sd5_sd149_sd5153__15 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterAndSetterNames_sd5_sd149_sd5153__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterAndSetterNames_sd5__7);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd11 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd11_failAssert3_sd369 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd11_failAssert3_sd369_sd7111() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_arg0_3187 = "5&qv$[LbKI#64En`yS4x";
            java.lang.String __DSPOT_name_6 = "{ha!&Bcvg[?i!rb0/|]6";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // StatementAdd: generate variable from return value
            java.lang.Class<?> __DSPOT_invoc_12 = // StatementAdd: add invocation of a method
            meta.getSetterType(__DSPOT_name_6);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd11 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            meta.hasDefaultConstructor();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_12.getResource(__DSPOT_arg0_3187);
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd15 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd15_failAssert4_sd437 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd15_failAssert4_sd437_sd2118() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_184 = "& CZ;6xP0<S_F6rq5 w=";
            java.lang.String __DSPOT_name_9 = "}8wu]&8(Dgh`l V!3a(!";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // StatementAdd: add invocation of a method
            meta.metaClassForProperty(__DSPOT_name_9);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd15 should have thrown ReflectionException");
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_16 = // StatementAdd: add invocation of a method
            meta.findProperty(__DSPOT_name_184);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_16.toLowerCase();
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd6 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd6_failAssert0_sd245 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd6_failAssert0_sd245_sd7211() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg3_3255 = 546262957;
            int __DSPOT_arg2_3254 = 1283333730;
            java.lang.String __DSPOT_arg1_3253 = ",5PU0$^Jab7yIq%DF%N;";
            int __DSPOT_arg0_3252 = -217334731;
            java.lang.String __DSPOT_name_120 = "EY]mb@&7yoEh?_F)3VJg";
            java.lang.String __DSPOT_name_3 = "2[|+mr6#-VtX(r!Fs2l>";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_3);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd6 should have thrown ReflectionException");
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_16 = // StatementAdd: add invocation of a method
            meta.findProperty(__DSPOT_name_120);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_16.regionMatches(__DSPOT_arg0_3252, __DSPOT_arg1_3253, __DSPOT_arg2_3254, __DSPOT_arg3_3255);
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd13 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd13_sd215 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd13_sd215_literalMutationString6657() {
        java.lang.String __DSPOT_name_108 = "T)uukoMbx9bx>ua@F;_a";
        java.lang.String __DSPOT_name_7 = "1 Some Street";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterAndSetterNames_sd13__6 = // StatementAdd: add invocation of a method
        meta.hasGetter(__DSPOT_name_7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterAndSetterNames_sd13_sd215__11 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_108);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd4 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd4_sd121 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd4_sd121_sd5342_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_2355 = "=l;$4[.ri<.TPKf)[ECp";
            boolean __DSPOT_useCamelCaseMapping_52 = true;
            java.lang.String __DSPOT_name_51 = "I:lZE.`#n5*TWD1iXd&W";
            java.lang.String __DSPOT_name_0 = "-*k},GdhscbCS@!x*zH_";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_shouldCheckGetterAndSetterNames_sd4__6 = // StatementAdd: add invocation of a method
            meta.findProperty(__DSPOT_name_0);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_shouldCheckGetterAndSetterNames_sd4_sd121__12 = // StatementAdd: add invocation of a method
            meta.findProperty(__DSPOT_name_51, __DSPOT_useCamelCaseMapping_52);
            // StatementAdd: add invocation of a method
            meta.hasGetter(__DSPOT_name_2355);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_sd4_sd121_sd5342 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd4 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterAndSetterNames_sd4_literalMutationString112 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_sd4_literalMutationString112_literalMutationString2685() {
        java.lang.String __DSPOT_name_0 = "-*k},GdhsbCI@!x*zH_";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterAndSetterNames_sd4__6 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterAndSetterNames_sd4__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_sd9397_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_3822 = "&>]5s)0Qf]G6]#wwfot=";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.hasGetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_3822);
            org.junit.Assert.fail("shouldCheckGetterExistance_sd9397 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_sd9406_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_3828 = "2*hXP>uSOz9F3A=HK|gB";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.hasGetter("[0]");
            // StatementAdd: add invocation of a method
            meta.metaClassForProperty(__DSPOT_name_3828);
            org.junit.Assert.fail("shouldCheckGetterExistance_sd9406 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_sd9396() {
        boolean __DSPOT_useCamelCaseMapping_3821 = false;
        java.lang.String __DSPOT_name_3820 = "o>V9DCjgbo&gCzovAD=>";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__7 = meta.hasGetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__8 = meta.hasGetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__9 = meta.hasGetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__10 = meta.hasGetter("richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__11 = meta.hasGetter("richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__12 = meta.hasGetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__13 = meta.hasGetter("richType.richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__14 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__15 = meta.hasGetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__16 = meta.hasGetter("richType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__16);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__17 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__17);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__18 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_sd9396__18);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterExistance_sd9396__19 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_3820, __DSPOT_useCamelCaseMapping_3821);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterExistance_sd9396__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__17);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_sd9396__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396__10);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9367() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__5 = meta.hasGetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__6 = meta.hasGetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__8 = meta.hasGetter("richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__9 = meta.hasGetter("richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__10 = meta.hasGetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__11 = meta.hasGetter("richType.richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__13 = meta.hasGetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__14 = meta.hasGetter("riceType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9367__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9367__16 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9367__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9367__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9367__10);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9355() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__5 = meta.hasGetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__6 = meta.hasGetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__7 = meta.hasGetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__8 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__9 = meta.hasGetter("richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__10 = meta.hasGetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__11 = meta.hasGetter("richType.richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__12 = meta.hasGetter("richType.richPropetrty");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9355__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__13 = meta.hasGetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__14 = meta.hasGetter("richType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9355__16 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9355__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9355__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9355__7);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9332() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__5 = meta.hasGetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__6 = meta.hasGetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__7 = meta.hasGetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__8 = meta.hasGetter("sg_|;ZD");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9332__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__9 = meta.hasGetter("richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__10 = meta.hasGetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__11 = meta.hasGetter("richType.richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__13 = meta.hasGetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__14 = meta.hasGetter("richType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9332__16 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9332__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9332__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9332__10);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_sd9400_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_3824 = "r[]]sY$r?@=P@VU%3;%M";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.hasGetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_3824);
            org.junit.Assert.fail("shouldCheckGetterExistance_sd9400 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9344 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9344_sd13058() {
        java.lang.String __DSPOT_name_4169 = "4B%2m.wT_R`e9uqV w^F";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__5 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__8 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__9 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__10 = meta.hasGetter("rwichType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__11 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__13 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__14 = meta.hasGetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9344__16 = meta.hasGetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterExistance_literalMutationString9344_sd13058__42 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_4169);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterExistance_literalMutationString9344_sd13058__42);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_sd9395 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_sd9395_add18075() {
        java.lang.String __DSPOT_name_3819 = "&aREfP7Y+7MhB` #/6&!";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395_add18075__6 = // MethodCallAdder
        meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__6 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__7 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__8 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__9 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__10 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__11 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__12 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__13 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__14 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__15 = meta.hasGetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__16 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__17 = meta.hasGetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterExistance_sd9395__18 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_3819);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterExistance_sd9395__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9395_add18075__6);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9365 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9365_literalMutationString14999() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__5 = meta.hasGetter("rchField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__8 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__9 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__10 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__11 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__13 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__14 = meta.hasGetter("1 Some Street");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9365__16 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_sd9396 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_sd9396_add18185() {
        boolean __DSPOT_useCamelCaseMapping_3821 = false;
        java.lang.String __DSPOT_name_3820 = "o>V9DCjgbo&gCzovAD=>";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__7 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__8 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__9 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__10 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__11 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396_add18185__22 = // MethodCallAdder
        meta.hasGetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396_add18185__22);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__12 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__13 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__14 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__15 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__16 = meta.hasGetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__17 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9396__18 = meta.hasGetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterExistance_sd9396__19 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_3820, __DSPOT_useCamelCaseMapping_3821);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterExistance_sd9396__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_sd9396_add18185__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9372 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9372_sd15769() {
        java.lang.String __DSPOT_name_4457 = "Z.+A0[Y*Vd][t*uwV{dp";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__5 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__8 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__9 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__10 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__11 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__13 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__14 = meta.hasGetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__15 = meta.hasGetter("1 Some Street");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372__16 = meta.hasGetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9372_sd15769__42 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_4457);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9372_sd15769__42);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9366 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9366_add15170() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__5 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366_add15170__11 = // MethodCallAdder
        meta.hasGetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9366_add15170__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__8 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__9 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__10 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__11 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__13 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__14 = meta.hasGetter("richype.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__16 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckGetterExistance_literalMutationString9366_add15170__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9363 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9363_literalMutationString14827 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9363_literalMutationString14827_literalMutationString30103() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__5 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__8 = meta.hasGetter("richwMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__9 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__10 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__11 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__12 = meta.hasGetter("richType.richProerty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__13 = meta.hasGetter("richType.ichList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__14 = meta.hasGetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__16 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_sd9399 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_sd9399_failAssert1_sd18920 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_sd9399_failAssert1_sd18920_literalMutationString20404() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_3823 = "ZFQ3U}J,$}=:m^`>CL*%";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__8 = meta.hasGetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__9 = meta.hasGetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__10 = meta.hasGetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__11 = meta.hasGetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__12 = meta.hasGetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__13 = meta.hasGetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__14 = meta.hasGetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__15 = meta.hasGetter("");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__16 = meta.hasGetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__17 = meta.hasGetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__18 = meta.hasGetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_sd9399_failAssert1_sd18920__19 = meta.hasGetter("[0]");
            // StatementAdd: generate variable from return value
            java.lang.Class<?> __DSPOT_invoc_20 = // StatementAdd: add invocation of a method
            meta.getGetterType(__DSPOT_name_3823);
            org.junit.Assert.fail("shouldCheckGetterExistance_sd9399 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_20.getAnnotatedInterfaces();
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9366 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9366_add15170 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9366_add15170_literalMutationString24466() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__5 = meta.hasGetter("1 Some Street");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366_add15170__11 = // MethodCallAdder
        meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__8 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__9 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__10 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__11 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__13 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__14 = meta.hasGetter("richype.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9366__16 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9312 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9312_literalMutationString9939 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9312_literalMutationString9939_sd31041() {
        boolean __DSPOT_useCamelCaseMapping_6039 = true;
        java.lang.String __DSPOT_name_6038 = "Jlme:@(iY@{N_|0;GFyR";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__5 = meta.hasGetter("1 Some Street");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__8 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__9 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__10 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__11 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__13 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__14 = meta.hasGetter("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9312__16 = meta.hasGetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterExistance_literalMutationString9312_literalMutationString9939_sd31041__43 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_6038, __DSPOT_useCamelCaseMapping_6039);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterExistance_literalMutationString9312_literalMutationString9939_sd31041__43);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9356 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9356_literalMutationString14163 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9356_literalMutationString14163_sd26062_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_5493 = "j?L!m!+G`hvv<hV*mImu";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__5 = meta.hasGetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__6 = meta.hasGetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__7 = meta.hasGetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__8 = meta.hasGetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__9 = meta.hasGetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__10 = meta.hasGetter("1 Some Street");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__11 = meta.hasGetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__12 = meta.hasGetter("ri hType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__13 = meta.hasGetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__14 = meta.hasGetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__15 = meta.hasGetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9356__16 = meta.hasGetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getSetterType(__DSPOT_name_5493);
            org.junit.Assert.fail("shouldCheckGetterExistance_literalMutationString9356_literalMutationString14163_sd26062 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9363 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9363_literalMutationString14827 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9363_literalMutationString14827_add30137() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__5 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__8 = meta.hasGetter("richwMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__9 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__10 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__11 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__12 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363_literalMutationString14827_add30137__29 = // MethodCallAdder
        meta.hasGetter("richType.ichList");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9363_literalMutationString14827_add30137__29);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__13 = meta.hasGetter("richType.ichList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__14 = meta.hasGetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__15 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_literalMutationString9363__16 = meta.hasGetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckGetterExistance_literalMutationString9363_literalMutationString14827_add30137__29);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9319 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_literalMutationString9319_sd10650 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_literalMutationString9319_sd10650_failAssert4_literalMutationString40131() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_3924 = "1 Some Street";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__5 = meta.hasGetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__6 = meta.hasGetter("1 Some Street");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__7 = meta.hasGetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__8 = meta.hasGetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__9 = meta.hasGetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__10 = meta.hasGetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__11 = meta.hasGetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__12 = meta.hasGetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__13 = meta.hasGetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__14 = meta.hasGetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__15 = meta.hasGetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_literalMutationString9319__16 = meta.hasGetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_3924);
            org.junit.Assert.fail("shouldCheckGetterExistance_literalMutationString9319_sd10650 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_add9390 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_add9390_sd17570 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_add9390_sd17570_failAssert2_literalMutationString39974() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_4642 = "> z&4Nm/2t]qCuJ<qpVJ";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__5 = meta.hasGetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__6 = meta.hasGetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__7 = meta.hasGetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__8 = meta.hasGetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__9 = meta.hasGetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__10 = meta.hasGetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__11 = meta.hasGetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__12 = // MethodCallAdder
            meta.hasGetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__14 = meta.hasGetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__15 = meta.hasGetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__16 = meta.hasGetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__17 = meta.hasGetter("1 Some Street");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckGetterExistance_add9390__18 = meta.hasGetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_4642);
            org.junit.Assert.fail("shouldCheckGetterExistance_add9390_sd17570 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_add9386 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_add9386_literalMutationString17078 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_add9386_literalMutationString17078_sd31740() {
        boolean __DSPOT_useCamelCaseMapping_6109 = true;
        java.lang.String __DSPOT_name_6108 = ".$[ uDZH*_9a#XG%6 |}";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__5 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__6 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__7 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__8 = // MethodCallAdder
        meta.hasGetter("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__10 = meta.hasGetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__11 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__12 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__13 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__14 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__15 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__16 = meta.hasGetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__17 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_add9386__18 = meta.hasGetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterExistance_add9386_literalMutationString17078_sd31740__47 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_6108, __DSPOT_useCamelCaseMapping_6109);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterExistance_add9386_literalMutationString17078_sd31740__47);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_sd9395 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckGetterExistance_sd9395_literalMutationString18071 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckGetterExistance_sd9395_literalMutationString18071_literalMutationString32486() {
        java.lang.String __DSPOT_name_3819 = "&aREfP7Y+7MhB` #/6&!";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__6 = meta.hasGetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__7 = meta.hasGetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__8 = meta.hasGetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__9 = meta.hasGetter("richap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__10 = meta.hasGetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__11 = meta.hasGetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__12 = meta.hasGetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__13 = meta.hasGetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__14 = meta.hasGetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__15 = meta.hasGetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__16 = meta.hasGetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckGetterExistance_sd9395__17 = meta.hasGetter("70]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckGetterExistance_sd9395__18 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_3819);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckGetterExistance_sd9395__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40863() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__5 = meta.hasSetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__6 = meta.hasSetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__7 = meta.hasSetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__8 = meta.hasSetter("richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__9 = meta.hasSetter("richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__10 = meta.hasSetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__11 = meta.hasSetter("richnype.richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40863__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__12 = meta.hasSetter("richType.richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__13 = meta.hasSetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__15 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40863__16 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40863__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40863__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40863__14);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40919_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_7052 = "5L:NGv2I57yrQ49$.}VH";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.metaClassForProperty(__DSPOT_name_7052);
            org.junit.Assert.fail("shouldCheckSetterExistance_sd40919 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40908() {
        java.lang.String __DSPOT_name_7043 = ",T0tc<IPJ7xF 2{M@J5>";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__6 = meta.hasSetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__7 = meta.hasSetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__8 = meta.hasSetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__9 = meta.hasSetter("richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__10 = meta.hasSetter("richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__11 = meta.hasSetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__12 = meta.hasSetter("richType.richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__13 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__14 = meta.hasSetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__15 = meta.hasSetter("richType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__16 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__16);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__17 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_sd40908__17);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckSetterExistance_sd40908__18 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_7043);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40908__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_sd40908__17);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908__11);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40860() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__5 = meta.hasSetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__6 = meta.hasSetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__7 = meta.hasSetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__8 = meta.hasSetter("richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__9 = meta.hasSetter("richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__10 = meta.hasSetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__11 = meta.hasSetter("richType.richF,ield");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40860__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__12 = meta.hasSetter("richType.richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__13 = meta.hasSetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__15 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40860__16 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40860__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40860__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40860__5);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40909() {
        boolean __DSPOT_useCamelCaseMapping_7045 = true;
        java.lang.String __DSPOT_name_7044 = "(VY(Wm -y&a0p..W(2v#";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__7 = meta.hasSetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__8 = meta.hasSetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__9 = meta.hasSetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__10 = meta.hasSetter("richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__11 = meta.hasSetter("richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__12 = meta.hasSetter("richType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__13 = meta.hasSetter("richType.richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__14 = meta.hasSetter("richType.richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__15 = meta.hasSetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__16 = meta.hasSetter("richType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__16);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__17 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__17);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__18 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_sd40909__18);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckSetterExistance_sd40909__19 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_7044, __DSPOT_useCamelCaseMapping_7045);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40909__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_sd40909__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__17);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40909__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40849() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__5 = meta.hasSetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__6 = meta.hasSetter("richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__7 = meta.hasSetter("richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__8 = meta.hasSetter("richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__9 = meta.hasSetter("=Blj]d?evwz");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40849__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__10 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__11 = meta.hasSetter("richType.richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__12 = meta.hasSetter("richType.richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__13 = meta.hasSetter("richType.richList");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__15 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40849__16 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40849__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40849__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_literalMutationString40849__10);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40913_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_7048 = "X1z+0;4wN{Di-2nmN:Jb";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_7048);
            org.junit.Assert.fail("shouldCheckSetterExistance_sd40913 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40910_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_7046 = "75}2 ?M s8?HT?OVbDv>";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_7046);
            org.junit.Assert.fail("shouldCheckSetterExistance_sd40910 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40856 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40856_sd44476_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_7386 = "$!V%]:Meyy{S7P/p:qd-";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__5 = meta.hasSetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__6 = meta.hasSetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__7 = meta.hasSetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__8 = meta.hasSetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__9 = meta.hasSetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__10 = meta.hasSetter("rchType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__11 = meta.hasSetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__12 = meta.hasSetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__13 = meta.hasSetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__14 = meta.hasSetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__15 = meta.hasSetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40856__16 = meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_7386);
            org.junit.Assert.fail("shouldCheckSetterExistance_literalMutationString40856_sd44476 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40827 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40827_add41664() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827_add41664__5 = // MethodCallAdder
        meta.hasSetter("richFie_ld");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40827_add41664__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__5 = meta.hasSetter("richFie_ld");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__6 = meta.hasSetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__7 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__8 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__9 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__10 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__11 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__12 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__13 = meta.hasSetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__15 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__16 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40827_add41664__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40868 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40868_add45627() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__5 = meta.hasSetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__6 = meta.hasSetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__7 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__8 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__9 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__10 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__11 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868_add45627__26 = // MethodCallAdder
        meta.hasSetter("richTye.richProperty");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40868_add45627__26);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__12 = meta.hasSetter("richTye.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__13 = meta.hasSetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__15 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40868__16 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40868_add45627__26);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40827 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40827_literalMutationString41643() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__5 = meta.hasSetter("richFie_ld");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__6 = meta.hasSetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__7 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__8 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__9 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__10 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__11 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__12 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__13 = meta.hasSetter("-.f@.6nx.^_&<R7Fs");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__15 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40827__16 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40908 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40908_sd49601() {
        java.lang.String __DSPOT_name_7913 = ":Z.KzuUWwZ9Z&W.-vd3h";
        java.lang.String __DSPOT_name_7043 = ",T0tc<IPJ7xF 2{M@J5>";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__6 = meta.hasSetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__7 = meta.hasSetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__8 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__9 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__10 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__11 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__12 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__13 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__14 = meta.hasSetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__15 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__16 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__17 = meta.hasSetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckSetterExistance_sd40908__18 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_7043);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40908__18);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckSetterExistance_sd40908_sd49601__47 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_7913);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40908_sd49601__47);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40908__18);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40885 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40885_add47270() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__5 = meta.hasSetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__6 = meta.hasSetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__7 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__8 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__9 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__10 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__11 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__12 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__13 = meta.hasSetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885_add47270__35 = // MethodCallAdder
        meta.hasSetter("richType.richList0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40885_add47270__35);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__15 = meta.hasSetter("richType.richList0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40885__16 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldCheckSetterExistance_literalMutationString40885_add47270__35);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40852 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40852_sd44094_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_7348 = "8oy:q&esCahEmhk$1_2|";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__5 = meta.hasSetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__6 = meta.hasSetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__7 = meta.hasSetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__8 = meta.hasSetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__9 = meta.hasSetter("richL1st[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__10 = meta.hasSetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__11 = meta.hasSetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__12 = meta.hasSetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__13 = meta.hasSetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__14 = meta.hasSetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__15 = meta.hasSetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40852__16 = meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_7348);
            org.junit.Assert.fail("shouldCheckSetterExistance_literalMutationString40852_sd44094 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40909 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40909_add49692() {
        boolean __DSPOT_useCamelCaseMapping_7045 = true;
        java.lang.String __DSPOT_name_7044 = "(VY(Wm -y&a0p..W(2v#";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator create local variable with return value of invocation
        org.apache.ibatis.reflection.MetaClass o_shouldCheckSetterExistance_sd40909_add49692__5 = // MethodCallAdder
        org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__7 = meta.hasSetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__8 = meta.hasSetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__9 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__10 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__11 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__12 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__13 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__14 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__15 = meta.hasSetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__16 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__17 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__18 = meta.hasSetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckSetterExistance_sd40909__19 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_7044, __DSPOT_useCamelCaseMapping_7045);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40909__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)o_shouldCheckSetterExistance_sd40909_add49692__5).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40840 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40840_sd42942_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_7232 = "{[[%K$MKBH3:ewt:X 8%";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__5 = meta.hasSetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__6 = meta.hasSetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__7 = meta.hasSetter("richL[st");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__8 = meta.hasSetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__9 = meta.hasSetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__10 = meta.hasSetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__11 = meta.hasSetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__12 = meta.hasSetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__13 = meta.hasSetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__14 = meta.hasSetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__15 = meta.hasSetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40840__16 = meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.metaClassForProperty(__DSPOT_name_7232);
            org.junit.Assert.fail("shouldCheckSetterExistance_literalMutationString40840_sd42942 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40845 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40845_literalMutationString43335 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40845_literalMutationString43335_sd56568_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_8612 = "_WRs+2YLO@vmf^@t,}Wj";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__5 = meta.hasSetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__6 = meta.hasSetter("richHroperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__7 = meta.hasSetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__8 = meta.hasSetter("ricMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__9 = meta.hasSetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__10 = meta.hasSetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__11 = meta.hasSetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__12 = meta.hasSetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__13 = meta.hasSetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__14 = meta.hasSetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__15 = meta.hasSetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40845__16 = meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_8612);
            org.junit.Assert.fail("shouldCheckSetterExistance_literalMutationString40845_literalMutationString43335_sd56568 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40908 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40908_sd49601 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40908_sd49601_add58681() {
        java.lang.String __DSPOT_name_7913 = ":Z.KzuUWwZ9Z&W.-vd3h";
        java.lang.String __DSPOT_name_7043 = ",T0tc<IPJ7xF 2{M@J5>";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908_sd49601_add58681__7 = // MethodCallAdder
        meta.hasSetter("richField");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908_sd49601_add58681__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__6 = meta.hasSetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__7 = meta.hasSetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__8 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__9 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__10 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__11 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__12 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__13 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__14 = meta.hasSetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__15 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__16 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40908__17 = meta.hasSetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckSetterExistance_sd40908__18 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_7043);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40908__18);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckSetterExistance_sd40908_sd49601__47 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_7913);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40908_sd49601__47);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40908__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldCheckSetterExistance_sd40908_sd49601_add58681__7);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40878 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40878_literalMutationString46567 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40878_literalMutationString46567_sd70883_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_10115 = ",!JPOyu%L?cAi&1-P=qe";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__5 = meta.hasSetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__6 = meta.hasSetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__7 = meta.hasSetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__8 = meta.hasSetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__9 = meta.hasSetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__10 = meta.hasSetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__11 = meta.hasSetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__12 = meta.hasSetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__13 = meta.hasSetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__14 = meta.hasSetter("select");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__15 = meta.hasSetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40878__16 = meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getSetterType(__DSPOT_name_10115);
            org.junit.Assert.fail("shouldCheckSetterExistance_literalMutationString40878_literalMutationString46567_sd70883 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40913 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505_literalMutationString56426() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_7048 = "X1z+0;4wN{Di-2nmN:Jb";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__8 = meta.hasSetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__9 = meta.hasSetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__10 = meta.hasSetter("1 Some Street");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__11 = meta.hasSetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__12 = meta.hasSetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__13 = meta.hasSetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__14 = meta.hasSetter("richType.richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__15 = meta.hasSetter("1 Some Street");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__16 = meta.hasSetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__17 = meta.hasSetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__18 = meta.hasSetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_sd40913_failAssert2_literalMutationString50505__19 = meta.hasSetter("[0]");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_7048);
            org.junit.Assert.fail("shouldCheckSetterExistance_sd40913 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40914 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40914_literalMutationString49824 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40914_literalMutationString49824_literalMutationString58255() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__5 = meta.hasSetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__6 = meta.hasSetter("_ichProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__7 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__8 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__9 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__10 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__11 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__12 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__13 = meta.hasSetter("1 Some Street");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__15 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40914__16 = meta.hasSetter("[0]");
        // StatementAdd: add invocation of a method
        meta.getSetterNames();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40862 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40862_literalMutationString45038 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40862_literalMutationString45038_sd53591_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_8293 = "+(S*@NCv>n:SAJ$>WIbG";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__5 = meta.hasSetter("richField");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__6 = meta.hasSetter("richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__7 = meta.hasSetter("richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__8 = meta.hasSetter("richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__9 = meta.hasSetter("richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__10 = meta.hasSetter("richType");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__11 = meta.hasSetter("E*ywrQ@(A[:aeIr7IQ");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__12 = meta.hasSetter("richType.richProperty");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__13 = meta.hasSetter("richType.richList");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__14 = meta.hasSetter("richType.richMap");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__15 = meta.hasSetter("richType.richList[0]");
            // AssertGenerator create local variable with return value of invocation
            boolean o_shouldCheckSetterExistance_literalMutationString40862__16 = meta.hasSetter("[+0]");
            // StatementAdd: add invocation of a method
            meta.getGetterType(__DSPOT_name_8293);
            org.junit.Assert.fail("shouldCheckSetterExistance_literalMutationString40862_literalMutationString45038_sd53591 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40909 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_sd40909_literalMutationString49635 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_sd40909_literalMutationString49635_literalMutationString68523() {
        boolean __DSPOT_useCamelCaseMapping_7045 = true;
        java.lang.String __DSPOT_name_7044 = "(VY(Wm -y&a0p..W(2v#";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__7 = meta.hasSetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__8 = meta.hasSetter("richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__9 = meta.hasSetter("richLst");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__10 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__11 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__12 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__13 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__14 = meta.hasSetter("5u^XwQ)pC8yq.Rh}3fv(k");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__15 = meta.hasSetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__16 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__17 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_sd40909__18 = meta.hasSetter("[0]");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldCheckSetterExistance_sd40909__19 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_7044, __DSPOT_useCamelCaseMapping_7045);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldCheckSetterExistance_sd40909__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40832 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckSetterExistance_literalMutationString40832_add42155 */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckSetterExistance_literalMutationString40832_add42155_literalMutationString69916() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__5 = meta.hasSetter("richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__6 = meta.hasSetter("/ /r.II@p,)*");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__7 = meta.hasSetter("richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__8 = meta.hasSetter("richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__9 = meta.hasSetter("richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__10 = meta.hasSetter("richType");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__11 = meta.hasSetter("richType.richField");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__12 = meta.hasSetter("richType.richProperty");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__13 = meta.hasSetter("richType.richList");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832_add42155__32 = // MethodCallAdder
        meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__14 = meta.hasSetter("richType.richMap");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__15 = meta.hasSetter("richType.richList[0]");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldCheckSetterExistance_literalMutationString40832__16 = meta.hasSetter("[0]");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachGetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_sd72511_failAssert65() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_10236 = "SZ]q4KI,;)R$S-*4PZhj";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_10236);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_sd72511 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachGetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_literalMutationString72436_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getGetterType("rich4ield");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_literalMutationString72436 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachGetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_sd72516_failAssert68() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_10239 = "z`raV@u3%jk}|sag)>9D";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            // StatementAdd: add invocation of a method
            meta.getSetterType(__DSPOT_name_10239);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_sd72516 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachGetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_sd72514_failAssert67() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_10238 = "+gyvMN/R08R4i |]K&zu";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_10238);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_sd72514 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachSetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_sd256569_failAssert66() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_48866 = "OyG&2V5sC09-f%&C&XIF";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_48866);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_sd256569 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachSetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_literalMutationString256510_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("N!yE]u[");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_literalMutationString256510 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachSetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_sd256572_failAssert68() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_48868 = "!k>1h.L#:]W^2Va3/ytH";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_48868);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_sd256572 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachSetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_literalMutationString256551_failAssert62() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("Kf%#(Kr{S}d_(._}hQ4-");
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_literalMutationString256551 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldCheckTypeForEachSetter */
    @org.junit.Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_literalMutationString256522_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richT ype");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_literalMutationString256522 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439874_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87054 = "r-tkdJ]Y{bE&H7&GOWw%";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_87054);
            org.junit.Assert.fail("shouldFindPropertyName_sd439874 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439873_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87053 = "0g5.JCvG]f[7t]6](SI&";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            // StatementAdd: add invocation of a method
            meta.getGetterType(__DSPOT_name_87053);
            org.junit.Assert.fail("shouldFindPropertyName_sd439873 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439876_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87055 = "j_O*GK]iVP!fa#y^QCrm";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            // StatementAdd: add invocation of a method
            meta.getSetterType(__DSPOT_name_87055);
            org.junit.Assert.fail("shouldFindPropertyName_sd439876 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439879() {
        java.lang.String __DSPOT_name_87057 = "jwV@.idj-?kK(aDi)HVG";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439879__6 = meta.findProperty("RICHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439879__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_sd439879__7 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_87057);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldFindPropertyName_sd439879__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439879__6);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_literalMutationString439861() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_literalMutationString439861__5 = meta.findProperty("RIwHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439861__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439870() {
        boolean __DSPOT_useCamelCaseMapping_87051 = true;
        java.lang.String __DSPOT_name_87050 = "O>/zvD!UA(WlHI3O$@?g";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439870__7 = meta.findProperty("RICHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439870__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439870__8 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_87050, __DSPOT_useCamelCaseMapping_87051);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439870__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439870__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439871_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87052 = ":#5lZ83 /KuORj>j/yo0";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_87052);
            org.junit.Assert.fail("shouldFindPropertyName_sd439871 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439877 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439877_literalMutationString440212() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439877__5 = meta.findProperty("");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439877__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_sd439877__6 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439877__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439880 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439880_failAssert4_literalMutationString440713() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87058 = "Ei6-ZzpJ-UzE q@b/7{x";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_shouldFindPropertyName_sd439880_failAssert4_literalMutationString440713__8 = meta.findProperty("");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439880_failAssert4_literalMutationString440713__8);
            // StatementAdd: add invocation of a method
            meta.metaClassForProperty(__DSPOT_name_87058);
            org.junit.Assert.fail("shouldFindPropertyName_sd439880 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439877 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439877_sd440221() {
        java.lang.String __DSPOT_name_87189 = "#.#k*f{{r;0*N8Y<iMy[";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439877__5 = meta.findProperty("RICHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439877__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_sd439877__6 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439877_sd440221__13 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_87189);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439877_sd440221__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439877__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439870 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439870_add440155() {
        boolean __DSPOT_useCamelCaseMapping_87051 = true;
        java.lang.String __DSPOT_name_87050 = "O>/zvD!UA(WlHI3O$@?g";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439870__7 = meta.findProperty("RICHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439870__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439870_add440155__10 = // StatementAdd: add invocation of a method
        // MethodCallAdder
        meta.findProperty(__DSPOT_name_87050, __DSPOT_useCamelCaseMapping_87051);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439870_add440155__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439870__8 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_87050, __DSPOT_useCamelCaseMapping_87051);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439870__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439870__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439870_add440155__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439872 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439872_sd440188() {
        java.lang.String __DSPOT_name_87177 = "zB$eWLm,i./4;`39G!:p";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439872__5 = meta.findProperty("RICHfield");
        // StatementAdd: add invocation of a method
        meta.getGetterNames();
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_sd439872_sd440188__11 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_87177);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldFindPropertyName_sd439872_sd440188__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439872__5);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439871 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439871_failAssert0_sd440311() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87226 = "Q{CwnQ(t(&Rg>#cgN#H(";
            java.lang.String __DSPOT_name_87052 = ":#5lZ83 /KuORj>j/yo0";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_shouldFindPropertyName_sd439871_failAssert0_sd440311__9 = meta.findProperty("RICHfield");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439871_failAssert0_sd440311__9);
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_87052);
            org.junit.Assert.fail("shouldFindPropertyName_sd439871 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            meta.hasGetter(__DSPOT_name_87226);
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439879 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439879_add440272() {
        java.lang.String __DSPOT_name_87057 = "jwV@.idj-?kK(aDi)HVG";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator create local variable with return value of invocation
        org.apache.ibatis.reflection.MetaClass o_shouldFindPropertyName_sd439879_add440272__4 = // MethodCallAdder
        org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)o_shouldFindPropertyName_sd439879_add440272__4).hasDefaultConstructor());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439879__6 = meta.findProperty("RICHfield");
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_sd439879__7 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_87057);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439879__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)o_shouldFindPropertyName_sd439879_add440272__4).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_literalMutationString439865 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_literalMutationString439865_sd440030() {
        java.lang.String __DSPOT_name_87106 = "2?;!]Zb_Pq:8t!uX@_c.";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_literalMutationString439865__5 = meta.findProperty("RICgHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439865__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_literalMutationString439865_sd440030__9 = // StatementAdd: add invocation of a method
        meta.hasGetter(__DSPOT_name_87106);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldFindPropertyName_literalMutationString439865_sd440030__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439865__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439877 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439877_literalMutationString440214 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439877_literalMutationString440214_sd445234() {
        boolean __DSPOT_useCamelCaseMapping_89824 = false;
        java.lang.String __DSPOT_name_89823 = "E)&@n#*;d+#zpJ[&g(>y";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439877__5 = meta.findProperty("RICHfeld");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439877__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_sd439877__6 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439877_literalMutationString440214_sd445234__14 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_89823, __DSPOT_useCamelCaseMapping_89824);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439877_literalMutationString440214_sd445234__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439877__5);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_literalMutationString439864 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_literalMutationString439864_add440000 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_literalMutationString439864_add440000_literalMutationString445889() {
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_literalMutationString439864_add440000__5 = // MethodCallAdder
        meta.findProperty("");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439864_add440000__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_literalMutationString439864__5 = meta.findProperty("RICfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439864__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439864_add440000__5);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_literalMutationString439865 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_literalMutationString439865_sd440029 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_literalMutationString439865_sd440029_sd441368() {
        boolean __DSPOT_useCamelCaseMapping_87630 = true;
        java.lang.String __DSPOT_name_87629 = "Y,j1(hkrD()q4@%C.}E:";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_literalMutationString439865__5 = meta.findProperty("RICgHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439865__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_literalMutationString439865_sd440029__8 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_literalMutationString439865_sd440029_sd441368__14 = // StatementAdd: add invocation of a method
        meta.findProperty(__DSPOT_name_87629, __DSPOT_useCamelCaseMapping_87630);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439865_sd440029_sd441368__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439865__5);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439876 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439876_failAssert3_sd440599 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439876_failAssert3_sd440599_literalMutationString444930() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87464 = "njERK!Q^Z.]%:DR7up.H";
            java.lang.String __DSPOT_name_87055 = "j_O*GK]iVP!fa#y^QCrm";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_shouldFindPropertyName_sd439876_failAssert3_sd440599__9 = meta.findProperty("hXSBBYA*]");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439876_failAssert3_sd440599__9);
            // StatementAdd: add invocation of a method
            meta.getSetterType(__DSPOT_name_87055);
            org.junit.Assert.fail("shouldFindPropertyName_sd439876 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            meta.hasSetter(__DSPOT_name_87464);
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_literalMutationString439865 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_literalMutationString439865_sd440029 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_literalMutationString439865_sd440029_sd441376() {
        java.lang.String __DSPOT_name_87635 = "(`5TejwZ|!%=bf7_@.f.";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_literalMutationString439865__5 = meta.findProperty("RICgHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439865__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_literalMutationString439865_sd440029__8 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_literalMutationString439865_sd440029_sd441376__13 = // StatementAdd: add invocation of a method
        meta.hasGetter(__DSPOT_name_87635);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_shouldFindPropertyName_literalMutationString439865_sd440029_sd441376__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldFindPropertyName_literalMutationString439865__5);
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439880 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439880_failAssert4_sd440756 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439880_failAssert4_sd440756_literalMutationString446004() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87058 = "Ei6-ZzpJ-UzE q@b/7{x";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_8 = meta.findProperty("");
            // StatementAdd: add invocation of a method
            meta.metaClassForProperty(__DSPOT_name_87058);
            org.junit.Assert.fail("shouldFindPropertyName_sd439880 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_8.intern();
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439879 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439879_sd440281 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439879_sd440281_sd448025() {
        java.lang.String __DSPOT_name_87057 = "jwV@.idj-?kK(aDi)HVG";
        org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_shouldFindPropertyName_sd439879__6 = meta.findProperty("RICHfield");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439879__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_sd439879__7 = // StatementAdd: add invocation of a method
        meta.hasSetter(__DSPOT_name_87057);
        // StatementAdd: add invocation of a method
        meta.getSetterNames();
        // AssertGenerator create local variable with return value of invocation
        boolean o_shouldFindPropertyName_sd439879_sd440281_sd448025__15 = // StatementAdd: add invocation of a method
        meta.hasDefaultConstructor();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_shouldFindPropertyName_sd439879_sd440281_sd448025__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439879__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439871 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439871_failAssert0_literalMutationString440293_sd442416() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_88174 = "b*U,M#_c6QPH]xPIyp6T";
            java.lang.String __DSPOT_name_87052 = ":#5lZ83 /KuORj>j/yo0";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_shouldFindPropertyName_sd439871_failAssert0_literalMutationString440293__8 = meta.findProperty("");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldFindPropertyName_sd439871_failAssert0_literalMutationString440293__8);
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_87052);
            org.junit.Assert.fail("shouldFindPropertyName_sd439871 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            meta.findProperty(__DSPOT_name_88174);
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439874 */
    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldFindPropertyName_sd439874_failAssert2_sd440519 */
    @org.junit.Test(timeout = 10000)
    public void shouldFindPropertyName_sd439874_failAssert2_sd440519_add448210() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_87387 = "^#VnCoVvx^X)gV+@e% O";
            java.lang.String __DSPOT_name_87054 = "r-tkdJ]Y{bE&H7&GOWw%";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.DefaultReflectorFactory)reflectorFactory).isClassCacheEnabled());
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.RichType.class, reflectorFactory);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.apache.ibatis.reflection.MetaClass)meta).hasDefaultConstructor());
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_shouldFindPropertyName_sd439874_failAssert2_sd440519_add448210__9 = // MethodCallAdder
            meta.findProperty("RICHfield");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439874_failAssert2_sd440519_add448210__9);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_shouldFindPropertyName_sd439874_failAssert2_sd440519__9 = meta.findProperty("RICHfield");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("richField", o_shouldFindPropertyName_sd439874_failAssert2_sd440519__9);
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_87054);
            org.junit.Assert.fail("shouldFindPropertyName_sd439874 should have thrown ReflectionException");
            // StatementAdd: add invocation of a method
            meta.getSetterType(__DSPOT_name_87387);
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldTestDataTypeOfGenericMethod */
    @org.junit.Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_literalMutationString452321_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.generics.GenericConcrete.class, reflectorFactory);
            meta.getGetterType("R");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_literalMutationString452321 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldTestDataTypeOfGenericMethod */
    @org.junit.Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_literalMutationString452325_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.generics.GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("Z");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_literalMutationString452325 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldTestDataTypeOfGenericMethod */
    @org.junit.Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_sd452334_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_92579 = "Rj^zBQ/6*=Lt}d(cTc2U";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.generics.GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            // StatementAdd: add invocation of a method
            meta.getSetInvoker(__DSPOT_name_92579);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_sd452334 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }

    /* amplification of org.apache.ibatis.reflection.MetaClassTest#shouldTestDataTypeOfGenericMethod */
    @org.junit.Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_sd452331_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_92577 = "cz5bftu5(4u}cEne$*K+";
            org.apache.ibatis.reflection.ReflectorFactory reflectorFactory = new org.apache.ibatis.reflection.DefaultReflectorFactory();
            org.apache.ibatis.reflection.MetaClass meta = org.apache.ibatis.reflection.MetaClass.forClass(org.apache.ibatis.domain.misc.generics.GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            // StatementAdd: add invocation of a method
            meta.getGetInvoker(__DSPOT_name_92577);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_sd452331 should have thrown ReflectionException");
        } catch (org.apache.ibatis.reflection.ReflectionException eee) {
        }
    }
}
