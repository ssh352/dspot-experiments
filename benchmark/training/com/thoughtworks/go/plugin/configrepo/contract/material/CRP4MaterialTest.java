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


import CRP4Material.TYPE_NAME;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRP4MaterialTest extends CRBaseTest<CRP4Material> {
    private final String exampleView = "//depot/dev/src...          //anything/src/...";

    private final CRP4Material p4simple;

    private final CRP4Material p4custom;

    private final CRP4Material invalidP4NoView;

    private final CRP4Material invalidP4NoServer;

    private final CRP4Material invalidPasswordAndEncyptedPasswordSet;

    public CRP4MaterialTest() {
        p4simple = new CRP4Material("10.18.3.102:1666", exampleView);
        p4custom = new CRP4Material("p4materialName", "dir1", false, "10.18.3.102:1666", exampleView, "user1", "pass1", false, false, "lib", "tools");
        invalidP4NoView = new CRP4Material("10.18.3.102:1666", null);
        invalidP4NoServer = new CRP4Material(null, exampleView);
        invalidPasswordAndEncyptedPasswordSet = new CRP4Material("10.18.3.102:1666", null);
        invalidPasswordAndEncyptedPasswordSet.setPassword("pa$sw0rd");
        invalidPasswordAndEncyptedPasswordSet.setEncryptedPassword("26t=$j64");
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingMaterials() {
        CRMaterial value = p4custom;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is(TYPE_NAME));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializing() {
        CRMaterial value = p4custom;
        String json = gson.toJson(value);
        CRP4Material deserializedValue = ((CRP4Material) (gson.fromJson(json, CRMaterial.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

