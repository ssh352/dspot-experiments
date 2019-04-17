/**
 * The MIT License
 *
 * Copyright (c) 2016 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.install;


import InstallState.UNKNOWN;
import hudson.ExtensionList;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SmokeTest;


/**
 * Tests of {@link InstallState}.
 * Effectively the most of the tests do not need the Jenkins instance, but we want to
 * honor Jenkins extension points and hooks, which may influence the behavior.
 *
 * @author Oleg Nenashev
 */
@Category(SmokeTest.class)
public class InstallStateTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void shouldPefromCorrectConversionForAllNames() {
        ExtensionList<InstallState> states = InstallState.all();
        for (InstallState state : states) {
            InstallState afterRoundtrip = InstallStateTest.forName(state.name());
            // It also prevents occasional name duplications
            Assert.assertThat("State after the roundtrip must be equal to the original state", afterRoundtrip, CoreMatchers.equalTo(state));
            Assert.assertTrue((("State " + state) + " should return the extension point instance after deserialization"), (afterRoundtrip == state));
        }
    }

    @Test
    @Issue("JENKINS-35206")
    public void shouldNotFailOnNullXMLField() {
        String xml = "<jenkins.install.InstallState>\n" + ("  <isSetupComplete>true</isSetupComplete>\n" + "</jenkins.install.InstallState>");
        final InstallState state = InstallStateTest.forXml(xml);
        Assert.assertThat(state, CoreMatchers.equalTo(UNKNOWN));
    }

    @Test
    @Issue("JENKINS-35206")
    public void shouldNotFailOnEmptyName() {
        final InstallState state = InstallStateTest.forName("");
        Assert.assertThat(state, CoreMatchers.equalTo(UNKNOWN));
    }

    @Test
    @Issue("JENKINS-35206")
    public void shouldReturnUnknownStateForUnknownName() {
        final InstallState state = InstallStateTest.forName("NonExistentStateName");
        Assert.assertThat(state, CoreMatchers.equalTo(UNKNOWN));
    }
}
