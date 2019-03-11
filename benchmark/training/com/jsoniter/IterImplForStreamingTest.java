package com.jsoniter;


import IterImplForStreaming.numberChars;
import junit.framework.TestCase;


public class IterImplForStreamingTest extends TestCase {
    public void testReadMaxDouble() throws Exception {
        String maxDouble = "1.7976931348623157e+308";
        JsonIterator iter = JsonIterator.parse("1.7976931348623157e+308");
        numberChars numberChars = IterImplForStreaming.readNumber(iter);
        String number = new String(numberChars.chars, 0, numberChars.charsLength);
        TestCase.assertEquals(maxDouble, number);
    }
}

