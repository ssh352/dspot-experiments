/**
 * Copyright (C) 2016 Square, Inc.
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


public class AmplUtilTest {
    @org.junit.Test
    public void characterLiteral() {
        org.junit.Assert.assertEquals("a", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a'));
        org.junit.Assert.assertEquals("b", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b'));
        org.junit.Assert.assertEquals("c", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c'));
        org.junit.Assert.assertEquals("%", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%'));
        // common escapes
        org.junit.Assert.assertEquals("\\b", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b'));
        org.junit.Assert.assertEquals("\\t", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t'));
        org.junit.Assert.assertEquals("\\n", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n'));
        org.junit.Assert.assertEquals("\\f", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f'));
        org.junit.Assert.assertEquals("\\r", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r'));
        org.junit.Assert.assertEquals("\"", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"'));
        org.junit.Assert.assertEquals("\\\'", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\''));
        org.junit.Assert.assertEquals("\\\\", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\'));
        // octal escapes
        org.junit.Assert.assertEquals("\\u0000", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000'));
        org.junit.Assert.assertEquals("\\u0007", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007'));
        org.junit.Assert.assertEquals("?", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?'));
        org.junit.Assert.assertEquals("\\u007f", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f'));
        org.junit.Assert.assertEquals("¿", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf'));
        org.junit.Assert.assertEquals("ÿ", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff'));
        // unicode escapes
        org.junit.Assert.assertEquals("\\u0000", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000'));
        org.junit.Assert.assertEquals("\\u0001", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001'));
        org.junit.Assert.assertEquals("\\u0002", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002'));
        org.junit.Assert.assertEquals("€", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac'));
        org.junit.Assert.assertEquals("☃", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603'));
        org.junit.Assert.assertEquals("♠", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660'));
        org.junit.Assert.assertEquals("♣", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663'));
        org.junit.Assert.assertEquals("♥", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665'));
        org.junit.Assert.assertEquals("♦", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666'));
        org.junit.Assert.assertEquals("✵", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735'));
        org.junit.Assert.assertEquals("✺", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a'));
        org.junit.Assert.assertEquals("／", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f'));
    }

    @org.junit.Test
    public void stringLiteral() {
        stringLiteral("abc");
        stringLiteral("♦♥♠♣");
        stringLiteral("\u20ac\\t@\\t$", "\u20ac\t@\t$", " ");
        stringLiteral("abc();\\n\"\n  + \"def();", "abc();\ndef();", " ");
        stringLiteral("This is \\\"quoted\\\"!", "This is \"quoted\"!", " ");
        stringLiteral("e^{i\\\\pi}+1=0", "e^{i\\pi}+1=0", " ");
    }

    void stringLiteral(java.lang.String string) {
        stringLiteral(string, string, " ");
    }

    void stringLiteral(java.lang.String expected, java.lang.String value, java.lang.String indent) {
        org.junit.Assert.assertEquals((("\"" + expected) + "\""), com.squareup.javapoet.Util.stringLiteralWithDoubleQuotes(value, indent));
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf76_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create random local variable
            java.lang.Object[] vc_51 = new java.lang.Object []{new java.lang.Object()};
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_49 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            boolean vc_47 = false;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_45 = (com.squareup.javapoet.Util)null;
            // StatementAdderMethod cloned existing statement
            vc_45.checkArgument(vc_47, vc_49, vc_51);
            // MethodAssertGenerator build local variable
            Object o_72_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf76 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf83() {
        org.junit.Assert.assertEquals("a", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a'));
        org.junit.Assert.assertEquals("b", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b'));
        org.junit.Assert.assertEquals("c", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c'));
        org.junit.Assert.assertEquals("%", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%'));
        // common escapes
        org.junit.Assert.assertEquals("\\b", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b'));
        org.junit.Assert.assertEquals("\\t", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t'));
        org.junit.Assert.assertEquals("\\n", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n'));
        org.junit.Assert.assertEquals("\\f", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f'));
        org.junit.Assert.assertEquals("\\r", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r'));
        org.junit.Assert.assertEquals("\"", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"'));
        org.junit.Assert.assertEquals("\\\'", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\''));
        org.junit.Assert.assertEquals("\\\\", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\'));
        // octal escapes
        org.junit.Assert.assertEquals("\\u0000", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000'));
        org.junit.Assert.assertEquals("\\u0007", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007'));
        org.junit.Assert.assertEquals("?", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?'));
        org.junit.Assert.assertEquals("\\u007f", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f'));
        org.junit.Assert.assertEquals("¿", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf'));
        org.junit.Assert.assertEquals("ÿ", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff'));
        // unicode escapes
        org.junit.Assert.assertEquals("\\u0000", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000'));
        org.junit.Assert.assertEquals("\\u0001", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001'));
        org.junit.Assert.assertEquals("\\u0002", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002'));
        org.junit.Assert.assertEquals("€", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac'));
        org.junit.Assert.assertEquals("☃", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603'));
        org.junit.Assert.assertEquals("♠", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660'));
        org.junit.Assert.assertEquals("♣", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663'));
        org.junit.Assert.assertEquals("♥", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665'));
        org.junit.Assert.assertEquals("♦", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666'));
        org.junit.Assert.assertEquals("✵", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735'));
        org.junit.Assert.assertEquals("✺", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a'));
        // StatementAdderOnAssert create null value
        java.lang.Object[] vc_57 = (java.lang.Object[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_57);
        // StatementAdderOnAssert create null value
        java.lang.String vc_55 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_55);
        // StatementAdderOnAssert create random local variable
        boolean vc_54 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_54);
        // StatementAdderOnAssert create null value
        com.squareup.javapoet.Util vc_52 = (com.squareup.javapoet.Util)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_52);
        // StatementAdderMethod cloned existing statement
        vc_52.checkState(vc_54, vc_55, vc_57);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_52);
        org.junit.Assert.assertEquals("／", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f'));
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf72_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create random local variable
            java.lang.Object[] vc_51 = new java.lang.Object []{new java.lang.Object()};
            // StatementAdderOnAssert create null value
            java.lang.String vc_48 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            boolean vc_47 = false;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_45 = (com.squareup.javapoet.Util)null;
            // StatementAdderMethod cloned existing statement
            vc_45.checkArgument(vc_47, vc_48, vc_51);
            // MethodAssertGenerator build local variable
            Object o_72_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf72 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf45_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create null value
            java.util.Collection<javax.lang.model.element.Modifier> vc_30 = (java.util.Collection)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_28 = (com.squareup.javapoet.Util)null;
            // StatementAdderMethod cloned existing statement
            vc_28.hasDefaultModifier(vc_30);
            // MethodAssertGenerator build local variable
            Object o_68_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf45 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf84_cf1171_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create random local variable
            java.lang.Object[] vc_58 = new java.lang.Object []{new java.lang.Object(),new java.lang.Object()};
            // StatementAdderOnAssert create null value
            java.lang.String vc_55 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_66_0 = vc_55;
            // StatementAdderOnAssert create random local variable
            boolean vc_54 = true;
            // MethodAssertGenerator build local variable
            Object o_70_0 = vc_54;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_52 = (com.squareup.javapoet.Util)null;
            // MethodAssertGenerator build local variable
            Object o_74_0 = vc_52;
            // StatementAdderMethod cloned existing statement
            vc_52.checkState(vc_54, vc_55, vc_58);
            // MethodAssertGenerator build local variable
            Object o_78_0 = vc_52;
            // StatementAdderOnAssert create random local variable
            javax.lang.model.element.Modifier[] vc_324 = new javax.lang.model.element.Modifier []{};
            // StatementAdderOnAssert create null value
            java.util.Set<javax.lang.model.element.Modifier> vc_321 = (java.util.Set)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_319 = (com.squareup.javapoet.Util)null;
            // StatementAdderMethod cloned existing statement
            vc_319.requireExactlyOneOf(vc_321, vc_324);
            // MethodAssertGenerator build local variable
            Object o_88_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf84_cf1171 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf49_cf239_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create literal from method
            char char_vc_1 = '%';
            // MethodAssertGenerator build local variable
            Object o_64_0 = char_vc_1;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_32 = (com.squareup.javapoet.Util)null;
            // MethodAssertGenerator build local variable
            Object o_68_0 = vc_32;
            // AssertGenerator replace invocation
            java.lang.String o_characterLiteral_cf49__66 = // StatementAdderMethod cloned existing statement
vc_32.characterLiteralWithoutSingleQuotes(char_vc_1);
            // MethodAssertGenerator build local variable
            Object o_72_0 = o_characterLiteral_cf49__66;
            // StatementAdderOnAssert create null value
            java.lang.Object[] vc_122 = (java.lang.Object[])null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_121 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            boolean vc_119 = false;
            // StatementAdderMethod cloned existing statement
            vc_32.checkState(vc_119, vc_121, vc_122);
            // MethodAssertGenerator build local variable
            Object o_82_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf49_cf239 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf50_cf325_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create random local variable
            char vc_34 = ' ';
            // MethodAssertGenerator build local variable
            Object o_64_0 = vc_34;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_32 = (com.squareup.javapoet.Util)null;
            // MethodAssertGenerator build local variable
            Object o_68_0 = vc_32;
            // AssertGenerator replace invocation
            java.lang.String o_characterLiteral_cf50__66 = // StatementAdderMethod cloned existing statement
vc_32.characterLiteralWithoutSingleQuotes(vc_34);
            // MethodAssertGenerator build local variable
            Object o_72_0 = o_characterLiteral_cf50__66;
            // StatementAdderOnAssert create null value
            java.util.Collection<javax.lang.model.element.Modifier> vc_160 = (java.util.Collection)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_158 = (com.squareup.javapoet.Util)null;
            // StatementAdderMethod cloned existing statement
            vc_158.hasDefaultModifier(vc_160);
            // MethodAssertGenerator build local variable
            Object o_80_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf50_cf325 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf73_failAssert6_literalMutation2749() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_1_0, "a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, "b");
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "c");
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, "%");
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "\\b");
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "\\t");
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, "\\n");
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_16_0, "\\f");
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_18_0, "\\r");
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, "\"");
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_22_0, "\\\'");
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_24_0, "\\\\");
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, "\\u0000");
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, "\\u0007");
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_31_0, "?");
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_33_0, "\\u007f");
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, "\u00bf");
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_37_0, "\u00ff");
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_39_0, "\\u0000");
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_42_0, "\\u0001");
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_44_0, "\\u0002");
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_46_0, "\u20ac");
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_48_0, "\u2603");
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_50_0, "\u2660");
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_52_0, "\u2663");
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_54_0, "\u2665");
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_56_0, "\u2666");
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_58_0, "\u2735");
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, "\u273a");
            // StatementAdderOnAssert create null value
            java.lang.Object[] vc_50 = (java.lang.Object[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_50);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_4 = "\\u0,002";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_4, "\\u0,002");
            // StatementAdderOnAssert create random local variable
            boolean vc_47 = false;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(vc_47);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_45 = (com.squareup.javapoet.Util)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_45);
            // StatementAdderMethod cloned existing statement
            vc_45.checkArgument(vc_47, String_vc_4, vc_50);
            // MethodAssertGenerator build local variable
            Object o_72_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf73 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf83_cf700_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create null value
            java.lang.Object[] vc_57 = (java.lang.Object[])null;
            // MethodAssertGenerator build local variable
            Object o_64_0 = vc_57;
            // StatementAdderOnAssert create null value
            java.lang.String vc_55 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_68_0 = vc_55;
            // StatementAdderOnAssert create random local variable
            boolean vc_54 = true;
            // MethodAssertGenerator build local variable
            Object o_72_0 = vc_54;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_52 = (com.squareup.javapoet.Util)null;
            // MethodAssertGenerator build local variable
            Object o_76_0 = vc_52;
            // StatementAdderMethod cloned existing statement
            vc_52.checkState(vc_54, vc_55, vc_57);
            // MethodAssertGenerator build local variable
            Object o_80_0 = vc_52;
            // StatementAdderOnAssert create null value
            java.lang.String vc_250 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            boolean vc_249 = false;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_247 = (com.squareup.javapoet.Util)null;
            // StatementAdderMethod cloned existing statement
            vc_247.checkState(vc_249, vc_250, vc_57);
            // MethodAssertGenerator build local variable
            Object o_90_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf83_cf700 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf50_cf367_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create random local variable
            char vc_34 = ' ';
            // MethodAssertGenerator build local variable
            Object o_64_0 = vc_34;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_32 = (com.squareup.javapoet.Util)null;
            // MethodAssertGenerator build local variable
            Object o_68_0 = vc_32;
            // AssertGenerator replace invocation
            java.lang.String o_characterLiteral_cf50__66 = // StatementAdderMethod cloned existing statement
vc_32.characterLiteralWithoutSingleQuotes(vc_34);
            // MethodAssertGenerator build local variable
            Object o_72_0 = o_characterLiteral_cf50__66;
            // StatementAdderOnAssert create null value
            java.lang.Object[] vc_180 = (java.lang.Object[])null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_178 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            boolean vc_177 = false;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_175 = (com.squareup.javapoet.Util)null;
            // StatementAdderMethod cloned existing statement
            vc_175.checkArgument(vc_177, vc_178, vc_180);
            // MethodAssertGenerator build local variable
            Object o_84_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf50_cf367 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 10000)
    public void characterLiteral_cf88_cf2717_failAssert10_literalMutation3596_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
                // MethodAssertGenerator build local variable
                Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
                // MethodAssertGenerator build local variable
                Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
                // MethodAssertGenerator build local variable
                Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
                // MethodAssertGenerator build local variable
                Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
                // MethodAssertGenerator build local variable
                Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
                // MethodAssertGenerator build local variable
                Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
                // MethodAssertGenerator build local variable
                Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
                // MethodAssertGenerator build local variable
                Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
                // MethodAssertGenerator build local variable
                Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
                // MethodAssertGenerator build local variable
                Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
                // MethodAssertGenerator build local variable
                Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
                // MethodAssertGenerator build local variable
                Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
                // MethodAssertGenerator build local variable
                Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0007');
                // MethodAssertGenerator build local variable
                Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
                // MethodAssertGenerator build local variable
                Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u007f');
                // MethodAssertGenerator build local variable
                Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
                // MethodAssertGenerator build local variable
                Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
                // MethodAssertGenerator build local variable
                Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0000');
                // MethodAssertGenerator build local variable
                Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0001');
                // MethodAssertGenerator build local variable
                Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u0002');
                // MethodAssertGenerator build local variable
                Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
                // MethodAssertGenerator build local variable
                Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
                // MethodAssertGenerator build local variable
                Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
                // MethodAssertGenerator build local variable
                Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
                // MethodAssertGenerator build local variable
                Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
                // MethodAssertGenerator build local variable
                Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
                // MethodAssertGenerator build local variable
                Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
                // MethodAssertGenerator build local variable
                Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
                // StatementAdderOnAssert create random local variable
                java.lang.Object[] vc_58 = new java.lang.Object []{new java.lang.Object(),new java.lang.Object()};
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_56 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_66_0 = vc_56;
                // StatementAdderOnAssert create random local variable
                boolean vc_54 = // TestDataMutator on numbers
                false;
                // MethodAssertGenerator build local variable
                Object o_70_0 = vc_54;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.Util vc_52 = (com.squareup.javapoet.Util)null;
                // MethodAssertGenerator build local variable
                Object o_74_0 = vc_52;
                // StatementAdderMethod cloned existing statement
                vc_52.checkState(vc_54, vc_56, vc_58);
                // MethodAssertGenerator build local variable
                Object o_78_0 = vc_52;
                // StatementAdderOnAssert create null value
                javax.lang.model.element.Modifier[] vc_583 = (javax.lang.model.element.Modifier[])null;
                // StatementAdderOnAssert create null value
                java.util.Set<javax.lang.model.element.Modifier> vc_581 = (java.util.Set)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.Util vc_579 = (com.squareup.javapoet.Util)null;
                // StatementAdderMethod cloned existing statement
                vc_579.requireExactlyOneOf(vc_581, vc_583);
                // MethodAssertGenerator build local variable
                Object o_88_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
                org.junit.Assert.fail("characterLiteral_cf88_cf2717 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("characterLiteral_cf88_cf2717_failAssert10_literalMutation3596 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }
}
