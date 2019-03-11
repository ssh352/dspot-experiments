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
package com.thoughtworks.go.presentation;


import TriStateSelection.Action;
import TriStateSelection.Assigner;
import com.thoughtworks.go.config.Agents;
import com.thoughtworks.go.config.ResourceConfig;
import com.thoughtworks.go.config.ResourceConfigs;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TriStateSelectionTest {
    private Set<ResourceConfig> resourceConfigs;

    private Agents agents;

    @Test
    public void shouldHaveActionRemoveIfThereAreNoAgents() {
        List<TriStateSelection> selections = TriStateSelection.forAgentsResources(resourceConfigs, agents);
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("one", Action.remove)));
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("two", Action.remove)));
        Assert.assertThat(selections.size(), Matchers.is(2));
    }

    @Test
    public void shouldHaveActionAddIfAllAgentsHaveThatResource() {
        resourceConfigs.add(new ResourceConfig("all"));
        agents.add(new com.thoughtworks.go.config.AgentConfig("uuid1", "host1", "127.0.0.1", new ResourceConfigs("all")));
        agents.add(new com.thoughtworks.go.config.AgentConfig("uuid2", "host2", "127.0.0.2", new ResourceConfigs("all")));
        List<TriStateSelection> selections = TriStateSelection.forAgentsResources(resourceConfigs, agents);
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("all", Action.add)));
    }

    @Test
    public void shouldBeNoChangeIfAllAgentsHaveThatResource() {
        resourceConfigs.add(new ResourceConfig("some"));
        agents.add(new com.thoughtworks.go.config.AgentConfig("uuid1", "host1", "127.0.0.1", new ResourceConfigs("some")));
        agents.add(new com.thoughtworks.go.config.AgentConfig("uuid2", "host2", "127.0.0.2", new ResourceConfigs()));
        List<TriStateSelection> selections = TriStateSelection.forAgentsResources(resourceConfigs, agents);
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("some", Action.nochange)));
    }

    @Test
    public void shouldHaveActionRemoveIfNoAgentsHaveResource() {
        resourceConfigs.add(new ResourceConfig("none"));
        agents.add(new com.thoughtworks.go.config.AgentConfig("uuid1", "host1", "127.0.0.1", new ResourceConfigs("one")));
        agents.add(new com.thoughtworks.go.config.AgentConfig("uuid2", "host2", "127.0.0.2", new ResourceConfigs("two")));
        List<TriStateSelection> selections = TriStateSelection.forAgentsResources(resourceConfigs, agents);
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("none", Action.remove)));
    }

    @Test
    public void shouldListResourceSelectionInAlhpaOrder() {
        HashSet<ResourceConfig> resourceConfigs = new HashSet<>();
        resourceConfigs.add(new ResourceConfig("B02"));
        resourceConfigs.add(new ResourceConfig("b01"));
        resourceConfigs.add(new ResourceConfig("a01"));
        List<TriStateSelection> selections = TriStateSelection.forAgentsResources(resourceConfigs, agents);
        Assert.assertThat(selections.get(0), Matchers.is(new TriStateSelection("a01", Action.remove)));
        Assert.assertThat(selections.get(1), Matchers.is(new TriStateSelection("b01", Action.remove)));
        Assert.assertThat(selections.get(2), Matchers.is(new TriStateSelection("B02", Action.remove)));
    }

    @Test
    public void shouldDisableWhenDisableVoted() {
        final boolean[] associate = new boolean[1];
        final Assigner<String, String> disableWhenEql = new Assigner<String, String>() {
            public boolean shouldAssociate(String a, String b) {
                return associate[0];
            }

            public String identifier(String s) {
                return s;
            }

            public boolean shouldEnable(String a, String b) {
                return !(a.equals(b));
            }
        };
        final HashSet<String> assignables = new HashSet<>(Arrays.asList("quux", "baz"));
        associate[0] = true;
        List<TriStateSelection> selections = TriStateSelection.convert(assignables, Arrays.asList("foo", "bar"), disableWhenEql);
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("quux", Action.add)));
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("baz", Action.add)));
        associate[0] = false;
        selections = TriStateSelection.convert(assignables, Arrays.asList("foo", "bar"), disableWhenEql);
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("quux", Action.remove)));
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("baz", Action.remove)));
        associate[0] = true;
        selections = TriStateSelection.convert(assignables, Arrays.asList("quux", "bar"), disableWhenEql);
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("quux", Action.add, false)));
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("baz", Action.add, true)));
        associate[0] = false;
        selections = TriStateSelection.convert(assignables, Arrays.asList("bar", "baz"), disableWhenEql);
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("quux", Action.remove, true)));
        Assert.assertThat(selections, Matchers.hasItem(new TriStateSelection("baz", Action.remove, false)));
    }
}

