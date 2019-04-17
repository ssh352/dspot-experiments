/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
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


import HTTPServerConfig.DEFAULT_PORT;
import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static HTTPServerConfig.LANGUAGE_MODEL_OPTION;


public class HTTPServerConfigTest {
    @Test
    public void testArgumentParsing() {
        HTTPServerConfig config1 = new HTTPServerConfig(new String[]{  });
        Assert.assertThat(config1.getPort(), Is.is(DEFAULT_PORT));
        Assert.assertThat(config1.isPublicAccess(), Is.is(false));
        Assert.assertThat(config1.isVerbose(), Is.is(false));
        HTTPServerConfig config2 = new HTTPServerConfig("--public".split(" "));
        Assert.assertThat(config2.getPort(), Is.is(DEFAULT_PORT));
        Assert.assertThat(config2.isPublicAccess(), Is.is(true));
        Assert.assertThat(config2.isVerbose(), Is.is(false));
        HTTPServerConfig config3 = new HTTPServerConfig("--port 80".split(" "));
        Assert.assertThat(config3.getPort(), Is.is(80));
        Assert.assertThat(config3.isPublicAccess(), Is.is(false));
        Assert.assertThat(config3.isVerbose(), Is.is(false));
        HTTPServerConfig config4 = new HTTPServerConfig("--port 80 --public".split(" "));
        Assert.assertThat(config4.getPort(), Is.is(80));
        Assert.assertThat(config4.isPublicAccess(), Is.is(true));
        Assert.assertThat(config4.isVerbose(), Is.is(false));
    }

    @Test
    public void shouldLoadLanguageModelDirectoryFromCommandLineArguments() throws IOException {
        // given
        ClassLoader classLoader = this.getClass().getClassLoader();
        String languageModelDirectory = "languageModelDirectory";
        String targetLanguageModelDirectory = classLoader.getResource(("org/languagetool/server/" + languageModelDirectory)).getFile();
        // when
        HTTPServerConfig config = new HTTPServerConfig(new String[]{ LANGUAGE_MODEL_OPTION, targetLanguageModelDirectory });
        // then
        Assert.assertNotNull(config.languageModelDir);
        Assert.assertTrue(config.languageModelDir.exists());
        Assert.assertTrue(config.languageModelDir.getAbsolutePath().endsWith(languageModelDirectory));
    }
}
