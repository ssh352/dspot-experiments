package com.baeldung.string.formatter;


import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.IllegalFormatCodePointException;
import org.junit.Assert;
import org.junit.Test;


public class StringFormatterExampleUnitTest {
    @Test
    public void givenString_whenFormatSpecifierForCalendar_thenGotExpected() {
        // Syntax of Format Specifiers for Date/Time Representation
        Calendar c = new GregorianCalendar(2017, 11, 10);
        String s = String.format("The date is: %tm %1$te,%1$tY", c);
        Assert.assertEquals("The date is: 12 10,2017", s);
    }

    @Test
    public void givenString_whenGeneralConversion_thenConvertedString() {
        // General Conversions
        String s = String.format("The correct answer is %s", false);
        Assert.assertEquals("The correct answer is false", s);
        s = String.format("The correct answer is %b", null);
        Assert.assertEquals("The correct answer is false", s);
        s = String.format("The correct answer is %B", true);
        Assert.assertEquals("The correct answer is TRUE", s);
    }

    @Test
    public void givenString_whenCharConversion_thenConvertedString() {
        // Character Conversions
        String s = String.format("The correct answer is %c", 'a');
        Assert.assertEquals("The correct answer is a", s);
        s = String.format("The correct answer is %c", null);
        Assert.assertEquals("The correct answer is null", s);
        s = String.format("The correct answer is %C", 'b');
        Assert.assertEquals("The correct answer is B", s);
        s = String.format("The valid unicode character: %c", 1024);
        Assert.assertTrue(Character.isValidCodePoint(1024));
        Assert.assertEquals("The valid unicode character: ?", s);
    }

    @Test(expected = IllegalFormatCodePointException.class)
    public void givenString_whenIllegalCodePointForConversion_thenError() {
        String s = String.format("The valid unicode character: %c", 1179647);
        Assert.assertFalse(Character.isValidCodePoint(1179647));
        Assert.assertEquals("The valid unicode character: ?", s);
    }

    @Test
    public void givenString_whenNumericIntegralConversion_thenConvertedString() {
        // Numeric Integral Conversions
        String s = String.format("The number 25 in decimal = %d", 25);
        Assert.assertEquals("The number 25 in decimal = 25", s);
        s = String.format("The number 25 in octal = %o", 25);
        Assert.assertEquals("The number 25 in octal = 31", s);
        s = String.format("The number 25 in hexadecimal = %x", 25);
        Assert.assertEquals("The number 25 in hexadecimal = 19", s);
    }

    @Test
    public void givenString_whenNumericFloatingConversion_thenConvertedString() {
        // Numeric Floating-point Conversions
        String s = String.format(("The computerized scientific format of 10000.00 " + "= %e"), 10000.0);
        Assert.assertEquals("The computerized scientific format of 10000.00 = 1.000000e+04", s);
        s = String.format("The decimal format of 10.019 = %f", 10.019);
        Assert.assertEquals("The decimal format of 10.019 = 10.019000", s);
    }

    @Test
    public void givenString_whenLineSeparatorConversion_thenConvertedString() {
        // Line Separator Conversion
        String s = String.format("First Line %nSecond Line");
        Assert.assertEquals((("First Line " + (System.getProperty("line.separator"))) + "Second Line"), s);
    }

    @Test
    public void givenString_whenSpecifyFlag_thenGotFormattedString() {
        // Without left-justified flag
        String s = String.format("Without left justified flag: %5d", 25);
        Assert.assertEquals("Without left justified flag:    25", s);
        // Using left-justified flag
        s = String.format("With left justified flag: %-5d", 25);
        Assert.assertEquals("With left justified flag: 25   ", s);
    }

    @Test
    public void givenString_whenSpecifyPrecision_thenGotExpected() {
        // Precision
        String s = String.format("Output of 25.09878 with Precision 2: %.2f", 25.09878);
        Assert.assertEquals("Output of 25.09878 with Precision 2: 25.10", s);
        s = String.format("Output of general conversion type with Precision 2: %.2b", true);
        Assert.assertEquals("Output of general conversion type with Precision 2: tr", s);
    }

    @Test
    public void givenString_whenSpecifyArgumentIndex_thenGotExpected() {
        Calendar c = new GregorianCalendar(2017, 11, 10);
        // Argument_Index
        String s = String.format("The date is: %tm %1$te,%1$tY", c);
        Assert.assertEquals("The date is: 12 10,2017", s);
        s = String.format("The date is: %tm %<te,%<tY", c);
        Assert.assertEquals("The date is: 12 10,2017", s);
    }

    @Test
    public void givenAppendable_whenCreateFormatter_thenFormatterWorksOnAppendable() {
        // Using String Formatter with Appendable
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format("I am writting to a %s Instance.", sb.getClass());
        Assert.assertEquals("I am writting to a class java.lang.StringBuilder Instance.", sb.toString());
    }

    @Test
    public void givenString_whenNoArguments_thenExpected() {
        // Using String Formatter without arguments
        String s = String.format("John scored 90%% in Fall semester");
        Assert.assertEquals("John scored 90% in Fall semester", s);
    }
}

