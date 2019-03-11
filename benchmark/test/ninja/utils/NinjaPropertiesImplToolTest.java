/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.utils;


import NinjaConstant.applicationSecret;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.util.UUID;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class NinjaPropertiesImplToolTest {
    @Test(expected = RuntimeException.class)
    public void testProdThrowsError() throws Exception {
        PropertiesConfiguration defaultConfiguration = new PropertiesConfiguration();
        Configuration compositeConfiguration = new PropertiesConfiguration();
        String uuid = UUID.randomUUID().toString();
        String baseDirWithoutTrailingSlash = "/tmp/ninja-test-" + uuid;
        boolean isProd = true;
        NinjaPropertiesImplTool.checkThatApplicationSecretIsSet(isProd, baseDirWithoutTrailingSlash, defaultConfiguration, compositeConfiguration);
        FileUtils.deleteDirectory(new File(baseDirWithoutTrailingSlash));
    }

    @Test
    public void testMissingSecretCreatesNewOneInDevMode() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String baseDirWithoutTrailingSlash = "/tmp/ninja-test-" + uuid;
        String devConf = (baseDirWithoutTrailingSlash + (File.separator)) + "src/main/java/conf/application.conf";
        PropertiesConfiguration defaultConfiguration = new PropertiesConfiguration();
        defaultConfiguration.setFileName(devConf);
        Configuration compositeConfiguration = new PropertiesConfiguration();
        boolean isProd = false;
        NinjaPropertiesImplTool.checkThatApplicationSecretIsSet(isProd, baseDirWithoutTrailingSlash, defaultConfiguration, compositeConfiguration);
        Assert.assertTrue(((compositeConfiguration.getString(applicationSecret).length()) == 64));
        Assert.assertTrue(((defaultConfiguration.getString(applicationSecret).length()) == 64));
        Assert.assertTrue(Files.toString(new File(devConf), Charsets.UTF_8).contains(applicationSecret));
        // tear down
        FileUtils.deleteDirectory(new File(baseDirWithoutTrailingSlash));
    }

    @Test
    public void testNothingHappensWhenApplicationSecretIsThere() throws Exception {
        PropertiesConfiguration defaultConfiguration = new PropertiesConfiguration();
        Configuration compositeConfiguration = new PropertiesConfiguration();
        compositeConfiguration.setProperty(applicationSecret, "secret");
        String uuid = UUID.randomUUID().toString();
        String baseDirWithoutTrailingSlash = "/tmp/ninja-test-" + uuid;
        boolean isProd = true;
        // works in prod mode
        NinjaPropertiesImplTool.checkThatApplicationSecretIsSet(isProd, baseDirWithoutTrailingSlash, defaultConfiguration, compositeConfiguration);
        Assert.assertFalse(new File((baseDirWithoutTrailingSlash + "src/main/java/conf/application.conf")).exists());
        isProd = false;
        // also works in the other modes:
        NinjaPropertiesImplTool.checkThatApplicationSecretIsSet(isProd, baseDirWithoutTrailingSlash, defaultConfiguration, compositeConfiguration);
        Assert.assertFalse(new File((baseDirWithoutTrailingSlash + "src/main/java/conf/application.conf")).exists());
        FileUtils.deleteDirectory(new File(baseDirWithoutTrailingSlash));
    }
}

