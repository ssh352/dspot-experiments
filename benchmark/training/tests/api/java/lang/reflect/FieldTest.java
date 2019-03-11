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


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import tests.support.Support_Field;


public class FieldTest extends TestCase {
    // BEGIN android-note
    // This test had a couple of bugs in it. Some parts of the code were
    // unreachable before. Also some tests expected the wrong excpetions
    // to be thrown. This version has been validated to pass on a standard
    // JDK 1.5.
    // END android-note
    public class TestClass {
        @FieldTest.AnnotationRuntime0
        @FieldTest.AnnotationRuntime1
        @FieldTest.AnnotationClass0
        @FieldTest.AnnotationSource0
        public int annotatedField;

        class Inner {}
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    static @interface AnnotationRuntime0 {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    static @interface AnnotationRuntime1 {}

    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.FIELD })
    static @interface AnnotationClass0 {}

    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.FIELD })
    static @interface AnnotationSource0 {}

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    static @interface InheritedRuntime {}

    public class GenericField<S, T extends Number> {
        S field;

        T boundedField;

        int intField;
    }

    static class TestField {
        public static int pubfield1;

        private static int privfield1 = 123;

        protected int intField = Integer.MAX_VALUE;

        protected final int intFField = Integer.MAX_VALUE;

        protected static int intSField = Integer.MAX_VALUE;

        private final int intPFField = Integer.MAX_VALUE;

        protected short shortField = Short.MAX_VALUE;

        protected final short shortFField = Short.MAX_VALUE;

        protected static short shortSField = Short.MAX_VALUE;

        private final short shortPFField = Short.MAX_VALUE;

        protected boolean booleanField = true;

        protected static boolean booleanSField = true;

        protected final boolean booleanFField = true;

        private final boolean booleanPFField = true;

        protected byte byteField = Byte.MAX_VALUE;

        protected static byte byteSField = Byte.MAX_VALUE;

        protected final byte byteFField = Byte.MAX_VALUE;

        private final byte bytePFField = Byte.MAX_VALUE;

        protected long longField = Long.MAX_VALUE;

        protected final long longFField = Long.MAX_VALUE;

        protected static long longSField = Long.MAX_VALUE;

        private final long longPFField = Long.MAX_VALUE;

        protected double doubleField = Double.MAX_VALUE;

        protected static double doubleSField = Double.MAX_VALUE;

        protected static final double doubleSFField = Double.MAX_VALUE;

        protected final double doubleFField = Double.MAX_VALUE;

        private final double doublePFField = Double.MAX_VALUE;

        protected float floatField = Float.MAX_VALUE;

        protected final float floatFField = Float.MAX_VALUE;

        protected static float floatSField = Float.MAX_VALUE;

        private final float floatPFField = Float.MAX_VALUE;

        protected char charField = 'T';

        protected static char charSField = 'T';

        private final char charPFField = 'T';

        protected final char charFField = 'T';

        private static final int x = 1;

        public volatile transient int y = 0;

        protected static volatile transient int prsttrvol = 99;
    }

    public class TestFieldSub1 extends FieldTest.TestField {}

    public class TestFieldSub2 extends FieldTest.TestField {}

    static class A {
        protected short shortField = Short.MAX_VALUE;
    }

    static enum TestEnum {

        A,
        B,
        C;
        int field;
    }

    /**
     * java.lang.reflect.Field#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        // Test for method boolean
        // java.lang.reflect.Field.equals(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        try {
            f = x.getClass().getDeclaredField("shortField");
        } catch (Exception e) {
            TestCase.fail(("Exception during getType test : " + (e.getMessage())));
        }
        try {
            TestCase.assertTrue("Same Field returned false", f.equals(f));
            TestCase.assertTrue("Inherited Field returned false", f.equals(x.getClass().getDeclaredField("shortField")));
            TestCase.assertTrue("Identical Field from different class returned true", (!(f.equals(FieldTest.A.class.getDeclaredField("shortField")))));
        } catch (Exception e) {
            TestCase.fail(("Exception during getType test : " + (e.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#get(java.lang.Object)
     */
    public void test_getLjava_lang_Object() throws Throwable {
        // Test for method java.lang.Object
        // java.lang.reflect.Field.get(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = x.getClass().getDeclaredField("doubleField");
        Double val = ((Double) (f.get(x)));
        TestCase.assertTrue("Returned incorrect double field value", ((val.doubleValue()) == (Double.MAX_VALUE)));
        // Test getting a static field;
        f = x.getClass().getDeclaredField("doubleSField");
        f.set(x, new Double(1.0));
        val = ((Double) (f.get(x)));
        TestCase.assertEquals("Returned incorrect double field value", 1.0, val.doubleValue());
        // Try a get on a private field
        boolean thrown = false;
        try {
            f = TestAccess.class.getDeclaredField("xxx");
            TestCase.assertNotNull(f);
            f.get(null);
            TestCase.fail("No expected IllegalAccessException");
        } catch (IllegalAccessException ok) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Try a get on a private field in nested member
        // temporarily commented because it breaks J9 VM
        // Regression for HARMONY-1309
        // f = x.getClass().getDeclaredField("privfield1");
        // assertEquals(x.privfield1, f.get(x));
        // Try a get using an invalid class.
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.get(new String());
            TestCase.fail("No expected IllegalArgumentException");
        } catch (IllegalArgumentException exc) {
            // Correct - Passed an Object that does not declare or inherit f
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = FieldTest.TestField.class.getDeclaredField("intField");
            f.get(null);
            TestCase.fail("Expected NullPointerException not thrown");
        } catch (NullPointerException exc) {
            // Correct - Passed an Object that does not declare or inherit f
            thrown = true;
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static fields
        thrown = false;
        try {
            f = FieldTest.TestField.class.getDeclaredField("doubleSField");
            f.get(null);
            TestCase.assertTrue("Exception thrown", true);
        } catch (Exception exc) {
            TestCase.fail("No exception expected");
        }
    }

    class SupportSubClass extends Support_Field {
        Object getField(char primitiveType, Object o, Field f, Class expectedException) {
            Object res = null;
            try {
                primitiveType = Character.toUpperCase(primitiveType);
                switch (primitiveType) {
                    case 'I' :
                        // int
                        res = new Integer(f.getInt(o));
                        break;
                    case 'J' :
                        // long
                        res = new Long(f.getLong(o));
                        break;
                    case 'Z' :
                        // boolean
                        res = new Boolean(f.getBoolean(o));
                        break;
                    case 'S' :
                        // short
                        res = new Short(f.getShort(o));
                        break;
                    case 'B' :
                        // byte
                        res = new Byte(f.getByte(o));
                        break;
                    case 'C' :
                        // char
                        res = new Character(f.getChar(o));
                        break;
                    case 'D' :
                        // double
                        res = new Double(f.getDouble(o));
                        break;
                    case 'F' :
                        // float
                        res = new Float(f.getFloat(o));
                        break;
                    default :
                        res = f.get(o);
                }
                if (expectedException != null) {
                    TestCase.fail(("expected exception " + (expectedException.getName())));
                }
            } catch (Exception e) {
                if (expectedException == null) {
                    TestCase.fail(("unexpected exception " + e));
                } else {
                    TestCase.assertTrue(((("expected exception " + (expectedException.getName())) + " and got ") + e), e.getClass().equals(expectedException));
                }
            }
            return res;
        }

        void setField(char primitiveType, Object o, Field f, Class expectedException, Object value) {
            try {
                primitiveType = Character.toUpperCase(primitiveType);
                switch (primitiveType) {
                    case 'I' :
                        // int
                        f.setInt(o, ((Integer) (value)).intValue());
                        break;
                    case 'J' :
                        // long
                        f.setLong(o, ((Long) (value)).longValue());
                        break;
                    case 'Z' :
                        // boolean
                        f.setBoolean(o, ((Boolean) (value)).booleanValue());
                        break;
                    case 'S' :
                        // short
                        f.setShort(o, ((Short) (value)).shortValue());
                        break;
                    case 'B' :
                        // byte
                        f.setByte(o, ((Byte) (value)).byteValue());
                        break;
                    case 'C' :
                        // char
                        f.setChar(o, ((Character) (value)).charValue());
                        break;
                    case 'D' :
                        // double
                        f.setDouble(o, ((Double) (value)).doubleValue());
                        break;
                    case 'F' :
                        // float
                        f.setFloat(o, ((Float) (value)).floatValue());
                        break;
                    default :
                        f.set(o, value);
                }
                if (expectedException != null) {
                    TestCase.fail(((((("expected exception " + (expectedException.getName())) + " for field ") + (f.getName())) + ", value ") + value));
                }
            } catch (Exception e) {
                if (expectedException == null) {
                    TestCase.fail(((((("unexpected exception " + e) + " for field ") + (f.getName())) + ", value ") + value));
                } else {
                    TestCase.assertTrue(((((((("expected exception " + (expectedException.getName())) + " and got ") + e) + " for field ") + (f.getName())) + ", value ") + value), e.getClass().equals(expectedException));
                }
            }
        }
    }

    /**
     * java.lang.reflect.Field#get(java.lang.Object)
     * java.lang.reflect.Field#getByte(java.lang.Object)
     * java.lang.reflect.Field#getBoolean(java.lang.Object)
     * java.lang.reflect.Field#getShort(java.lang.Object)
     * java.lang.reflect.Field#getInt(java.lang.Object)
     * java.lang.reflect.Field#getLong(java.lang.Object)
     * java.lang.reflect.Field#getFloat(java.lang.Object)
     * java.lang.reflect.Field#getDouble(java.lang.Object)
     * java.lang.reflect.Field#getChar(java.lang.Object)
     * java.lang.reflect.Field#set(java.lang.Object, java.lang.Object)
     * java.lang.reflect.Field#setByte(java.lang.Object, byte)
     * java.lang.reflect.Field#setBoolean(java.lang.Object, boolean)
     * java.lang.reflect.Field#setShort(java.lang.Object, short)
     * java.lang.reflect.Field#setInt(java.lang.Object, int)
     * java.lang.reflect.Field#setLong(java.lang.Object, long)
     * java.lang.reflect.Field#setFloat(java.lang.Object, float)
     * java.lang.reflect.Field#setDouble(java.lang.Object, double)
     * java.lang.reflect.Field#setChar(java.lang.Object, char)
     */
    public void testProtectedFieldAccess() {
        Class fieldClass = new Support_Field().getClass();
        String fieldName = null;
        Field objectField = null;
        Field booleanField = null;
        Field byteField = null;
        Field charField = null;
        Field shortField = null;
        Field intField = null;
        Field longField = null;
        Field floatField = null;
        Field doubleField = null;
        try {
            fieldName = "objectField";
            objectField = fieldClass.getDeclaredField(fieldName);
            fieldName = "booleanField";
            booleanField = fieldClass.getDeclaredField(fieldName);
            fieldName = "byteField";
            byteField = fieldClass.getDeclaredField(fieldName);
            fieldName = "charField";
            charField = fieldClass.getDeclaredField(fieldName);
            fieldName = "shortField";
            shortField = fieldClass.getDeclaredField(fieldName);
            fieldName = "intField";
            intField = fieldClass.getDeclaredField(fieldName);
            fieldName = "longField";
            longField = fieldClass.getDeclaredField(fieldName);
            fieldName = "floatField";
            floatField = fieldClass.getDeclaredField(fieldName);
            fieldName = "doubleField";
            doubleField = fieldClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            TestCase.fail(((("missing field " + fieldName) + " in test support class ") + (fieldClass.getName())));
        }
        // create the various objects that might or might not have an instance
        // of the field
        Support_Field parentClass = new Support_Field();
        FieldTest.SupportSubClass subclass = new FieldTest.SupportSubClass();
        FieldTest.SupportSubClass otherSubclass = new FieldTest.SupportSubClass();
        Object plainObject = new Object();
        Class illegalAccessExceptionClass = new IllegalAccessException().getClass();
        Class illegalArgumentExceptionClass = new IllegalArgumentException().getClass();
        // The test will attempt to use pass an object to set for object, byte,
        // short, ..., float and double fields
        // and pass a byte to to setByte for byte, short, ..., float and double
        // fields and so on.
        // It will also test if IllegalArgumentException is thrown when the
        // field does not exist in the given object and that
        // IllegalAccessException is thrown when trying to access an
        // inaccessible protected field.
        // The test will also check that IllegalArgumentException is thrown for
        // all other attempts.
        // Ordered by widening conversion, except for 'L' at the beg (which
        // stands for Object).
        // If the object provided to set can be unwrapped to a primitive, then
        // the set method can set
        // primitive fields.
        char[] types = new char[]{ 'L', 'B', 'S', 'C', 'I', 'J', 'F', 'D' };
        Field[] fields = new Field[]{ objectField, byteField, shortField, charField, intField, longField, floatField, doubleField };
        Object[] values = new Object[]{ new Byte(((byte) (1))), new Byte(((byte) (1))), new Short(((short) (1))), new Character(((char) (1))), new Integer(1), new Long(1), new Float(1), new Double(1) };
        // test set methods
        for (int i = 0; i < (types.length); i++) {
            char type = types[i];
            Object value = values[i];
            for (int j = i; j < (fields.length); j++) {
                Field field = fields[j];
                fieldName = field.getName();
                if ((field == charField) && (type != 'C')) {
                    // the exception is that bytes and shorts CANNOT be
                    // converted into chars even though chars CAN be
                    // converted into ints, longs, floats and doubles
                    subclass.setField(type, subclass, field, illegalArgumentExceptionClass, value);
                } else {
                    // setting type into field);
                    subclass.setField(type, subclass, field, null, value);
                    subclass.setField(type, otherSubclass, field, null, value);
                    subclass.setField(type, parentClass, field, illegalAccessExceptionClass, value);
                    // Failed on JDK.
                    subclass.setField(type, plainObject, field, illegalAccessExceptionClass, value);
                }
            }
            for (int j = 0; j < i; j++) {
                Field field = fields[j];
                fieldName = field.getName();
                // not setting type into field);
                subclass.setField(type, subclass, field, illegalArgumentExceptionClass, value);
            }
        }
        // test setBoolean
        Boolean booleanValue = Boolean.TRUE;
        subclass.setField('Z', subclass, booleanField, null, booleanValue);
        subclass.setField('Z', otherSubclass, booleanField, null, booleanValue);
        subclass.setField('Z', parentClass, booleanField, illegalAccessExceptionClass, booleanValue);
        // Failed on JDK
        subclass.setField('Z', plainObject, booleanField, illegalAccessExceptionClass, booleanValue);
        for (int j = 0; j < (fields.length); j++) {
            Field listedField = fields[j];
            fieldName = listedField.getName();
            // not setting boolean into listedField
            subclass.setField('Z', subclass, listedField, illegalArgumentExceptionClass, booleanValue);
        }
        for (int i = 0; i < (types.length); i++) {
            char type = types[i];
            Object value = values[i];
            subclass.setField(type, subclass, booleanField, illegalArgumentExceptionClass, value);
        }
        // We perform the analagous test on the get methods.
        // ordered by widening conversion, except for 'L' at the end (which
        // stands for Object), to which all primitives can be converted by
        // wrapping
        char[] newTypes = new char[]{ 'B', 'S', 'C', 'I', 'J', 'F', 'D', 'L' };
        Field[] newFields = new Field[]{ byteField, shortField, charField, intField, longField, floatField, doubleField, objectField };
        fields = newFields;
        types = newTypes;
        // test get methods
        for (int i = 0; i < (types.length); i++) {
            char type = types[i];
            for (int j = 0; j <= i; j++) {
                Field field = fields[j];
                fieldName = field.getName();
                if ((type == 'C') && (field != charField)) {
                    // the exception is that bytes and shorts CANNOT be
                    // converted into chars even though chars CAN be
                    // converted into ints, longs, floats and doubles
                    subclass.getField(type, subclass, field, illegalArgumentExceptionClass);
                } else {
                    // getting type from field
                    subclass.getField(type, subclass, field, null);
                    subclass.getField(type, otherSubclass, field, null);
                    subclass.getField(type, parentClass, field, illegalAccessExceptionClass);
                    subclass.getField(type, plainObject, field, illegalAccessExceptionClass);
                }
            }
            for (int j = i + 1; j < (fields.length); j++) {
                Field field = fields[j];
                fieldName = field.getName();
                subclass.getField(type, subclass, field, illegalArgumentExceptionClass);
            }
        }
        // test getBoolean
        subclass.getField('Z', subclass, booleanField, null);
        subclass.getField('Z', otherSubclass, booleanField, null);
        subclass.getField('Z', parentClass, booleanField, illegalAccessExceptionClass);
        subclass.getField('Z', plainObject, booleanField, illegalAccessExceptionClass);
        for (int j = 0; j < (fields.length); j++) {
            Field listedField = fields[j];
            fieldName = listedField.getName();
            // not getting boolean from listedField
            subclass.getField('Z', subclass, listedField, illegalArgumentExceptionClass);
        }
        for (int i = 0; i < ((types.length) - 1); i++) {
            char type = types[i];
            subclass.getField(type, subclass, booleanField, illegalArgumentExceptionClass);
        }
        Object res = subclass.getField('L', subclass, booleanField, null);
        TestCase.assertTrue(("unexpected object " + res), (res instanceof Boolean));
    }

    /**
     * java.lang.reflect.Field#getBoolean(java.lang.Object)
     */
    public void test_getBooleanLjava_lang_Object() {
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        boolean val = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            val = f.getBoolean(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during getBoolean test: " + (e.toString())));
        }
        TestCase.assertTrue("Returned incorrect boolean field value", val);
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.getBoolean(x);
            TestCase.fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("IllegalArgumentException expected but not thrown");
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanPFField");
            f.getBoolean(x);
            TestCase.fail("IllegalAccessException expected but not thrown");
        } catch (IllegalAccessException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalAccessException expected but not thrown" + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.getBoolean(null);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static field
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanSField");
            boolean staticValue = f.getBoolean(null);
            TestCase.assertTrue("Wrong value returned", staticValue);
        } catch (Exception ex) {
            TestCase.fail("No exception expected");
        }
    }

    /**
     * java.lang.reflect.Field#getByte(java.lang.Object)
     */
    public void test_getByteLjava_lang_Object() {
        // Test for method byte
        // java.lang.reflect.Field.getByte(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        byte val = 0;
        try {
            f = x.getClass().getDeclaredField("byteField");
            val = f.getByte(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during getbyte test : " + (e.getMessage())));
        }
        TestCase.assertTrue("Returned incorrect byte field value", (val == (Byte.MAX_VALUE)));
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.getByte(x);
            TestCase.fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("IllegalArgumentException expected but not thrown");
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("bytePFField");
            f.getByte(x);
            TestCase.fail("IllegalAccessException expected but not thrown");
        } catch (IllegalAccessException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalAccessException expected but not thrown" + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("byteField");
            f.getByte(null);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static field
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("byteSField");
            byte staticValue = f.getByte(null);
            TestCase.assertEquals("Wrong value returned", Byte.MAX_VALUE, staticValue);
        } catch (Exception ex) {
            TestCase.fail(("No exception expected " + (ex.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#getChar(java.lang.Object)
     */
    public void test_getCharLjava_lang_Object() {
        // Test for method char
        // java.lang.reflect.Field.getChar(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        char val = 0;
        try {
            f = x.getClass().getDeclaredField("charField");
            val = f.getChar(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during getCharacter test: " + (e.toString())));
        }
        TestCase.assertEquals("Returned incorrect char field value", 'T', val);
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.getChar(x);
            TestCase.fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("IllegalArgumentException expected but not thrown");
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("charPFField");
            f.getChar(x);
            TestCase.fail("IllegalAccessException expected but not thrown");
        } catch (IllegalAccessException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalAccessException expected but not thrown" + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("charField");
            f.getChar(null);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static field
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("charSField");
            char staticValue = f.getChar(null);
            TestCase.assertEquals("Wrong value returned", 'T', staticValue);
        } catch (Exception ex) {
            TestCase.fail(("No exception expected " + (ex.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#getDeclaringClass()
     */
    public void test_getDeclaringClass() {
        // Test for method java.lang.Class
        // java.lang.reflect.Field.getDeclaringClass()
        Field[] fields;
        try {
            fields = new FieldTest.TestField().getClass().getFields();
            TestCase.assertTrue("Returned incorrect declaring class", fields[0].getDeclaringClass().equals(new FieldTest.TestField().getClass()));
            // Check the case where the field is inherited to be sure the parent
            // is returned as the declarator
            fields = new FieldTest.TestFieldSub1().getClass().getFields();
            TestCase.assertTrue("Returned incorrect declaring class", fields[0].getDeclaringClass().equals(new FieldTest.TestField().getClass()));
        } catch (Exception e) {
            TestCase.fail(("Exception : " + (e.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#getDouble(java.lang.Object)
     */
    public void test_getDoubleLjava_lang_Object() {
        // Test for method double
        // java.lang.reflect.Field.getDouble(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        double val = 0.0;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            val = f.getDouble(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during getDouble test: " + (e.toString())));
        }
        TestCase.assertTrue("Returned incorrect double field value", (val == (Double.MAX_VALUE)));
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.getDouble(x);
            TestCase.fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalArgumentException expected but not thrown " + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("doublePFField");
            f.getDouble(x);
            TestCase.fail("IllegalAccessException expected but not thrown");
        } catch (IllegalAccessException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalAccessException expected but not thrown" + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.getDouble(null);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static field
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleSFField");
            double staticValue = f.getDouble(null);
            TestCase.assertEquals("Wrong value returned", Double.MAX_VALUE, staticValue);
        } catch (Exception ex) {
            TestCase.fail(("No exception expected " + (ex.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#getFloat(java.lang.Object)
     */
    public void test_getFloatLjava_lang_Object() {
        // Test for method float
        // java.lang.reflect.Field.getFloat(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        float val = 0;
        try {
            f = x.getClass().getDeclaredField("floatField");
            val = f.getFloat(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during getFloat test : " + (e.getMessage())));
        }
        TestCase.assertTrue("Returned incorrect float field value", (val == (Float.MAX_VALUE)));
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.getFloat(x);
            TestCase.fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalArgumentException expected but not thrown " + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("floatPFField");
            f.getFloat(x);
            TestCase.fail("IllegalAccessException expected but not thrown");
        } catch (IllegalAccessException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalAccessException expected but not thrown" + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("floatField");
            f.getFloat(null);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static field
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("floatSField");
            float staticValue = f.getFloat(null);
            TestCase.assertEquals("Wrong value returned", Float.MAX_VALUE, staticValue);
        } catch (Exception ex) {
            TestCase.fail(("No exception expected " + (ex.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#getInt(java.lang.Object)
     */
    public void test_getIntLjava_lang_Object() {
        // Test for method int java.lang.reflect.Field.getInt(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        int val = 0;
        try {
            f = x.getClass().getDeclaredField("intField");
            val = f.getInt(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during getInt test : " + (e.getMessage())));
        }
        TestCase.assertTrue("Returned incorrect Int field value", (val == (Integer.MAX_VALUE)));
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.getInt(x);
            TestCase.fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalArgumentException expected but not thrown " + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("intPFField");
            f.getInt(x);
            TestCase.fail("IllegalAccessException expected but not thrown");
        } catch (IllegalAccessException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalAccessException expected but not thrown" + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("intField");
            f.getInt(null);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static field
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("intSField");
            int staticValue = f.getInt(null);
            TestCase.assertEquals("Wrong value returned", Integer.MAX_VALUE, staticValue);
        } catch (Exception ex) {
            TestCase.fail(("No exception expected " + (ex.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#getLong(java.lang.Object)
     */
    public void test_getLongLjava_lang_Object() {
        // Test for method long
        // java.lang.reflect.Field.getLong(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        long val = 0;
        try {
            f = x.getClass().getDeclaredField("longField");
            val = f.getLong(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during getLong test : " + (e.getMessage())));
        }
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.getLong(x);
            TestCase.fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalArgumentException expected but not thrown " + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("longPFField");
            f.getLong(x);
            TestCase.fail("IllegalAccessException expected but not thrown");
        } catch (IllegalAccessException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalAccessException expected but not thrown" + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("longField");
            f.getLong(null);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static field
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("longSField");
            long staticValue = f.getLong(null);
            TestCase.assertEquals("Wrong value returned", Long.MAX_VALUE, staticValue);
        } catch (Exception ex) {
            TestCase.fail(("No exception expected " + (ex.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#getModifiers()
     */
    public void test_getModifiers() {
        // Test for method int java.lang.reflect.Field.getModifiers()
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        try {
            f = x.getClass().getDeclaredField("prsttrvol");
        } catch (Exception e) {
            TestCase.fail(("Exception during getModifiers test: " + (e.toString())));
        }
        int mod = f.getModifiers();
        int mask = ((Modifier.PROTECTED) | (Modifier.STATIC)) | ((Modifier.TRANSIENT) | (Modifier.VOLATILE));
        int nmask = (Modifier.PUBLIC) | (Modifier.NATIVE);
        TestCase.assertTrue("Returned incorrect field modifiers: ", (((mod & mask) == mask) && ((mod & nmask) == 0)));
    }

    /**
     * java.lang.reflect.Field#getName()
     */
    public void test_getName() {
        // Test for method java.lang.String java.lang.reflect.Field.getName()
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        try {
            f = x.getClass().getDeclaredField("shortField");
        } catch (Exception e) {
            TestCase.fail(("Exception during getType test : " + (e.getMessage())));
        }
        TestCase.assertEquals("Returned incorrect field name", "shortField", f.getName());
    }

    /**
     * java.lang.reflect.Field#getShort(java.lang.Object)
     */
    public void test_getShortLjava_lang_Object() {
        // Test for method short
        // java.lang.reflect.Field.getShort(java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        short val = 0;
        try {
            f = x.getClass().getDeclaredField("shortField");
            val = f.getShort(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during getShort test : " + (e.getMessage())));
        }
        TestCase.assertTrue("Returned incorrect short field value", (val == (Short.MAX_VALUE)));
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.getShort(x);
            TestCase.fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalArgumentException expected but not thrown " + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("shortPFField");
            f.getShort(x);
            TestCase.fail("IllegalAccessException expected but not thrown");
        } catch (IllegalAccessException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail(("IllegalAccessException expected but not thrown" + (ex.getMessage())));
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("shortField");
            f.getShort(null);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test no NPE on static field
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("shortSField");
            short staticValue = f.getShort(null);
            TestCase.assertEquals("Wrong value returned", Short.MAX_VALUE, staticValue);
        } catch (Exception ex) {
            TestCase.fail(("No exception expected " + (ex.getMessage())));
        }
    }

    /**
     * java.lang.reflect.Field#getType()
     */
    public void test_getType() {
        // Test for method java.lang.Class java.lang.reflect.Field.getType()
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        try {
            f = x.getClass().getDeclaredField("shortField");
        } catch (Exception e) {
            TestCase.fail(("Exception during getType test : " + (e.getMessage())));
        }
        TestCase.assertTrue(("Returned incorrect field type: " + (f.getType().toString())), f.getType().equals(short.class));
    }

    /**
     * java.lang.reflect.Field#set(java.lang.Object, java.lang.Object)
     */
    public void test_setLjava_lang_ObjectLjava_lang_Object() throws Exception {
        // Test for method void java.lang.reflect.Field.set(java.lang.Object,
        // java.lang.Object)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        double val = 0.0;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.set(x, new Double(1.0));
            val = f.getDouble(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during set test : " + (e.getMessage())));
        }
        TestCase.assertEquals("Returned incorrect double field value", 1.0, val);
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.set(x, new Double(1.0));
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleFField");
            TestCase.assertFalse(f.isAccessible());
            f.set(x, new Double(1.0));
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.set(null, true);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("doubleSField");
        f.set(null, new Double(1.0));
        val = f.getDouble(x);
        TestCase.assertEquals("Returned incorrect double field value", 1.0, val);
    }

    /**
     * java.lang.reflect.Field#setBoolean(java.lang.Object, boolean)
     */
    public void test_setBooleanLjava_lang_ObjectZ() throws Exception {
        // Test for method void
        // java.lang.reflect.Field.setBoolean(java.lang.Object, boolean)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        boolean val = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setBoolean(x, false);
            val = f.getBoolean(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during setboolean test: " + (e.toString())));
        }
        TestCase.assertTrue("Returned incorrect float field value", (!val));
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.setBoolean(x, false);
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanPFField");
            TestCase.assertFalse(f.isAccessible());
            f.setBoolean(x, true);
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setBoolean(null, true);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("booleanSField");
        f.setBoolean(null, false);
        val = f.getBoolean(x);
        TestCase.assertFalse("Returned incorrect boolean field value", val);
    }

    /**
     * java.lang.reflect.Field#setByte(java.lang.Object, byte)
     */
    public void test_setByteLjava_lang_ObjectB() throws Exception {
        // Test for method void
        // java.lang.reflect.Field.setByte(java.lang.Object, byte)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        byte val = 0;
        try {
            f = x.getClass().getDeclaredField("byteField");
            f.setByte(x, ((byte) (1)));
            val = f.getByte(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during setByte test : " + (e.getMessage())));
        }
        TestCase.assertEquals("Returned incorrect float field value", 1, val);
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setByte(x, Byte.MIN_VALUE);
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("bytePFField");
            TestCase.assertFalse(f.isAccessible());
            f.setByte(x, Byte.MIN_VALUE);
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("byteField");
            f.setByte(null, Byte.MIN_VALUE);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("byteSField");
        f.setByte(null, Byte.MIN_VALUE);
        val = f.getByte(x);
        TestCase.assertEquals("Returned incorrect byte field value", Byte.MIN_VALUE, val);
    }

    /**
     * java.lang.reflect.Field#setChar(java.lang.Object, char)
     */
    public void test_setCharLjava_lang_ObjectC() throws Exception {
        // Test for method void
        // java.lang.reflect.Field.setChar(java.lang.Object, char)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        char val = 0;
        try {
            f = x.getClass().getDeclaredField("charField");
            f.setChar(x, ((char) (1)));
            val = f.getChar(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during setChar test : " + (e.getMessage())));
        }
        TestCase.assertEquals("Returned incorrect float field value", 1, val);
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setChar(x, Character.MIN_VALUE);
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("charPFField");
            TestCase.assertFalse(f.isAccessible());
            f.setChar(x, Character.MIN_VALUE);
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("charField");
            f.setChar(null, Character.MIN_VALUE);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("charSField");
        f.setChar(null, Character.MIN_VALUE);
        val = f.getChar(x);
        TestCase.assertEquals("Returned incorrect char field value", Character.MIN_VALUE, val);
    }

    /**
     * java.lang.reflect.Field#setDouble(java.lang.Object, double)
     */
    public void test_setDoubleLjava_lang_ObjectD() throws Exception {
        // Test for method void
        // java.lang.reflect.Field.setDouble(java.lang.Object, double)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        double val = 0.0;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.setDouble(x, Double.MIN_VALUE);
            val = f.getDouble(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during setDouble test: " + (e.toString())));
        }
        TestCase.assertEquals("Returned incorrect double field value", Double.MIN_VALUE, val);
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setDouble(x, Double.MIN_VALUE);
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("doublePFField");
            TestCase.assertFalse(f.isAccessible());
            f.setDouble(x, Double.MIN_VALUE);
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("doubleField");
            f.setDouble(null, Double.MIN_VALUE);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("doubleSField");
        f.setDouble(null, Double.MIN_VALUE);
        val = f.getDouble(x);
        TestCase.assertEquals("Returned incorrect double field value", Double.MIN_VALUE, val);
    }

    /**
     * java.lang.reflect.Field#setFloat(java.lang.Object, float)
     */
    public void test_setFloatLjava_lang_ObjectF() throws Exception {
        // Test for method void
        // java.lang.reflect.Field.setFloat(java.lang.Object, float)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        float val = 0.0F;
        try {
            f = x.getClass().getDeclaredField("floatField");
            f.setFloat(x, Float.MIN_VALUE);
            val = f.getFloat(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during setFloat test : " + (e.getMessage())));
        }
        TestCase.assertEquals("Returned incorrect float field value", Float.MIN_VALUE, val, 0.0);
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setFloat(x, Float.MIN_VALUE);
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("floatPFField");
            TestCase.assertFalse(f.isAccessible());
            f.setFloat(x, Float.MIN_VALUE);
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("floatField");
            f.setFloat(null, Float.MIN_VALUE);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("floatSField");
        f.setFloat(null, Float.MIN_VALUE);
        val = f.getFloat(x);
        TestCase.assertEquals("Returned incorrect float field value", Float.MIN_VALUE, val);
    }

    /**
     * java.lang.reflect.Field#setInt(java.lang.Object, int)
     */
    public void test_setIntLjava_lang_ObjectI() throws Exception {
        // Test for method void java.lang.reflect.Field.setInt(java.lang.Object,
        // int)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        int val = 0;
        try {
            f = x.getClass().getDeclaredField("intField");
            f.setInt(x, Integer.MIN_VALUE);
            val = f.getInt(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during setInteger test: " + (e.toString())));
        }
        TestCase.assertEquals("Returned incorrect int field value", Integer.MIN_VALUE, val);
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setInt(x, Integer.MIN_VALUE);
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("intPFField");
            TestCase.assertFalse(f.isAccessible());
            f.setInt(x, Integer.MIN_VALUE);
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("intField");
            f.setInt(null, Integer.MIN_VALUE);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("intSField");
        f.setInt(null, Integer.MIN_VALUE);
        val = f.getInt(x);
        TestCase.assertEquals("Returned incorrect int field value", Integer.MIN_VALUE, val);
    }

    /**
     * java.lang.reflect.Field#setLong(java.lang.Object, long)
     */
    public void test_setLongLjava_lang_ObjectJ() throws Exception {
        // Test for method void
        // java.lang.reflect.Field.setLong(java.lang.Object, long)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        long val = 0L;
        try {
            f = x.getClass().getDeclaredField("longField");
            f.setLong(x, Long.MIN_VALUE);
            val = f.getLong(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during setLong test : " + (e.getMessage())));
        }
        TestCase.assertEquals("Returned incorrect long field value", Long.MIN_VALUE, val);
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setLong(x, Long.MIN_VALUE);
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("longPFField");
            TestCase.assertFalse(f.isAccessible());
            f.setLong(x, Long.MIN_VALUE);
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("longField");
            f.setLong(null, Long.MIN_VALUE);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("longSField");
        f.setLong(null, Long.MIN_VALUE);
        val = f.getLong(x);
        TestCase.assertEquals("Returned incorrect long field value", Long.MIN_VALUE, val);
    }

    /**
     * java.lang.reflect.Field#setShort(java.lang.Object, short)
     */
    public void test_setShortLjava_lang_ObjectS() throws Exception {
        // Test for method void
        // java.lang.reflect.Field.setShort(java.lang.Object, short)
        FieldTest.TestField x = new FieldTest.TestField();
        Field f = null;
        short val = 0;
        try {
            f = x.getClass().getDeclaredField("shortField");
            f.setShort(x, Short.MIN_VALUE);
            val = f.getShort(x);
        } catch (Exception e) {
            TestCase.fail(("Exception during setShort test : " + (e.getMessage())));
        }
        TestCase.assertEquals("Returned incorrect short field value", Short.MIN_VALUE, val);
        // test wrong type
        boolean thrown = false;
        try {
            f = x.getClass().getDeclaredField("booleanField");
            f.setShort(x, Short.MIN_VALUE);
            TestCase.fail("Accessed field of invalid type");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalArgumentException expected but not thrown", thrown);
        // test not accessible
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("shortPFField");
            TestCase.assertFalse(f.isAccessible());
            f.setShort(x, Short.MIN_VALUE);
            TestCase.fail("Accessed inaccessible field");
        } catch (IllegalAccessException ex) {
            thrown = true;
        }
        TestCase.assertTrue("IllegalAccessException expected but not thrown", thrown);
        // Test NPE
        thrown = false;
        try {
            f = x.getClass().getDeclaredField("shortField");
            f.setShort(null, Short.MIN_VALUE);
            TestCase.fail("NullPointerException expected but not thrown");
        } catch (NullPointerException ex) {
            thrown = true;
        } catch (Exception ex) {
            TestCase.fail("NullPointerException expected but not thrown");
        }
        TestCase.assertTrue("NullPointerException expected but not thrown", thrown);
        // Test setting a static field;
        f = x.getClass().getDeclaredField("shortSField");
        f.setShort(null, Short.MIN_VALUE);
        val = f.getShort(x);
        TestCase.assertEquals("Returned incorrect short field value", Short.MIN_VALUE, val);
    }

    /**
     * java.lang.reflect.Field#toString()
     */
    public void test_toString() {
        // Test for method java.lang.String java.lang.reflect.Field.toString()
        Field f = null;
        try {
            f = FieldTest.TestField.class.getDeclaredField("x");
        } catch (Exception e) {
            TestCase.fail(("Exception getting field : " + (e.getMessage())));
        }
        TestCase.assertEquals("Field returned incorrect string", "private static final int tests.api.java.lang.reflect.FieldTest$TestField.x", f.toString());
    }

    public void test_getDeclaredAnnotations() throws Exception {
        Field field = FieldTest.TestClass.class.getField("annotatedField");
        Annotation[] annotations = field.getDeclaredAnnotations();
        TestCase.assertEquals(2, annotations.length);
        Set<Class<?>> ignoreOrder = new HashSet<Class<?>>();
        ignoreOrder.add(annotations[0].annotationType());
        ignoreOrder.add(annotations[1].annotationType());
        TestCase.assertTrue("Missing @AnnotationRuntime0", ignoreOrder.contains(FieldTest.AnnotationRuntime0.class));
        TestCase.assertTrue("Missing @AnnotationRuntime1", ignoreOrder.contains(FieldTest.AnnotationRuntime1.class));
    }

    public void test_isEnumConstant() throws Exception {
        Field field = FieldTest.TestEnum.class.getDeclaredField("A");
        TestCase.assertTrue("Enum constant not recognized", field.isEnumConstant());
        field = FieldTest.TestEnum.class.getDeclaredField("field");
        TestCase.assertFalse("Non enum constant wrongly stated as enum constant", field.isEnumConstant());
        field = FieldTest.TestClass.class.getDeclaredField("annotatedField");
        TestCase.assertFalse("Non enum constant wrongly stated as enum constant", field.isEnumConstant());
    }

    public void test_isSynthetic() throws Exception {
        Field[] fields = FieldTest.TestClass.Inner.class.getDeclaredFields();
        TestCase.assertEquals("Not exactly one field returned", 1, fields.length);
        TestCase.assertTrue("Enum constant not recognized", fields[0].isSynthetic());
        Field field = FieldTest.TestEnum.class.getDeclaredField("field");
        TestCase.assertFalse("Non synthetic field wrongly stated as synthetic", field.isSynthetic());
        field = FieldTest.TestClass.class.getDeclaredField("annotatedField");
        TestCase.assertFalse("Non synthetic field wrongly stated as synthetic", field.isSynthetic());
    }

    public void test_getGenericType() throws Exception {
        Field field = FieldTest.GenericField.class.getDeclaredField("field");
        Type type = field.getGenericType();
        @SuppressWarnings("unchecked")
        TypeVariable typeVar = ((TypeVariable) (type));
        TestCase.assertEquals("Wrong type name returned", "S", typeVar.getName());
        Field boundedField = FieldTest.GenericField.class.getDeclaredField("boundedField");
        Type boundedType = boundedField.getGenericType();
        @SuppressWarnings("unchecked")
        TypeVariable boundedTypeVar = ((TypeVariable) (boundedType));
        TestCase.assertEquals("Wrong type name returned", "T", boundedTypeVar.getName());
        TestCase.assertEquals("More than one bound found", 1, boundedTypeVar.getBounds().length);
        TestCase.assertEquals("Wrong bound returned", Number.class, boundedTypeVar.getBounds()[0]);
    }

    public void test_toGenericString() throws Exception {
        Field field = FieldTest.GenericField.class.getDeclaredField("field");
        TestCase.assertEquals("Wrong generic string returned", "S tests.api.java.lang.reflect.FieldTest$GenericField.field", field.toGenericString());
        Field boundedField = FieldTest.GenericField.class.getDeclaredField("boundedField");
        TestCase.assertEquals("Wrong generic string returned", "T tests.api.java.lang.reflect.FieldTest$GenericField.boundedField", boundedField.toGenericString());
        Field ordinary = FieldTest.GenericField.class.getDeclaredField("intField");
        TestCase.assertEquals("Wrong generic string returned", "int tests.api.java.lang.reflect.FieldTest$GenericField.intField", ordinary.toGenericString());
    }

    public void test_hashCode() throws Exception {
        Field field = FieldTest.TestClass.class.getDeclaredField("annotatedField");
        TestCase.assertEquals("Wrong hashCode returned", ((field.getName().hashCode()) ^ (field.getDeclaringClass().getName().hashCode())), field.hashCode());
    }
}

