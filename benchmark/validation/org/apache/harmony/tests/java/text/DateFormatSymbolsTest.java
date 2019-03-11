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
package org.apache.harmony.tests.java.text;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import junit.framework.TestCase;


public class DateFormatSymbolsTest extends TestCase {
    private DateFormatSymbols dfs;

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#DateFormatSymbols()
     */
    public void test_Constructor() {
        // Test for method java.text.DateFormatSymbols()
        // Used in tests
        new DateFormatSymbols();
    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#DateFormatSymbols(java.util.Locale)
     */
    public void test_ConstructorLjava_util_Locale() {
        // Test for method java.text.DateFormatSymbols(java.util.Locale)
        new DateFormatSymbols(new Locale("en", "us"));
    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getAvailableLocales()
     */
    public void test_getAvailableLocales_no_provider() throws Exception {
        Locale[] locales = DateFormatSymbols.getAvailableLocales();
        TestCase.assertNotNull(locales);
        // must contain Locale.US
        boolean flag = false;
        for (Locale locale : locales) {
            if (locale.equals(Locale.US)) {
                flag = true;
                break;
            }
        }
        TestCase.assertTrue(flag);
    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getInstance()
     */
    public void test_getInstance() {
        DateFormatSymbols.getInstance();
        TestCase.assertEquals(new DateFormatSymbols(), DateFormatSymbols.getInstance());
        TestCase.assertEquals(new DateFormatSymbols(Locale.getDefault()), DateFormatSymbols.getInstance());
        TestCase.assertNotSame(DateFormatSymbols.getInstance(), DateFormatSymbols.getInstance());
    }

    public void test_getInstanceLjava_util_Locale() {
        try {
            DateFormatSymbols.getInstance(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertEquals(new DateFormatSymbols(Locale.GERMANY), DateFormatSymbols.getInstance(Locale.GERMANY));
        Locale locale = new Locale("not exist language", "not exist country");
        DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
        TestCase.assertEquals(DateFormatSymbols.getInstance(Locale.ROOT), symbols);
    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.text.DateFormatSymbols.clone()
        DateFormatSymbols symbols = new DateFormatSymbols();
        DateFormatSymbols clone = ((DateFormatSymbols) (symbols.clone()));
        TestCase.assertTrue("Not equal", symbols.equals(clone));
    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        // Test for method boolean
        // java.text.DateFormatSymbols.equals(java.lang.Object)
        TestCase.assertTrue("Equal object returned true", dfs.equals(dfs.clone()));
        dfs.setLocalPatternChars("KKKKKKKKK");
        TestCase.assertTrue("Un-Equal objects returned false", (!(dfs.equals(new DateFormatSymbols()))));
    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getAmPmStrings()
     */
    public void test_getAmPmStrings() {
        // Test for method java.lang.String []
        // java.text.DateFormatSymbols.getAmPmStrings()
        String[] retVal = dfs.getAmPmStrings();
        String[] val = new String[]{ "AM", "PM" };
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Array values do not match", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getEras()
     */
    public void test_getEras() {
        // Test for method java.lang.String []
        // java.text.DateFormatSymbols.getEras()
        String[] retVal = dfs.getEras();
        String[] val = new String[]{ "BC", "AD" };
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Array values do not match", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getMonths()
     */
    public void test_getMonths() {
        // Test for method java.lang.String []
        // java.text.DateFormatSymbols.getMonths()
        String[] retVal = dfs.getMonths();
        String[] val = new String[]{ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
        TestCase.assertEquals("Returned wrong array: ", val.length, retVal.length);
        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Array values do not match", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getShortMonths()
     */
    public void test_getShortMonths() {
        // Test for method java.lang.String []
        // java.text.DateFormatSymbols.getShortMonths()
        String[] retVal = dfs.getShortMonths();
        String[] val = new String[]{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        TestCase.assertEquals("Returned wrong array: ", val.length, retVal.length);
        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Array values do not match", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getShortWeekdays()
     */
    public void test_getShortWeekdays() {
        // Test for method java.lang.String []
        // java.text.DateFormatSymbols.getShortWeekdays()
        String[] retVal = dfs.getShortWeekdays();
        String[] val = new String[]{ "", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Array values do not match", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getWeekdays()
     */
    public void test_getWeekdays() {
        // Test for method java.lang.String []
        // java.text.DateFormatSymbols.getWeekdays()
        String[] retVal = dfs.getWeekdays();
        String[] val = new String[]{ "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Array values do not match", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#getZoneStrings()
     */
    public void test_getZoneStrings() {
        // Test for method java.lang.String [][]
        // java.text.DateFormatSymbols.getZoneStrings()
        String[][] val = new String[][]{ new String[]{ "XX", "XX", "XX", "XX", "XX" }, new String[]{ "YY", "YY", "YY", "YY", "YY" } };
        dfs.setZoneStrings(val);
        String[][] retVal = dfs.getZoneStrings();
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Failed to set strings", Arrays.equals(retVal[i], val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#hashCode()
     */
    public void test_hashCode() {
        // Test for method int java.text.DateFormatSymbols.hashCode()
        int hc1 = dfs.hashCode();
        int hc2 = dfs.hashCode();
        TestCase.assertTrue(((("hashCode() returned inconsistent number : " + hc1) + " - ") + hc2), (hc1 == hc2));
        TestCase.assertTrue("hashCode() returns different values for equal() objects", ((dfs.hashCode()) == (dfs.clone().hashCode())));
    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#setAmPmStrings(java.lang.String[])
     */
    public void test_setAmPmStrings$Ljava_lang_String() {
        // Test for method void
        // java.text.DateFormatSymbols.setAmPmStrings(java.lang.String [])
        String[] val = new String[]{ "XX", "YY" };
        dfs.setAmPmStrings(val);
        String[] retVal = dfs.getAmPmStrings();
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Failed to set strings", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#setEras(java.lang.String[])
     */
    public void test_setEras$Ljava_lang_String() {
        // Test for method void
        // java.text.DateFormatSymbols.setEras(java.lang.String [])
        String[] val = new String[]{ "XX", "YY" };
        dfs.setEras(val);
        String[] retVal = dfs.getEras();
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Failed to set strings", retVal[i].equals(val[i]));

    }

    public void test_setLocalPatternCharsLjava_lang_String() {
        String patternChars = "GyMZZkHmsSEHHFwWahKz";
        dfs.setLocalPatternChars(patternChars);
        TestCase.assertEquals(patternChars, dfs.getLocalPatternChars());
        try {
            // Regression for HARMONY-466
            new DateFormatSymbols().setLocalPatternChars(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#setMonths(java.lang.String[])
     */
    public void test_setMonths$Ljava_lang_String() {
        // Test for method void
        // java.text.DateFormatSymbols.setMonths(java.lang.String [])
        String[] val = new String[]{ "XX", "YY" };
        dfs.setMonths(val);
        String[] retVal = dfs.getMonths();
        TestCase.assertTrue("Return is identical", (retVal != (dfs.getMonths())));
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Failed to set strings", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#setShortMonths(java.lang.String[])
     */
    public void test_setShortMonths$Ljava_lang_String() {
        // Test for method void
        // java.text.DateFormatSymbols.setShortMonths(java.lang.String [])
        String[] val = new String[]{ "XX", "YY" };
        dfs.setShortMonths(val);
        String[] retVal = dfs.getShortMonths();
        TestCase.assertTrue("Return is identical", (retVal != (dfs.getShortMonths())));
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Failed to set strings", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#setShortWeekdays(java.lang.String[])
     */
    public void test_setShortWeekdays$Ljava_lang_String() {
        // Test for method void
        // java.text.DateFormatSymbols.setShortWeekdays(java.lang.String [])
        String[] val = new String[]{ "XX", "YY" };
        dfs.setShortWeekdays(val);
        String[] retVal = dfs.getShortWeekdays();
        TestCase.assertTrue("Return is identical", (retVal != (dfs.getShortWeekdays())));
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Failed to set strings", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#setWeekdays(java.lang.String[])
     */
    public void test_setWeekdays$Ljava_lang_String() {
        // Test for method void
        // java.text.DateFormatSymbols.setWeekdays(java.lang.String [])
        String[] val = new String[]{ "XX", "YY" };
        dfs.setWeekdays(val);
        String[] retVal = dfs.getWeekdays();
        TestCase.assertTrue("Return is identical", (retVal != (dfs.getWeekdays())));
        if ((retVal.length) != (val.length))
            TestCase.fail("Returned wrong array");

        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue("Failed to set strings", retVal[i].equals(val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#setZoneStrings(java.lang.String[][])
     */
    public void test_setZoneStrings$$Ljava_lang_String() {
        // Test for method void
        // java.text.DateFormatSymbols.setZoneStrings(java.lang.String [][])
        String[][] val = new String[][]{ new String[]{ "XX", "XX", "XX", "XX", "XX" }, new String[]{ "YY", "YY", "YY", "YY", "YY" } };
        dfs.setZoneStrings(val);
        String[][] retVal = dfs.getZoneStrings();
        TestCase.assertTrue("get returns identical", (retVal != (dfs.getZoneStrings())));
        TestCase.assertTrue("get[0] returns identical", ((retVal[0]) != (dfs.getZoneStrings()[0])));
        TestCase.assertTrue("get returned identical", (retVal != val));
        TestCase.assertEquals("Returned wrong array", val.length, retVal.length);
        for (int i = 0; i < (val.length); i++)
            TestCase.assertTrue(("Failed to set strings: " + (retVal[i])), Arrays.equals(retVal[i], val[i]));

    }

    /**
     *
     *
     * @unknown java.text.DateFormatSymbols#setZoneStrings(java.lang.String[][])

    Tests setting zone strings to invalid values
    Regression for HARMONY-6337
     */
    public void test_setZoneStrings_invalid() {
        // failing cases
        String[][] val1 = null;
        try {
            dfs.setZoneStrings(val1);
            TestCase.fail("Attempt to set zone strings a null array should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        String[][] val2 = new String[][]{ new String[]{ "XX", "XX" }, new String[]{ "YY", "YY" } };
        try {
            dfs.setZoneStrings(val2);
            TestCase.fail(("Attempt to set zone strings to a 2D array that contains one or more " + "rows of length less than 5 should throw IllegalArgumentException"));
        } catch (IllegalArgumentException e) {
            // expected because each subarray has length < 5
        }
        String[][] val3 = new String[][]{ new String[]{ "a", "b", "c", "d", "e" }, new String[]{ "a", "b", "c", "d", "e" }, new String[]{ "a", "b", "c", "d" }, new String[]{ "a", "b", "c", "d", "e" } };
        try {
            dfs.setZoneStrings(val3);
            TestCase.fail(("Attempt to set zone strings to a 2D array that contains one or more " + "rows of length less than 5 should throw IllegalArgumentException"));
        } catch (IllegalArgumentException e) {
            // expected because each subarray has length < 5
        }
    }

    // Test serialization mechanism of DateFormatSymbols
    public void test_serialization() throws Exception {
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.FRANCE);
        String[][] zoneStrings = symbols.getZoneStrings();
        TestCase.assertNotNull(zoneStrings);
        // serialize
        ByteArrayOutputStream byteOStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOStream = new ObjectOutputStream(byteOStream);
        objectOStream.writeObject(symbols);
        // and deserialize
        ObjectInputStream objectIStream = new ObjectInputStream(new ByteArrayInputStream(byteOStream.toByteArray()));
        DateFormatSymbols symbolsD = ((DateFormatSymbols) (objectIStream.readObject()));
        String[][] zoneStringsD = symbolsD.getZoneStrings();
        TestCase.assertNotNull(zoneStringsD);
        TestCase.assertEquals(symbols, symbolsD);
    }
}

