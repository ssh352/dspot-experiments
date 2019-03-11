/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.config.server.environment;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentRepository.PatternMatchingJGitEnvironmentRepository;
import org.springframework.cloud.config.server.test.ConfigServerTestUtils;
import org.springframework.core.env.StandardEnvironment;


/**
 *
 *
 * @author Andy Chan (iceycake)
 * @author Dave Syer
 * @author Spencer Gibb
 * @author Gareth Clay
 */
public class MultipleJGitEnvironmentRepositoryTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private StandardEnvironment environment = new StandardEnvironment();

    private MultipleJGitEnvironmentRepository repository;

    @Test
    public void defaultRepo() {
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void baseDirRepo() {
        this.repository.setUri(this.repository.getUri().replace("config-repo", "{application}"));
        repository.setBasedir(new File("target/testBase"));
        JGitEnvironmentRepository newRepo = this.repository.getRepository(this.repository, "config-repo", "staging", "master");
        assertThat(newRepo.getBasedir().getAbsolutePath().contains("target/testBase")).isTrue();
    }

    @Test
    public void defaultRepoNested() throws IOException {
        String uri = ConfigServerTestUtils.prepareLocalRepo("another-config-repo");
        this.repository.setUri(uri);
        this.repository.setSearchPaths(new String[]{ "sub" });
        this.repository.findOne("bar", "staging", "master");
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/sub/application.yml"));
        assertVersion(environment);
    }

    @Test
    public void defaultRepoBranch() {
        Environment environment = this.repository.findOne("bar", "staging", "raw");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void defaultRepoTag() {
        Environment environment = this.repository.findOne("bar", "staging", "foo");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void defaultRepoTwice() {
        this.repository.findOne("bar", "staging", "master");
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void defaultRepoBasedir() {
        this.repository.setBasedir(new File("target/testBase"));
        assertThat(this.repository.getBasedir().toString()).contains("target/testBase");
        assertThat(this.repository.getRepos().get("test1").getBasedir().toString()).contains("/test1");
    }

    @Test
    public void mappingRepo() {
        Environment environment = this.repository.findOne("test1-svc", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((getUri("*test1*")) + "/test1-svc.properties"));
        assertVersion(environment);
    }

    @Test
    public void defaultLabel() {
        this.repository.setDefaultLabel("raw");
        Environment environment = this.repository.findOne("bar", "staging", null);
        assertThat(environment.getLabel()).isEqualTo("raw");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void mappingRepoWithDefaultLabel() {
        Environment environment = this.repository.findOne("test1-svc", "staging", null);
        assertThat(environment.getLabel()).isEqualTo("master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((getUri("*test1*")) + "/test1-svc.properties"));
        assertVersion(environment);
    }

    @Test
    public void shouldSetTransportConfigCallback() throws Exception {
        TransportConfigCallback mockCallback1 = Mockito.mock(TransportConfigCallback.class);
        TransportConfigCallback mockCallback2 = Mockito.mock(TransportConfigCallback.class);
        PatternMatchingJGitEnvironmentRepository repo1 = createRepository("test1", "*test1*", "test1Uri");
        PatternMatchingJGitEnvironmentRepository repo2 = createRepository("test2", "*test2*", "test2Uri");
        repo2.setTransportConfigCallback(mockCallback2);
        Map<String, PatternMatchingJGitEnvironmentRepository> repos = new HashMap<>();
        repos.put("test1", repo1);
        repos.put("test2", repo2);
        this.repository.setRepos(repos);
        this.repository.setTransportConfigCallback(mockCallback1);
        this.repository.afterPropertiesSet();
        assertThat(mockCallback1).isEqualTo(repo1.getTransportConfigCallback());
        assertThat(mockCallback2).isEqualTo(repo2.getTransportConfigCallback());
    }

    @Test
    public void setCorrectCredentials() throws Exception {
        final String repo1Username = "repo1-username";
        final String repo1Password = "repo1-password";
        final String repo2Passphrase = "repo2-passphrase";
        final String multiRepoUsername = "multi-repo-username";
        final String multiRepoPassword = "multi-repo-password";
        final String multiRepoPassphrase = "multi-repo-passphrase";
        PatternMatchingJGitEnvironmentRepository repo1 = createRepository("test1", "*test1*", "test1Uri");
        repo1.setUsername(repo1Username);
        repo1.setPassword(repo1Password);
        PatternMatchingJGitEnvironmentRepository repo2 = createRepository("test2", "*test2*", "test2Uri");
        repo2.setPassphrase(repo2Passphrase);
        Map<String, PatternMatchingJGitEnvironmentRepository> repos = new HashMap<>();
        repos.put("test1", repo1);
        repos.put("test2", repo2);
        this.repository.setRepos(repos);
        this.repository.setUsername(multiRepoUsername);
        this.repository.setPassword(multiRepoPassword);
        this.repository.setPassphrase(multiRepoPassphrase);
        this.repository.afterPropertiesSet();
        assertThat(repo1Username).as("Repo1 has its own username which should not be overwritten").isEqualTo(repo1.getUsername());
        assertThat(repo1Password).as("Repo1 has its own password which should not be overwritten").isEqualTo(repo1.getPassword());
        assertThat(multiRepoPassphrase).as("Repo1 did not specify a passphrase so this should have been copied from the multi repo").isEqualTo(repo1.getPassphrase());
        assertThat(multiRepoUsername).as("Repo2 did not specify a username so this should have been copied from the multi repo").isEqualTo(repo2.getUsername());
        assertThat(multiRepoPassword).as("Repo2 did not specify a username so this should have been copied from the multi repo").isEqualTo(repo2.getPassword());
        assertThat(repo2Passphrase).as("Repo2 has its own passphrase which should not have been overwritten").isEqualTo(repo2.getPassphrase());
    }

    @Test
    public void setSkipSslValidation() throws Exception {
        final boolean repo1SkipSslValidation = false;
        final boolean repo2SkipSslValidation = true;
        PatternMatchingJGitEnvironmentRepository repo1 = createRepository("test1", "*test1*", "test1Uri");
        repo1.setSkipSslValidation(repo1SkipSslValidation);
        PatternMatchingJGitEnvironmentRepository repo2 = createRepository("test2", "*test2*", "test2Uri");
        repo2.setSkipSslValidation(repo2SkipSslValidation);
        Map<String, PatternMatchingJGitEnvironmentRepository> repos = new HashMap<>();
        repos.put("test1", repo1);
        repos.put("test2", repo2);
        this.repository.setRepos(repos);
        this.repository.setSkipSslValidation(false);
        this.repository.afterPropertiesSet();
        assertThat(repo1.isSkipSslValidation()).as("If skip SSL validation is false at multi-repo level, then per-repo settings take priority").isFalse();
        assertThat(repo2.isSkipSslValidation()).as("If skip SSL validation is false at multi-repo level, then per-repo settings take priority").isTrue();
        this.repository.setSkipSslValidation(true);
        this.repository.afterPropertiesSet();
        assertThat(repo1.isSkipSslValidation()).as("If explicitly set to skip SSL validation at the multi-repo level, then apply same setting to sub-repos").isTrue();
        assertThat(repo2.isSkipSslValidation()).as("If explicitly set to skip SSL validation at the multi-repo level, then apply same setting to sub-repos").isTrue();
    }

    // test for gh-700
    @Test
    public void basedirCreatedIfNotExists() throws Exception {
        Path tempDir = Files.createTempDirectory("basedirCreatedTest");
        File parent = new File(tempDir.toFile(), "parent");
        File basedir = new File(parent, "basedir");
        this.repository.setBasedir(basedir);
        assertThat(basedir).doesNotExist();
        this.repository.afterPropertiesSet();
        assertThat(basedir).exists();
    }

    // test for gh-700
    @Test
    public void exceptionThrownIfBasedirDoesnotExistAndCannotBeCreated() throws Exception {
        File basedir = Mockito.mock(File.class);
        File absoluteBasedir = Mockito.mock(File.class);
        Mockito.when(basedir.getAbsoluteFile()).thenReturn(absoluteBasedir);
        Mockito.when(absoluteBasedir.exists()).thenReturn(false);
        Mockito.when(absoluteBasedir.mkdir()).thenReturn(false);
        this.repository.setBasedir(basedir);
        this.exception.expect(IllegalStateException.class);
        this.exception.expectMessage("Basedir does not exist and can not be created:");
        this.repository.afterPropertiesSet();
    }
}

