package sagan.tools.support;


import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ToolXmlConverter_SingleLegacyDownloadTests {
    private List<ToolSuiteDownloads> toolSuites;

    private ToolXmlConverter toolXmlConverter;

    @Test
    public void addsAReleaseName() throws Exception {
        MatcherAssert.assertThat(toolSuites.stream().map(ToolSuiteDownloads::getReleaseName).collect(Collectors.toList()), Matchers.contains("3.5.1.RELEASE", "3.5.0.RELEASE"));
    }

    @Test
    public void addsAPlatform() throws Exception {
        ToolSuiteDownloads toolSuite = toolSuites.get(0);
        MatcherAssert.assertThat(toolSuite.getPlatformList().size(), Matchers.equalTo(3));
        MatcherAssert.assertThat(toolSuite.getPlatformList().get(1).getName(), Matchers.equalTo("Mac"));
    }

    @Test
    public void addsAnEclipseVersionToThePlatform() throws Exception {
        ToolSuiteDownloads toolSuite = toolSuites.get(0);
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        MatcherAssert.assertThat(platform.getEclipseVersions().size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(platform.getEclipseVersions().get(0).getName(), Matchers.equalTo("4.3.2"));
        MatcherAssert.assertThat(platform.getEclipseVersions().get(1).getName(), Matchers.equalTo("3.8.2"));
    }

    @Test
    public void addsAnArchitectureToTheEclipseVersion() throws Exception {
        ToolSuiteDownloads toolSuite = toolSuites.get(0);
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
        MatcherAssert.assertThat(eclipseVersion.getArchitectures().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipseVersion.getArchitectures().get(0).getName(), Matchers.equalTo("Mac OS X (Cocoa)"));
    }

    @Test
    public void addsADownloadLinkTheArchitecture() throws Exception {
        ToolSuiteDownloads toolSuite = toolSuites.get(0);
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
        Architecture architecture = eclipseVersion.getArchitectures().get(0);
        MatcherAssert.assertThat(architecture.getDownloadLinks().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(architecture.getDownloadLinks().get(0).getUrl(), Matchers.equalTo("http://download.springsource.com/release/STS/3.5.1/dist/e4.3/spring-tool-suite-3.5.1.RELEASE-e4.3.2-macosx-cocoa-installer.dmg"));
        MatcherAssert.assertThat(architecture.getDownloadLinks().get(0).getOs(), Matchers.equalTo("mac"));
        MatcherAssert.assertThat(architecture.getDownloadLinks().get(0).getArchitecture(), Matchers.equalTo("32"));
    }
}

