/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.typescript.dto;


import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Florent Benoit
 */
public class TypeScriptDTOGeneratorMojoTest {
    /**
     * Rule to manage the mojo (inject, get variables from mojo)
     */
    @Rule
    public MojoRule rule = new MojoRule();

    /**
     * Resources of each test mapped on the name of the method
     */
    @Rule
    public TestResources resources = new TestResources();

    /**
     * Check that the TypeScript definition is generated and that WorkspaceDTO is generated
     * (dependency is part of the test)
     */
    @Test
    public void testCheckTypeScriptGenerated() throws Exception {
        File projectCopy = this.resources.getBasedir("project");
        File pom = new File(projectCopy, "pom.xml");
        Assert.assertNotNull(pom);
        Assert.assertTrue(pom.exists());
        TypeScriptDTOGeneratorMojo mojo = ((TypeScriptDTOGeneratorMojo) (this.rule.lookupMojo("build", pom)));
        configure(mojo, projectCopy);
        mojo.execute();
        File typeScriptFile = mojo.getTypescriptFile();
        // Check file has been generated
        Assert.assertTrue(typeScriptFile.exists());
        // Now check there is "org.eclipse.che.plugin.typescript.dto.MyCustomDTO" inside
        boolean foundMyCustomDTO = false;
        try (BufferedReader reader = Files.newBufferedReader(typeScriptFile.toPath(), StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            while ((line != null) && (!foundMyCustomDTO)) {
                if (line.contains("MyCustomDTO")) {
                    foundMyCustomDTO = true;
                }
                line = reader.readLine();
            } 
        }
        Assert.assertTrue("The MyCustomDTO has not been generated in the typescript definition file.", foundMyCustomDTO);
    }

    @Test
    public void checkDTSFileCreated() throws Exception {
        File projectCopy = this.resources.getBasedir("project-d-ts");
        File pom = new File(projectCopy, "pom.xml");
        Assert.assertNotNull(pom);
        Assert.assertTrue(pom.exists());
        TypeScriptDTOGeneratorMojo mojo = ((TypeScriptDTOGeneratorMojo) (this.rule.lookupMojo("build", pom)));
        configure(mojo, projectCopy);
        mojo.execute();
        File typeScriptFile = mojo.getTypescriptFile();
        Assert.assertNotNull(typeScriptFile);
        // Check file has been generated
        Assert.assertTrue(typeScriptFile.exists());
    }
}

