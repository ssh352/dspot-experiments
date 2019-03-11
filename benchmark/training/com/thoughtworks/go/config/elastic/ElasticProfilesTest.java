/**
 * Copyright 2016 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config.elastic;


import org.junit.Assert;
import org.junit.Test;


public class ElasticProfilesTest {
    @Test
    public void shouldFindProfileById() throws Exception {
        Assert.assertThat(new ElasticProfiles().find("foo"), is(nullValue()));
        ElasticProfile profile = new ElasticProfile("foo", "docker");
        Assert.assertThat(find("foo"), is(profile));
    }

    @Test
    public void shouldNotAllowMultipleProfilesWithSameId() throws Exception {
        ElasticProfile profile1 = new ElasticProfile("foo", null);
        ElasticProfile profile2 = new ElasticProfile("foo", null);
        ElasticProfiles profiles = new ElasticProfiles(profile1, profile2);
        profiles.validate(null);
        Assert.assertThat(profile1.errors().size(), is(1));
        Assert.assertThat(profile1.errors().asString(), is("Elastic agent profile id 'foo' is not unique"));
        Assert.assertThat(profile2.errors().size(), is(1));
        Assert.assertThat(profile2.errors().asString(), is("Elastic agent profile id 'foo' is not unique"));
    }
}

