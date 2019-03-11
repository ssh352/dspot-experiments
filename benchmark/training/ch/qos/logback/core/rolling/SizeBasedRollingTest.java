/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.rolling;


import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.rolling.testUtil.ScaffoldingForRollingTests;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.util.FileSize;
import java.io.IOException;
import java.util.List;
import org.junit.Test;


public class SizeBasedRollingTest extends ScaffoldingForRollingTests {
    RollingFileAppender<Object> rfa = new RollingFileAppender<Object>();

    FixedWindowRollingPolicy fwrp = new FixedWindowRollingPolicy();

    SizeBasedTriggeringPolicy<Object> sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy<Object>();

    EchoEncoder<Object> encoder = new EchoEncoder<Object>();

    /**
     * Test whether FixedWindowRollingPolicy throws an exception when the
     * ActiveFileName is not set.
     */
    @Test(expected = IllegalStateException.class)
    public void activeFileNameNotSet() {
        sizeBasedTriggeringPolicy.setMaxFileSize(new FileSize(100));
        sizeBasedTriggeringPolicy.start();
        fwrp.setFileNamePattern(((CoreTestConstants.OUTPUT_DIR_PREFIX) + "sizeBased-test1.%i"));
        fwrp.start();
        // The absence of activeFileName option should cause an exception.
    }

    @Test
    public void smoke() throws IOException, InterruptedException {
        expectedFilenameList.add(((randomOutputDir) + "a-sizeBased-smoke.log"));
        expectedFilenameList.add(((randomOutputDir) + "sizeBased-smoke.0"));
        expectedFilenameList.add(((randomOutputDir) + "sizeBased-smoke.1"));
        generic("zipped", "a-sizeBased-smoke.log", "sizeBased-smoke.%i", expectedFilenameList);
    }

    @Test
    public void gz() throws IOException, InterruptedException {
        expectedFilenameList.add(((randomOutputDir) + "a-sbr-gzed.log"));
        expectedFilenameList.add(((randomOutputDir) + "sbr-gzed.0.gz"));
        expectedFilenameList.add(((randomOutputDir) + "sbr-gzed.1.gz"));
        generic("gzed", "a-sbr-gzed.log", "sbr-gzed.%i.gz", expectedFilenameList);
    }

    // see also LBCORE-199
    @Test
    public void zipped() throws IOException, InterruptedException {
        expectedFilenameList.add(((randomOutputDir) + "a-sbr-zipped.log"));
        expectedFilenameList.add(((randomOutputDir) + "sbr-zipped.0.zip"));
        expectedFilenameList.add(((randomOutputDir) + "sbr-zipped.1.zip"));
        generic("zipped", "a-sbr-zipped.log", "sbr-zipped.%i.zip", expectedFilenameList);
        List<String> zipFiles = filterElementsInListBySuffix(".zip");
        zipEntryNameCheck(zipFiles, "sbr-zipped.20\\d{2}-\\d{2}-\\d{2}_\\d{4}");
    }
}

