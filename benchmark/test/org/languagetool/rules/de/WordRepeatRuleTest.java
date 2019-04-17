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
package org.languagetool.rules.de;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.German;
import org.languagetool.language.GermanyGerman;
import org.languagetool.rules.WordRepeatRule;


public class WordRepeatRuleTest {
    private final German german = new GermanyGerman();

    private final WordRepeatRule rule = new GermanWordRepeatRule(TestTools.getEnglishMessages(), german);

    @Test
    public void testRuleGerman() throws IOException {
        JLanguageTool lt = new JLanguageTool(german);
        assertGood("Das sind die S?tze, die die testen sollen.", lt);
        assertGood("S?tze, die die testen.", lt);
        assertGood("Das Haus, auf das das M?dchen zeigt.", lt);
        assertGood("Warum fragen Sie sie nicht selbst?", lt);
        assertGood("Er tut das, damit sie sie nicht sieht.", lt);
        assertBad("Die die S?tze zum testen.", lt);
        assertBad("Und die die S?tze zum testen.", lt);
        assertBad("Auf der der Fensterbank steht eine Blume.", lt);
        assertBad("Das Buch, in in dem es steht.", lt);
        assertBad("Das Haus, auf auf das M?dchen zurennen.", lt);
        assertBad("Sie sie gehen nach Hause.", lt);
    }
}
