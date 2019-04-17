/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.tools;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.FakeLanguage;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.TestTools;
import org.languagetool.rules.patterns.PatternRule;
import org.languagetool.rules.patterns.PatternToken;

import static ITSIssueType.Misspelling;


@SuppressWarnings("MagicNumber")
public class RuleMatchAsXmlSerializerTest {
    private static final RuleMatchAsXmlSerializer SERIALIZER = new RuleMatchAsXmlSerializer();

    private static final Language LANG = TestTools.getDemoLanguage();

    @Test
    public void testLanguageAttributes() throws IOException {
        String xml1 = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(Collections.<RuleMatch>emptyList(), "Fake", 5, NORMAL_API, RuleMatchAsXmlSerializerTest.LANG, Collections.<String>emptyList());
        Assert.assertTrue(xml1.contains("shortname=\"xx-XX\""));
        Assert.assertTrue(xml1.contains("name=\"Testlanguage\""));
        String xml2 = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(Collections.<RuleMatch>emptyList(), "Fake", 5, RuleMatchAsXmlSerializerTest.LANG, new FakeLanguage());
        Assert.assertTrue(xml2.contains("shortname=\"xx-XX\""));
        Assert.assertTrue(xml2.contains("name=\"Testlanguage\""));
        Assert.assertTrue(xml2.contains("shortname=\"yy\""));
        Assert.assertTrue(xml2.contains("name=\"FakeLanguage\""));
        Assert.assertThat(StringUtils.countMatches(xml2, "<matches"), Is.is(1));
        Assert.assertThat(StringUtils.countMatches(xml2, "</matches>"), Is.is(1));
    }

    @Test
    public void testApiModes() throws IOException {
        String xmlStart = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(Collections.<RuleMatch>emptyList(), "Fake", 5, START_API, RuleMatchAsXmlSerializerTest.LANG, Collections.<String>emptyList());
        Assert.assertThat(StringUtils.countMatches(xmlStart, "<matches"), Is.is(1));
        Assert.assertThat(StringUtils.countMatches(xmlStart, "</matches>"), Is.is(0));
        String xmlMiddle = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(Collections.<RuleMatch>emptyList(), "Fake", 5, CONTINUE_API, RuleMatchAsXmlSerializerTest.LANG, Collections.<String>emptyList());
        Assert.assertThat(StringUtils.countMatches(xmlMiddle, "<matches"), Is.is(0));
        Assert.assertThat(StringUtils.countMatches(xmlMiddle, "</matches>"), Is.is(0));
        String xmlEnd = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(Collections.<RuleMatch>emptyList(), "Fake", 5, END_API, RuleMatchAsXmlSerializerTest.LANG, Collections.<String>emptyList());
        Assert.assertThat(StringUtils.countMatches(xmlEnd, "<matches"), Is.is(0));
        Assert.assertThat(StringUtils.countMatches(xmlEnd, "</matches>"), Is.is(1));
        String xml = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(Collections.<RuleMatch>emptyList(), "Fake", 5, NORMAL_API, RuleMatchAsXmlSerializerTest.LANG, Collections.<String>emptyList());
        Assert.assertThat(StringUtils.countMatches(xml, "<matches"), Is.is(1));
        Assert.assertThat(StringUtils.countMatches(xml, "</matches>"), Is.is(1));
    }

    @Test
    public void testRuleMatchesToXML() throws IOException {
        List<RuleMatch> matches = new ArrayList<>();
        String text = "This is an test sentence. Here's another sentence with more text.";
        RuleMatchAsXmlSerializerTest.FakeRule rule = new RuleMatchAsXmlSerializerTest.FakeRule();
        RuleMatch match = new RuleMatch(rule, null, 8, 10, "myMessage");
        match.setColumn(99);
        match.setEndColumn(100);
        match.setLine(44);
        match.setEndLine(45);
        matches.add(match);
        String xml = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(matches, text, 5, NORMAL_API, RuleMatchAsXmlSerializerTest.LANG, Collections.<String>emptyList());
        Assert.assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"));
        Pattern matchesPattern = Pattern.compile(((".*<matches software=\"LanguageTool\" version=\"" + (JLanguageTool.VERSION)) + "\" buildDate=\".*?\">.*"), Pattern.DOTALL);
        Matcher matcher = matchesPattern.matcher(xml);
        Assert.assertTrue(((("Did not find expected '<matches>' element ('" + matchesPattern) + "\'), got:\n") + xml), matcher.matches());
        Assert.assertTrue(xml.contains((">\n" + ((("<error fromy=\"44\" fromx=\"98\" toy=\"45\" tox=\"99\" ruleId=\"FAKE_ID\" msg=\"myMessage\" " + "replacements=\"\" context=\"...s is an test...\" contextoffset=\"8\" offset=\"8\" errorlength=\"2\" ") + "category=\"Miscellaneous\" categoryid=\"MISC\" locqualityissuetype=\"misspelling\"/>\n") + "</matches>\n"))));
    }

