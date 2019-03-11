package com.baeldung.fileparser;


import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ScannerStringUnitTest {
    protected static final String TEXT_FILENAME = "src/test/resources/sampleTextFile.txt";

    @Test
    public void whenParsingExistingTextFile_thenGetArrayList() throws IOException {
        List<String> lines = ScannerStringExample.generateArrayListFromFile(ScannerStringUnitTest.TEXT_FILENAME);
        Assert.assertTrue("File does not has 2 lines", ((lines.size()) == 2));
    }
}

