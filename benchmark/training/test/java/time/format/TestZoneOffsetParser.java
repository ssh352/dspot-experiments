/**
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
/**
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package test.java.time.format;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.text.ParsePosition;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test ZoneOffsetPrinterParser.
 */
@RunWith(DataProviderRunner.class)
public class TestZoneOffsetParser extends AbstractTestPrinterParser {
    // -----------------------------------------------------------------------
    @Test
    public void test_parse_exactMatch_UTC() throws Exception {
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "Z").parseUnresolved("Z", pos);
        Assert.assertEquals(pos.getIndex(), 1);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    @Test
    public void test_parse_startStringMatch_UTC() throws Exception {
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "Z").parseUnresolved("ZOTHER", pos);
        Assert.assertEquals(pos.getIndex(), 1);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    @Test
    public void test_parse_midStringMatch_UTC() throws Exception {
        ParsePosition pos = new ParsePosition(5);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "Z").parseUnresolved("OTHERZOTHER", pos);
        Assert.assertEquals(pos.getIndex(), 6);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    @Test
    public void test_parse_endStringMatch_UTC() throws Exception {
        ParsePosition pos = new ParsePosition(5);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "Z").parseUnresolved("OTHERZ", pos);
        Assert.assertEquals(pos.getIndex(), 6);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_parse_exactMatch_UTC_EmptyUTC() throws Exception {
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "").parseUnresolved("", pos);
        Assert.assertEquals(pos.getIndex(), 0);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    @Test
    public void test_parse_startStringMatch_UTC_EmptyUTC() throws Exception {
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "").parseUnresolved("OTHER", pos);
        Assert.assertEquals(pos.getIndex(), 0);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    @Test
    public void test_parse_midStringMatch_UTC_EmptyUTC() throws Exception {
        ParsePosition pos = new ParsePosition(5);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "").parseUnresolved("OTHEROTHER", pos);
        Assert.assertEquals(pos.getIndex(), 5);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    @Test
    public void test_parse_endStringMatch_UTC_EmptyUTC() throws Exception {
        ParsePosition pos = new ParsePosition(5);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "").parseUnresolved("OTHER", pos);
        Assert.assertEquals(pos.getIndex(), 5);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_parse_caseSensitiveUTC_matchedCase() throws Exception {
        setCaseSensitive(true);
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "Z").parseUnresolved("Z", pos);
        Assert.assertEquals(pos.getIndex(), 1);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    @Test
    public void test_parse_caseSensitiveUTC_unmatchedCase() throws Exception {
        setCaseSensitive(true);
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "Z").parseUnresolved("z", pos);
        Assert.assertEquals(pos.getErrorIndex(), 0);
        Assert.assertEquals(parsed, null);
    }

    @Test
    public void test_parse_caseInsensitiveUTC_matchedCase() throws Exception {
        setCaseSensitive(false);
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "Z").parseUnresolved("Z", pos);
        Assert.assertEquals(pos.getIndex(), 1);
        assertParsed(parsed, ZoneOffset.UTC);
    }

    @Test
    public void test_parse_caseInsensitiveUTC_unmatchedCase() throws Exception {
        setCaseSensitive(false);
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter("+HH:MM:ss", "Z").parseUnresolved("z", pos);
        Assert.assertEquals(pos.getIndex(), 1);
        assertParsed(parsed, ZoneOffset.UTC);
    }
}
