package de.danoeh.antennapod.core.storage;


import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class APCleanupAlgorithmTest {
    @Test
    public void testCalcMostRecentDateForDeletion() throws Exception {
        APCleanupAlgorithm algo = new APCleanupAlgorithm(24);
        Date curDateForTest = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse("2018-11-13T14:08:56-0800");
        Date resExpected = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse("2018-11-12T14:08:56-0800");
        Date resActual = algo.calcMostRecentDateForDeletion(curDateForTest);
        Assert.assertEquals("cutoff for retaining most recent 1 day", resExpected, resActual);
    }
}

