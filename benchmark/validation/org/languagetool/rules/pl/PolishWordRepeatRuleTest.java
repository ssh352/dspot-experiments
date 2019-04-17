/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.pl;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Polish;
import org.languagetool.rules.RuleMatch;


public class PolishWordRepeatRuleTest {
    @Test
    public void testRule() throws IOException {
        final PolishWordRepeatRule rule = new PolishWordRepeatRule(TestTools.getEnglishMessages());
        RuleMatch[] matches;
        JLanguageTool langTool = new JLanguageTool(new Polish());
        // correct
        matches = rule.match(langTool.getAnalyzedSentence("To jest zdanie pr?bne."));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("On tak si? bardzo nie martwi?, bo przecie? musia? si? umy?."));
        Assert.assertEquals(0, matches.length);
        // repeated prepositions, don't count'em
        matches = rule.match(langTool.getAnalyzedSentence("Na dyskotece ta?czy? jeszcze, cho? by? na bani."));
        Assert.assertEquals(0, matches.length);
        // sf bug report:
        matches = rule.match(langTool.getAnalyzedSentence("?adnych ?ale?."));
        Assert.assertEquals(0, matches.length);
        // incorrect
        matches = rule.match(langTool.getAnalyzedSentence("By? on bowiem pi?knym strzelcem bowiem."));
        Assert.assertEquals(1, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("M?wi?a d?ugo, ?eby tylko m?wi? d?ugo."));
        Assert.assertEquals(2, matches.length);
    }
}
