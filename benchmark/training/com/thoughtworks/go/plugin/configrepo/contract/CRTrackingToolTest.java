/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.configrepo.contract;


import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRTrackingToolTest extends CRBaseTest<CRTrackingTool> {
    private final CRTrackingTool tracking;

    private final CRTrackingTool invalidNoLink;

    private final CRTrackingTool invalidNoRegex;

    public CRTrackingToolTest() {
        tracking = new CRTrackingTool("http://your-trackingtool/yourproject/${ID}", "evo-(\\d+)");
        invalidNoLink = new CRTrackingTool(null, "evo-(\\d+)");
        invalidNoRegex = new CRTrackingTool("http://your-trackingtool/yourproject/${ID}", null);
    }

    @Test
    public void shouldDeserializeFromAPILikeObject() {
        String json = "{\n" + (("    \"link\": \"https://github.com/gocd/api.go.cd/issues/${ID}\",\n" + "    \"regex\": \"##(d+)\"\n") + "  }");
        CRTrackingTool deserializedValue = gson.fromJson(json, CRTrackingTool.class);
        Assert.assertThat(deserializedValue.getLink(), Matchers.is("https://github.com/gocd/api.go.cd/issues/${ID}"));
        Assert.assertThat(deserializedValue.getRegex(), Matchers.is("##(d+)"));
        ErrorCollection errors = deserializedValue.getErrors();
        TestCase.assertTrue(errors.isEmpty());
    }
}
