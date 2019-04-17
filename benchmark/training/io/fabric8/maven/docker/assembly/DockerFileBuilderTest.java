package io.fabric8.maven.docker.assembly;


import Arguments.Builder;
import DockerFileKeyword.EXPOSE;
import DockerFileKeyword.RUN;
import DockerFileKeyword.USER;
import HealthCheckMode.none;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DockerFileBuilderTest {
    @Test
    public void testBuildDockerFile() throws Exception {
        Arguments a = Builder.get().withParam("c1").withParam("c2").build();
        String dockerfileContent = new DockerFileBuilder().add("/src", "/dest").baseImage("image").cmd(a).env(ImmutableMap.of("foo", "bar")).basedir("/export").expose(Collections.singletonList("8080")).maintainer("maintainer@example.com").workdir("/tmp").labels(ImmutableMap.of("com.acme.foobar", "How are \"you\" ?")).volumes(Collections.singletonList("/vol1")).run(Arrays.asList("echo something", "echo second")).content();
        String expected = loadFile("docker/Dockerfile.test");
        Assert.assertEquals(expected, stripCR(dockerfileContent));
    }

    @Test
    public void testBuildDockerFileMultilineLabel() throws Exception {
        Arguments a = Builder.get().withParam("c1").withParam("c2").build();
        String dockerfileContent = new DockerFileBuilder().add("/src", "/dest").baseImage("image").cmd(a).labels(ImmutableMap.of("key", "unquoted", "flag", "", "with_space", "1.fc nuremberg", "some-json", "{\n  \"key\": \"value\"\n}\n")).content();
        String expected = loadFile("docker/Dockerfile.multiline_label.test");
        Assert.assertEquals(expected, stripCR(dockerfileContent));
    }

    @Test
    public void testBuildLabelWithSpace() throws Exception {
        String dockerfileContent = new DockerFileBuilder().labels(ImmutableMap.of("key", "label with space")).content();
        Assert.assertTrue(stripCR(dockerfileContent).contains("LABEL key=\"label with space\""));
    }

    @Test
    public void testBuildDockerFileUDPPort() throws Exception {
        Arguments a = Builder.get().withParam("c1").withParam("c2").build();
        String dockerfileContent = new DockerFileBuilder().add("/src", "/dest").baseImage("image").cmd(a).basedir("/export").expose(Collections.singletonList("8080/udp")).maintainer("maintainer@example.com").workdir("/tmp").volumes(Collections.singletonList("/vol1")).run(Arrays.asList("echo something", "echo second")).content();
        String expected = loadFile("docker/Dockerfile_udp.test");
        Assert.assertEquals(expected, stripCR(dockerfileContent));
    }

    @Test
    public void testBuildDockerFileExplicitTCPPort() throws Exception {
        Arguments a = Builder.get().withParam("c1").withParam("c2").build();
        String dockerfileContent = new DockerFileBuilder().add("/src", "/dest").baseImage("image").cmd(a).basedir("/export").expose(Collections.singletonList("8080/tcp")).maintainer("maintainer@example.com").workdir("/tmp").volumes(Collections.singletonList("/vol1")).run(Arrays.asList("echo something", "echo second")).content();
        String expected = loadFile("docker/Dockerfile_tcp.test");
        Assert.assertEquals(expected, stripCR(dockerfileContent));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildDockerFileBadPort() throws Exception {
        Arguments a = Builder.get().withParam("c1").withParam("c2").build();
        new DockerFileBuilder().add("/src", "/dest").baseImage("image").cmd(a).env(ImmutableMap.of("foo", "bar")).basedir("/export").expose(Collections.singletonList("8080aaa/udp")).maintainer("maintainer@example.com").workdir("/tmp").labels(ImmutableMap.of("com.acme.foobar", "How are \"you\" ?")).volumes(Collections.singletonList("/vol1")).run(Arrays.asList("echo something", "echo second")).content();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildDockerFileBadProtocol() throws Exception {
        Arguments a = Builder.get().withParam("c1").withParam("c2").build();
        new DockerFileBuilder().add("/src", "/dest").baseImage("image").cmd(a).env(ImmutableMap.of("foo", "bar")).basedir("/export").expose(Collections.singletonList("8080/bogusdatagram")).maintainer("maintainer@example.com").workdir("/tmp").labels(ImmutableMap.of("com.acme.foobar", "How are \"you\" ?")).volumes(Collections.singletonList("/vol1")).run(Arrays.asList("echo something", "echo second")).content();
    }

    @Test
    public void testDockerFileOptimisation() throws Exception {
        Arguments a = Builder.get().withParam("c1").withParam("c2").build();
        String dockerfileContent = new DockerFileBuilder().add("/src", "/dest").baseImage("image").cmd(a).env(ImmutableMap.of("foo", "bar")).basedir("/export").expose(Collections.singletonList("8080")).maintainer("maintainer@example.com").workdir("/tmp").labels(ImmutableMap.of("com.acme.foobar", "How are \"you\" ?")).volumes(Collections.singletonList("/vol1")).run(Arrays.asList("echo something", "echo second", "echo third", "echo fourth", "echo fifth")).optimise().content();
        String expected = loadFile("docker/Dockerfile_optimised.test");
        Assert.assertEquals(expected, stripCR(dockerfileContent));
    }

    @Test
    public void testMaintainer() {
        String dockerfileContent = new DockerFileBuilder().maintainer("maintainer@example.com").content();
        Assert.assertThat(DockerFileBuilderTest.dockerfileToMap(dockerfileContent), Matchers.hasEntry("MAINTAINER", "maintainer@example.com"));
    }

    @Test
    public void testOptimise() {
        String dockerfileContent = new DockerFileBuilder().optimise().run(Arrays.asList("echo something", "echo two")).content();
        Assert.assertThat(DockerFileBuilderTest.dockerfileToMap(dockerfileContent), Matchers.hasEntry("RUN", "echo something && echo two"));
    }

    @Test
    public void testOptimiseOnEmptyRunCommandListDoesNotThrowException() {
        new DockerFileBuilder().optimise().content();
    }

    @Test
    public void testEntryPointShell() {
        Arguments a = Builder.get().withShell("java -jar /my-app-1.1.1.jar server").build();
        String dockerfileContent = new DockerFileBuilder().entryPoint(a).content();
        Assert.assertThat(DockerFileBuilderTest.dockerfileToMap(dockerfileContent), Matchers.hasEntry("ENTRYPOINT", "java -jar /my-app-1.1.1.jar server"));
    }

    @Test
    public void testEntryPointParams() {
        Arguments a = Builder.get().withParam("java").withParam("-jar").withParam("/my-app-1.1.1.jar").withParam("server").build();
        String dockerfileContent = new DockerFileBuilder().entryPoint(a).content();
        Assert.assertThat(DockerFileBuilderTest.dockerfileToMap(dockerfileContent), Matchers.hasEntry("ENTRYPOINT", "[\"java\",\"-jar\",\"/my-app-1.1.1.jar\",\"server\"]"));
    }

    @Test
    public void testHealthCheckCmdParams() {
        HealthCheckConfiguration hc = new HealthCheckConfiguration.Builder().cmd(new Arguments("echo hello")).interval("5s").timeout("3s").startPeriod("30s").retries(4).build();
        String dockerfileContent = new DockerFileBuilder().healthCheck(hc).content();
        Assert.assertThat(DockerFileBuilderTest.dockerfileToMap(dockerfileContent), Matchers.hasEntry("HEALTHCHECK", "--interval=5s --timeout=3s --start-period=30s --retries=4 CMD echo hello"));
    }

    @Test
    public void testHealthCheckNone() {
        HealthCheckConfiguration hc = new HealthCheckConfiguration.Builder().mode(none).build();
        String dockerfileContent = new DockerFileBuilder().healthCheck(hc).content();
        Assert.assertThat(DockerFileBuilderTest.dockerfileToMap(dockerfileContent), Matchers.hasEntry("HEALTHCHECK", "NONE"));
    }

    @Test
    public void testNoRootExport() {
        Assert.assertFalse(new DockerFileBuilder().add("/src", "/dest").basedir("/").content().contains("VOLUME"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalNonAbsoluteBaseDir() {
        new DockerFileBuilder().basedir("blub").content();
    }

    @Test
    public void testAssemblyUserWithChown() {
        String dockerFile = new DockerFileBuilder().assemblyUser("jboss:jboss:jboss").add("a", "a/nested").add("b", "b/deeper/nested").content();
        String EXPECTED_REGEXP = "chown\\s+-R\\s+jboss:jboss\\s+([^\\s]+)" + "\\s+&&\\s+cp\\s+-rp\\s+\\1/\\*\\s+/\\s+&&\\s+rm\\s+-rf\\s+\\1";
        Pattern pattern = Pattern.compile(EXPECTED_REGEXP);
        Assert.assertTrue(pattern.matcher(dockerFile).find());
    }

    @Test
    public void testUser() {
        String dockerFile = new DockerFileBuilder().assemblyUser("jboss:jboss:jboss").user("bob").add("a", "a/nested").add("b", "b/deeper/nested").content();
        String EXPECTED_REGEXP = "USER bob$";
        Pattern pattern = Pattern.compile(EXPECTED_REGEXP);
        Assert.assertTrue(pattern.matcher(dockerFile).find());
    }

    @Test
    public void testExportBaseDir() {
        Assert.assertTrue(new DockerFileBuilder().basedir("/export").content().contains("/export"));
        Assert.assertFalse(new DockerFileBuilder().baseImage("java").basedir("/export").content().contains("/export"));
        Assert.assertTrue(new DockerFileBuilder().baseImage("java").exportTargetDir(true).basedir("/export").content().contains("/export"));
        Assert.assertFalse(new DockerFileBuilder().baseImage("java").exportTargetDir(false).basedir("/export").content().contains("/export"));
    }

    @Test
    public void testDockerFileKeywords() {
        StringBuilder b = new StringBuilder();
        RUN.addTo(b, "apt-get", "update");
        Assert.assertEquals("RUN apt-get update\n", b.toString());
        b = new StringBuilder();
        EXPOSE.addTo(b, new String[]{ "1010", "2020" });
        Assert.assertEquals("EXPOSE 1010 2020\n", b.toString());
        b = new StringBuilder();
        USER.addTo(b, "roland");
        Assert.assertEquals("USER roland\n", b.toString());
    }
}
