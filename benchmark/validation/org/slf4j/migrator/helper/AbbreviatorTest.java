/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.slf4j.migrator.helper;


import org.junit.Assert;
import org.junit.Test;


public class AbbreviatorTest {
    static final char FS = '/';

    static final String INPUT_0 = "/abc/123456/ABC";

    static final String INPUT_1 = "/abc/123456/xxxxx/ABC";

    RandomHelper rh = new RandomHelper(AbbreviatorTest.FS);

    @Test
    public void testSmoke() {
        {
            Abbreviator abb = new Abbreviator(2, 100, AbbreviatorTest.FS);
            String r = abb.abbreviate(AbbreviatorTest.INPUT_0);
            Assert.assertEquals(AbbreviatorTest.INPUT_0, r);
        }
        {
            Abbreviator abb = new Abbreviator(3, 8, AbbreviatorTest.FS);
            String r = abb.abbreviate(AbbreviatorTest.INPUT_0);
            Assert.assertEquals("/abc/.../ABC", r);
        }
        {
            Abbreviator abb = new Abbreviator(3, 8, AbbreviatorTest.FS);
            String r = abb.abbreviate(AbbreviatorTest.INPUT_0);
            Assert.assertEquals("/abc/.../ABC", r);
        }
    }

    @Test
    public void testImpossibleToAbbreviate() {
        Abbreviator abb = new Abbreviator(2, 20, AbbreviatorTest.FS);
        String in = "iczldqwivpgm/mgrmvbjdxrwmqgprdjusth";
        String r = abb.abbreviate(in);
        Assert.assertEquals(in, r);
    }

    @Test
    public void testNoFS() {
        Abbreviator abb = new Abbreviator(2, 100, AbbreviatorTest.FS);
        String r = abb.abbreviate("hello");
        Assert.assertEquals("hello", r);
    }

    @Test
    public void testZeroPrefix() {
        {
            Abbreviator abb = new Abbreviator(0, 100, AbbreviatorTest.FS);
            String r = abb.abbreviate(AbbreviatorTest.INPUT_0);
            Assert.assertEquals(AbbreviatorTest.INPUT_0, r);
        }
    }

    @Test
    public void testTheories() {
        int MAX_RANDOM_FIXED_LEN = 20;
        int MAX_RANDOM_AVG_LEN = 20;
        int MAX_RANDOM_MAX_LEN = 100;
        for (int i = 0; i < 10000; i++) {
            // System.out.println("Test number " + i);
            // 0 <= fixedLen < MAX_RANDOM_FIXED_LEN
            int fixedLen = rh.nextInt(MAX_RANDOM_FIXED_LEN);
            // 5 <= averageLen < MAX_RANDOM_AVG_LEN
            int averageLen = (rh.nextInt(MAX_RANDOM_AVG_LEN)) + 3;
            // System.out.println("fixedLen="+fixedLen+", averageLen="+averageLen);
            int maxLen = (rh.nextInt(MAX_RANDOM_MAX_LEN)) + fixedLen;
            if (maxLen <= 1) {
                continue;
            }
            // System.out.println("maxLen="+maxLen);
            int targetLen = ((maxLen / 2) + (rh.nextInt((maxLen / 2)))) + 1;
            if (targetLen > maxLen) {
                targetLen = maxLen;
            }
            String filename = rh.buildRandomFileName(averageLen, maxLen);
            Abbreviator abb = new Abbreviator(fixedLen, targetLen, AbbreviatorTest.FS);
            String result = abb.abbreviate(filename);
            assertTheory0(averageLen, filename, result, fixedLen, targetLen);
            assertUsefulness(averageLen, filename, result, fixedLen, targetLen);
            assertTheory1(filename, result, fixedLen, targetLen);
            assertTheory2(filename, result, fixedLen, targetLen);
        }
    }
}

