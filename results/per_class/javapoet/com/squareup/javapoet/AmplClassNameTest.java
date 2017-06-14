/**
 * Copyright (C) 2014 Google, Inc.
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


package com.squareup.javapoet;


@org.junit.runner.RunWith(value = org.junit.runners.JUnit4.class)
public final class AmplClassNameTest {
    @org.junit.Rule
    public com.google.testing.compile.CompilationRule compilationRule = new com.google.testing.compile.CompilationRule();

    @org.junit.Test
    public void bestGuessForString_simpleClass() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(java.lang.String.class.getName())).isEqualTo(com.squareup.javapoet.ClassName.get("java.lang", "String"));
    }

    @org.junit.Test
    public void bestGuessNonAscii() {
        com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("com.\ud835\udc1andro\ud835\udc22d.\ud835\udc00ctiv\ud835\udc22ty");
        org.junit.Assert.assertEquals("com.\ud835\udc1andro\ud835\udc22d", className.packageName());
        org.junit.Assert.assertEquals("\ud835\udc00ctiv\ud835\udc22ty", className.simpleName());
    }

    static class OuterClass {
        static class InnerClass {        }
    }

    @org.junit.Test
    public void bestGuessForString_nestedClass() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(java.util.Map.Entry.class.getCanonicalName())).isEqualTo(com.squareup.javapoet.ClassName.get("java.util", "Map", "Entry"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(com.squareup.javapoet.AmplClassNameTest.OuterClass.InnerClass.class.getCanonicalName())).isEqualTo(com.squareup.javapoet.ClassName.get("com.squareup.javapoet", "ClassNameTest", "OuterClass", "InnerClass"));
    }

    @org.junit.Test
    public void bestGuessForString_defaultPackage() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass.Nested")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass", "Nested"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass.Nested.EvenMore")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass", "Nested", "EvenMore"));
    }

    @org.junit.Test
    public void bestGuessForString_confusingInput() {
        assertBestGuessThrows("");
        assertBestGuessThrows(".");
        assertBestGuessThrows(".Map");
        assertBestGuessThrows("java");
        assertBestGuessThrows("java.util");
        assertBestGuessThrows("java.util.");
        assertBestGuessThrows("java..util.Map.Entry");
        assertBestGuessThrows("java.util..Map.Entry");
        assertBestGuessThrows("java.util.Map..Entry");
        assertBestGuessThrows("com.test.$");
        assertBestGuessThrows("com.test.LooksLikeAClass.pkg");
        assertBestGuessThrows("!@#$gibberish%^&*");
    }

    private void assertBestGuessThrows(java.lang.String s) {
        try {
            com.squareup.javapoet.ClassName.bestGuess(s);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
        }
    }

    @org.junit.Test
    public void createNestedClass() {
        com.squareup.javapoet.ClassName foo = com.squareup.javapoet.ClassName.get("com.example", "Foo");
        com.squareup.javapoet.ClassName bar = foo.nestedClass("Bar");
        com.google.common.truth.Truth.assertThat(bar).isEqualTo(com.squareup.javapoet.ClassName.get("com.example", "Foo", "Bar"));
        com.squareup.javapoet.ClassName baz = bar.nestedClass("Baz");
        com.google.common.truth.Truth.assertThat(baz).isEqualTo(com.squareup.javapoet.ClassName.get("com.example", "Foo", "Bar", "Baz"));
    }

    @org.junit.Test
    public void classNameFromTypeElement() {
        javax.lang.model.util.Elements elements = compilationRule.getElements();
        javax.lang.model.element.TypeElement element = elements.getTypeElement(java.lang.Object.class.getCanonicalName());
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(element).toString()).isEqualTo("java.lang.Object");
    }

    @org.junit.Test
    public void classNameFromClass() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Object.class).toString()).isEqualTo("java.lang.Object");
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(com.squareup.javapoet.AmplClassNameTest.OuterClass.InnerClass.class).toString()).isEqualTo("com.squareup.javapoet.ClassNameTest.OuterClass.InnerClass");
    }

    @org.junit.Test
    public void peerClass() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Double.class).peerClass("Short")).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.Short.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("", "Double").peerClass("Short")).isEqualTo(com.squareup.javapoet.ClassName.get("", "Short"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("a.b", "Combo", "Taco").peerClass("Burrito")).isEqualTo(com.squareup.javapoet.ClassName.get("a.b", "Combo", "Burrito"));
    }

    @org.junit.Test
    public void fromClassRejectionTypes() {
        try {
            com.squareup.javapoet.ClassName.get(int.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException ignored) {
        }
        try {
            com.squareup.javapoet.ClassName.get(void.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException ignored) {
        }
        try {
            com.squareup.javapoet.ClassName.get(java.lang.Object[].class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException ignored) {
        }
    }

    @org.junit.Test
    public void reflectionName() {
        org.junit.Assert.assertEquals("java.lang.Object", com.squareup.javapoet.TypeName.OBJECT.reflectionName());
        org.junit.Assert.assertEquals("java.lang.Thread$State", com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName());
        org.junit.Assert.assertEquals("java.util.Map$Entry", com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName());
        org.junit.Assert.assertEquals("Foo", com.squareup.javapoet.ClassName.get("", "Foo").reflectionName());
        org.junit.Assert.assertEquals("Foo$Bar$Baz", com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "Baz").reflectionName());
        org.junit.Assert.assertEquals("a.b.c.Foo$Bar$Baz", com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName());
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessForString_defaultPackage */
    @org.junit.Test
    public void bestGuessForString_defaultPackage_literalMutation17897_failAssert46() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass"));
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass.Nested")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass", "Nested"));
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass.Nested.EvenMore")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass", "Nested", ""));
            org.junit.Assert.fail("bestGuessForString_defaultPackage_literalMutation17897 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessForString_nestedClass */
    @org.junit.Test
    public void bestGuessForString_nestedClass_literalMutation29721_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(java.util.Map.Entry.class.getCanonicalName())).isEqualTo(com.squareup.javapoet.ClassName.get("java.util", "Map", "Entry"));
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(com.squareup.javapoet.AmplClassNameTest.OuterClass.InnerClass.class.getCanonicalName())).isEqualTo(com.squareup.javapoet.ClassName.get("com.squareup.javapoet", "ClassNameTest", "}sd8^` qE:", "InnerClass"));
            org.junit.Assert.fail("bestGuessForString_nestedClass_literalMutation29721 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessForString_simpleClass */
    @org.junit.Test
    public void bestGuessForString_simpleClass_literalMutation37385_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(java.lang.String.class.getName())).isEqualTo(com.squareup.javapoet.ClassName.get("java.lang", "Str[ing"));
            org.junit.Assert.fail("bestGuessForString_simpleClass_literalMutation37385 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessForString_simpleClass */
    @org.junit.Test
    public void bestGuessForString_simpleClass_literalMutation37386_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(java.lang.String.class.getName())).isEqualTo(com.squareup.javapoet.ClassName.get("java.lang", ""));
            org.junit.Assert.fail("bestGuessForString_simpleClass_literalMutation37386 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 10000)
    public void bestGuessNonAscii_cf37818_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("com.\ud835\udc1andro\ud835\udc22d.\ud835\udc00ctiv\ud835\udc22ty");
            // MethodAssertGenerator build local variable
            Object o_3_0 = className.packageName();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_27 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            className.nestedClass(vc_27);
            // MethodAssertGenerator build local variable
            Object o_9_0 = className.simpleName();
            org.junit.Assert.fail("bestGuessNonAscii_cf37818 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 10000)
    public void bestGuessNonAscii_cf37751_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("com.\ud835\udc1andro\ud835\udc22d.\ud835\udc00ctiv\ud835\udc22ty");
            // MethodAssertGenerator build local variable
            Object o_3_0 = className.packageName();
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_2 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            className.annotated(vc_2);
            // MethodAssertGenerator build local variable
            Object o_9_0 = className.simpleName();
            org.junit.Assert.fail("bestGuessNonAscii_cf37751 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 10000)
    public void bestGuessNonAscii_cf37862_cf38138_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("com.\ud835\udc1andro\ud835\udc22d.\ud835\udc00ctiv\ud835\udc22ty");
            // MethodAssertGenerator build local variable
            Object o_3_0 = className.packageName();
            // AssertGenerator replace invocation
            java.lang.String o_bestGuessNonAscii_cf37862__5 = // StatementAdderMethod cloned existing statement
className.reflectionName();
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_bestGuessNonAscii_cf37862__5;
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_152 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            className.annotated(vc_152);
            // MethodAssertGenerator build local variable
            Object o_13_0 = className.simpleName();
            org.junit.Assert.fail("bestGuessNonAscii_cf37862_cf38138 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 10000)
    public void bestGuessNonAscii_cf37827_failAssert23_literalMutation38529_failAssert48() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("UG15jR@-vHIJR*?&N=>AjP-7");
                // MethodAssertGenerator build local variable
                Object o_3_0 = className.packageName();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_31 = new java.lang.String();
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.ClassName vc_28 = (com.squareup.javapoet.ClassName)null;
                // StatementAdderMethod cloned existing statement
                vc_28.peerClass(vc_31);
                // MethodAssertGenerator build local variable
                Object o_11_0 = className.simpleName();
                org.junit.Assert.fail("bestGuessNonAscii_cf37827 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("bestGuessNonAscii_cf37827_failAssert23_literalMutation38529 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 10000)
    public void bestGuessNonAscii_cf37770_cf37889_failAssert37_literalMutation40157_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("$I!zJ!;)VBufwU8pSarrkmq*");
                // MethodAssertGenerator build local variable
                Object o_3_0 = className.packageName();
                // AssertGenerator replace invocation
                com.squareup.javapoet.ClassName o_bestGuessNonAscii_cf37770__5 = // StatementAdderMethod cloned existing statement
className.enclosingClassName();
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_bestGuessNonAscii_cf37770__5;
                // StatementAdderOnAssert create null value
                java.lang.String vc_56 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                className.bestGuess(vc_56);
                // MethodAssertGenerator build local variable
                Object o_13_0 = className.simpleName();
                org.junit.Assert.fail("bestGuessNonAscii_cf37770_cf37889 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("bestGuessNonAscii_cf37770_cf37889_failAssert37_literalMutation40157 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 10000)
    public void bestGuessNonAscii_cf37862_cf38138_failAssert9_literalMutation39995_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("XOia3PqCc#7tZ?hh!![@w*j]");
                // MethodAssertGenerator build local variable
                Object o_3_0 = className.packageName();
                // AssertGenerator replace invocation
                java.lang.String o_bestGuessNonAscii_cf37862__5 = // StatementAdderMethod cloned existing statement
className.reflectionName();
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_bestGuessNonAscii_cf37862__5;
                // StatementAdderOnAssert create null value
                java.util.List<com.squareup.javapoet.AnnotationSpec> vc_152 = (java.util.List)null;
                // StatementAdderMethod cloned existing statement
                className.annotated(vc_152);
                // MethodAssertGenerator build local variable
                Object o_13_0 = className.simpleName();
                org.junit.Assert.fail("bestGuessNonAscii_cf37862_cf38138 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("bestGuessNonAscii_cf37862_cf38138_failAssert9_literalMutation39995 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#peerClass */
    @org.junit.Test
    public void peerClass_literalMutation68682_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Double.class).peerClass("Short")).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.Short.class));
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("", "Double").peerClass("Short")).isEqualTo(com.squareup.javapoet.ClassName.get("", "Short"));
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("a.b", "Combo", "Taco").peerClass("Burrito")).isEqualTo(com.squareup.javapoet.ClassName.get("a.b", "", "Burrito"));
            org.junit.Assert.fail("peerClass_literalMutation68682 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test(timeout = 10000)
    public void reflectionName_cf82340_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "Baz").reflectionName();
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_5452 = (java.util.List)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.ClassName vc_5450 = (com.squareup.javapoet.ClassName)null;
            // StatementAdderMethod cloned existing statement
            vc_5450.annotated(vc_5452);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName();
            org.junit.Assert.fail("reflectionName_cf82340 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test
    public void reflectionName_literalMutation82305_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.ClassName.get("", "<B&", "Bar", "Baz").reflectionName();
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName();
            org.junit.Assert.fail("reflectionName_literalMutation82305 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test
    public void reflectionName_literalMutation82337_failAssert40_literalMutation83250() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_1_0, "java.lang.Object");
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, "java.lang.Thread$State");
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, "java.util.Map$Entry");
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "Foo");
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "Baz").reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "Foo$Bar$Baz");
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.ClassName.get("a.b.c", "/oo", "Bar", "B#az").reflectionName();
            org.junit.Assert.fail("reflectionName_literalMutation82337 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test
    public void reflectionName_literalMutation82333_failAssert36_literalMutation83095() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_1_0, "java.lang.Object");
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, "java.lang.Thread$State");
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, "java.util.Map$Entry");
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "Foo");
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.ClassName.get("\n.build()$<$<", "Foo", "Bar", "Baz").reflectionName();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "\n.build()$<$<.Foo$Bar$Baz");
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "tp ", "Baz").reflectionName();
            org.junit.Assert.fail("reflectionName_literalMutation82333 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test(timeout = 10000)
    public void reflectionName_cf82373_failAssert50_literalMutation83579_failAssert19_literalMutation108715() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_1_0, "java.lang.Object");
                // MethodAssertGenerator build local variable
                Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_3_0, "java.lang.Thread$State");
                // MethodAssertGenerator build local variable
                Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_6_0, "java.util.Map$Entry");
                // MethodAssertGenerator build local variable
                Object o_9_0 = com.squareup.javapoet.ClassName.get("", "").reflectionName();
                // MethodAssertGenerator build local variable
                Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "Baz").reflectionName();
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_58 = "Foo";
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.ClassName vc_5474 = (com.squareup.javapoet.ClassName)null;
                // StatementAdderMethod cloned existing statement
                vc_5474.nestedClass(String_vc_58);
                // MethodAssertGenerator build local variable
                Object o_21_0 = com.squareup.javapoet.ClassName.get("a..c", "Foo", "Bar", "Baz").reflectionName();
                org.junit.Assert.fail("reflectionName_cf82373 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("reflectionName_cf82373_failAssert50_literalMutation83579 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test(timeout = 10000)
    public void reflectionName_cf82344_failAssert44_literalMutation83320_failAssert6_literalMutation108131() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_1_0, "java.lang.Object");
                // MethodAssertGenerator build local variable
                Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_3_0, "java.lang.Thread$State");
                // MethodAssertGenerator build local variable
                Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_6_0, "java.util.Map$Entry");
                // MethodAssertGenerator build local variable
                Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_9_0, "Foo");
                // MethodAssertGenerator build local variable
                Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Ar{", "Jar", "Baz").reflectionName();
                // StatementAdderOnAssert create null value
                java.lang.String vc_5456 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.ClassName vc_5454 = (com.squareup.javapoet.ClassName)null;
                // StatementAdderMethod cloned existing statement
                vc_5454.bestGuess(vc_5456);
                // MethodAssertGenerator build local variable
                Object o_21_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName();
                org.junit.Assert.fail("reflectionName_cf82344 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("reflectionName_cf82344_failAssert44_literalMutation83320 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

