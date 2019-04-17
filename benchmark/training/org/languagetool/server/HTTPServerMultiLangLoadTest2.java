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
package org.languagetool.server;


import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.tools.StringTools;


/**
 * Test HTTP server access from multiple threads with multiple languages.
 * Unlike HTTPServerMultiLangLoadTest, this always sends the same text
 * but actually checks results (compares multi-thread results to non-multi-thread).
 */
@Ignore("for interactive use; requires local Tatoeba data")
public class HTTPServerMultiLangLoadTest2 extends HTTPServerMultiLangLoadTest {
    private static final String DATA_PATH = "/media/Data/tatoeba/";

    private static final int MIN_TEXT_LENGTH = 500;

    private static final int MAX_TEXT_LENGTH = 1000;

    private static final int MAX_SLEEP_MILLIS = 10;

    private final Map<Language, String> textToResult = new HashMap<>();

    @Test
    @Override
    public void testHTTPServer() throws Exception {
        File dir = new File(HTTPServerMultiLangLoadTest2.DATA_PATH);
        List<Language> languages = new ArrayList<>();
        // languages.add(new German());
        languages.addAll(Languages.get());
        for (Language language : languages) {
            File file = new File(dir, (("tatoeba-" + (language.getShortCode())) + ".txt"));
            if (!(file.exists())) {
                System.err.println((("No data found for " + language) + ", language will not be tested"));
            } else {
                String content = StringTools.readerToString(new FileReader(file));
                int fromPos = random.nextInt(content.length());
                int toPos = (fromPos + (random.nextInt(HTTPServerMultiLangLoadTest2.MAX_TEXT_LENGTH))) + (HTTPServerMultiLangLoadTest2.MIN_TEXT_LENGTH);
                String textSubstring = content.substring(fromPos, Math.min(toPos, content.length()));
                langCodeToText.put(language, textSubstring);
                String response = checkByPOST(language, textSubstring);
                textToResult.put(language, response);
                System.err.println(((("Using " + (content.length())) + " bytes of data for ") + language));
            }
        }
        if ((langCodeToText.size()) == 0) {
            throw new RuntimeException(("No input data found in " + dir));
        }
        System.out.println((("Testing " + (langCodeToText.keySet().size())) + " languages and variants"));
        // super.testHTTPServer();  // start server in this JVM
        super.doTest();// assume server has been started manually in its own JVM

    }
}
