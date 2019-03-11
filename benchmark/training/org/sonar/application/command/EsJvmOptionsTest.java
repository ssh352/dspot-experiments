/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.application.command;


import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.test.ExceptionCauseMatcher;


public class EsJvmOptionsTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor_sets_mandatory_JVM_options() {
        EsJvmOptions underTest = new EsJvmOptions();
        assertThat(underTest.getAll()).containsExactly("-XX:+UseConcMarkSweepGC", "-XX:CMSInitiatingOccupancyFraction=75", "-XX:+UseCMSInitiatingOccupancyOnly", "-XX:+AlwaysPreTouch", "-server", "-Xss1m", "-Djava.awt.headless=true", "-Dfile.encoding=UTF-8", "-Djna.nosys=true", "-Djdk.io.permissionsUseCanonicalPath=true", "-Dio.netty.noUnsafe=true", "-Dio.netty.noKeySetOptimization=true", "-Dio.netty.recycler.maxCapacityPerThread=0", "-Dlog4j.shutdownHookEnabled=false", "-Dlog4j2.disable.jmx=true", "-Dlog4j.skipJansi=true");
    }

    @Test
    public void writeToJvmOptionFile_writes_all_JVM_options_to_file_with_warning_header() throws IOException {
        File file = temporaryFolder.newFile();
        EsJvmOptions underTest = new EsJvmOptions().add("-foo").add("-bar");
        underTest.writeToJvmOptionFile(file);
        assertThat(file).hasContent(("# This file has been automatically generated by SonarQube during startup.\n" + ((((((((((((((((((((("# Please use sonar.search.javaOpts and/or sonar.search.javaAdditionalOpts in sonar.properties to specify jvm options for Elasticsearch\n" + "\n") + "# DO NOT EDIT THIS FILE\n") + "\n") + "-XX:+UseConcMarkSweepGC\n") + "-XX:CMSInitiatingOccupancyFraction=75\n") + "-XX:+UseCMSInitiatingOccupancyOnly\n") + "-XX:+AlwaysPreTouch\n") + "-server\n") + "-Xss1m\n") + "-Djava.awt.headless=true\n") + "-Dfile.encoding=UTF-8\n") + "-Djna.nosys=true\n") + "-Djdk.io.permissionsUseCanonicalPath=true\n") + "-Dio.netty.noUnsafe=true\n") + "-Dio.netty.noKeySetOptimization=true\n") + "-Dio.netty.recycler.maxCapacityPerThread=0\n") + "-Dlog4j.shutdownHookEnabled=false\n") + "-Dlog4j2.disable.jmx=true\n") + "-Dlog4j.skipJansi=true\n") + "-foo\n") + "-bar")));
    }

    @Test
    public void writeToJvmOptionFile_throws_ISE_in_case_of_IOException() throws IOException {
        File notAFile = temporaryFolder.newFolder();
        EsJvmOptions underTest = new EsJvmOptions();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cannot write Elasticsearch jvm options file");
        expectedException.expectCause(ExceptionCauseMatcher.hasType(IOException.class));
        underTest.writeToJvmOptionFile(notAFile);
    }
}

