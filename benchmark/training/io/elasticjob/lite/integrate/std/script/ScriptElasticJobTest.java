/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */
package io.elasticjob.lite.integrate.std.script;


import io.elasticjob.lite.api.script.ScriptJob;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.fixture.util.ScriptElasticJobUtil;
import io.elasticjob.lite.integrate.AbstractBaseStdJobAutoInitTest;
import io.elasticjob.lite.integrate.WaitingUtils;
import io.elasticjob.lite.internal.config.LiteJobConfigurationGsonFactory;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ScriptElasticJobTest extends AbstractBaseStdJobAutoInitTest {
    public ScriptElasticJobTest() {
        super(ScriptJob.class);
    }

    @Test
    public void assertJobInit() throws IOException {
        ScriptElasticJobUtil.buildScriptCommandLine();
        WaitingUtils.waitingShortTime();
        String scriptCommandLine = getScriptCommandLine();
        LiteJobConfiguration liteJobConfig = LiteJobConfigurationGsonFactory.fromJson(getRegCenter().get((("/" + (getJobName())) + "/config")));
        Assert.assertThat(((io.elasticjob.lite.config.script.ScriptJobConfiguration) (liteJobConfig.getTypeConfig())).getScriptCommandLine(), CoreMatchers.is(scriptCommandLine));
    }
}

