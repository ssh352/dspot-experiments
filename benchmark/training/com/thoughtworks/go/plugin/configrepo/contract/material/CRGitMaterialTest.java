/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.configrepo.contract.material;


import CRGitMaterial.TYPE_NAME;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRGitMaterialTest extends CRBaseTest<CRGitMaterial> {
    private String url1 = "http://my.git.repository.com";

    private String url2 = "http://other.git.repository.com";

    private final CRGitMaterial simpleGit;

    private final CRGitMaterial simpleGitBranch;

    private final CRGitMaterial veryCustomGit;

    private final CRGitMaterial invalidNoUrl;

    private final CRGitMaterial whitelistGit;

    private final CRGitMaterial invalidBothWhiteListAndIgnore;

    public CRGitMaterialTest() {
        simpleGit = new CRGitMaterial();
        simpleGit.setUrl(url1);
        simpleGitBranch = new CRGitMaterial();
        simpleGitBranch.setUrl(url2);
        simpleGitBranch.setBranch("develop");
        veryCustomGit = new CRGitMaterial("gitMaterial1", "dir1", false, true, url1, "feature12", false, "externals", "tools");
        whitelistGit = new CRGitMaterial("gitMaterial1", "dir1", false, true, url1, "feature12", true, "externals", "tools");
        invalidNoUrl = new CRGitMaterial("gitMaterial1", "dir1", false, true, null, "feature12", false, "externals", "tools");
        invalidBothWhiteListAndIgnore = new CRGitMaterial("gitMaterial1", "dir1", false, true, url1, "feature12", false, "externals", "tools");
        invalidBothWhiteListAndIgnore.setWhitelistNoCheck("src", "tests");
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingMaterials() {
        CRMaterial value = veryCustomGit;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is(TYPE_NAME));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializing() {
        CRMaterial value = veryCustomGit;
        String json = gson.toJson(value);
        CRGitMaterial deserializedValue = ((CRGitMaterial) (gson.fromJson(json, CRMaterial.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