    @Test
    public void testRuleMatchesToXMLWithCategory() throws IOException {
        List<RuleMatch> matches = new ArrayList<>();
        String text = "This is a test sentence.";
        List<PatternToken> patternTokens = Collections.emptyList();
        Rule patternRule = new PatternRule("MY_ID", RuleMatchAsXmlSerializerTest.LANG, patternTokens, "my description", "my message", "short message");
        patternRule.setCategory(new Category(new CategoryId("TEST_ID"), "MyCategory"));
        RuleMatch match = new RuleMatch(patternRule, null, 8, 10, "myMessage");
        match.setColumn(99);
        match.setEndColumn(100);
        match.setLine(44);
        match.setEndLine(45);
        matches.add(match);
        String xml = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(matches, text, 5, RuleMatchAsXmlSerializerTest.LANG, RuleMatchAsXmlSerializerTest.LANG);
        Assert.assertTrue(xml.contains((">\n" + ((("<error fromy=\"44\" fromx=\"98\" toy=\"45\" tox=\"99\" ruleId=\"MY_ID\" msg=\"myMessage\" " + "replacements=\"\" context=\"...s is a test ...\" contextoffset=\"8\" offset=\"8\" errorlength=\"2\" category=\"MyCategory\" ") + "categoryid=\"TEST_ID\" locqualityissuetype=\"uncategorized\"/>\n") + "</matches>\n"))));
        patternRule.setCategory(new Category(new CategoryId("CAT_ID"), "MyCategory"));
        RuleMatch match2 = new RuleMatch(patternRule, null, 8, 10, "myMessage");
        String xml2 = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(Collections.singletonList(match2), text, 5, RuleMatchAsXmlSerializerTest.LANG, RuleMatchAsXmlSerializerTest.LANG);
        Assert.assertTrue(xml2.contains("category=\"MyCategory\""));
        Assert.assertTrue(xml2.contains("categoryid=\"CAT_ID\""));
    }

    @Test
    public void testRuleMatchesWithShortMessage() throws IOException {
        List<RuleMatch> matches = new ArrayList<>();
        String text = "This is a test sentence.";
        RuleMatch match = new RuleMatch(new RuleMatchAsXmlSerializerTest.FakeRule(), null, 8, 10, "myMessage", "short message");
        matches.add(match);
        String xml = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(matches, text, 5, RuleMatchAsXmlSerializerTest.LANG, null);
        Assert.assertTrue(xml.contains("shortmsg=\"short message\""));
    }

    @Test
    public void testRuleMatchesWithUrlToXML() throws IOException {
        List<RuleMatch> matches = new ArrayList<>();
        String text = "This is an test sentence. Here's another sentence with more text.";
        RuleMatch match = new RuleMatch(new RuleMatchAsXmlSerializerTest.FakeRule() {
            @Override
            public URL getUrl() {
                return Tools.getUrl("http://server.org?id=1&foo=bar");
            }
        }, null, 8, 10, "myMessage");
        match.setColumn(99);
        match.setEndColumn(100);
        match.setLine(44);
        match.setEndLine(45);
        matches.add(match);
        String xml = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(matches, text, 5, NORMAL_API, RuleMatchAsXmlSerializerTest.LANG, Collections.<String>emptyList());
        Assert.assertTrue(xml.contains((">\n" + ((("<error fromy=\"44\" fromx=\"98\" toy=\"45\" tox=\"99\" ruleId=\"FAKE_ID\" msg=\"myMessage\" " + "replacements=\"\" context=\"...s is an test...\" contextoffset=\"8\" offset=\"8\" errorlength=\"2\" url=\"http://server.org?id=1&amp;foo=bar\" ") + "category=\"Miscellaneous\" categoryid=\"MISC\" locqualityissuetype=\"misspelling\"/>\n") + "</matches>\n"))));
    }

    @Test
    public void testRuleMatchesToXMLEscapeBug() throws IOException {
        List<RuleMatch> matches = new ArrayList<>();
        String text = "This is \"an test sentence. Here\'s another sentence with more text.";
        RuleMatch match = new RuleMatch(new RuleMatchAsXmlSerializerTest.FakeRule(), null, 9, 11, "myMessage");
        match.setColumn(99);
        match.setEndColumn(100);
        match.setLine(44);
        match.setEndLine(45);
        matches.add(match);
        String xml = RuleMatchAsXmlSerializerTest.SERIALIZER.ruleMatchesToXml(matches, text, 5, NORMAL_API, RuleMatchAsXmlSerializerTest.LANG, Collections.<String>emptyList());
        Assert.assertTrue(xml.contains((">\n" + ((("<error fromy=\"44\" fromx=\"98\" toy=\"45\" tox=\"99\" ruleId=\"FAKE_ID\" msg=\"myMessage\" " + "replacements=\"\" context=\"... is &quot;an test...\" contextoffset=\"8\" offset=\"9\" errorlength=\"2\" ") + "category=\"Miscellaneous\" categoryid=\"MISC\" locqualityissuetype=\"misspelling\"/>\n") + "</matches>\n"))));
    }

    private static class FakeRule extends PatternRule {
        FakeRule() {
            super("FAKE_ID", TestTools.getDemoLanguage(), Collections.singletonList(new PatternToken("foo", true, false, false)), "My fake description", "Fake message", "Fake short message");
        }

        @Override
        public ITSIssueType getLocQualityIssueType() {
            return Misspelling;
        }
    }
}
